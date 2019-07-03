package com.osh.rvs.action.equipment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.service.DevicesManageService;
import com.osh.rvs.service.DevicesTypeService;
import com.osh.rvs.service.LineService;
import com.osh.rvs.service.ToolsManageService;
import com.osh.rvs.service.UploadService;
import com.osh.rvs.service.equipment.DeviceJigRepairService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.validator.Validators;

public class DeviceJigRepairRecordAction extends BaseAction {

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
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("DeviceJigRepairRecordAction.init start");

		DevicesTypeService dtService = new DevicesTypeService();

		// 默认报修时间
		Calendar submitTime = Calendar.getInstance();
		submitTime.add(Calendar.MONTH, -1);
		req.setAttribute("submitTimeStart", DateUtil.toString(submitTime.getTime(), DateUtil.DATE_PATTERN));

		// 品名
		String nReferChooser = dtService.getDevicesTypeReferChooser(conn);
		req.setAttribute("nReferChooser", nReferChooser);

		LineService lService = new LineService();
		String lOptions = lService.getOptions(conn, "", "(全部)");
		req.setAttribute("lOptions", lOptions);

		// 管理等级
		req.setAttribute("goManageLevel", CodeListUtils.getGridOptions("devices_manage_level"));

		// 状态
		req.setAttribute("goStatus",CodeListUtils.getGridOptions("devices_status"));

		DevicesManageService dmService = new DevicesManageService();
		req.setAttribute("dReferChooser", dmService.getOptions(conn));

		ToolsManageService toolsManageService = new ToolsManageService();
		req.setAttribute("jReferChooser", toolsManageService.getOptions(conn));

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();
		// 设备管理(设备管理画面)
		String privacy = "";
		if (privacies.contains(RvsConsts.PRIVACY_TECHNOLOGY)) {
			privacy = "technology";
		} 
		req.setAttribute("privacy", privacy);

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("DeviceJigRepairRecordAction.init end");
	}

	/**
	 * 详细也面初始化
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void detail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("DeviceJigRepairRecordAction.detail start");

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();
		// 设备管理(设备管理画面)
		String privacy = "";
		if (privacies.contains(RvsConsts.PRIVACY_TECHNOLOGY)) {
			privacy = "technology";
		} 
		req.setAttribute("privacy", privacy);

		// 迁移到页面
		actionForward = mapping.findForward("detail");

		log.info("DeviceJigRepairRecordAction.detail end");
	}

	/**
	 * 检索一览
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("DeviceJigRepairRecordAction.search start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		DeviceJigRepairService service = new DeviceJigRepairService();
		// 设备工具修理记录一览表
		service.search(form, conn, listResponse, errors);

		listResponse.put("errors", errors);

		returnJsonResponse(res, listResponse);
		log.info("DeviceJigRepairRecordAction.search end");
	}

	/**
	 * 报修查询
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void checkForSubmit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("DeviceJigRepairRecordAction.checkForSubmit start");

		Map<String, Object> cbResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();

		if (privacies.contains(RvsConsts.PRIVACY_TECHNOLOGY)
				|| privacies.contains(RvsConsts.PRIVACY_PROCESSING)) {
			cbResponse.put("limitted", false);
		} else {
			cbResponse.put("limitted", true);
		}

		cbResponse.put("line_id", user.getLine_id());

		cbResponse.put("errors", errors);

		returnJsonResponse(res, cbResponse);
		log.info("DeviceJigRepairRecordAction.checkForSubmit end");
	}

	/**
	 * 报修
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doSubmit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("DeviceJigRepairRecordAction.doSubmit start");

		Map<String, Object> cbResponse = new HashMap<String, Object>();
		// 新建记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		DeviceJigRepairService service = new DeviceJigRepairService();
		if (errors.size() == 0) {
			service.checkSubmitPrivacy(user, form, errors, conn);
		}

		if (errors.size() == 0) {
			service.sumbit(form, user, conn);
		}

		cbResponse.put("errors", errors);

		returnJsonResponse(res, cbResponse);
		log.info("DeviceJigRepairRecordAction.doSubmit end");
	}

	/**
	 * 验收
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doConfirm(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("DeviceJigRepairRecordAction.doConfirm start");

		Map<String, Object> cbResponse = new HashMap<String, Object>();
		// 新建记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		DeviceJigRepairService service = new DeviceJigRepairService();
		if (errors.size() == 0) {
			service.checkConfirmPrivacy(user, form, errors, conn);
		}

		if (errors.size() == 0) {
			service.confirm(form, user, conn);
		}

		cbResponse.put("errors", errors);

		returnJsonResponse(res, cbResponse);
		log.info("DeviceJigRepairRecordAction.doConfirm end");
	}

	/**
	 * 详细页面数据
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void showDetail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("DeviceJigRepairRecordAction.showDetail start");

		String key = req.getParameter("device_jig_repair_record_key");

		Map<String, Object> cbResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		DeviceJigRepairService service = new DeviceJigRepairService();
		cbResponse.put("retForm", service.detail(key, conn));

		cbResponse.put("errors", errors);

		returnJsonResponse(res, cbResponse);
		log.info("DeviceJigRepairRecordAction.showDetail end");
	}

	/**
	 * 维修编辑
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doRepair(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("DeviceJigRepairRecordAction.doRepair start");

		Map<String, Object> cbResponse = new HashMap<String, Object>();
		// 新建记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		DeviceJigRepairService service = new DeviceJigRepairService();

		if (errors.size() == 0) {
			service.repairEdit(form, user, conn);
		}

		cbResponse.put("errors", errors);

		returnJsonResponse(res, cbResponse);
		log.info("DeviceJigRepairRecordAction.doRepair end");
	}

	/**
	 * 上传照片
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void sourceImage(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("DeviceJigRepairRecordAction.sourceImage start");

		// Ajax回馈对象
		Map<String, Object> jsonResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		UploadService fservice = new UploadService();
		String tempFilePath = fservice.getFile2Local(form, errors);

		DeviceJigRepairService service = new DeviceJigRepairService();

		String photo_file_name = tempFilePath.substring(tempFilePath.lastIndexOf("\\") + 1);
		service.copyPhoto(req.getParameter("device_jig_repair_record_key"), photo_file_name);

		// 检查发生错误时报告错误信息
		jsonResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(res, jsonResponse);

		log.info("DeviceJigRepairRecordAction.sourceImage end");
	}

	/**
	 * 删除照片
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void delImage(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("DeviceJigRepairRecordAction.delImage start");

		// Ajax回馈对象
		Map<String, Object> jsonResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		DeviceJigRepairService service = new DeviceJigRepairService();

		service.delPhoto(req.getParameter("device_jig_repair_record_key"));

		// 检查发生错误时报告错误信息
		jsonResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(res, jsonResponse);

		log.info("DeviceJigRepairRecordAction.delImage end");
	}
	
}
