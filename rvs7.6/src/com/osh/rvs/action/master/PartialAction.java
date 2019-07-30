package com.osh.rvs.action.master;

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
import com.osh.rvs.bean.master.PartialEntity;
import com.osh.rvs.bean.master.PartialPositionEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.master.PartialForm;
import com.osh.rvs.form.master.PartialPositionForm;
import com.osh.rvs.form.master.PartialUnpackForm;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.PartialService;
import com.osh.rvs.service.PartialUnpackService;
import com.osh.rvs.service.partial.PartialPositionService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.validator.Validators;

public class PartialAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	private PartialService service = new PartialService();
	private final PartialUnpackService partialUnpackService = new PartialUnpackService();

	/**
	 * 零件管理画面初始表示处理
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param req
	 *            页面请求
	 * @param res
	 *            页面响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	@Privacies(permit = { 1, 0 })
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("PartialAction.init start");

		PartialForm partialForm = (PartialForm) form;
		PartialEntity partialEntity = new PartialEntity();

		/* 表单数据复制到数据对象上 */
		BeanUtil.copyToBean(partialForm, partialEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		ModelService modelService = new ModelService();
		String mReferChooser = modelService.getOptions(conn);
		req.setAttribute("mReferChooser", mReferChooser);// 维修对象型号集合

		// 数据到页面
		req.setAttribute("sValue_currency", CodeListUtils.getSelectOptions("value_currency", null, ""));
		req.setAttribute("govalue_currency", CodeListUtils.getGridOptions("value_currency"));

		// 规格种别
		req.setAttribute("specKind", CodeListUtils.getSelectOptions("partial_spec_kind", null, ""));
		req.setAttribute("gridSpecKind", CodeListUtils.getGridOptions("partial_spec_kind"));
		
		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		
		//权限区分
		List<Integer> privacies = user.getPrivacies();
		if (privacies.contains(RvsConsts.PRIVACY_SA) || privacies.contains(RvsConsts.PRIVACY_PARTIAL_MANAGER)
				|| privacies.contains(RvsConsts.PRIVACY_ADMIN)) {
			req.setAttribute("role", "operator");
		}else{
			req.setAttribute("role", "other");
		}
		
		actionForward = mapping.findForward(FW_INIT);
		log.info("PartialAction.init end");
	}

	/**
	 * 零件管理查询一览处理
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param req
	 *            页面请求
	 * @param res
	 *            页面响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("PartialAction.search start");

		PartialForm partialForm = (PartialForm) form;
		/* Ajax回馈对象 */
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> infos = new ArrayList<MsgInfo>();
		PartialEntity partialEntity = new PartialEntity();

		/* 表单数据复制到数据对象上 */
		BeanUtil.copyToBean(partialForm, partialEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		/* 查询 */
		List<PartialForm> slist = service.searchPartial(partialEntity, conn);
		/* 查询数据--〉Map */
		listResponse.put("servicePartial", slist);
		request.getSession().setAttribute("result", slist);
		/* errors */
		listResponse.put("errors", infos);
		returnJsonResponse(response, listResponse);

		log.info("PartialAction.search end");
	}

	/**
	 * 废改订编辑
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("PartialAction.edit start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		PartialEntity partialEntity = new PartialEntity();
		/* 表单复制到Bean */
		BeanUtil.copyToBean(form, partialEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		/* 查询 */
		List<PartialForm> list = service.searchPartial(partialEntity, conn);
		if (list.size() > 0) {
			PartialForm returnForm = list.get(0);
			listResponse.put("returnForm", returnForm);
		}
		/* 型号集合 */
		Map<String, String> modelNameMap = service.getPartialModelNameS(partialEntity, conn, errors);
		String optionsModelName = CodeListUtils.getSelectOptions(modelNameMap, "", "", false);

		listResponse.put("smodelNameOptions", optionsModelName);

		listResponse.put("errors", errors);

		returnJsonResponse(response, listResponse);
		log.info("PartialAction.edit end");
	}

	/**
	 * 零件管理双击获取数据
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param req
	 *            页面请求
	 * @param res
	 *            页面响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void detail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("PartialAction.detail start");

		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();
		PartialEntity partialEntity = new PartialEntity();
		
		BeanUtil.copyToBean(form, partialEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		if (errors.size() == 0) {
			/*取得本零件的详细信息*/
			PartialForm resultForm = service.getDetail(partialEntity, conn, errors);
			if (resultForm != null) {
				listResponse.put("partialForm", resultForm);
				
				PartialUnpackForm partialUnpackForm = new PartialUnpackForm();
				partialUnpackForm.setPartial_id(resultForm.getPartial_id());
				partialUnpackForm = partialUnpackService.getPartialUnpack(partialUnpackForm, conn);

				listResponse.put("partialUnpackForm", partialUnpackForm);
			}
			PartialPositionEntity partialPositionEntity = new PartialPositionEntity();
			partialPositionEntity.setPartial_id(partialEntity.getPartial_id());
			/*双击取得本零件的型号工位和有效期的(jqgrid)*/
			List<PartialPositionForm> partialList = service.searchPartialModelNameProcessCodeActiveData(partialPositionEntity, conn);
			if (partialList.size() > 0) {
				listResponse.put("returnPartial", partialList);
			}
		}
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		log.info("PartialAction.detail end");
	}

	/**
	 * 零件管理新建页面一览处理
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param req
	 *            页面请求
	 * @param res
	 *            页面响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void doinsert(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("PartialAction.doinsert start");
		/* Ajax反馈对象 */
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		/* 表单合法性检查 */
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();

		/* 验证零件编码和零件是否输入值 */
		service.customValidate(form, conn, errors);

		PartialForm partialForm = (PartialForm) form;

		PartialUnpackForm partialUnpackForm = new PartialUnpackForm();
		partialUnpackForm.setSplit_quantity(partialForm.getSplit_quantity());

		if ("1".equals(partialForm.getUnpack_flg())) {
			v = BeanUtil.createBeanValidators(partialUnpackForm, BeanUtil.CHECK_TYPE_PASSEMPTY);
			v.add("split_quantity", v.required("分装数量"));
			errors = v.validate();
		}
		
		if (errors.size() == 0) {
			String partialID = service.insert(form, req.getSession(), conn, errors);
			
			if ("1".equals(partialForm.getUnpack_flg())) {
				partialUnpackForm.setPartial_id(partialID);

				partialUnpackService.insert(partialUnpackForm, conn);
			}

		}
		/* 检查错误时报告错误信息 */
		callbackResponse.put("errors", errors);
		/* 返回Json格式响应信息 */
		returnJsonResponse(res, callbackResponse);

		log.info("PartialAction.doinsert end");
	}

	/**
	 * 零件管理删除一览处理
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param req
	 *            页面请求
	 * @param res
	 *            页面响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void dodelete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("PartialAction.dodelete start");
		// 盛放错误信息
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		// 检查时返回的验证信息
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();
		// 当检查错误信息无
		if (errors.size() == 0) {
			service.delete(form, request.getSession(), conn, errors);
		}
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回JSON格式响应的信息
		returnJsonResponse(response, callbackResponse);
		log.info("PartialAction.dodelete end");
	}

	/**
	 * 零件管理更新一览处理（更新partial表里的code和name字段）
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param req
	 *            页面请求
	 * @param res
	 *            页面响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void doupdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("PartialAction.doupdate start");
		// 盛放错误信息
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		// 检查表单验证信息
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();
		service.customValidate(form, conn, errors);
		
		PartialForm partialForm = (PartialForm) form;

		PartialUnpackForm partialUnpackForm = new PartialUnpackForm();
		partialUnpackForm.setSplit_quantity(partialForm.getSplit_quantity());

		if ("1".equals(partialForm.getUnpack_flg())) {
			v = BeanUtil.createBeanValidators(partialUnpackForm, BeanUtil.CHECK_TYPE_PASSEMPTY);
			v.add("split_quantity", v.required("分装数量"));
			errors = v.validate();
		}
		
		// 无错误时更新数据
		if (errors.size() == 0) {
			// 更新数据时从前台的传递一个参数，该参数返回值是true or false获取这个返回值 传递给service 执行service的判断
			service.update(form, req.getSession(), conn, errors);
			
			partialUnpackForm.setPartial_id(partialForm.getPartial_id());

			PartialUnpackForm respForm = partialUnpackService.getPartialUnpack(partialUnpackForm, conn);

			if ("1".equals(partialForm.getUnpack_flg())) {
				if (respForm == null) {
					partialUnpackService.insert(partialUnpackForm, conn);
				} else {
					partialUnpackService.update(partialUnpackForm, conn);
				}
			} else {
				if (respForm != null) {
					partialUnpackService.delete(partialUnpackForm, conn);
				}
			}
		}
		callbackResponse.put("errors", errors);
		returnJsonResponse(res, callbackResponse);
		log.info("PartialAction.doupdate end");
	}

	/**
	 * 零件管理双击修改内容(partial表code和name、partial_price表value_currency和price)
	 * 
	 * @param mapping
	 * @param form
	 *            表单
	 * @param req
	 *            页面请求
	 * @param res
	 *            页面响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void doupdatePartial(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("PartialAction.doupdatePartial start");
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();
		if (errors.size() == 0) {
			/* req.getParameter("judgeHistorylimitdate")从前台传递的有效截至日期 */
			service.updatePartialCodeName(form, req.getParameter("judgeHistorylimitdate"), req.getSession(), conn,
					errors);
		}
		callbackResponse.put("errors", errors);
		returnJsonResponse(res, callbackResponse);
		log.info("PartialAction.doupdatePartial end");
	}

	/**
	 * 零件集合(autocomplete)
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void getAutocomplete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("PartialAction.getAutocomplete start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		List<Map<String, String>> list = service.getPartialAutoCompletes(request.getParameter("code"), conn);
		listResponse.put("sPartialCode", list);// 零件集合

		listResponse.put("errors", infoes);
		returnJsonResponse(response, listResponse);

		log.info("PartialAction.getAutocomplete end");
	}

	/**
	 * 废改订有效期更新
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doupdateActiveTime(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("PartialAction.doupdateActiveTime start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		if (errors.size() == 0) {
			service.updatePartialPosition(form, request.getSession(), conn, errors,request);
		}

		returnJsonResponse(response, callbackResponse);
		log.info("PartialAction.doupdateActiveTime end");
	}

	/**
	 * 废改订有效期和零件更新
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doupdateActiveTimeCode(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("PartialAction.doupdateActiveTimeCode start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		// 检查表单验证信息
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY); // TODO CTP
		List<MsgInfo> errors = v.validate();
		PartialPositionService partialPositionService = new PartialPositionService();
		// 验证零件新建零件不能相同
		partialPositionService.customValidateChange(form, conn, errors);

		if (errors.size() == 0) {
			service.updatePartialPositionNewPartial(request, form, request.getSession(), conn, errors);
		}

		callbackResponse.put("errors", errors);

		returnJsonResponse(response, callbackResponse);
		log.info("PartialAction.doupdateActiveTimeCode end");
	}
	
	/**
	 * 零件价格上传
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doUploadPrice(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("PartialAction.doUploadPrice start");
		
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		service.updatePrice(form,conn,errors);
		
		callbackResponse.put("errors", errors);
		returnJsonResponse(response, callbackResponse);
		
		log.info("PartialAction.doUploadPrice end");
	}
	
	/**
	 * 下载零件价格
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void reportPrice(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("PartialAction.reportPrice start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		String fileName ="零件价格.xls";
		String filePath = service.downloadPrice(conn);
		
		listResponse.put("fileName", fileName);
		listResponse.put("filePath", filePath);
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("PartialAction.reportPrice end");
	}
	
}
