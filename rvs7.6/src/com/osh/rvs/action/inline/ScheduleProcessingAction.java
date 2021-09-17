/**
 * 系统名：OSH-RVS<br>
 * 模块名：现品计划<br>
 * 机能名：进度管理<br>
 * @author 冯晓春
 * @version 0.01
 */
package com.osh.rvs.action.inline;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.ReportMetaData;
import com.osh.rvs.common.ReportUtils;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.inline.ScheduleForm;
import com.osh.rvs.service.CapacityService;
import com.osh.rvs.service.CategoryService;
import com.osh.rvs.service.DownloadService;
import com.osh.rvs.service.OperatorService;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.inline.ScheduleProcessService;
import com.osh.rvs.service.inline.ScheduleService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.validator.Validators;

public class ScheduleProcessingAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());
	private ScheduleProcessService scheduleProcessService = new ScheduleProcessService();
	// 机种信息管理处理生成
	private CategoryService categoryService = new CategoryService();
	// 工位信息管理处理生成
	private PositionService positionService = new PositionService();
	
	//产能
	private CapacityService capacityService = new CapacityService();
	/**
	 * 设备类别管理画面初始表示处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ScheduleProcessingAction.init start");

		// 机种信息取得
		String cOptions = categoryService.getOptions(conn);
		// 机种信息设定
		req.setAttribute("cOptions", cOptions);
		
		// 工位信息取得
		String pReferChooser = positionService.getOptions(conn);
		// 工位信息设定
		req.setAttribute("pReferChooser", pReferChooser);

		SectionService sectionService = new SectionService();
		String sOptions = sectionService.getOptions(conn, "(全部)");
		req.setAttribute("sOptions", sOptions);

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		// 权限区分
		List<Integer> privacies = user.getPrivacies();
		if (privacies.contains(RvsConsts.PRIVACY_SCHEDULE)) {
			req.setAttribute("role", "planner");
		} else if (privacies.contains(RvsConsts.PRIVACY_PROCESSING)) {
			req.setAttribute("role", "manager");
		} else {
			req.setAttribute("role", "none");
		}

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("ScheduleProcessingAction.init end");
	}
	
	public void searchBoth(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ScheduleProcessingAction.searchBoth start");
		
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();
		for (MsgInfo error : errors) {
			log.warn("error=" + error.getErrmsg());
		}

		if (errors.size() == 0) {

			// 取得用户信息
			HttpSession session = req.getSession();
			LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
			List<Integer> privacies = user.getPrivacies();
			Integer resolveLevel = 99;
			if (privacies.contains(RvsConsts.PRIVACY_SCHEDULE)) {
				resolveLevel = 3;
			} else if (privacies.contains(RvsConsts.PRIVACY_PROCESSING)) {
				resolveLevel = 2;
			}

			// 执行检索
			List<ScheduleForm> lResultForm = scheduleProcessService.getMaterialList(form, conn, errors, resolveLevel);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("material_list", lResultForm);
		}

		if (errors.size() == 0) {
			// 执行检索
			List<ScheduleForm> lResultForm = scheduleProcessService.getScheduleList(form, conn, errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("schedule_list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ScheduleProcessingAction.searchBoth end");
	}

	/**
	 *  维修对象查询一览处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ScheduleProcessingAction.search start");
		
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();
		for (MsgInfo error : errors) {
			log.warn("error=" + error.getErrmsg());
		}

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();
		Integer resolveLevel = 99;
		if (privacies.contains(RvsConsts.PRIVACY_SCHEDULE)) {
			resolveLevel = 3;
		} else if (privacies.contains(RvsConsts.PRIVACY_PROCESSING)) {
			resolveLevel = 2;
		}

		if (errors.size() == 0) {
			// 执行检索
			List<ScheduleForm> lResultForm = scheduleProcessService.getMaterialList(form, conn, errors, resolveLevel);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("material_list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ScheduleProcessingAction.search end");
	}

	/**
	 *  计划信息一览处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void searchSchedule(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ScheduleProcessingAction.searchSchedule start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		if (errors.size() == 0) {
			// 执行检索
			List<ScheduleForm> lResultForm = scheduleProcessService.getScheduleList(form, conn, errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("schedule_list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ScheduleProcessingAction.searchSchedule end");
	}

	@Privacies(permit={1, 0})
	public void searchOutSchedule(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ScheduleProcessingAction.searchSchedule start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		if (errors.size() == 0) {
			// 执行检索
			List<ScheduleForm> lResultForm = scheduleProcessService.getOutScheduleList(form, conn, errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("schedule_list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ScheduleProcessingAction.searchSchedule end");
	}
	
	/**
	 * 计划信息更新实行处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void doupdateSchedule(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		
		log.info("ScheduleProcessingAction.updateSchedule start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行更新
			scheduleProcessService.updateSchedule(form, req.getSession(), conn, errors);
		}

		if (errors.size() == 0) {
			// 执行检索
			List<ScheduleForm> lResultForm = scheduleProcessService.getScheduleList(form, conn, errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("schedule_list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ScheduleProcessingAction.updateSchedule end");
	}

	/**
	 * 计划信息删除实行处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void dodeleteSchedule(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		
		log.info("ScheduleProcessingAction.deleteSchedule start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行更新
			scheduleProcessService.deleteSchedule(form, req.getSession(), conn, errors);
		}

		if (errors.size() == 0) {
			// 执行检索
			List<ScheduleForm> lResultForm = scheduleProcessService.getScheduleList(form, conn, errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("schedule_list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ScheduleProcessingAction.deleteSchedule end");
	}
	
	public void doupdateToPuse(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) {
		log.info("ScheduleProcessingAction.updateToPuse start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String id = req.getParameter("id");
		scheduleProcessService.updateToPuse(conn, id);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ScheduleProcessingAction.updateToPuse end");
	}
	
	public void report(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("ScheduleProcessingAction.report start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		if (errors.size() == 0) {
			// 执行检索
			List<ScheduleForm> lResultForm = scheduleProcessService.getMaterialList(form, conn, errors, 99);
			
			String filePath = ReportUtils.createReport(lResultForm, ReportMetaData.materialTitles, ReportMetaData.materialColNames);
			listResponse.put("filePath", filePath);
		}
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("ScheduleProcessingAction.report end");
	}

	public void reportSchedule(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("ScheduleProcessingAction.reportSchedule start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		if (errors.size() == 0) {
			// 执行检索
			List<ScheduleForm> lResultForm = scheduleProcessService.getScheduleList(form, conn, errors);
			
			String filePath = ReportUtils.createReport(lResultForm, ReportMetaData.scheduleTitles, ReportMetaData.scheduleColNames);
			listResponse.put("filePath", filePath);
		}
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("ScheduleProcessingAction.reportSchedule end");
	}

	public void export(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		String filePath = req.getParameter("filePath");
		Date today = new Date();
		String folder = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(today, "yyyyMM");

		String fileName = new String("维修对象一览.xls".getBytes("gbk"),"iso-8859-1");
		
		DownloadService dservice = new DownloadService();
		dservice.writeFile(res, DownloadService.CONTENT_TYPE_EXCEL, fileName, folder + "\\" + filePath);
	}

	public void exportSchedule(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		String filePath = req.getParameter("filePath");
		String fileName = new String("当日计划一览.xls".getBytes("gbk"),"iso-8859-1");
		
		DownloadService dservice = new DownloadService();
		dservice.writeFile(res, DownloadService.CONTENT_TYPE_EXCEL, fileName, filePath);
	}

	/**
	 * 取得不良信息处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void getwarning(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("ScheduleProcessingAction.getwarning start");

		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检查发生错误时报告错误信息
		listResponse.put("errors", new ArrayList<MsgInfo>());

		String material_id = req.getParameter("material_id");

		ScheduleService service = new ScheduleService();
		listResponse.put("warning", service.getWarning(material_id, conn));

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ScheduleProcessingAction.getwarning end");
	}
	
	/**
	 * 产能设定
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 页面请求
	 * @param response 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void capacity_setting(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("ScheduleProcessingAction.capacity_setting start");
		
		//迁移到画面
		actionForward = mapping.findForward("capacity_setting");
		
		log.info("ScheduleProcessingAction.capacity_setting end");
	}
	

	/**
	 * KPI日报画面片段表示处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void daily_report(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ScheduleProcessingAction.daily_report start");

		// 迁移到页面
		actionForward = mapping.findForward("daily_report");

		log.info("ScheduleProcessingAction.daily_report end");
	}

	/**
	 * KPI日报数据取得
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void getDayKpiOfWeek(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ScheduleProcessingAction.getDayKpiOfWeek start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", new ArrayList<MsgInfo>());

		ScheduleService service = new ScheduleService();
		service.getDayKpiOfWeek(callbackResponse, conn);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("ScheduleProcessingAction.getDayKpiOfWeek end");
	}

	public void doUpdateDailyKpi(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) {
		log.info("ScheduleProcessingAction.doUpdateDailyKpi start");
		
		scheduleProcessService.updateDailyKpi(req, conn);
		
		log.info("ScheduleProcessingAction.doUpdateDailyKpi end");
	}
	
	/**
	 * 最大产能获得
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 */
	public void getUpperLimit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn){
		log.info("ScheduleProcessingAction.getUpperLimit start");
		
		Map<String,Object> callbackResponse = new HashMap<String,Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
//		//产能所需课室 
//		List<ScheduleForm> resultSectionNames = capacityService.searchSectionName(conn);
//		callbackResponse.put("resultSectionNames", resultSectionNames);

		List<ScheduleForm> resultBeans = capacityService.searchCapacitySetting(conn);
		callbackResponse.put("resultBeans",resultBeans);
		
		//检查发生错误时报告错误信息
		callbackResponse.put("errors",errors);
		//返回JSON格式响应信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("ScheduleProcessingAction.getUpperLimit end");
	}
	
	/**
	 * 修改产能
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 */
	public void doUpdateUpperLimit(ActionMapping mapping,ActionForm form,HttpServletRequest req,HttpServletResponse res,SqlSessionManager conn){
		log.info("ScheduleProcessingAction.doUpdateUpperLimit start");
		
		Map<String,Object> callbackResponse = new HashMap<String,Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		capacityService.updateUpperLimit(req,conn);

		//检查发生错误时报告错误信息
		callbackResponse.put("errors",errors);
		//返回JSON格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("ScheduleProcessingAction.doUpdateUpperLimit end");
	}

	/**
	 * 操作者课室权限取得
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 */
	public void getSectionDispatch(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn){
		log.info("ScheduleProcessingAction.getSectionDispatch start");
		
		Map<String,Object> callbackResponse = new HashMap<String,Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		// 课室列表（去掉线外课室）
		SectionService sService = new SectionService();

		callbackResponse.put("resultSectionNames", sService.getSectionsForOperators(conn));

		// 全部作业人员
		OperatorService oService = new OperatorService();
		callbackResponse.put("workflgOperators", oService.getWorkflgOperators(conn));

		// 全部已经分配课室（不含无效）
		callbackResponse.put("dispatchedSections", oService.getAllOperatorSections(conn));

		//检查发生错误时报告错误信息
		callbackResponse.put("errors",errors);
		//返回JSON格式响应信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("ScheduleProcessingAction.getSectionDispatch end");
	}

	public void doSetSectionDispatch(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn){
		log.info("ScheduleProcessingAction.setSectionDispatch start");
		
		Map<String,Object> callbackResponse = new HashMap<String,Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		OperatorService oService = new OperatorService();
		oService.insertOperatorSection(req, conn);

		//检查发生错误时报告错误信息
		callbackResponse.put("errors",errors);
		//返回JSON格式响应信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("ScheduleProcessingAction.setSectionDispatch end");
	}
}
