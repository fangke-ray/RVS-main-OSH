package com.osh.rvs.action.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.support.SuppliesDetailForm;
import com.osh.rvs.service.OperatorService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.support.SuppliesDetailService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

/**
 * 
 * @Description 物品申购明细
 * @author liuxb
 * @date 2021-12-3 上午9:48:51
 */
public class SuppliesDetailAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());
	private SuppliesDetailService suppliesDetailService = new SuppliesDetailService();
	private SectionService sectionService = new SectionService();
	private OperatorService operatorService = new OperatorService();

	/**
	 * 初始化
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("SuppliesDetailAction.init start");

		// 分发课室
		String sectionOptions = sectionService.getAllOptions(conn);
		req.setAttribute("sectionOptions", sectionOptions);

		// 操作者
		String oReferChooser = operatorService.getAllOperatorName(conn);
		req.setAttribute("oReferChooser", oReferChooser);

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		// 权限
		List<Integer> privacies = user.getPrivacies();
		String sectionId = user.getSection_id();

		// 登录者ID
		req.setAttribute("loginID", user.getOperator_id());
		// 登录者账号
		req.setAttribute("jobNo", user.getJob_no());

		// 经理人员
		boolean isMamager = false;
		if (user.getRole_id().equals(RvsConsts.ROLE_MANAGER)) {
			isMamager = true;
		}
		req.setAttribute("isMamager", isMamager);

		// 支援课
		boolean isSupport = false;
		if ("00000000006".equals(sectionId)) {
			isSupport = true;
		}
		req.setAttribute("isSupport", isSupport);

		// 线长人员
		boolean isLiner = false;
		if (!CommonStringUtil.isEmpty(sectionId)
				&& (privacies.contains(RvsConsts.PRIVACY_LINE) 
						|| privacies.contains(RvsConsts.PRIVACY_RECEPT_EDIT) 
						|| privacies.contains(RvsConsts.PRIVACY_QA_MANAGER)
						|| privacies.contains(RvsConsts.PRIVACY_TECHNOLOGY) 
						|| privacies.contains(RvsConsts.PRIVACY_TECHNICAL_MANAGE))) {
			isLiner = true;
		}
		req.setAttribute("isLiner", isLiner);
		
		//支援课经理及以上
		boolean signEdit = false;
		if("00000000006".equals(sectionId) && user.getRole_id().equals(RvsConsts.ROLE_MANAGER)){
			signEdit = true;
		}
		req.setAttribute("signEdit", signEdit);

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("SuppliesDetailAction.init end");
	}

	/**
	 * 检索
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("SuppliesDetailAction.search start");

		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		// 表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			List<SuppliesDetailForm> list = suppliesDetailService.search(form, conn);
			callbackResponse.put("list", list);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);

		log.info("SuppliesDetailAction.search end");
	}

	/**
	 * 新建物品申购明细
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doInsert(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("SuppliesDetailAction.doInsert start");

		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.delete("supplies_key");
		v.delete("section_id");
		v.delete("applicator_id");
		v.add("nesssary_reason", v.required("用途"));

		List<MsgInfo> errors = v.validate();
		if (errors.size() == 0) {
			SuppliesDetailForm suppliesDetailForm = (SuppliesDetailForm) form;
			// 检查数量大于0
			int quantity = Integer.valueOf(suppliesDetailForm.getQuantity()).intValue();
			if (quantity <= 0) {
				MsgInfo msgInfo = new MsgInfo();
				msgInfo.setComponentid("quantity");
				msgInfo.setErrcode("validator.invalidParam.invalidMoreThanZero");
				msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidMoreThanZero", "数量", "0"));
				errors.add(msgInfo);
			}

			if (errors.size() == 0) {
				suppliesDetailService.insert(req, form, conn);
			}
		}

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SuppliesDetailAction.doInsert end");
	}

	/**
	 * 获取物品申购明细
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void getDetail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("SuppliesDetailAction.getDetail start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			SuppliesDetailForm pageForm = (SuppliesDetailForm) form;
			// Key
			String suppliesKey = pageForm.getSupplies_key();
			SuppliesDetailForm respForm = suppliesDetailService.getSuppliseDetailByKey(suppliesKey, conn);

			if (respForm == null) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("refer_key");
				error.setErrcode("dbaccess.recordNotExist");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", ""));
				errors.add(error);
			} else {
				listResponse.put("detail", respForm);
			}
		}

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SuppliesDetailAction.getDetail end");
	}

	/**
	 * 查询出所有'物品申购单 Key'为空，且有'上级确认者 ID'的记录
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void searchComfirmAndNoOrderKey(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("SuppliesDetailAction.searchComfirmAndNoOrderKey start");

		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		List<SuppliesDetailForm> list = suppliesDetailService.searchComfirmAndNoOrderKey(conn);

		listResponse.put("errors", errors);
		listResponse.put("list", list);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SuppliesDetailAction.searchComfirmAndNoOrderKey end");
	}

	/**
	 * 确认、驳回（经理）
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doComfirm(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("SuppliesDetailAction.doComfirm start");

		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			SuppliesDetailForm pageForm = (SuppliesDetailForm) form;
			// Key
			String suppliesKey = pageForm.getSupplies_key();
			SuppliesDetailForm respForm = suppliesDetailService.getSuppliseDetailByKey(suppliesKey, conn);
			if (respForm == null) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("refer_key");
				error.setErrcode("dbaccess.recordNotExist");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", ""));
				errors.add(error);
			} else {
				suppliesDetailService.confirm(req, pageForm, conn);
			}
		}

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SuppliesDetailAction.doComfirm end");
	}

	/**
	 * 更新
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("SuppliesDetailAction.doUpdate start");

		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			suppliesDetailService.updateApplication(form, conn);
		}

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SuppliesDetailAction.doUpdate end");
	}

	/**
	 * 删除明细
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doDelete(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("SuppliesDetailAction.doDelete start");

		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();
		
		if(errors.size() == 0){
			SuppliesDetailForm pageForm = (SuppliesDetailForm) form;
			// Key
			String suppliesKey = pageForm.getSupplies_key();
			SuppliesDetailForm respForm = suppliesDetailService.getSuppliseDetailByKey(suppliesKey, conn);
			
			if (respForm == null) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("refer_key");
				error.setErrcode("dbaccess.recordNotExist");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", ""));
				errors.add(error);
			} else {
				suppliesDetailService.delete(pageForm, conn);
			}
		}

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SuppliesDetailAction.doDelete end");
	}
	
	/**
	 * 查询待收货
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void searchWaittingRecept(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("SuppliesDetailAction.searchWaittingRecept start");

		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		List<SuppliesDetailForm> list = suppliesDetailService.searchWaittingRecept(conn);

		listResponse.put("errors", errors);
		listResponse.put("list", list);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SuppliesDetailAction.searchWaittingRecept end");
	}
	
	/**
	 * 收货
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param con
	 * @throws Exception
	 */
	public void doRecept(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("SuppliesDetailAction.doRecept start");
		
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		Map<String, String[]> parameters = req.getParameterMap();
		List<String> keys = new AutofillArrayList<String>(String.class);
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
		
		for (String parameterKey : parameters.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("keys".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameters.get(parameterKey);
					if ("supplies_key".equals(column)) {
						keys.set(icounts, value[0]);
					}
				}
			}
		}
		
		for (String suppliesKey : keys) {
			SuppliesDetailForm suppliesDetailForm = new SuppliesDetailForm();
			suppliesDetailForm.setSupplies_key(suppliesKey);
			suppliesDetailService.recept(suppliesDetailForm, conn);
		}
		
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("SuppliesDetailAction.doRecept end");
	}
	
	/**
	 * 验收
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doInlineRecept(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("SuppliesDetailAction.doInlineRecept start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		
		List<MsgInfo> errors = v.validate();
		if(errors.size() == 0){
			suppliesDetailService.inlineRecept(form, conn);
		}
		
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("SuppliesDetailAction.doInlineRecept end");
	}
	
	/**
	 * 发票
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doInvoice(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("SuppliesDetailAction.doInvoice start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		v.add("invoice_no", v.required("发票号码"));
		
		List<MsgInfo> errors = v.validate();
		if(errors.size() == 0){
			suppliesDetailService.updateInvoiceNo(form, conn);
		}

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SuppliesOrderAction.doInvoice end");
	}

}