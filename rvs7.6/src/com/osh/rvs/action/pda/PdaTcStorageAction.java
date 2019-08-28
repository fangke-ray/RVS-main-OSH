package com.osh.rvs.action.pda;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.qf.TurnoverCaseEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.qf.TurnoverCaseForm;
import com.osh.rvs.service.AcceptFactService;
import com.osh.rvs.service.ProductionFeatureService;
import com.osh.rvs.service.qf.FactMaterialService;
import com.osh.rvs.service.qf.TurnoverCaseService;

import framework.huiqing.common.util.message.ApplicationMessage;

public class PdaTcStorageAction extends PdaBaseAction {

	private Logger log = Logger.getLogger(getClass());
	private static final String TC_MOVE_FROM = "TC_MOVE_FROM";

	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,SqlSession conn) throws Exception {
		log.info("PdaTcStorageAction.init start");

		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 切换作业
		AcceptFactService afService = new AcceptFactService();
		afService.switchWorking("106", user);

		TurnoverCaseForm tcForm = (TurnoverCaseForm) form;
		TurnoverCaseService service = new TurnoverCaseService();

		// 货架号
		String shelf = tcForm.getShelf();

		// 全待入库列表
		List<TurnoverCaseForm> storagePlanList = service.getStoragePlanList(conn);

		// 如果没有货架号, 取得最早需入库货架
		if (shelf == null) {
			if (storagePlanList.size() == 0) {
				// A货架
				shelf = "A";
			} else {
				shelf = storagePlanList.get(0).getShelf();
			}
			session.removeAttribute(TC_MOVE_FROM);
		} else if (storagePlanList.size() > 0) {
			String direct_flg = tcForm.getDirect_flg();
			// 翻页
			if (direct_flg != null && !"".equals(direct_flg)) {
				if ("-1".equals(direct_flg)) {
					String prevShelf = null;
					for (TurnoverCaseForm storagePlan : storagePlanList) {
						if (shelf.equals(storagePlan.getShelf())) {
							break;
						}
						prevShelf = storagePlan.getShelf();
					}
					if (prevShelf == null) {
						prevShelf = storagePlanList.get(storagePlanList.size() - 1).getShelf();
					}
					shelf = prevShelf;
				} else if ("1".equals(direct_flg)) {
					String nextShelf = null; boolean hit = false;
					for (TurnoverCaseForm storagePlan : storagePlanList) {
						if (shelf.equals(storagePlan.getShelf())) {
							hit = true;
						} else if (hit) {
							nextShelf = storagePlan.getShelf();
							break;
						}
					}
					if (nextShelf == null) {
						nextShelf = storagePlanList.get(0).getShelf();
					}
					shelf = nextShelf;
				}
			}
		}

		// 当前货架待入库列表
		List<TurnoverCaseForm> storagePlanListOnShelf = service.filterOnShelf(storagePlanList, shelf);

		req.setAttribute("waitCount", storagePlanList.size());
		req.setAttribute("shelf", shelf);
		req.setAttribute("storagePlanListOnShelf", storagePlanListOnShelf);
		req.setAttribute("shelfMap", service.getShelfMap(shelf, storagePlanListOnShelf, conn));

		actionForward = mapping.findForward(FW_INIT);

		log.info("PdaTcStorageAction.init end");
	}

	public void doStorage(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("PdaTcStorageAction.doStorage start");

		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		TurnoverCaseForm tcForm = (TurnoverCaseForm) form;
		TurnoverCaseService service = new TurnoverCaseService();

		// 提交入库对象
		String location = tcForm.getLocation();
		TurnoverCaseEntity entity = null;

		// 移动起点
		String moveFromLocation = (String) session.getAttribute(TC_MOVE_FROM);

		// 货架号
		String shelf = tcForm.getShelf();
		boolean storaged = false;

		if (moveFromLocation == null) { // 入库动作
			if (location != null)
				entity = service.getEntityByLocationForStorage(location, conn);

			if (entity != null) {
				// 如果入库对象货架不一致
				if (!shelf.equals(entity.getShelf())) {
					// 不做入库动作
					location = null;
					// 切换货架
					shelf = entity.getShelf();
				} else {
					// 判断是否完成消毒
					ProductionFeatureService pfService = new ProductionFeatureService();
					int count = pfService.checkFinishedDisinfection(entity.getMaterial_id(), conn);
					if (count == 0) {
						// 错误信息
						req.setAttribute("errmsg", ApplicationMessage.WARNING_MESSAGES.getMessage("info.turnoverCase.notFinishedDisinfection"));
					} else {
						// 入库动作
						service.checkStorage(conn, location);
						storaged = true;
					}
				}
			} else {
				// 错误信息
				req.setAttribute("errmsg", ApplicationMessage.WARNING_MESSAGES.getMessage("info.turnoverCase.notStoragePlan"));
			}
		} else { // 移库动作
			entity = service.getEntityByLocationForStorage(moveFromLocation, conn);

			TurnoverCaseEntity emptyEntity = null;
			if (location != null)
				emptyEntity = service.checkEmptyLocation(location, conn);

			if (entity != null && emptyEntity != null) {
				// 执行检索
				service.changelocation(conn, entity.getMaterial_id(), location + "+");

				session.removeAttribute(TC_MOVE_FROM);
			} else {
				// 错误信息
				req.setAttribute("errmsg", ApplicationMessage.WARNING_MESSAGES.getMessage("info.turnoverCase.notSpacing"));
			}
		}

		// 全待入库列表
		List<TurnoverCaseForm> storagePlanList = service.getStoragePlanList(conn);

		String direct_flg = tcForm.getDirect_flg();
		// 翻页
		if (storaged && storagePlanList.size() > 0 && direct_flg != null && !"".equals(direct_flg)) {
			if ("1".equals(direct_flg)) {
				String nextShelf = null;
				for (TurnoverCaseForm storagePlan : storagePlanList) {
					if (shelf.compareTo(storagePlan.getShelf()) < 0) {
						nextShelf = storagePlan.getShelf();
						break;
					}
				}
				if (nextShelf == null) {
					nextShelf = storagePlanList.get(0).getShelf();
				}
				shelf = nextShelf;
			}
		}

		// 当前货架待入库列表
		List<TurnoverCaseForm> storagePlanListOnShelf = service.filterOnShelf(storagePlanList, shelf);

		if (storaged) {
			// 更新到现品作业记录（维修品）
			FactMaterialService fmsService = new FactMaterialService();
			int updateCount = fmsService.insertFactMaterial(user.getOperator_id(), entity.getMaterial_id(), 1, conn);
			// 通知后台刷新作业标记
			if (updateCount > 0) {
				AcceptFactService afService = new AcceptFactService();
				afService.fingerOperatorRefresh(user.getOperator_id());
			}
		}

		req.setAttribute("shelf", shelf);
		req.setAttribute("waitCount", storagePlanList.size());
		req.setAttribute("storagePlanListOnShelf", storagePlanListOnShelf);
		req.setAttribute("shelfMap", service.getShelfMap(shelf, storagePlanListOnShelf, conn));

		actionForward = mapping.findForward(FW_INIT);

		log.info("PdaTcStorageAction.doStorage end");
	}

	public void startTransfer(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("PdaTcStorageAction.startTransfer start");

		actionForward = mapping.findForward(FW_INIT);

		TurnoverCaseForm tcForm = (TurnoverCaseForm) form;
		TurnoverCaseService service = new TurnoverCaseService();

		TurnoverCaseEntity entity = service.getEntityByLocationForStorage(tcForm.getLocation(), conn);

		if (entity != null) {	
			// 移动起点
			HttpSession session = req.getSession();
			session.setAttribute(TC_MOVE_FROM, tcForm.getLocation());
		} else {
			// 错误信息
			req.setAttribute("errmsg", ApplicationMessage.WARNING_MESSAGES.getMessage("info.turnoverCase.notStoragePlan"));
		}

		// 货架号
		String shelf = tcForm.getShelf();

		// 全待入库列表
		List<TurnoverCaseForm> storagePlanList = service.getStoragePlanList(conn);

		// 当前货架待入库列表
		List<TurnoverCaseForm> storagePlanListOnShelf = service.filterOnShelf(storagePlanList, shelf);

		req.setAttribute("shelf", shelf);
		req.setAttribute("waitCount", storagePlanList.size());
		req.setAttribute("storagePlanListOnShelf", storagePlanListOnShelf);
		req.setAttribute("shelfMap", service.getShelfMap(shelf, storagePlanListOnShelf, conn));

		log.info("PdaTcStorageAction.startTransfer end");
	}

	public void cancelTransfer(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("PdaTcStorageAction.cancelTransfer start");

		actionForward = mapping.findForward(FW_INIT);

		TurnoverCaseForm tcForm = (TurnoverCaseForm) form;
		TurnoverCaseService service = new TurnoverCaseService();

		// 取消移动
		HttpSession session = req.getSession();
		session.removeAttribute(TC_MOVE_FROM);

		// 货架号
		String shelf = tcForm.getShelf();

		// 全待入库列表
		List<TurnoverCaseForm> storagePlanList = service.getStoragePlanList(conn);

		// 当前货架待入库列表
		List<TurnoverCaseForm> storagePlanListOnShelf = service.filterOnShelf(storagePlanList, shelf);

		req.setAttribute("shelf", shelf);
		req.setAttribute("waitCount", storagePlanList.size());
		req.setAttribute("storagePlanListOnShelf", storagePlanListOnShelf);
		req.setAttribute("shelfMap", service.getShelfMap(shelf, storagePlanListOnShelf, conn));

		log.info("PdaTcStorageAction.cancelTransfer end");
	}
}
