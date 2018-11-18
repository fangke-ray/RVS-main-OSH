package com.osh.rvs.action.pda;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.form.partial.ConsumableApplicationForm;
import com.osh.rvs.form.pda.PdaApplyDetailForm;
import com.osh.rvs.form.pda.PdaApplyElementForm;
import com.osh.rvs.service.partial.ConsumableApplyService;
import com.osh.rvs.service.partial.PdaApplyService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;

/**
 * 
 * @Title PdaApplyDetailAction.java
 * @Project rvs
 * @Package com.osh.rvs.action.pda
 * @ClassName: PdaApplyDetailAction
 * @Description: TODO
 * @author lxb
 * @date 2015-5-29 上午10:48:08
 */
public class PdaApplyDetailAction extends PdaBaseAction {
	private Logger log = Logger.getLogger(getClass());

	/**
	 * 初始化
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("PdaApplyDetailAction.init start");

		PdaApplyDetailForm detailForm = (PdaApplyDetailForm) form;
		String consumable_application_key = (String)req.getAttribute("consumable_application_key");
		detailForm.setConsumable_application_key(consumable_application_key);
		PdaApplyService service = new PdaApplyService();

		// 消耗品申请单明细
		service.searchApplyDetailList(detailForm, conn);

		actionForward = mapping.findForward(FW_INIT);

		log.info("PdaApplyDetailAction.init end");
	}

	/**
	 * 取得明细
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void getDetail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("PdaApplyDetailAction.getDetail start");

		PdaApplyDetailForm detailForm = (PdaApplyDetailForm) form;

		// 检查输入的code是否在列表中存在
		String code = detailForm.getCode().trim();
		boolean isExist = false;
		List<PdaApplyElementForm> detailList = detailForm.getDetail_list();
		for (PdaApplyElementForm elementForm : detailList) {
			if (code.equalsIgnoreCase(elementForm.getCode())) {
				if (elementForm.getType().equals("6")) {
					if (elementForm.getSupply_quantity().equals(elementForm.getApply_quantity())) {
						continue;
					} else {
						req.setAttribute("petitioner_id", elementForm.getPetitioner_id());
					}
				}
				isExist = true;
				break;
			}
		}
		detailForm.setCode(code);

		if (!isExist) {
			req.setAttribute("errors", "输入的零件编码在列表中不存在。");

			actionForward = mapping.findForward(FW_INIT);
		} else {
			actionForward = mapping.findForward(FW_SUCCESS);
		}

		log.info("PdaApplyDetailAction.getDetail end");
	}

	/**
	 * 初始化
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("PdaApplyDetailAction.doUpdate start");

		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		PdaApplyDetailForm detailForm = (PdaApplyDetailForm) form;

		if (detailForm != null) {

			PdaApplyService pdaService = new PdaApplyService();

			// 判断发放数量 是否>有效库存
			for (PdaApplyElementForm elementForm : detailForm.getDetail_list()) {
				Integer available_inventory = Integer.valueOf(elementForm.getAvailable_inventory());//有效库存
				Integer supply_quantity = Integer.valueOf(elementForm.getSupply_quantity());

				if(supply_quantity > available_inventory){//发放数量 > 有效库存
					MsgInfo msg = new MsgInfo();
					msg.setErrmsg("消耗品代码为: " + elementForm.getCode() +"的发放数量大于有效库存");
					elementForm.setSupply_quantity("0");
					elementForm.setDisp_supply_quantity("0");
					boolean pass = pdaService.showDisplay(elementForm);
					if (!pass) {
						errors.add(msg);
						break;
					}
				}
			}

			if (errors.size() == 0) {
				conn.startManagedSession(ExecutorType.BATCH, TransactionIsolationLevel.REPEATABLE_READ);

				ConsumableApplyService service = new ConsumableApplyService();

				List<ConsumableApplicationForm> formList = new AutofillArrayList<>(ConsumableApplicationForm.class);
				for (PdaApplyElementForm elementForm : detailForm.getDetail_list()) {
					if (pdaService.isGroupLine(elementForm.getDisp_flg())) {
						continue;
					}
					ConsumableApplicationForm caForm = new ConsumableApplicationForm();
					copyToForm(elementForm, caForm);
					formList.add(caForm);
				}
				service.update(detailForm, formList, conn, errors, req);
			}
		}

		if (errors.size() > 0) {
			req.setAttribute("errors", getStrMsgInfo(errors));
			actionForward = mapping.findForward(FW_INIT);
		} else {
			actionForward = mapping.findForward("toList");
		}

		log.info("PdaApplyDetailAction.doUpdate end");
	}

	private void copyToForm(PdaApplyElementForm elementForm, ConsumableApplicationForm caForm) {
		caForm.setConsumable_application_key(elementForm.getConsumable_application_key());
		caForm.setPartial_id(elementForm.getPartial_id());
		caForm.setPetitioner_id(elementForm.getPetitioner_id());
		caForm.setType(elementForm.getType());
		caForm.setCode(elementForm.getCode());
		caForm.setAvailable_inventory(elementForm.getAvailable_inventory());
		caForm.setWaitting_quantity(elementForm.getWaitting_quantity());
		caForm.setOpenflg(elementForm.getOpen_flg());
		caForm.setSupply_quantity(elementForm.getSupply_quantity());
		caForm.setDb_supply_quantity(elementForm.getDb_supply_quantity());
	}
}
