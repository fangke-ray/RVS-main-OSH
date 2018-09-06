package com.osh.rvs.action.master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.master.ToolsTypeEntity;
import com.osh.rvs.form.master.ToolsTypeForm;
import com.osh.rvs.service.ToolsTypeService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.validator.Validators;

public class ToolsTypeAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	private ToolsTypeService service = new ToolsTypeService();

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
		log.info("ToolsTypeAction .init start");

		actionForward = mapping.findForward(FW_INIT);

		log.info("ToolsTypeAction .init end");
	}

	/**
	 * 治具品名一览
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response, SqlSession conn) {
		log.info("ToolsTypeAction .search start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		ToolsTypeForm toolsTypeForm = (ToolsTypeForm) form;
		ToolsTypeEntity toolsTypeEntity = new ToolsTypeEntity();

		BeanUtil.copyToBean(toolsTypeForm, toolsTypeEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<ToolsTypeForm> toolsTypeForms = service.searchToolsType(toolsTypeEntity, conn);

		listResponse.put("toolsTypeForms", toolsTypeForms);

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("ToolsTypeAction .search end");
	}

	/**
	 * 新建治具品名
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 */
	public void doinsert(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("ToolsTypeAction .doupdate start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 插入新建治具品名
		service.insertToolsType(form,conn,request.getSession(),errors);

		/* 检查错误时报告错误信息 */
		callbackResponse.put("errors", errors);
		/* 返回Json格式响应信息 */
		returnJsonResponse(response, callbackResponse);
		log.info("ToolsTypeAction .doupdate end");
	}
	
	/**
	 * 删除治具品名
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void dodelete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("ToolsTypeAction.dodelete start");
		// 盛放错误信息
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		// 检查时返回的验证信息
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();
		// 当检查错误信息无
		if (errors.size() == 0) {
			service.deleteToolsType(form,conn, request.getSession(),errors);
		}
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回JSON格式响应的信息
		returnJsonResponse(response, callbackResponse);
		log.info("ToolsTypeAction.dodelete end");
	}
	
	/**
	 * 双击修改治具品名
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doupdate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("ToolsTypeAction.doupdate start");
		// 盛放错误信息
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		// 检查表单验证信息
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();
		
		service.updateToolsType(form, conn, request.getSession(),errors);

		callbackResponse.put("errors", errors);
		returnJsonResponse(response, callbackResponse);
		log.info("ToolsTypeAction.doupdate end");
	}
}
