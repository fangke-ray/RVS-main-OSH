/**
 * 系统名：OSH-RVS<br>
 * 模块名：现品计划<br>
 * 机能名：计划管理<br>
 * @author 邵智祺
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
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.ReportMetaData;
import com.osh.rvs.common.ReportUtils;
import com.osh.rvs.common.ReverseResolution;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.inline.ScheduleForm;
import com.osh.rvs.service.CategoryService;
import com.osh.rvs.service.DownloadService;
import com.osh.rvs.service.MaterialProcessService;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.ProductionFeatureService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.UserDefineCodesService;
import com.osh.rvs.service.inline.ForSolutionAreaService;
import com.osh.rvs.service.inline.ScheduleService;
import com.osh.rvs.service.qf.WipService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

public class ScheduleAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	// 机种信息管理处理生成
	private CategoryService categoryService = new CategoryService();
	// 工位信息管理处理生成
	private PositionService positionService = new PositionService();
	// 计划信息管理处理生成
	private ScheduleService scheduleService = new ScheduleService();

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

		log.info("ScheduleAction.init start");

		// 机种信息取得
		String cOptions = categoryService.getOptions(conn);
		// 机种信息设定
		req.setAttribute("cOptions", cOptions);

		// OCM取得
		req.setAttribute("oOptions", CodeListUtils.getSelectOptions("material_ocm", null, ""));

		// level取得
		req.setAttribute("lOptions",CodeListUtils.getSelectOptions("material_level_inline", null, "", false));

		// 工位信息取得
		String pReferChooser = positionService.getOptions(conn);
		// 工位信息设定
		req.setAttribute("pReferChooser", pReferChooser);

		UserDefineCodesService udcService = new UserDefineCodesService();
		req.setAttribute("defaultSupportCount", udcService.searchUserDefineCodesValueByCode("SCHEDULE_SUPPORT_COUNT", conn)); // SCHEDULE_SUPPORT_COUNT

		SectionService sectionService = new SectionService();
		String sOptions = sectionService.getOptions(conn, "(全部)");
		req.setAttribute("sOptions", sOptions);
		req.setAttribute("scheduleSections", scheduleService.getScheduleSections(sectionService.getSectionInline(conn)));

		req.setAttribute("sikakeTable", scheduleService.getSikakeTable(conn));

		// 分线取得
		req.setAttribute("pxOptions", CodeListUtils.getSelectOptions("material_px", null, ""));
		req.setAttribute("pxGridOptions", CodeListUtils.getGridOptions("material_px"));

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		// 权限区分
		List<Integer> privacies = user.getPrivacies();
		if (!privacies.contains(104)) {
			req.setAttribute("role", "other");
		} else {
			req.setAttribute("role", "planner");
		}

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("ScheduleAction.init end");
	}

	/**
	 * 查询在线一览以及排入今天计划一览
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void searchBoth(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ScheduleAction.searchBoth start");
		
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行检索
			List<ScheduleForm> lResultForm = scheduleService.getMaterialList(form, conn, errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("material_list", lResultForm);
		}

		if (errors.size() == 0) {
			// 执行检索
			List<ScheduleForm> lResultForm = scheduleService.getScheduleList(form, conn, errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("schedule_list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ScheduleAction.searchBoth end");
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

		log.info("ScheduleAction.search start");
		
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();
		for (MsgInfo error : errors) {
			log.warn("error=" + error.getErrmsg());
		}

		if (errors.size() == 0) {
			// 执行检索
			List<ScheduleForm> lResultForm = scheduleService.getMaterialList(form, conn, errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("material_list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ScheduleAction.search end");
	}

	/**
	 * 计划信息一览处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void searchSchedule(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ScheduleAction.searchSchedule start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		if (errors.size() == 0) {
			// 执行检索
			List<ScheduleForm> lResultForm = scheduleService.getScheduleList(form, conn, errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("schedule_list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ScheduleAction.searchSchedule end");
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
		
		log.info("ScheduleAction.updateSchedule start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行更新
			scheduleService.updateSchedule(form, req.getSession(), conn, errors);
			if ("9999/12/31".equals(req.getParameter("scheduled_assign_date"))) {
				String appendMessage = req.getParameter("unknown_comment");
				MaterialService mService = new MaterialService();
				MaterialEntity mform = new MaterialEntity();
				mform.setMaterial_id(req.getParameter("ids"));
				mform.setScheduled_manager_comment(appendMessage);
				mform.setFix_type(1);
				mService.updateComment(mform , conn);
			}
		}

		if (errors.size() == 0) {
			String no_list = req.getParameter("no_list");
			if (!"true".equals(no_list)) {
				// 执行检索
				List<ScheduleForm> lResultForm = scheduleService.getMaterialList(form, conn, errors);
				
				// 查询结果放入Ajax响应对象
				listResponse.put("material_list", lResultForm);
			}
		}

		// 刷新计划数据
		if (errors.size() == 0) {
			// 执行检索
			List<ScheduleForm> lResultForm = scheduleService.getScheduleList(form, conn, errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("schedule_list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ScheduleAction.updateSchedule end");
	}

	/**
	 * 计划区间信息更新实行处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void doUpdateSchedulePeriod(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
	
		log.info("ScheduleAction.doUpdateSchedulePeriod start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行更新
			scheduleService.updateSchedulePeriod(req, conn, errors);
		}

		if (errors.size() == 0) {
			// 执行检索
			List<ScheduleForm> lResultForm = scheduleService.getScheduleList(form, conn, errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("schedule_list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ScheduleAction.doUpdateSchedulePeriod end");
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
		
		log.info("ScheduleAction.deleteSchedule start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行更新
			scheduleService.deleteSchedule(form, req.getSession(), conn, errors);
		}

		if (errors.size() == 0) {
			// 执行检索
			List<ScheduleForm> lResultForm = scheduleService.getMaterialList(form, conn, errors);

			// 查询结果放入Ajax响应对象
			listResponse.put("material_list", lResultForm);
		}

		if (errors.size() == 0) {
			// 执行检索
			List<ScheduleForm> lResultForm = scheduleService.getScheduleList(form, conn, errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("schedule_list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ScheduleAction.deleteSchedule end");
	}

	public void doupdateToPause(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("ScheduleAction.updateToPause start");

		String material_id = req.getParameter("material_id");
		String move_reason = req.getParameter("move_reason");
		String processing_position = req.getParameter("processing_position");

		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		ProductionFeatureService featureService = new ProductionFeatureService();
		int result = featureService.checkOperateResult(material_id, conn);
		if (result > 0) {
			// 如果有工位在对其进行作业则警告，不能进行未修理返还。
			MsgInfo info = new MsgInfo();
			info.setErrcode("info.modify.stop.working");
			info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.modify.stop.working"));
			errors.add(info);
		} else {
			String position_id = ReverseResolution.getPositionByProcessCode(processing_position, conn);
			scheduleService.updateToPuse(material_id, move_reason, position_id, conn);
		}

		Map<String, Object> listResponse = new HashMap<String, Object>();

		if (errors.size() == 0) {
			// 执行检索
			List<ScheduleForm> lResultForm = scheduleService.getMaterialList(form, conn, errors);

			// 查询结果放入Ajax响应对象
			listResponse.put("material_list", lResultForm);
		}


//		List<String> triggerList = new ArrayList<String>();
//		// 计算预估完成日
//		triggerList.add("http://localhost:8080/rvspush/trigger/delete_finish_time/"
//				+ material_id + "/any");
//
//		conn.commit();
//		RvsUtils.sendTrigger(triggerList);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ScheduleAction.updateToPause end");
	}
	
	public void doStop(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("ScheduleAction.doStop start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		String id = req.getParameter("id");

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		ProductionFeatureService featureService = new ProductionFeatureService();
		// 删除目前的等待作业
		featureService.removeWorking(id, null, conn);
		// 删除未完成的工程进度
		MaterialProcessService pocessService = new MaterialProcessService();
		pocessService.removeByBreak(id, conn);
		// 删除未完成的烘干作业 TODO

		WipService wService = new WipService();
		wService.stop(conn, id);

		ForSolutionAreaService fsaService = new ForSolutionAreaService();
		fsaService.solveAsStop(id, user, conn);

		List<String> triggerList = new ArrayList<String>();
		// 删除预估完成日
		triggerList.add("http://localhost:8080/rvspush/trigger/delete_finish_time/"
				+ id + "/any");

		conn.commit();
		RvsUtils.sendTrigger(triggerList);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ScheduleAction.doStop end");
	}

	public void report(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("ScheduleAction.report start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		if (errors.size() == 0) {
			// 执行检索
			List<ScheduleForm> lResultForm = scheduleService.getMaterialList(form, conn, errors);
			
			String filePath = ReportUtils.createReport(lResultForm, ReportMetaData.materialTitles, ReportMetaData.materialColNames);
			listResponse.put("filePath", filePath);
		}
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("ScheduleAction.report end");
	}
	
	public void reportSchedule(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("ScheduleAction.reportSchedule start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		// 检索条件表单合法性检查
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		if (errors.size() == 0) {
			// 执行检索
			List<ScheduleForm> lResultForm = scheduleService.getReportScheduleList(form, conn, errors);
			
			String filePath = ReportUtils.createReport(lResultForm, ReportMetaData.scheduleTitles, ReportMetaData.scheduleColNames);
			listResponse.put("filePath", filePath);
		}
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("ScheduleAction.reportSchedule end");
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
		Date today = new Date();
		String folder = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(today, "yyyyMM");

		String fileName = new String(("排入计划一览.xls").getBytes("gbk"),"iso-8859-1");

		DownloadService dservice = new DownloadService();
		dservice.writeFile(res, DownloadService.CONTENT_TYPE_EXCEL, fileName, folder + "\\" + filePath);
	}

	
}