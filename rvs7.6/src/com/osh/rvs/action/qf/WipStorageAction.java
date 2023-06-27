package com.osh.rvs.action.qf;

import java.io.IOException;
import java.io.PrintWriter;
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

import com.osh.rvs.bean.qf.WipStorageEntity;
import com.osh.rvs.service.qf.WipService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

public class WipStorageAction extends BaseAction {

	/**
	 * 建立库位
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={1})
	public void doCreate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("WipStorageAction.doCreate start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 新建记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> msgInfos = v.validate();

		if (msgInfos.size() == 0) {
			WipService service = new WipService();

			service.create(form, msgInfos, conn);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);
		// 返回Json格式回馈信息
		returnJsonResponse(response, callbackResponse);
		log.info("WipStorageAction.doCreate end");
	}

	/**
	 * 取得库位一览
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void getLocationMap(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("WipStorageAction.getLocationMap start");

		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		WipService wipService = new WipService();
		wipService.getLocationMap(form, callbackResponse, conn, errors);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Html格式回馈信息
		PrintWriter out;
		try {
			res.setCharacterEncoding("UTF-8");
			out = res.getWriter();
			out.print(callbackResponse.get("storageHtml"));
			out.flush();
		} catch (IOException e) {
			// 迁移到页面
			actionForward = mapping.findForward(FW_INIT);
		}
		log.info("WipStorageAction.getLocationMap end");
	}

	/**
	 * 取得库位一览
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void getLocationDetail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("WipStorageAction.getLocationDetail start");

		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		WipService wipService = new WipService();
		WipStorageEntity wsEntity = wipService.getLocationDetail(req.getParameter("wip_storage_code"), conn);
		callbackResponse.put("storageDetail", wsEntity);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);

		log.info("WipStorageAction.getLocationDetail end");
	}

	/**
	 * 更新库位
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={1})
	public void doChangeSetting(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("WipStorageAction.doChangeSetting start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 新建记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> msgInfos = v.validate();

		if (msgInfos.size() == 0) {
			WipService service = new WipService();

			service.changeSetting(form, msgInfos, conn);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);
		// 返回Json格式回馈信息
		returnJsonResponse(response, callbackResponse);
		log.info("WipStorageAction.doChangeSetting end");
	}

	/**
	 * 删除库位
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={1})
	public void doRemove(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("WipStorageAction.doRemove start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 新建记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> msgInfos = v.validate();

		if (msgInfos.size() == 0) {
			WipService service = new WipService();

			service.remove(form, msgInfos, conn);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);
		// 返回Json格式回馈信息
		returnJsonResponse(response, callbackResponse);
		log.info("WipStorageAction.doRemove end");
	}
}
