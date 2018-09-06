package com.osh.rvs.action.inline;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import framework.huiqing.action.BaseAction;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.action.inline
 * 
 */
public class DailyKpiAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());
	
	/**
	 * 初始化
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void init(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("DailyKpiAction.init start");
		
		actionForward=mapping.findForward(FW_INIT);

		log.info("DailyKpiAction.init end");
	}
}
