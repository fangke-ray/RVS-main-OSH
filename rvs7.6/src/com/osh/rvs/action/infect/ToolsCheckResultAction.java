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

import com.osh.rvs.bean.infect.ToolsCheckResultEntity;
import com.osh.rvs.form.infect.ToolsCheckResultForm;
import com.osh.rvs.service.DevicesManageService;
import com.osh.rvs.service.LineService;
import com.osh.rvs.service.OperatorService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.ToolsCheckResultService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class ToolsCheckResultAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());
	private SectionService sectionService = new SectionService();
	private LineService lineService = new LineService();
	private DevicesManageService devicesManageService = new DevicesManageService();

	private OperatorService operatorService = new OperatorService();
	private ToolsCheckResultService service = new ToolsCheckResultService();
	/**
	 * 治具点检结果初始化
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

		log.info("ToolsCheckResultAction.init start");

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
		
		//点检人员
		String oReferChooser = operatorService.getAllToolsOperatorName(conn);
		req.setAttribute("oReferChooser", oReferChooser);
		
		//当前月
		Calendar calendar = Calendar.getInstance();
		int currMonth = calendar.get(Calendar.MONTH)+1;
		req.setAttribute("currMonth", currMonth);
		
		//状态jqgrid显示
		req.setAttribute("SCheckedStatus", CodeListUtils.getGridOptions("checked_status"));
		
		log.info("ToolsCheckResultAction.init end");
	}
	
	/**
	 * 治具点检结果一览
	 * @param mapping
	 * @param form
	 * @param response
	 * @param request
	 * @param conn
	 */
	public void search(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn) throws Exception{
		log.info("ToolsCheckResultAction.search start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		ToolsCheckResultForm toolsCheckResultForm = (ToolsCheckResultForm)form;
		ToolsCheckResultEntity toolsCheckResultEntity = new ToolsCheckResultEntity();
        //Form to Bean
		BeanUtil.copyToBean(toolsCheckResultForm, toolsCheckResultEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//日常点检结果
		List<ToolsCheckResultForm> resultForms = service.searchToolsCheckResult(toolsCheckResultForm,conn);
		
        listResponse.put("resultForms", resultForms);
        
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		
		log.info("ToolsCheckResultAction.search end");
	}
	/**
	 * 治具点检结果--详细=课室+工程+工位
	 * @param mapping
	 * @param form
	 * @param response
	 * @param request
	 * @param conn
	 */
	public void detailSectionLinePosition(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn) throws Exception{
		log.info("ToolsCheckResultAction.detailSectionLinePosition start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		ToolsCheckResultForm toolsCheckResultForm = (ToolsCheckResultForm)form;
		ToolsCheckResultEntity toolsCheckResultEntity = new ToolsCheckResultEntity();
        //Form to Bean
		BeanUtil.copyToBean(toolsCheckResultForm, toolsCheckResultEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//治具点检结果--详细=课室+工程+工位
		ToolsCheckResultForm detailForms = service.searchSectionLinePosition(toolsCheckResultEntity, conn);
			
		listResponse.put("detailForms", detailForms);
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		
		log.info("ToolsCheckResultAction.detailSectionLinePosition end");
	}
	/**
	 * 治具点检结果--双击详细
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 */
	public void detailCheckResult(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn) throws Exception{
		log.info("ToolsCheckResultAction.detailCheckResult start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		ToolsCheckResultForm toolsCheckResultForm = (ToolsCheckResultForm)form;
		ToolsCheckResultEntity ToolsCheckResultEntity = new ToolsCheckResultEntity();
        //Form to Bean
		BeanUtil.copyToBean(toolsCheckResultForm, ToolsCheckResultEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//治具检记录
		List<ToolsCheckResultForm> resultForms = service.detailCheckResult(ToolsCheckResultEntity, conn);
			
		listResponse.put("resultForms", resultForms);
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		
		log.info("ToolsCheckResultAction.detailCheckResult end");
	}
}
