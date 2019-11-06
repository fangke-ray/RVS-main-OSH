package com.osh.rvs.action.partial;

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

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.partial.FactPartialWarehouseForm;
import com.osh.rvs.form.partial.PartialWarehouseDetailForm;
import com.osh.rvs.form.partial.PartialWarehouseForm;
import com.osh.rvs.form.qf.AfProductionFeatureForm;
import com.osh.rvs.service.AcceptFactService;
import com.osh.rvs.service.partial.FactPartialWarehouseService;
import com.osh.rvs.service.partial.PartialWarehouseDetailService;
import com.osh.rvs.service.partial.PartialWarehouseService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

/**
 * 零件入库单管理
 * 
 * @author liuxb
 * 
 */
public class PartialWarehouseAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());

	/** 零件入库单 **/
	private PartialWarehouseService partialWarehouseService = new PartialWarehouseService();
	/** 零件入库单明细 **/
	private PartialWarehouseDetailService partialWarehouseDetailService = new PartialWarehouseDetailService();
	/** 零件入库作业数 **/
	private FactPartialWarehouseService factPartialWarehouseService = new FactPartialWarehouseService();
	/** 间接人员作业信息 **/
	private AcceptFactService acceptFactService = new AcceptFactService();

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
		log.info("PartialWarehouseAction.init start");

		// 入库进展
		request.setAttribute("sStep", CodeListUtils.getSelectOptions("partial_warehouse_step", null, ""));
		request.setAttribute("goStep", CodeListUtils.getGridOptions("partial_warehouse_step"));

		actionForward = mapping.findForward(FW_INIT);

		log.info("PartialWarehouseAction.init end");
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
		log.info("PartialWarehouseAction.search start");

		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			List<PartialWarehouseForm> finish = partialWarehouseService.search(form, conn);
			listResponse.put("finish", finish);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PartialWarehouseAction.search end");
	}

	/**
	 * 零件入库单明细一览
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void detail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("PartialWarehouseAction.detail start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		PartialWarehouseForm partialWarehouseForm = (PartialWarehouseForm) form;
		List<PartialWarehouseDetailForm> list = partialWarehouseDetailService.searchByKey(partialWarehouseForm.getKey(), conn);

		listResponse.put("list", list);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PartialWarehouseAction.detail end");
	}

	/**
	 * 上传文件
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doUpload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("PartialWarehouseAction.doUpload start");

		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		partialWarehouseService.upload(form, request, conn, errors);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);

		log.info("PartialWarehouseAction.doUpload end");
	}

	/**
	 * 根据入库进展查询入库单信息
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void searchByStep(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("PartialWarehouseAction.searchByStep start");

		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String step = request.getParameter("step");
		String[] steps = step.split(",");

		List<PartialWarehouseForm> list = partialWarehouseService.searchByStep(steps, conn);
		listResponse.put("list", list);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);

		log.info("PartialWarehouseAction.searchByStep end");
	}

	/**
	 * 作业信息
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void searchWorkInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("PartialWarehouseAction.searchWorkInfo start");

		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 当前登录者
		LoginData user = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		// 根据操作者ID查找未结束作业信息
		AfProductionFeatureForm productionForm = acceptFactService.getUnFinish(user.getOperator_id(), conn);
		
		if(productionForm == null){
			MsgInfo error = new MsgInfo();
			error.setErrcode("info.linework.workingLost");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.workingLost"));
			errors.add(error);
		}

		if(errors.size() == 0){
			PartialWarehouseForm partialWarehouseForm = (PartialWarehouseForm) form;
			// 入库单KEY
			String key = partialWarehouseForm.getKey();
			// 作业类型
			String productionType = partialWarehouseForm.getProduction_type();

			/** 入库单信息 **/
			partialWarehouseForm = partialWarehouseService.getByKey(key, conn);

			List<PartialWarehouseDetailForm> list = new ArrayList<PartialWarehouseDetailForm>();
			/** 入库单明细 **/
			if ("213".equals(productionType)) {// 核对/上架
				list = partialWarehouseDetailService.searchByKey(key, conn);
			} else {// 分装
				list = partialWarehouseDetailService.searchUnpackByKey(key, conn);
			}

			/** 入库单每种规格作业总数 **/
			List<PartialWarehouseDetailForm> kindList = new ArrayList<PartialWarehouseDetailForm>();

			if ("213".equals(productionType)) {// 核对/上架
				kindList = partialWarehouseDetailService.countCollactionQuantityOfKind(key, conn);
			} else {// 分装
				kindList = partialWarehouseDetailService.countUnpackQuantityOfKind(key, conn);
			}

			/** 入库单已经作业每种规格总数 **/
			FactPartialWarehouseForm factPartialWarehouseForm = new FactPartialWarehouseForm();
			// 设置入库单KEY
			factPartialWarehouseForm.setPartial_warehouse_key(key);
			// 设置作业类型
			factPartialWarehouseForm.setProduction_type(productionType);
			// 设置作业KEY
			factPartialWarehouseForm.setAf_pf_key(productionForm.getAf_pf_key());
			List<FactPartialWarehouseForm> factList = factPartialWarehouseService.countQuantityOfSpecKind(factPartialWarehouseForm, conn);

			/** 当前作业每种规格数量 **/
			List<FactPartialWarehouseForm> curFactList = factPartialWarehouseService.search(factPartialWarehouseForm, conn);

			listResponse.put("partialWarehouseForm", partialWarehouseForm);
			listResponse.put("list", list);
			listResponse.put("kindList", kindList);
			listResponse.put("factList", factList);
			listResponse.put("curFactList", curFactList);
		}
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);

		log.info("PartialWarehouseAction.searchWorkInfo end");
	}

	/**
	 * 确认
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doConfirm(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("PartialWarehouseAction.doConfirm start");

		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		partialWarehouseService.updateQuantity(request, conn, errors);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);

		log.info("PartialWarehouseAction.doConfirm end");
	}

}
