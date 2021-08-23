/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：课室系统管理事件<br>
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

import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.form.master.PositionForm;
import com.osh.rvs.form.master.SectionForm;
import com.osh.rvs.mapper.master.PositionMapper;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.SectionService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

public class SectionAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	/**
	 * 课室系统管理处理
	 */
	private SectionService service = new SectionService();

	/**
	 * 课室管理画面初始表示处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("SectionAction.init start");

		// 取得权限下拉框信息
		List<String[]> pList = new ArrayList<String[]>();

		// 从数据库中查询记录
		PositionMapper dao = conn.getMapper(PositionMapper.class);
		List<PositionEntity> lpb = dao.getAllPosition();

		// 建立页面返回表单
		List<PositionForm> lpf = new ArrayList<PositionForm>();

		if (lpb != null && lpb.size() > 0) {
			// 数据对象复制到表单
			BeanUtil.copyToFormList(lpb, lpf, null, PositionForm.class);
			for(PositionForm pf : lpf) {
				if (pf.getDelete_flg().equals("0")) {
					String[] pline = new String[3];
					pline[0] = pf.getId();
					pline[1] = pf.getName();
					pline[2] = pf.getProcess_code();
					pList.add(pline);
				}
			}
	
			String pReferChooser = CodeListUtils.getReferChooser(pList);
			req.setAttribute("pReferChooser", pReferChooser);
		} else {
			req.setAttribute("pReferChooser", "");
		}

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("SectionAction.init end");
	}

	/**
	 * 课室查询一览处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("SectionAction.search start");
		// Ajax响应对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行检索
			List<SectionForm> lResultForm = service.search(form, conn, req.getParameter("privacy_id"), errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("list", lResultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SectionAction.search end");
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
	@Privacies(permit={1, 0})
	public void detail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("SectionAction.detail start");
		// Ajax响应对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 查询记录
			SectionForm resultForm = service.getDetail(form, conn, errors);

			// 查询结果放入Ajax响应对象
			listResponse.put("sectionForm", resultForm);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SectionAction.detail end");

	}

	/**
	 * 课室数据新建登录实行处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void doinsert(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{

		log.info("SectionAction.doinsert start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 新建记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();

		SectionForm sectionForm = (SectionForm) form;
		// 其他合法性检查
		// service.customValidate(sectionForm, errors);

		if (errors.size() == 0) {
			// 执行插入
			service.insert(sectionForm, req.getSession(), conn, errors);
			PositionService.clearCaches();
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("SectionAction.doinsert end");
	}

	/**
	 * 课室数据更新实行处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void doupdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("SectionAction.doupdate start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 修改记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();

		SectionForm sectionForm = (SectionForm) form;
		// 其他合法性检查
		service.customValidate(sectionForm, errors);

		if (errors.size() == 0) {
			// 执行更新
			service.update(sectionForm, req.getSession(), conn, errors);
			PositionService.clearCaches();
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("SectionAction.doupdate end");
	}

	/**
	 * 课室数据删除实行处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void dodelete(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("SectionAction.dodelete start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 删除记录表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			// 执行删除
			service.delete(form, req.getSession(), conn, errors);
			PositionService.clearCaches();
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, callbackResponse);

		log.info("SectionAction.dodelete end");
	}
}
