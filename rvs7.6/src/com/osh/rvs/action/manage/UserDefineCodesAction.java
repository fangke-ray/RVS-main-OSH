package com.osh.rvs.action.manage;

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

import com.osh.rvs.bean.manage.UserDefineCodesEntity;
import com.osh.rvs.form.manage.UserDefineCodesForm;
import com.osh.rvs.service.UserDefineCodesService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class UserDefineCodesAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	private UserDefineCodesService service = new UserDefineCodesService();

	/**
	 * 用户定义数值 页面初始化
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("UserDefineCodesAction.init start");

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("UserDefineCodesAction.init end");
	}

	/**
	 * 查询 用户定义数值 详细信息
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("UserDefineCodesAction.search start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 查询出用户定义数值的详细信息
		List<UserDefineCodesForm> userDefineCodesFormList = service.searchUserDefineCodes(conn);

		listResponse.put("errors", errors);
		listResponse.put("userDefineCodesFormList", userDefineCodesFormList);

		returnJsonResponse(response, listResponse);

		log.info("UserDefineCodesAction.search end");
	}

	/**
	 * 更新用户定义 设定值
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("UserDefineCodesAction.doUpdate start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		UserDefineCodesForm userDefineCodesForm = (UserDefineCodesForm) form;
		UserDefineCodesEntity userDefineCodesEntity = new UserDefineCodesEntity();

		BeanUtil.copyToBean(userDefineCodesForm, userDefineCodesEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		String code = userDefineCodesEntity.getCode();
		if (code.indexOf("AFST-") >= 0) {
			// 更新间接作业标准工时
			service.updateStandards(userDefineCodesEntity, conn);
		} else {
			// 更新用户定义设定值
			service.updateUserDefineCodes(userDefineCodesEntity, conn);
		}

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("UserDefineCodesAction.doUpdate start");
	}
}
