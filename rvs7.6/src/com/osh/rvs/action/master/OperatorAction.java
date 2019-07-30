/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：维修对象机种系统管理事件<br>
 * @author 龚镭敏
 * @version 0.01
 */
package com.osh.rvs.action.master;

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

import com.osh.rvs.form.master.OperatorForm;
import com.osh.rvs.service.AcceptFactService;
import com.osh.rvs.service.LineService;
import com.osh.rvs.service.OperatorService;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.RoleService;
import com.osh.rvs.service.SectionService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;


public class OperatorAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	/**
	 * 维修对象机种系统管理处理
	 */
	private OperatorService service = new OperatorService();

	/**
	 * 用户管理画面初始表示处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("OperatorAction.init start");

		SectionService sservice = new SectionService();
		// 取得下拉框信息
		String sOptions = sservice.getAllOptions(conn);
		req.setAttribute("sOptions", sOptions);

		LineService lservice = new LineService();
		// 取得下拉框信息
		String lOptions = lservice.getOptions(conn);
		req.setAttribute("lOptions", lOptions);

		RoleService rservice = new RoleService();
		// 取得下拉框信息
		String rOptions = rservice.getOptions(conn);
		req.setAttribute("rOptions", rOptions);

		// 取得类别下拉框信息
		String rkOptions = CodeListUtils.getSelectOptions("role_rank_kind", null, "", false);
		req.setAttribute("rkOptions", rkOptions);
		req.setAttribute("rkGo",CodeListUtils.getGridOptions("role_rank_kind"));
		
		// 取得帐号分类信息
		String atOptions = CodeListUtils.getSelectOptions("account_type", null, "", false);
		req.setAttribute("atOptions", atOptions);

		PositionService pService = new PositionService();
		req.setAttribute("pReferChooser", pService.getOptions(conn));

		AcceptFactService afService = new AcceptFactService();
		req.setAttribute("afReferChooser", afService.getOptions(conn));

		// 分线
		req.setAttribute("pxOptions", CodeListUtils.getSelectOptions("operator_px", "0", "0::(全部)", false));

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("OperatorAction.init end");
	}

	/**
	 * 用户查询一览处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("OperatorAction.search start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行检索
			List<OperatorForm> lResultForm = service.search(form, conn, errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("OperatorAction.search end");
	}

	/**
	 * 取得详细编辑信息处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void showedit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("OperatorAction.showedit start");
		// Ajax响应对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 查询记录
			OperatorForm resultForm = service.getShowedit(form, conn, errors);
			if (resultForm != null) {
				// 查询结果放入Ajax响应对象
				listResponse.put("operatorForm", resultForm);
			}
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("OperatorAction.showedit end");
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

		log.info("OperatorAction.detail start");
		// Ajax响应对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 查询记录
			OperatorForm resultForm = service.getDetail(form, conn, errors);
			if (resultForm != null) {
				// 查询结果放入Ajax响应对象
				listResponse.put("operatorForm", resultForm);
			}
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("OperatorAction.detail end");
	}

	/**
	 * 用户数据新建登录实行处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void doinsert(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{

		log.info("OperatorAction.doinsert start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 新建记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();

		// 特别检查
		OperatorForm oForm = (OperatorForm) form;
		service.customValidate(oForm, conn, errors);

		if (errors.size() == 0) {
			// 执行插入
			service.insert(oForm, req.getSession(), conn, errors);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);

		log.info("OperatorAction.doinsert end");
	}

	/**
	 * 用户数据更新实行处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void doupdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("OperatorAction.doupdate start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 修改记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();

		// 特别检查
		OperatorForm oForm = (OperatorForm) form;
		service.customValidate(oForm, conn, errors);

		if (errors.size() == 0) {
			// 执行更新
			service.update(oForm, req.getSession(), conn, errors);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("OperatorAction.doupdate end");
	}

	/**
	 * 用户数据删除实行处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void dodelete(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("OperatorAction.dodelete start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 删除记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行删除
			service.delete(form, req.getSession(), conn, errors);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("OperatorAction.dodelete end");
	}

	/**
	 * 为用户重新设定密码
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void dogeneratepasswd(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("OperatorAction.dogeneratepasswd start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 重新设定密码记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行重新设定密码
			service.generatepasswd(form, req.getSession(), conn, errors);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("OperatorAction.dogeneratepasswd end");
	}
}
