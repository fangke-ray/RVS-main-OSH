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

import com.osh.rvs.form.master.LightFixForm;
import com.osh.rvs.service.LightFixService;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.ProcessAssignService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

public class LightFixAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	/**
	 * 小修理标准编制管理
	 */
	private LightFixService service = new LightFixService();

	/**
	 * 画面初始表示处理
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit = { 2, 0 })
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("LightFixAction.init start");

		// 取得类别下拉框信息
		String kOptions = CodeListUtils.getSelectOptions("repair_category_kind", null, "", false);
		req.setAttribute("kOptions", kOptions);

		PositionService positionService = new PositionService();
		String pReferChooser = positionService.getOptions(conn, false, true);
		req.setAttribute("pReferChooser", pReferChooser);

		// 取得类别下拉框信息
		List<String[]> cList = new ArrayList<String[]>();

		Map<String, String> kindList = CodeListUtils.getList("repair_category_kind");
		for (String kind_code : kindList.keySet()) {
			String kind = kindList.get(kind_code);
			String[] pline = new String[2];
			pline[0] = kind_code;
			pline[1] = kind;
			cList.add(pline);
		}
		String kReferChooser = CodeListUtils.getReferChooser(cList);
		req.setAttribute("kReferChooser", kReferChooser);

		ProcessAssignService paService = new ProcessAssignService();
		String patOptions = paService.getGroupOptions("", conn);
		req.setAttribute("patOptions", patOptions);

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("LightFixAction.init end");
	}

	/**
	 * 查询一览处理
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit = { 2, 0 })
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("LightFixAction.search start");

		LightFixForm lightFixForm = (LightFixForm) form;

		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行检索
			List<LightFixForm> lResultForm = service.search(lightFixForm, conn);

			// 查询结果放入Ajax响应对象
			listResponse.put("list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("LightFixAction.search end");
	}

	/**
	 * 取得详细信息处理
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit = { 2, 0 })
	public void detail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("LightFixAction.detail start");

		LightFixForm lightFixForm = (LightFixForm) form;

		// Ajax响应对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			 // 查询记录
			 LightFixForm resultForm = service.getDetail(lightFixForm.getLight_fix_id(), conn);
			 // 查询结果放入Ajax响应对象
			 listResponse.put("detail", resultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("LightFixAction.detail end");
	}

	/**
	 * 新建登录实行处理
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit = { 2, 0 })
	public void doinsert(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("LightFixAction.doinsert start");

		LightFixForm lightFixForm = (LightFixForm) form;

		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 新建记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {			
			// 执行插入
			service.insert(errors, lightFixForm, req.getSession(), conn);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);

		log.info("LightFixAction.doinsert end");
	}

	/**
	 * 更新实行处理
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit = { 2, 0 })
	public void doupdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("LightFixAction.doupdate start");

		LightFixForm lightFixForm = (LightFixForm) form;

		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 修改记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行更新
			service.update(errors, lightFixForm, req.getSession(), conn);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("LightFixAction.doupdate end");
	}

	/**
	 * 删除实行处理
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit = { 2, 0 })
	public void dodelete(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("LightFixAction.dodelete start");

		LightFixForm lightFixForm = (LightFixForm) form;

		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 删除记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行删除
			service.delete(lightFixForm.getLight_fix_id(), conn);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("LightFixAction.dodelete end");
	}
}
