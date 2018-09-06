package com.osh.rvs.action.infect;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.form.infect.DeviceRegularlyCheckResultForm;
import com.osh.rvs.service.CheckResultService;
import com.osh.rvs.service.DevicesTypeService;
import com.osh.rvs.service.LineService;
import com.osh.rvs.service.OperatorService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.infect.DeviceRegularlyCheckResultService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.action.infect
 * @ClassName: DeviceRegularlyCheckResultAction
 * @Description: 设备工具定期点检
 * @author lxb
 * @date 2014-8-19 上午8:58:21
 * 
 */
public class DeviceRegularlyCheckResultAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());

	/**
	 * 页面初始化
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param request
	 *            请求
	 * @param response
	 *            响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSession conn)
			throws Exception {
		log.info("DeviceRegularlyCheckResultAction.init start");

		// 分发科室
		SectionService sectionService = new SectionService();
		String sectionOptions = sectionService.getAllOptions(conn);
		request.setAttribute("sectionOptions", sectionOptions);

		// 责任工程
		LineService lineService = new LineService();
		String lineOptions = lineService.getOptions(conn);
		request.setAttribute("lineOptions", lineOptions);

		// 责任工位
		DeviceRegularlyCheckResultService deviceRegularlyCheckResultService = new DeviceRegularlyCheckResultService();
		String pReferChooser = deviceRegularlyCheckResultService.getOptions(conn);
		request.setAttribute("pReferChooser", pReferChooser);

		// 责任人员
		OperatorService operatorService = new OperatorService();
		String rReferChooser = operatorService.getAllOperatorName(conn);
		request.setAttribute("rReferChooser", rReferChooser);
		
		DevicesTypeService devicesTypeService = new DevicesTypeService();
		//品名
		String nReferChooser=devicesTypeService.getDevicesTypeReferChooser(conn);
		request.setAttribute("nReferChooser", nReferChooser);
		
		
		request.setAttribute("sChecked_status", CodeListUtils.getGridOptions("checked_status"));

		actionForward = mapping.findForward(FW_INIT);

		log.info("DeviceRegularlyCheckResultAction.init end");
	}

	/**
	 * 检索
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param request
	 *            请求
	 * @param response
	 *            响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSession conn)
			throws Exception {
		log.info("DeviceRegularlyCheckResultAction.search start");

		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		DeviceRegularlyCheckResultService service = new DeviceRegularlyCheckResultService();
		List<DeviceRegularlyCheckResultForm> month_finished = service.searchByMonth(form, conn);
		List<DeviceRegularlyCheckResultForm> year_finished = service.searchByYear(form, conn);
		
		
		
		Calendar cal = Calendar.getInstance();
		CheckResultService checkResultService=new CheckResultService();
		Date dates[][]=checkResultService.getWeekEndsOfMonth(cal);
		int weeks=dates.length;// 一个月中周的数量

		int cur_week=service.getIndexOfCurrentWeek(cal);//当前日期在第几周
		
		int curMonth=cal.get(Calendar.MONTH)+1;//当前月
		
		List<DeviceRegularlyCheckResultForm> week_finished = service.searchByWeek(form, conn,cal,cur_week);

		listResponse.put("errors", errors);
		listResponse.put("month_finished", month_finished);
		listResponse.put("year_finished", year_finished);
		listResponse.put("week_finished", week_finished);
		listResponse.put("weeks", weeks);
		listResponse.put("cur_week", cur_week);
		listResponse.put("curMonth", curMonth);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);

		log.info("DeviceRegularlyCheckResultAction.search end");
	}

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void detail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSession conn)
			throws Exception {
		log.info("DeviceRegularlyCheckResultAction.detail start");

		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		DeviceRegularlyCheckResultService service = new DeviceRegularlyCheckResultService();
		
		List<DeviceRegularlyCheckResultForm> finished =service.searchDetail(form, conn);
		DeviceRegularlyCheckResultForm returnForm=null;
		if(finished!=null && finished.size()>0){
			 returnForm=finished.get(0);
		}
				

		listResponse.put("errors", errors);
		listResponse.put("finished", finished);
		listResponse.put("returnForm", returnForm);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);

		log.info("DeviceRegularlyCheckResultAction.detail end");
	}

}
