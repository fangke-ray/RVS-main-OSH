package com.osh.rvs.action.pda;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.qf.TurnoverCaseEntity;
import com.osh.rvs.form.qf.TurnoverCaseForm;
import com.osh.rvs.service.qf.TurnoverCaseService;

import framework.huiqing.common.util.message.ApplicationMessage;

public class PdaTcShippingAction extends PdaBaseAction {

	private Logger log = Logger.getLogger(getClass());

	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,SqlSession conn) throws Exception {
		log.info("PdaTcShippingAction.init start");

		TurnoverCaseForm tcForm = (TurnoverCaseForm) form;
		TurnoverCaseService service = new TurnoverCaseService();

		// 货架号
		String shelf = tcForm.getShelf();

		// 全待出库列表
		List<TurnoverCaseForm> shippingPlanList = service.getWarehousingPlanList(conn);

		// 如果没有货架号, 取得最早需出库货架
		if (shelf == null) {
			if (shippingPlanList.size() == 0) {
				// A货架
				shelf = "A";
			} else {
				shelf = shippingPlanList.get(0).getShelf();
			}
		} else if (shippingPlanList.size() > 0) {
			String direct_flg = tcForm.getDirect_flg();
			// 翻页
			if (direct_flg != null && !"".equals(direct_flg)) {
				if ("-1".equals(direct_flg)) {
					String prevShelf = null;
					for (TurnoverCaseForm shippingPlan : shippingPlanList) {
						if (shelf.equals(shippingPlan.getShelf())) {
							break;
						}
						prevShelf = shippingPlan.getShelf();
					}
					if (prevShelf == null) {
						prevShelf = shippingPlanList.get(shippingPlanList.size() - 1).getShelf();
					}
					shelf = prevShelf;
				} else if ("1".equals(direct_flg)) {
					String nextShelf = null; boolean hit = false;
					for (TurnoverCaseForm storagePlan : shippingPlanList) {
						if (shelf.equals(storagePlan.getShelf())) {
							hit = true;
						} else if (hit) {
							nextShelf = storagePlan.getShelf();
							break;
						}
					}
					if (nextShelf == null) {
						nextShelf = shippingPlanList.get(0).getShelf();
					}
					shelf = nextShelf;
				}
			}
		}

		// 当前货架待出库列表
		List<TurnoverCaseForm> shippingPlanListOnShelf = service.filterOnShelf(shippingPlanList, shelf);

		req.setAttribute("waitCount", shippingPlanList.size());
		req.setAttribute("shelf", shelf);
		req.setAttribute("shippingPlanListOnShelf", shippingPlanListOnShelf);
		req.setAttribute("shelfMap", service.getShelfMap(shelf, shippingPlanListOnShelf, conn));

		actionForward = mapping.findForward(FW_INIT);

		log.info("PdaTcShippingAction.init end");
	}

	public void doShipping(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("PdaTcShippingAction.doShipping start");

		TurnoverCaseForm tcForm = (TurnoverCaseForm) form;
		TurnoverCaseService service = new TurnoverCaseService();

		// 提交出库对象
		String location = tcForm.getLocation();
		TurnoverCaseEntity entity = null;
		if (location != null)
			entity = service.getEntityByLocationForShipping(location, conn);

		// 货架号
		String shelf = tcForm.getShelf();

		boolean warehoused = false;
		if (entity != null) {
			// 如果出库对象货架不一致
			if (!shelf.equals(entity.getShelf())) {
				// 不做出库动作
				location = null;
				// 切换货架
				shelf = entity.getShelf();
			} else {
				// 出库动作
				service.warehousing(conn, location);
				warehoused = true;
			}
		} else {
			// 错误信息
			req.setAttribute("errmsg", ApplicationMessage.WARNING_MESSAGES.getMessage("info.turnoverCase.notShippingPlan"));
		}

		// 全待出库列表
		List<TurnoverCaseForm> shippingPlanList = service.getWarehousingPlanList(conn);

		String direct_flg = tcForm.getDirect_flg();
		// 翻页
		if (warehoused && shippingPlanList.size() > 0 && direct_flg != null && !"".equals(direct_flg)) {
			if ("1".equals(direct_flg)) {
				String nextShelf = null;
				for (TurnoverCaseForm storagePlan : shippingPlanList) {
					if (shelf.compareTo(storagePlan.getShelf()) < 0) {
						nextShelf = storagePlan.getShelf();
						break;
					}
				}
				if (nextShelf == null) {
					nextShelf = shippingPlanList.get(0).getShelf();
				}
				shelf = nextShelf;
			}
		}

		// 当前货架待出库列表
		List<TurnoverCaseForm> shippingPlanListOnShelf = service.filterOnShelf(shippingPlanList, shelf);

		req.setAttribute("shelf", shelf);
		req.setAttribute("waitCount", shippingPlanList.size());
		req.setAttribute("shippingPlanListOnShelf", shippingPlanListOnShelf);
		req.setAttribute("shelfMap", service.getShelfMap(shelf, shippingPlanListOnShelf, conn));

		actionForward = mapping.findForward(FW_INIT);

		log.info("PdaTcShippingAction.doShipping end");
	}
}
