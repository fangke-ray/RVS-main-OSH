package com.osh.rvs.action.infect;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.infect.DailyCheckResultEntity;
import com.osh.rvs.form.infect.DailyCheckResultForm;
import com.osh.rvs.service.DevicesManageService;
import com.osh.rvs.service.DevicesTypeService;
import com.osh.rvs.service.LineService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.infect.DailyCheckResultService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class DailyCheckResultAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	private SectionService sectionService = new SectionService();
	private LineService lineService = new LineService();
	private DevicesTypeService devicesTypeService = new DevicesTypeService();	
	private DevicesManageService devicesManageService = new DevicesManageService();

	private DailyCheckResultService service  =new DailyCheckResultService();
	/**
	 * 日常点检结果初始化
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

		log.info("DailyCheckResultAction.init start");

		actionForward = mapping.findForward(FW_INIT);

		// 分发课室
		String sectionOptions = sectionService.getAllOptions(conn);
		req.setAttribute("sectionOptions", sectionOptions);

		// 责任工程
		String lineOptions = lineService.getOptions(conn);
		req.setAttribute("lineOptions", lineOptions);

		// 工位
		String pReferChooser = devicesManageService.getOptionPtions(conn);
		req.setAttribute("pReferChooser", pReferChooser);
		
		//品名
		String nReferChooser=devicesTypeService.getDevicesTypeReferChooser(conn);
		req.setAttribute("nReferChooser", nReferChooser);

		//管理员
		String oReferChooser = devicesManageService.getDevicesManageroptions(conn);
		req.setAttribute("oReferChooser", oReferChooser);
		
		//当前日期
		Calendar calendar = Calendar.getInstance();
		int currDay = calendar.get(Calendar.DAY_OF_MONTH);
		req.setAttribute("currDay", currDay);
		
		//状态select
		req.setAttribute("SCheckedStatus", CodeListUtils.getGridOptions("checked_status"));
		
		log.info("DailyCheckResultAction.init end");
	}
	
	/**
	 * 日常点检结果一览 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn) throws Exception{
		log.info("DailyCheckResultAction.search start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		DailyCheckResultForm dailyCheckResultForm = (DailyCheckResultForm)form;
        
		//当前月的天数
		Calendar calendar = Calendar.getInstance();
		//当前月天数
		int maxDay=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

		//日常点检结果
		List<DailyCheckResultForm> resultForms = service.searchDailyCheckResult(dailyCheckResultForm,calendar,conn);
		
        listResponse.put("resultForms", resultForms);
        
        listResponse.put("maxDay", maxDay);
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("DailyCheckResultAction.search end");
	}
	
	/**
	 * 双击--详细=课室+工程+工位
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void detailSectionLinePosition(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn) throws Exception{
		log.info("DailyCheckResultAction.detailSectionLinePosition start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		DailyCheckResultForm dailyCheckResultForm = (DailyCheckResultForm)form;
		DailyCheckResultEntity dailyCheckResultEntity = new DailyCheckResultEntity();
        //Form to Bean
		BeanUtil.copyToBean(dailyCheckResultForm, dailyCheckResultEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//日常点检结果--详细=课室+工程+工位
		DailyCheckResultForm detailForms = service.searchSectionLinePosition(dailyCheckResultEntity, conn);
			
		listResponse.put("detailForms", detailForms);
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("DailyCheckResultAction.detailSectionLinePosition end");
	}
	/**
	 * 双击--详细=设备工具点检记录
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void detailCheckResult(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn) throws Exception{
		log.info("DailyCheckResultAction.detailCheckResult start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		DailyCheckResultForm dailyCheckResultForm = (DailyCheckResultForm)form;
		DailyCheckResultEntity dailyCheckResultEntity = new DailyCheckResultEntity();
        //Form to Bean
		BeanUtil.copyToBean(dailyCheckResultForm, dailyCheckResultEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//设备工具点检记录
		List<DailyCheckResultForm> resultForms = service.detailCheckResult(dailyCheckResultEntity, conn);
			
		listResponse.put("resultForms", resultForms);
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("DailyCheckResultAction.detailCheckResult end");
	}
}
