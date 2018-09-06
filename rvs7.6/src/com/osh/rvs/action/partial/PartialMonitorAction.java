package com.osh.rvs.action.partial;

import java.util.ArrayList;
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

import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.partial.PartialOrderRecordForm;
import com.osh.rvs.mapper.master.HolidayMapper;
import com.osh.rvs.service.CategoryService;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.partial.PartialOrderRecordService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.DateUtil;

public class PartialMonitorAction extends BaseAction {

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
		log.info("PartialMonitorAction.init start");
		
		String mReferChooser = modelService.getOptions(conn);
		req.setAttribute("mReferChooser", mReferChooser);

		// 零件订购日		默认开始日的值填成当日 - 1周	
		Date now = new Date();
		HolidayMapper hMapper = conn.getMapper(HolidayMapper.class); 

		Map<String, Object> cond = new HashMap<String, Object>();
		cond.put("date", now);
		cond.put("interval", -RvsConsts.PLANE_INV);
		Date periodStart = hMapper.addWorkdays(cond);
		cond.put("interval", -1);
		Date periodEnd = hMapper.addWorkdays(cond);

		req.setAttribute("search_order_date_start", DateUtil.toString(periodStart, DateUtil.DATE_PATTERN));
		req.setAttribute("search_order_date_end", DateUtil.toString(periodEnd, DateUtil.DATE_PATTERN));

		req.setAttribute("mReferChooser", mReferChooser);

		req.setAttribute("lOptions", CodeListUtils.getSelectOptions("material_level_inline", null, "全部", false));
		req.setAttribute("eOptions", CodeListUtils.getSelectOptions("echelon_code", null, "全部", false));

		CategoryService categoryService = new CategoryService();
		String cOptions = categoryService.getOptions(conn);
		req.setAttribute("cOptions", cOptions);

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("PartialMonitorAction.init end");
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

		log.info("PartialMonitorAction.searchPartial start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		String neo = req.getParameter("neo");
		PartialOrderRecordForm partialOrderRecordForm=(PartialOrderRecordForm)form;
		
		String strl_high=partialOrderRecordForm.getL_high();//消耗率高于警报线
		String strl_low=partialOrderRecordForm.getL_low();//消耗率低于警报线
		
		req.getSession().setAttribute("strl_high",strl_high);
		req.getSession().setAttribute("strl_low", strl_low);
		
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

		List<PartialOrderRecordForm> partials = partialOrderService.searchPartialsOnForeboard(form, periodDays, conn);
		listResponse.put("partials", partials);
		
		req.getSession().setAttribute("importRresult", partials);//将查询结果保存在session中

		if (periodDays > 0)
			listResponse.put("periodMessage", partialOrderService.getPeriodMessage(periodDays, period));

//		List<PartialOrderRecordForm> outPartials = partialOrderService.searchPartialsOutOfForeboard(form, periodDays, conn);
//		listResponse.put("outPartials", outPartials);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PartialMonitorAction.searchPartial end");
	}

	/**
	 * 取得零件消耗历史数据
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void getChartData(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {


		log.info("PartialMonitorAction.getChartData start");
		// Ajax回馈对象	
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		String partial_id = req.getParameter("partial_id");

		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY , 0);
		now.set(Calendar.MINUTE , 0);
		now.set(Calendar.SECOND , 0);
		now.set(Calendar.MILLISECOND , 0);

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(now.getTimeInMillis());
		cal.add(Calendar.MONTH, -6);
		while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
			cal.add(Calendar.DATE, 1);
		}

		// 取得图表x轴及坐标
		partialOrderService.getChartPoint4Supply(partial_id, cal, now, callbackResponse, conn);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("PartialMonitorAction.getChartData end");
	}
	
	/**
	 * 当前结果导出
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void report(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("PartialMonitorAction.report start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		PartialOrderRecordService service=new PartialOrderRecordService();

		String fileName ="零件基准设定监控.xls";
		String filePath = service.createPartialMonitorReport(request);
		listResponse.put("fileName", fileName);
		listResponse.put("filePath", filePath);
			
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("PartialMonitorAction.report end");
	}
	

	/**
	 * 当前结果导出
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void reportCurPage(ActionMapping mapping,ActionForm form,HttpServletRequest req,HttpServletResponse res,SqlSession conn)throws Exception{
		log.info("PartialMonitorAction.reportCurPage start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		PartialOrderRecordService service=new PartialOrderRecordService();
		String fileName ="零件基准设定监控.xls";
		String filePath = service.dowload(req);
		
		listResponse.put("fileName", fileName);
		listResponse.put("filePath", filePath);
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("PartialMonitorAction.reportCurPage start");
	}
}
