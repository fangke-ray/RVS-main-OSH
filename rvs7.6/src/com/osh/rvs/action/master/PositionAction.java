/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：工位系统管理事件<br>
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

import com.osh.rvs.bean.master.PositionGroupEntity;
import com.osh.rvs.form.master.PositionForm;
import com.osh.rvs.service.LineService;
import com.osh.rvs.service.PositionService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

public class PositionAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	/**
	 * 工位系统管理处理
	 */
	private PositionService service = new PositionService();

	/**
	 * 工位管理画面初始表示处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("PositionAction.init start");

		LineService lservice = new LineService();
		// 取得下拉框信息
		String lOptions = lservice.getOptions(conn);
		req.setAttribute("lOptions", lOptions);

		req.setAttribute("spOptions", CodeListUtils.getSelectOptions("position_spec", null, ""));

		req.setAttribute("pReferChooser", service.getOptions(conn, true, true));

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("PositionAction.init end");
	}

	/**
	 * 工位查询一览处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("PositionAction.search start");
		// Ajax响应对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行检索
			List<PositionForm> lResultForm = service.search(form, conn, errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionAction.search end");
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

		log.info("PositionAction.detail start");
		// Ajax响应对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 查询记录
			PositionForm resultForm = service.getDetail(form, conn, errors);

			if (resultForm != null) {
				String reqId = req.getParameter("id");
				List<PositionGroupEntity> positionGroup = service.getGroupPositionById(reqId, conn);
				if (positionGroup.size() > 0) {
					listResponse.put("positionGroup", positionGroup);
				}
				// 查询结果放入Ajax响应对象
				listResponse.put("positionForm", resultForm);

				if (resultForm.getMapping_position_id() == null) {
					Map<String, List<String>> positionMappings = PositionService.getPositionMappings(conn);
					listResponse.put("positionMappings", positionMappings.get(reqId));
				}
				if (resultForm.getUnitized_position_id() == null) {
					Map<String, List<String>> positionMappings = PositionService.getPositionUnitizeds(conn);
					listResponse.put("positionUnitizeds", positionMappings.get(reqId));
				}
			}
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PositionAction.detail end");

	}

	/**
	 * 工位数据新建登录实行处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void doinsert(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{

		log.info("PositionAction.doinsert start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 新建记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();

		PositionForm positionForm = (PositionForm) form;
		// 其他合法性检查
		service.customValidate(positionForm, conn, errors);

		List<PositionGroupEntity> positionGroupList = service.checkPositionGroup(req, conn, errors);

		if (errors.size() == 0) {
			// 执行插入
			service.insert(positionForm, req.getSession(), conn, errors);

			service.createPositionGroup(null, positionGroupList, conn);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("PositionAction.doinsert end");
	}

	/**
	 * 工位数据更新实行处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void doupdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("PositionAction.doupdate start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 修改记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.delete("process_code");
		List<MsgInfo> errors = v.validate();

		PositionForm positionForm = (PositionForm) form;
		// 其他合法性检查
		service.customValidate(positionForm, conn, errors);

		List<PositionGroupEntity> positionGroupList = service.checkPositionGroup(req, conn, errors);

		if (errors.size() == 0) {
			// 执行更新
			service.update(positionForm, req.getSession(), conn, errors);

			service.createPositionGroup(positionForm.getId(), positionGroupList, conn);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("PositionAction.doupdate end");
	}

	/**
	 * 工位数据删除实行处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={2, 0})
	public void dodelete(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("PositionAction.dodelete start");
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

		log.info("PositionAction.dodelete end");
	}

	public void domapping(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("PositionAction.domapping start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 删除记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		List<String> mappingList = service.checkMapping(req, conn, errors);

		if (errors.size() == 0) {
			// 执行删除
			service.mapping(req.getParameter("id"), mappingList, conn);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("PositionAction.domapping end");
	}

	public void domappingUnitized(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("PositionAction.domappingUnitized start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 删除记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		List<String> mappingList = service.checkUnitized(req, conn, errors);

		if (errors.size() == 0) {
			// 执行删除
			service.mappingUnitized(req.getParameter("id"), mappingList, conn);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("PositionAction.domappingUnitized end");
	}
}
