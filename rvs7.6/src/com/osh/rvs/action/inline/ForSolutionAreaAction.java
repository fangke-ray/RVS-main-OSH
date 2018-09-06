
package com.osh.rvs.action.inline;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

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
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.inline.ForSolutionAreaForm;
import com.osh.rvs.service.CategoryService;
import com.osh.rvs.service.LineService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.inline.ForSolutionAreaService;
import com.osh.rvs.service.inline.ScheduleProcessService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.validator.Validators;

public class ForSolutionAreaAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());
	private ScheduleProcessService scheduleProcessService = new ScheduleProcessService();
	// 机种信息管理处理生成
	private CategoryService categoryService = new CategoryService();

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

		log.info("ForSolutionAreaAction.init start");

		// 机种信息取得
		String cOptions = categoryService.getOptions(conn);
		// 机种信息设定
		req.setAttribute("cOptions", cOptions);

		// 工位信息管理处理生成
		LineService lineService = new LineService();
		String lOptions = lineService.getInlineOptions(conn);
		req.setAttribute("lOptions", lOptions);

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

		String reasonOptions = CodeListUtils.getSelectOptions("offline_reason", null, "", false);
		req.setAttribute("reasonOptions", reasonOptions);
		req.setAttribute("h_level_eo", CodeListUtils.getGridOptions("material_level"));
		req.setAttribute("h_bo_eo", CodeListUtils.getGridOptions("bo_flg"));
		req.setAttribute("h_or_eo", CodeListUtils.getGridOptions("offline_reason"));

		req.setAttribute("today", DateUtil.toString(new Date(), DateUtil.DATE_PATTERN));

		String switchFrom = req.getParameter("switch_from");
		String switchName = "RA";
		if (isEmpty(switchFrom)) { 
			switchFrom = "scheduleProcessing.do"; 
		} else {
			if (!switchFrom.contains("Processing")) {
				switchName = "SA"; // 计划
			}
		}
		req.setAttribute("switch_from", switchFrom);
		req.setAttribute("switch_name", switchName);

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("ForSolutionAreaAction.init end");
	}
	
	/**
	 * 维修对象查询一览处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ForSolutionAreaAction.search start");
		
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

			ForSolutionAreaService forSolutionAreaService = new ForSolutionAreaService();
			Integer resolveLevel = 99;
			if (privacies.contains(RvsConsts.PRIVACY_SCHEDULE)) {
				resolveLevel = 3;
			} else if (privacies.contains(RvsConsts.PRIVACY_PROCESSING)) {
				resolveLevel = 2;
			}
			// 执行检索
			List<ForSolutionAreaForm> lResultForm = forSolutionAreaService.getAreaList(form, req, conn, errors, resolveLevel, false);

			// 查询结果放入Ajax响应对象
			listResponse.put("material_list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ForSolutionAreaAction.search end");
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
	public void doSolve(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		
		log.info("ForSolutionAreaAction.doSolve start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<String> triggerList = new ArrayList<String>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			ForSolutionAreaService forSolutionAreaService = new ForSolutionAreaService();

			// 取得用户信息
			HttpSession session = req.getSession();
			LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

			forSolutionAreaService.solve(form, triggerList, user, conn);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		if (triggerList.size() > 0 && errors.size() == 0) {
			conn.commit();
			RvsUtils.sendTrigger(triggerList);
		}

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ForSolutionAreaAction.doSolve end");
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
		
		log.info("ForSolutionAreaAction.deleteSchedule start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ForSolutionAreaAction.deleteSchedule end");
	}
	
	public void doupdateToPuse(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) {
		log.info("ForSolutionAreaAction.updateToPuse start");
		
		String id = req.getParameter("id");
		scheduleProcessService.updateToPuse(conn, id);
		
		log.info("ForSolutionAreaAction.updateToPuse end");
	}

	/**
	 * 维修对象查询一览/下载用
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void searchDownload(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ForSolutionAreaAction.searchDownload start");
		
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {

			ForSolutionAreaService forSolutionAreaService = new ForSolutionAreaService();

			// 执行检索
			List<ForSolutionAreaForm> lResultForm = forSolutionAreaService.getAreaList(form, req, conn, errors, 0, true);

			String fileName ="待处理一览.xlsx";

			String filePath = forSolutionAreaService.createForSolutionAreaRecord(lResultForm);
			
			listResponse.put("fileName", fileName);
			listResponse.put("filePath", filePath);

			// 查询结果放入Ajax响应对象
			listResponse.put("material_list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ForSolutionAreaAction.searchDownload end");
	}

}
