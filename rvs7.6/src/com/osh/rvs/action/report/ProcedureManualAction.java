package com.osh.rvs.action.report;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import framework.huiqing.action.BaseAction;

public class ProcedureManualAction extends BaseAction {
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

		log.info("ProcedureManualAction.init start");

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("ProcedureManualAction.init end");
	}
}
