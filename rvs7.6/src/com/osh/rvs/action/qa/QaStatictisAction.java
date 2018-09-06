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

import com.osh.rvs.service.qa.QualityAssuranceService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.message.ApplicationMessage;

public class QaStatictisAction extends BaseAction {
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
	@Privacies(permit = { 1, 0 })
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("QaStatictisAction.init start");

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		Calendar today = Calendar.getInstance();
		// 去月底
		today.set(Calendar.DATE, 1);
		today.add(Calendar.MONTH, 1);
		today.add(Calendar.DATE, -1);

		QualityAssuranceService service = new QualityAssuranceService();
		Map<String, Object> ret = service.getYearMonthByDate(today);
		req.setAttribute("yOptions", ret.get("yOptions"));
		req.setAttribute("mOptions", ret.get("mOptions"));
		req.setAttribute("sMonth", ret.get("sMonth"));
		req.setAttribute("yearMonthValue", ret.get("yearMonthValue"));

		log.info("QaStatictisAction.init end");
	}

	@Privacies(permit = { 1, 0 })
	public void changeYear(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		
		log.info("QaStatictisAction.changeYear start");

		// Ajax响应对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> msgInfoes = new ArrayList<MsgInfo>();

		QualityAssuranceService service = new QualityAssuranceService();
		String select_year = req.getParameter("select_year");

		if (select_year == null) {
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setComponentid("select_month");
			msgInfo.setErrcode("validator.required");
			msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "切换月份"));
			msgInfoes.add(msgInfo);
		} else {
			Calendar cal = Calendar.getInstance();
			// 设定月份选择
			cal.set(Calendar.YEAR, Integer.parseInt(select_year));
			
			Map<String, Object> ret = service.getYearMonthByDate(cal);
			listResponse.put("mOptions", ret.get("mOptions"));
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", msgInfoes);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("QaStatictisAction.changeYear end");
	}

	@Privacies(permit = { 1, 0 })
	public void doSetMonthData(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		
		log.info("QaStatictisAction.getMonthData start");

		// Ajax响应对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> msgInfoes = new ArrayList<MsgInfo>();

		QualityAssuranceService service = new QualityAssuranceService();
		String select_year = req.getParameter("select_year");
		String select_month = req.getParameter("select_month");

		Calendar cal = Calendar.getInstance();
		if (select_year == null || select_month == null) {
		} else {
			// 设定月份选择
			cal.set(Calendar.YEAR, Integer.parseInt(select_year));
			cal.set(Calendar.MONTH, Integer.parseInt(select_month) - 1);
		}

		// 去月底
		cal.set(Calendar.DATE, 1);
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1);

		Map<String, Object> retData = service.getData(cal, conn);
		listResponse.put("retData", retData);

		Map<String, Object> ret = service.getYearMonthByDate(cal);
		listResponse.put("mOptions", ret.get("mOptions"));
		listResponse.put("sMonth", ret.get("sMonth"));
		listResponse.put("yearMonthValue", ret.get("yearMonthValue"));

		// 检查发生错误时报告错误信息
		listResponse.put("errors", msgInfoes);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("QaStatictisAction.getMonthData end");
	}

	@Privacies(permit = { 1, 0 })
	public void doUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		
		log.info("QaStatictisAction.doUpdate start");

		// Ajax响应对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> msgInfoes = new ArrayList<MsgInfo>();

		QualityAssuranceService service = new QualityAssuranceService();
		service.setMonthData(req.getParameterMap(), conn);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", msgInfoes);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("QaStatictisAction.doUpdate end");
	}
}
