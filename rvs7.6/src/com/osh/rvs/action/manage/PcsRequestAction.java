/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：工程检查票改废订画面<br>
 * @author 龚镭敏
 * @version 1.31
 */
package com.osh.rvs.action.manage;

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
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.common.PcsUtils;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.master.PcsRequestForm;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.manage.PcsRequestService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

public class PcsRequestAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	/**
	 * 工程检查票改废订画面初始表示处理
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit = { 2, 0 })
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {

		log.info("PcsRequestAction.init start");

		// 取得工程检查票类型
		Map<String, String> folderTypes = PcsUtils.getFolderTypes();
		req.setAttribute("lLineType", CodeListUtils.getSelectOptions(folderTypes, null, "", false));
		String hLineTypeEo = "";
		for (String ftKey : folderTypes.keySet()) {
			hLineTypeEo += ftKey + ":" + folderTypes.get(ftKey) + ";";
		}
		hLineTypeEo += "0:";
		req.setAttribute("hLineTypeEo", hLineTypeEo);

		// h_change_means_eo
		req.setAttribute("lChangeMean", CodeListUtils.getSelectOptions("pcs_request_change_mean", null, "(全部)", false));
		req.setAttribute("hChangeMeansEo", CodeListUtils.getGridOptions("pcs_request_change_mean"));

		// 型号列表
		ModelService modelService = new ModelService();
		String mReferChooser = modelService.getOptions(conn);
		req.setAttribute("mReferChooser", mReferChooser);

		req.setAttribute("hMLevelEo", CodeListUtils.getGridOptions("material_level"));

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		if (user.getPrivacies().contains(RvsConsts.PRIVACY_ADMIN)) {
			req.setAttribute("admin", "true");
		} else {
			req.setAttribute("admin", "false");
		}

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("PcsRequestAction.init end");
	}

	/**
	 * 工程检查票改废订画面初始数据查询
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("PcsRequestAction.search start");
		Map<String, Object> lResponseResult = new HashMap<String, Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		PcsRequestService service = new PcsRequestService();
		List<PcsRequestForm> lForms = service.findHistory(form, conn);

		lResponseResult.put("lForms", lForms);
		lResponseResult.put("errors", msgInfos);
		returnJsonResponse(res, lResponseResult);
		log.info("PcsRequestAction.search end");
	}

	/**
	 * 建立“工程检查票改废订画面”依赖
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doCreate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("PcsRequestAction.doCreate start");
		Map<String, Object> lResponseResult = new HashMap<String, Object>();
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.delete("line_id", "file_name");
		List<MsgInfo> msgErrors = v.validate();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();
		PcsRequestService service = new PcsRequestService();
		if (msgErrors.size() == 0) {
			service.customValidate(form, req.getParameterMap(), req.getSession(), 
					msgErrors, msgInfos, lResponseResult);
			if (msgErrors.size() == 0 && msgInfos.size() == 0) {
				service.create(req.getSession(), conn);
			}
		}

		lResponseResult.put("errors", msgErrors);
		lResponseResult.put("infoes", msgInfos);
		returnJsonResponse(res, lResponseResult);
		log.info("PcsRequestAction.doCreate end");
	}

	/**
	 * 取得对比的工程检查票
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void getCompare(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("PcsRequestAction.getCompare " + req.getParameter("pcs_request_key") + " start");
		Map<String, Object> lResponseResult = new HashMap<String, Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		PcsRequestService service = new PcsRequestService();
		service.getCompare(req.getParameter("pcs_request_key"), lResponseResult, conn);

		lResponseResult.put("errors", msgInfos);
		returnJsonResponse(res, lResponseResult);
		log.info("PcsRequestAction.getCompare end");
	}

	/**
	 * 取得工程检查票测试样板
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void getTest(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("PcsRequestAction.getTest " + req.getParameter("pcs_request_key") + " start");
		Map<String, Object> lResponseResult = new HashMap<String, Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		PcsRequestService service = new PcsRequestService();
		service.getTest(req.getParameter("pcs_request_key"), lResponseResult, conn);

		lResponseResult.put("errors", msgInfos);
		returnJsonResponse(res, lResponseResult);
		log.info("PcsRequestAction.getTest end");
	}

	/**
	 * 取得工程检查票测试结果
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void makeTestPdf(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("PcsRequestAction.makeTestPdf " + req.getParameter("pcs_request_key") + " start");
		Map<String, Object> lResponseResult = new HashMap<String, Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		PcsRequestService service = new PcsRequestService();
		String cachePath = service.makeTest(req, conn);

		lResponseResult.put("temp_file", cachePath);
		lResponseResult.put("errors", msgInfos);
		returnJsonResponse(res, lResponseResult);
		log.info("PcsRequestAction.makeTestPdf end");
	}

	public void getWorkings(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("PcsRequestAction.getWorkings start");
		Map<String, Object> lResponseResult = new HashMap<String, Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		PcsRequestService service = new PcsRequestService();
		List<MaterialEntity> materials = service.getWorkingMaterials(req.getParameterMap(), conn);

		lResponseResult.put("materials", materials);
		lResponseResult.put("errors", msgInfos);
		returnJsonResponse(res, lResponseResult);
		log.info("PcsRequestAction.getWorkings end");
	}

	/**
	 * 实现“工程检查票改废订画面”依赖
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doImport(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("PcsRequestAction.doImport start");

		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();
		Map<String, Object> lResponseResult = new HashMap<String, Object>();

		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		PcsRequestService service = new PcsRequestService();
		service.importToSystem(req.getParameterMap(), user, conn);

		lResponseResult.put("errors", msgInfos);
		returnJsonResponse(res, lResponseResult);
		log.info("PcsRequestAction.doImport end");
	}

	/**
	 * 取消“工程检查票改废订画面”依赖
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doRemove(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("PcsRequestAction.doRemove start");

		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();
		Map<String, Object> lResponseResult = new HashMap<String, Object>();

		PcsRequestService service = new PcsRequestService();
		service.remove(req.getParameterMap(), conn);

		lResponseResult.put("errors", msgInfos);
		returnJsonResponse(res, lResponseResult);
		log.info("PcsRequestAction.doRemove end");
	}

	public void getForEdit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("PcsRequestAction.getForEdit " + req.getParameter("pcs_request_key") + " start");
		Map<String, Object> lResponseResult = new HashMap<String, Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		PcsRequestService service = new PcsRequestService();
		PcsRequestForm pcsRequestForm = service.getForEdit(req.getParameter("pcs_request_key"), conn);

		lResponseResult.put("pcs_request", pcsRequestForm);
		lResponseResult.put("errors", msgInfos);
		returnJsonResponse(res, lResponseResult);
		log.info("PcsRequestAction.getForEdit end");
	}

	/**
	 * 编辑依赖
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doEdit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("PcsRequestAction.doEdit start");
		Map<String, Object> lResponseResult = new HashMap<String, Object>();
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.delete("line_id", "file_name", "line_type", "change_means");
		List<MsgInfo> msgErrors = v.validate();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();
		PcsRequestService service = new PcsRequestService();
		if (msgErrors.size() == 0) {
			service.customValidateEdit(form, req.getSession(), msgInfos);
			if (msgErrors.size() == 0 && msgInfos.size() == 0) {
				service.update(form, req.getSession(), conn);
			}
		}

		lResponseResult.put("errors", msgErrors);
		lResponseResult.put("infoes", msgInfos);
		returnJsonResponse(res, lResponseResult);
		log.info("PcsRequestAction.doEdit end");
	}

}
