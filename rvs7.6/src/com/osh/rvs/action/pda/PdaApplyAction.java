package com.osh.rvs.action.pda;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.form.pda.PdaApplyForm;
import com.osh.rvs.service.partial.PdaApplyService;

/**
 * 
 * @Title PdaApplyAction.java
 * @Project rvs
 * @Package com.osh.rvs.action.pda
 * @ClassName: PdaApplyAction
 * @Description: TODO
 * @author lxb
 * @date 2015-5-29 上午10:48:08
 */
public class PdaApplyAction extends PdaBaseAction {
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
		log.info("PdaApplyAction.init start");

		PdaApplyForm applyForm = (PdaApplyForm)form;
		PdaApplyService service = new PdaApplyService();

		// 消耗品申请单一览
		service.searchApplyList(applyForm, conn);

		req.getSession().removeAttribute("pdaApplyDetailForm");

		actionForward = mapping.findForward(FW_INIT);

		log.info("PdaApplyAction.init end");
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
	public void getDetail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("PdaApplyAction.getDetail start");

		PdaApplyForm applyForm = (PdaApplyForm)form;
		req.setAttribute("consumable_application_key", applyForm.getConsumable_application_key());

		actionForward = mapping.findForward(FW_SUCCESS);

		log.info("PdaApplyAction.getDetail end");
	}
}
