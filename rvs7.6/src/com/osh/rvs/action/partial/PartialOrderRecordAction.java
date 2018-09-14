package com.osh.rvs.action.partial;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.form.data.MonthFilesDownloadForm;
import com.osh.rvs.form.partial.PartialOrderRecordForm;
import com.osh.rvs.service.CategoryService;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.partial.PartialOrderRecordService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.DateUtil;

public class PartialOrderRecordAction extends BaseAction {

	private static final String PERIOD_DAYS = "PERIOD_DAYS";
	private static final String PERIOD_EDGE = "PERIOD_EDGE";
	private PartialOrderRecordService partialOrderService = new PartialOrderRecordService();
	private ModelService modelService = new ModelService();

	/**
	 * 页面打开
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("PartialOrderRecordAction.init start");
		
		String mReferChooser = modelService.getOptions(conn);
		req.setAttribute("mReferChooser", mReferChooser);

		// 零件订购日		默认开始日的值填成当日 - 1周	
		Calendar aWeekAgo = Calendar.getInstance();
		aWeekAgo.add(Calendar.DATE, -7);
		req.setAttribute("search_order_date_start", DateUtil.toString(aWeekAgo.getTime(), DateUtil.DATE_PATTERN));

		req.setAttribute("mReferChooser", mReferChooser);

		req.setAttribute("lOptions", CodeListUtils.getSelectOptions("material_level_inline", null, "全部", false));
		req.setAttribute("eOptions", CodeListUtils.getSelectOptions("echelon_code", null, "全部", false));

		CategoryService categoryService = new CategoryService();
		String cOptions = categoryService.getOptions(conn);
		req.setAttribute("cOptions", cOptions);

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("PartialOrderRecordAction.init end");
	}

	/**
	 * 检索零件现状
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void searchPartial(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {

		log.info("PartialOrderRecordAction.searchPartial start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		String neo = req.getParameter("neo");

		Date[] period = {new Date(), new Date()};
		Integer periodDays = null;
		HttpSession session = req.getSession();
		// 条件变更查询
		if ("1".equals(neo)) {
			periodDays = partialOrderService.getPeriodByConditions(form, period, conn);
			if (periodDays != 0) {
				session.setAttribute(PERIOD_EDGE, period);
				session.setAttribute(PERIOD_DAYS, periodDays);
			} else {
				session.removeAttribute(PERIOD_EDGE);
				session.removeAttribute(PERIOD_DAYS);
			}
		} else {
			period = (Date[]) session.getAttribute(PERIOD_EDGE);
			periodDays = (Integer) session.getAttribute(PERIOD_DAYS);
		}

		List<PartialOrderRecordForm> partials = partialOrderService.searchPartials(form, periodDays, conn);
		listResponse.put("partials", partials);

		if (periodDays > 0)
			listResponse.put("periodMessage", partialOrderService.getPeriodMessage(periodDays, period));

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PartialOrderRecordAction.searchPartial end");
	}

	/**
	 * 检索扥级型号现状
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void searchLevelModel(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		
		log.info("PartialOrderRecordAction.searchLevelModel start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		String neo = req.getParameter("neo");
		Date[] period = {new Date(), new Date()};
		Integer periodDays = null;
		HttpSession session = req.getSession();
		// 条件变更查询
		if ("1".equals(neo)) {
			periodDays = partialOrderService.getPeriodByConditions(form, period, conn);
			if (periodDays != 0) {
				session.setAttribute(PERIOD_EDGE, period);
				session.setAttribute(PERIOD_DAYS, periodDays);
			} else {
				session.removeAttribute(PERIOD_EDGE);
				session.removeAttribute(PERIOD_DAYS);
			}
		} else {
			period = (Date[]) session.getAttribute(PERIOD_EDGE);
			periodDays = (Integer) session.getAttribute(PERIOD_DAYS);
		}

		List<PartialOrderRecordForm> levelModels = partialOrderService.searchLevelModels(form, periodDays, conn);
		listResponse.put("levelModels", levelModels);

		if (periodDays != null && periodDays > 0)
			listResponse.put("periodMessage", partialOrderService.getPeriodMessage(periodDays, period));
		
		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("PartialOrderRecordAction.searchLevelModel end");
	}

	/**
	 * 检索梯队现状
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void searchEchelon(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		
		log.info("PartialOrderRecordAction.searchEchelon start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// TODO BUg
		String neo = req.getParameter("neo");
		Date[] period = {new Date(), new Date()};
		Integer periodDays = null;
		HttpSession session = req.getSession();
		// 条件变更查询
		if ("1".equals(neo)) {
			periodDays = partialOrderService.getPeriodByConditions(form, period, conn);
			if (periodDays != 0) {
				session.setAttribute(PERIOD_EDGE, period);
				session.setAttribute(PERIOD_DAYS, periodDays);
			} else {
				session.removeAttribute(PERIOD_EDGE);
				session.removeAttribute(PERIOD_DAYS);
			}
		} else {
			period = (Date[]) session.getAttribute(PERIOD_EDGE);
			periodDays = (Integer) session.getAttribute(PERIOD_DAYS);
		}

		List<PartialOrderRecordForm> echelons = partialOrderService.searchEchelons(form, periodDays, conn);
		listResponse.put("echelons", echelons);
		
		if (periodDays > 0)
			listResponse.put("periodMessage", partialOrderService.getPeriodMessage(periodDays, period));

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("PartialOrderRecordAction.searchEchelon end");
	}

	/**
	 * 建立分析报表
	 */
	public void makeReport(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		
		log.info("PartialOrderRecordAction.makeReport start");

		HttpSession session = req.getSession();
		Map<String, Object> fileResponse = new HashMap<String, Object>();

		Date[] period = (Date[]) session.getAttribute(PERIOD_EDGE);
		Integer periodDays = (Integer) session.getAttribute(PERIOD_DAYS);

		String filePath = partialOrderService.createReport(form, period, periodDays, conn);
		String fileName = "BO分析表.xls";
		fileResponse.put("fileName", fileName);
		fileResponse.put("filePath", filePath);

		returnJsonResponse(res, fileResponse);

		log.info("PartialOrderRecordAction.makeReport end");
	}
	
	
	/**
	 * 月档案详细一览
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 页面请求
	 * @param response 页面响应
	 * @param conn 数据库会话
	 * @throws Exception Exception
	 */
	public void searchMonthFiles(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn) throws Exception{
		log.info("PartialOrderRecordAction.searchMonthFiles start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		List<MonthFilesDownloadForm> filesList = partialOrderService.getMonthFiles();
		
		listResponse.put("filesList", filesList);
		
		//返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("PartialOrderRecordAction.searchMonthFiles end");
	}
	
}
