package com.osh.rvs.action.qa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.qa.ServiceRepairResolveForm;
import com.osh.rvs.service.qa.ServiceRepairResolveService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.DateUtil;

public class ServiceRepairResolveAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());

	ServiceRepairResolveService service = new ServiceRepairResolveService();

	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("ServiceRepairResolveAction.init start");

		actionForward = mapping.findForward(FW_INIT);

		// 责任区分Select
		request.setAttribute("sLiability_flg", CodeListUtils.getSelectOptions("liability_flg", null, ""));

		LoginData user = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		String privacy = (!user.getPrivacies().contains(RvsConsts.PRIVACY_QUALITY_ASSURANCE) && user.getPrivacies()
				.contains(RvsConsts.PRIVACY_QA_MANAGER)) ? "view" : "qa";

		// 经理操作
		if (user.getPrivacies().contains(RvsConsts.PRIVACY_PROCESSING)) {
			privacy = "manager";
		}

		// 技术课人员操作
		if ("00000000010".equals(user.getSection_id())) {
			privacy = "technician";
		}

		// 当前操作者的ID
		String operator_id = user.getOperator_id();
		//获取当前时间的前两个星期日期
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -14);
	    String twoWeeksAgo =DateUtil.toString(calendar.getTime(),"yyyy/MM/dd");
	    request.setAttribute("two_weeks_ago", twoWeeksAgo);
	    
		request.setAttribute("operator_id", operator_id);

		request.setAttribute("privacy", privacy);
		log.info("ServiceRepairResolveAction.init start");
	}

	/**
	 * 查询 保修期内返品分析对策 详细信息
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("ServiceRepairResolveAction.search start");
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 检索分析表的详细数据
		List<ServiceRepairResolveForm> returnForms = service.searchServiceRepairResolve(form, conn);

		
		listResponse.put("returnForms", returnForms);
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		log.info("ServiceRepairResolveAction.search end");
	}

	/**
	 * 型号下拉数据
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void getAutocomplete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("ServiceRepairResolveAction.getAutocomplete start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		// 型号
		List<String> list = service.getModelNameAutoCompletes(conn);
		listResponse.put("sModelName", list.toArray());// 型号集合

		listResponse.put("errors", infoes);
		returnJsonResponse(response, listResponse);

		log.info("ServiceRepairResolveAction.getAutocomplete end");
	}

	/**
	 * 更新保内返品对策对应内容
	 * 
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doupdate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("ServiceRepairResolveAction.doupdate start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		ServiceRepairResolveForm serviceRepairResolveForm = (ServiceRepairResolveForm) form;
		// 更新
		service.updateServiceRepairResolve(serviceRepairResolveForm, conn,request.getParameter("isSolution"),request.getParameter("isResolve"));

		// 检查发生错误时报告错误信息
		listResponse.put("errors", infoes);
		returnJsonResponse(response, listResponse);
		log.info("ServiceRepairResolveAction.doupdate end");
	}

	/**
	 * 分析书导出 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void report(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("ServiceRepairResolveAction.report start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		String fileName = "保修期内返修品分析表.xls";
 
		String filePath = service.createWorkReport(form, request, conn);
		listResponse.put("fileName", fileName);
		listResponse.put("filePath", filePath);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);

		log.info("ServiceRepairResolveAction.report end");
	}
}
