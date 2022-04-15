package com.osh.rvs.action.inline;

import java.util.ArrayList;
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
import com.osh.rvs.form.inline.DisassembleStorageForm;
import com.osh.rvs.service.CategoryService;
import com.osh.rvs.service.inline.DisassembleStorageService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 * 分解库位
 * 
 * @author Gonglm
 *
 */
public class DisassembleStorageAction extends BaseAction {

	private Logger log = Logger.getLogger(DisassembleStorageAction.class);

	/**
	 * 页面初始化
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("DisassembleStorageAction.init start");

		CategoryService categoryService = new CategoryService();
		String cOptions = categoryService.getEndoscopeOptions("", conn);
		request.setAttribute("cOptions", cOptions);// 维修对象机种集合

		DisassembleStorageService service = new DisassembleStorageService();
		request.setAttribute("pOptions", service.getStoragePosition(conn));// 对象工位

		// level取得
		request.setAttribute("lOptions",CodeListUtils.getSelectOptions("material_level_inline", null, "", false));

		// 取得用户信息
		HttpSession session = request.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		// 权限区分
		List<Integer> privacies = user.getPrivacies();
		if (!privacies.contains(RvsConsts.PRIVACY_ADMIN)) {
			if ("00000000001".equals(user.getSection_id()) && "00000000012".equals(user.getLine_id()) 
					&& privacies.contains(RvsConsts.PRIVACY_POSITION)) {
				request.setAttribute("role", "line");
			}
		} else {
			request.setAttribute("role", "admin");
		}

		actionForward = mapping.findForward(FW_INIT);

		log.info("DisassembleStorageAction.init end");
	}

	/**
	 * 一览
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("DisassembleStorageAction.search start");

		// 对Ajax响应
		Map<String, Object> lResponseResult = new HashMap<String, Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();
		// 获取表单数据
		DisassembleStorageForm DisassembleStorageForm = (DisassembleStorageForm) form;

		DisassembleStorageService service = new DisassembleStorageService();
		List<DisassembleStorageForm> list = service.search(DisassembleStorageForm, conn, msgInfos);

		lResponseResult.put("list", list);
		lResponseResult.put("errors", msgInfos);
		returnJsonResponse(response, lResponseResult);

		log.info("DisassembleStorageAction.search end");
	}

	/**
	 * 位置分布
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void getLocationMap(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("DisassembleStorageAction.getLocationMap start");

		// 对Ajax响应
		Map<String, Object> lResponseResult = new HashMap<String, Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		DisassembleStorageService service = new DisassembleStorageService();
		String processCode=  request.getParameter("process_code");

		service.getLocationMap(request.getParameter("material_id"), request.getParameter("position_id"), 
				processCode != null, lResponseResult, conn, msgInfos);

		lResponseResult.put("errors", msgInfos);
		returnJsonResponse(response, lResponseResult);

		log.info("DisassembleStorageAction.getLocationMap end");
	}

	public void doChangelocation(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("DisassembleStorageAction.doChangelocation start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		DisassembleStorageService service = new DisassembleStorageService();

		service.changeLocation(form, msgInfos, conn);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);
		// 返回Json格式回馈信息
		returnJsonResponse(response, callbackResponse);

		log.info("DisassembleStorageAction.doChangelocation end");
	}

	/**
	 * 放入库位
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doPutin(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("DisassembleStorageAction.doPutin start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		DisassembleStorageService service = new DisassembleStorageService();

		service.putin(form, msgInfos, conn);

		HttpSession session = request.getSession();
		String finger = (String) session.getAttribute(RvsConsts.JUST_WORKING);
		if (msgInfos.size() == 0 && finger != null) {
			service.updateFinger(form, finger, session, callbackResponse);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);
		// 返回Json格式回馈信息
		returnJsonResponse(response, callbackResponse);
		log.info("DisassembleStorageAction.doPutin end");
	}
	
	/**
	 * 手动出库
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doWarehouse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("DisassembleStorageAction.doWarehouse start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 新建记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> msgInfos = v.validate();

		if (msgInfos.size() == 0) {
			DisassembleStorageService service = new DisassembleStorageService();

			service.warehouse(form, msgInfos, conn);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);
		// 返回Json格式回馈信息
		returnJsonResponse(response, callbackResponse);
		log.info("DisassembleStorageAction.doWarehouse end");
	}

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
	public void doCreate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("DisassembleStorageAction.doCreate start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 新建记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> msgInfos = v.validate();

		if (msgInfos.size() == 0) {
			DisassembleStorageService service = new DisassembleStorageService();

			service.create(form, msgInfos, conn);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);
		// 返回Json格式回馈信息
		returnJsonResponse(response, callbackResponse);
		log.info("DisassembleStorageAction.doCreate end");
	}

	/**
	 * 调整库位
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doChange(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("DisassembleStorageAction.doChange start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 新建记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> msgInfos = v.validate();

		if (msgInfos.size() == 0) {
			DisassembleStorageService service = new DisassembleStorageService();

			service.changeSetting(form, msgInfos, conn);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);
		// 返回Json格式回馈信息
		returnJsonResponse(response, callbackResponse);
		log.info("DisassembleStorageAction.doChange end");
	}

	/**
	 * 取消库位
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doRemove(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("DisassembleStorageAction.doRemove start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 新建记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> msgInfos = v.validate();

		if (msgInfos.size() == 0) {
			DisassembleStorageService service = new DisassembleStorageService();

			service.remove(form, msgInfos, conn);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", msgInfos);
		// 返回Json格式回馈信息
		returnJsonResponse(response, callbackResponse);
		log.info("DisassembleStorageAction.doRemove end");
	}

}
