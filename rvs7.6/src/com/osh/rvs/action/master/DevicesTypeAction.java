package com.osh.rvs.action.master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.master.DevicesTypeEntity;
import com.osh.rvs.form.master.DevicesTypeForm;
import com.osh.rvs.service.DevicesTypeService;
import com.osh.rvs.service.UploadService;

import org.apache.log4j.Logger;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.validator.Validators;

public class DevicesTypeAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	private DevicesTypeService service = new DevicesTypeService();

	/**
	 * 画面初始化
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("DevicesTypeAction .init start");
		
		
		//特定设备工具种类
		req.setAttribute("specializedDeviceType", CodeListUtils.getSelectOptions("specialized_device_type",null,""));
		req.setAttribute("gSpecializedDeviceType",CodeListUtils.getGridOptions("specialized_device_type"));

		actionForward = mapping.findForward(FW_INIT);

		log.info("DevicesTypeAction .init end");
	}

	/**
	 * 设备工具品名一览
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response, SqlSession conn) {
		log.info("DevicesTypeAction .search start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		DevicesTypeForm devicesTypeForm = (DevicesTypeForm) form;
		DevicesTypeEntity devicesTypeEntity = new DevicesTypeEntity();

		BeanUtil.copyToBean(devicesTypeForm, devicesTypeEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<DevicesTypeForm> devicesTypeForms = service.searchDevicesType(devicesTypeEntity, conn);

		listResponse.put("devicesTypeForms", devicesTypeForms);

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("DevicesTypeAction .search end");
	}

	/**
	 * 新建设备工具品名
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 */
	public void doinsert(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("DevicesTypeAction .doupdate start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		/*验证*/
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors=v.validate();

		// 插入新建设备工具品名
		service.insertDevicesType(form,conn,request.getSession(),errors);

		/* 检查错误时报告错误信息 */
		callbackResponse.put("errors", errors);
		/* 返回Json格式响应信息 */
		returnJsonResponse(response, callbackResponse);
		log.info("DevicesTypeAction .doupdate end");
	}
	
	/**
	 * 删除设备工具品名
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void dodelete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("DevicesTypeAction.dodelete start");
		// 盛放错误信息
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		// 检查时返回的验证信息
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();
		// 当检查错误信息无
		if (errors.size() == 0) {
			service.deleteDevicesType(form,conn, request.getSession(),errors);
		}
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回JSON格式响应的信息
		returnJsonResponse(response, callbackResponse);
		log.info("DevicesTypeAction.dodelete end");
	}
	
	/**
	 * 双击修改设备工具品名
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doupdate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("DevicesTypeAction.doupdate start");
		// 盛放错误信息
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		// 检查表单验证信息
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();
		
		service.updateDevicesType(form, conn, request.getSession(),errors);

		callbackResponse.put("errors", errors);
		returnJsonResponse(response, callbackResponse);
		log.info("DevicesTypeAction.doupdate end");
	}
	
	/**
	 * 上传安全操作手顺图片
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void sourceImage(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("DevicesTypeAction.sourceImage start");

		// Ajax回馈对象
		Map<String, Object> jsonResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		UploadService fservice = new UploadService();
		String tempFilePath = fservice.getFile2Local(form, errors);

		String photo_file_name = tempFilePath.substring(tempFilePath.lastIndexOf("\\") + 1);
		service.copyPhoto(req.getParameter("devices_type_id"), photo_file_name);

		// 检查发生错误时报告错误信息
		jsonResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(res, jsonResponse);

		log.info("DevicesTypeAction.sourceImage end");
	}
}
