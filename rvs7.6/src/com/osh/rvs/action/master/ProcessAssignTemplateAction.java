/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：工程系统管理事件<br>
 * @author 龚镭敏
 * @version 0.01
 */
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

import com.osh.rvs.bean.master.ModelEntity;
import com.osh.rvs.form.master.ProcessAssignForm;
import com.osh.rvs.form.master.ProcessAssignTemplateForm;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.ProcessAssignService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

public class ProcessAssignTemplateAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	/**
	 * 工程系统管理处理
	 */
	private ProcessAssignService service = new ProcessAssignService();

	/**
	 * 工程管理画面初始表示处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ProcessAssignTemplateAction.init start");

		String paOptions = service.getOptions("", conn);
		req.setAttribute("paOptions", paOptions);

		String dkOptions = CodeListUtils.getSelectOptions("process_assign_derive_kind", null, "");
		req.setAttribute("dkOptions", dkOptions);

		String ftOptions = CodeListUtils.getSelectOptions("process_assign_fix_type", null, null);
		req.setAttribute("ftOptions", ftOptions);

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("ProcessAssignTemplateAction.init end");
	}

	/**
	 * 工程管理画面初始表示处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void getPositions(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ProcessAssignTemplateAction.getPositions start");
		// Ajax响应对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 执行检索
		List<Map<String, String>> positions = service.getInlinePositions(conn);

		// 查询结果放入Ajax响应对象
		listResponse.put("list", positions);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ProcessAssignTemplateAction.getPositions end");
	}

	/**
	 * 工程查询一览处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ProcessAssignTemplateAction.search start");
		// Ajax响应对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行检索
			List<ProcessAssignTemplateForm> lResultForm = service.searchTemplate(form, conn, errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ProcessAssignTemplateAction.search end");
	}

	/**
	 * 取得详细信息处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void detail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ProcessAssignTemplateAction.detail start");
		// Ajax响应对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 查询记录
			ProcessAssignTemplateForm resultForm = service.getDetail(form, conn, errors);

			// 查询结果放入Ajax响应对象
			listResponse.put("templateForm", resultForm);

			// 查询流程明细
			List<ProcessAssignForm> l = service.getAssigns(resultForm.getId(), conn, errors);

			// 查询结果放入Ajax响应对象
			listResponse.put("processAssigns", l);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ProcessAssignTemplateAction.detail end");

	}

	/**
	 * 工程数据新建登录实行处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void doinsert(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{

		log.info("ProcessAssignTemplateAction.doinsert start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 新建记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();
		service.customValidate(form, errors);

		Map<String, String[]> parameterMap = req.getParameterMap();

		if (errors.size() == 0) {
			// 执行插入
			service.insert(form, parameterMap, req.getSession(), conn, errors);

			String paOptions = service.getOptions("", conn);
			callbackResponse.put("paOptions", paOptions);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("ProcessAssignTemplateAction.doinsert end");
	}

	/**
	 * 工程数据更新实行处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void doupdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("ProcessAssignTemplateAction.doupdate start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 修改记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();
		service.customValidate(form, errors);

		if (errors.size() == 0) {
			// 执行更新
			service.update(form, req.getParameterMap(), req.getSession(), conn, errors);

			String paOptions = service.getOptions("", conn);
			callbackResponse.put("paOptions", paOptions);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("ProcessAssignTemplateAction.doupdate end");
	}

	/**
	 * 工程数据删除实行处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void dodelete(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("ProcessAssignTemplateAction.dodelete start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 删除记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行删除
			service.delete(form, req.getSession(), conn, errors);

			String paOptions = service.getOptions("", conn);
			callbackResponse.put("paOptions", paOptions);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("ProcessAssignTemplateAction.dodelete end");
	}

	/**
	 * 取得流程派生的对应
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void getDerivePair(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ProcessAssignTemplateAction.getDerivePair start");
		// Ajax响应对象
		Map<String, Object> cbResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String modelId = req.getParameter("model_id");
		String deriveKind = req.getParameter("derive_kind");
		String anmlExp = req.getParameter("anml_exp");

		if (anmlExp != null && "1".equals(anmlExp)) {
			List<String> anmlProcesses = ProcessAssignService.getAnmlProcesses(conn);
			if (anmlProcesses.size() > 0) {
				cbResponse.put("dpResult", 
						service.getDerivePair(anmlProcesses.get(0), deriveKind, conn));
			}
		} else {
			ModelService mService = new ModelService();
			ModelEntity mEntity = mService.getDetailEntity(modelId, conn);

			if (mEntity != null && mEntity.getDefault_pat_id() != null) {
				cbResponse.put("dpResult", 
						service.getDerivePair(mEntity.getDefault_pat_id(), deriveKind, conn));
			}
		}

		// 检查发生错误时报告错误信息
		cbResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, cbResponse);

		log.info("ProcessAssignTemplateAction.getDerivePair end");
	}

	public void getPositionsOfLineByPat(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("ProcessAssignTemplateAction.getPositionsOfLineByPat start");
		// Ajax响应对象
		Map<String, Object> cbResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String patId = req.getParameter("pat_id");
		String lineId = req.getParameter("line_id");

		cbResponse.put("processCodes", 
					service.getPositionsOfLineByPat(patId, lineId, conn));

		// 检查发生错误时报告错误信息
		cbResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, cbResponse);

		log.info("ProcessAssignTemplateAction.getPositionsOfLineByPat end");
	}
}
