package com.osh.rvs.action.partial;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.osh.rvs.bean.partial.ConsumableListEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.partial.ConsumableListForm;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.partial.ConsumableListService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.validator.Validators;

public class ConsumableListAction extends BaseAction{
	private Logger log = Logger.getLogger(getClass());
	private ConsumableListService service = new ConsumableListService();
	
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("ConsumableListAction.init start");

		ModelService modelService = new ModelService();
		String mReferChooser = modelService.getOptions(conn);
		req.setAttribute("mReferChooser", mReferChooser);// 维修对象型号集合
		Calendar   cal_1=Calendar.getInstance();
		cal_1.set(Calendar.DAY_OF_MONTH,1);
		req.setAttribute("first",DateUtil.toString(cal_1.getTime(), DateUtil.DATE_PATTERN));
		String cost_rate_alram_belowline =service.getcost_rate_alram_belowline(conn);
		req.setAttribute("cost_rate_alram_belowline", cost_rate_alram_belowline);
		/* 分类 */
		req.setAttribute("Options", CodeListUtils.getSelectOptions("consumable_type",null,""));
		req.setAttribute("gqOptions",CodeListUtils.getGridOptions("consumable_type"));
		/* 补充周期 */
		req.setAttribute("sSupplyCycleOptions", CodeListUtils.getSelectOptions("consumable_supply_cycle",null,""));
		/* 补充日 */
		req.setAttribute("sSupplyDayOptions", CodeListUtils.getSelectOptions("consumable_supply_day",null,""));
		
		/* 耗时 */
		req.setAttribute("sShelfCost", CodeListUtils.getSelectOptions("consumable_shelf_cost",null,""));
		req.setAttribute("jqShelfCost",CodeListUtils.getGridOptions("consumable_shelf_cost"));
		
		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		
		//权限区分
		List<Integer> privacies = user.getPrivacies();
		if (privacies.contains(RvsConsts.PRIVACY_FACT_MATERIAL)) {
			req.setAttribute("role", "fact");
		} else {
			req.setAttribute("role", "other");
		}

		
		actionForward = mapping.findForward(FW_INIT);
		log.info("ConsumableListAction.init end");
	}
	/**
	 * 消耗品仓库库存一览查询
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("ConsumableListAction.search start");

		ConsumableListForm consumableListForm = (ConsumableListForm) form;

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		ConsumableListEntity consumableListEntity = new ConsumableListEntity();
		if(consumableListForm.getSearch_count_period_start()=="" || consumableListForm.getSearch_count_period_start()==null){
			Calendar cal_1=Calendar.getInstance();
			cal_1.set(Calendar.DAY_OF_MONTH,1);
			consumableListForm.setSearch_count_period_start(DateUtil.toString(cal_1.getTime(), DateUtil.DATE_PATTERN));
		}
		if(consumableListForm.getSearch_count_period_end()=="" || consumableListForm.getSearch_count_period_end()==null){
			consumableListForm.setSearch_count_period_end(DateUtil.toString(new Date(), DateUtil.DATE_PATTERN));
		}
		
		/* 表单复制到Bean */
		BeanUtil.copyToBean(consumableListForm, consumableListEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		if(consumableListEntity.getConsumed_rate_alarmline() == null){
			consumableListEntity.setConsumed_rate_alarmline(0);
		}
		/* 查询 */
		List<ConsumableListForm> list = service.searchConsumableList(consumableListEntity, conn);
		listResponse.put("consumableList", list);
		listResponse.put("errors", errors);

		returnJsonResponse(response, listResponse);
		log.info("ConsumableListAction.search end");
	}
	/**
	 * 消耗品加入库存处理
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
		log.info("ConsumableListAction.doinsert start");
		/* Ajax反馈对象 */
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		/* 表单合法性检查 */
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			service.insert(form, req.getSession(), conn, errors);
		}
		/* 检查错误时报告错误信息 */
		callbackResponse.put("errors", errors);
		/* 返回Json格式响应信息 */
		returnJsonResponse(res, callbackResponse);

		log.info("ConsumableListAction.doinsert end");
	}
	/**
	 * 消耗品加入库存check查询(autocomplete)
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
		log.info("ConsumableListAction.getAutocomplete start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		ConsumableListForm consumableListForm = (ConsumableListForm) form;
		String code = consumableListForm.getCode();
		List<ConsumableListForm> list = service.getPartialAutoCompletes(code, conn);
		if(list.size() != 1 ){
			listResponse.put("reasult_flg", "0");
			
		}else{
			listResponse.put("reasult_flg", "1");
			ConsumableListForm result = new ConsumableListForm();
			result.setCode(list.get(0).getCode());
			result.setPartial_id(list.get(0).getPartial_id());
			listResponse.put("sPartialCode", result);// 零件集合
		}


		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("ConsumableListAction.getAutocomplete end");
	}
	
	/**
	 * 盘点数据查询(autocomplete)
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void getAdjustSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("ConsumableListAction.getAdjustSearch start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		ConsumableListForm consumableListForm = (ConsumableListForm) form;
		String code = consumableListForm.getCode();
		List<ConsumableListForm> list = service.getAdjustSearch(code, conn);
		if(list.size() != 1){
			listResponse.put("adjustflg", '0');
		}else{
			listResponse.put("adjustflg", '1');
			ConsumableListForm result = new ConsumableListForm();
			result = list.get(0);
			listResponse.put("adjustsearch", result);
		}
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("ConsumableListAction.getAdjustSearch end");
	}
	
	/**
	 * 盘点数据修改(autocomplete)
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doadjust(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("ConsumableListAction.doAdjust start");
		// 取得用户信息
		HttpSession session = request.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		// 检查表单验证信息
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();
		Map<String, Object> listResponse = new HashMap<String, Object>();
		ConsumableListForm consumableListForm = (ConsumableListForm) form;
		/* 表单复制到Bean */
		ConsumableListEntity consumableListEntity = new ConsumableListEntity();
		BeanUtil.copyToBean(consumableListForm, consumableListEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		String operator_id =user.getOperator_id();
		consumableListEntity.setOperator_id(operator_id);
		if(errors.size() == 0){
			service.doAdjust(consumableListEntity, request.getSession(),conn);
			if(consumableListEntity.getAvailable_inventory_temp() != 0){
				service.doAdjustInsert(consumableListEntity,request.getSession(), conn);
			}
		}

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("ConsumableListAction.doAdjust end");
	}
	
	/**
	 * 消耗品加入库存check查询(autocomplete)
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void getConsumableFlg(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("ConsumableListAction.getConsumableFlg start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		ConsumableListForm consumableListForm = (ConsumableListForm) form;
		ConsumableListEntity consumableListEntity = new ConsumableListEntity();
		BeanUtil.copyToBean(consumableListForm, consumableListEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		Integer partial_id = consumableListEntity.getPartial_id();
		List<ConsumableListForm> list = service.getConsumableDetail(partial_id, conn);
		if(list.size() == 0){
			listResponse.put("consumableflg", '0');
		}else{
			listResponse.put("consumableflg", '1');
			ConsumableListForm returnForm = list.get(0);
			listResponse.put("consumabledetail", returnForm);
		}


		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("ConsumableListAction.getConsumableFlg end");
	}

	/**
	 * 修改库存设置
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
		log.info("ConsumableListAction.edit start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		ConsumableListEntity consumableListEntity = new ConsumableListEntity();
		/* 表单复制到Bean */
		BeanUtil.copyToBean(form, consumableListEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		/* 查询 */
		List<ConsumableListForm> list = service.searchConsumableDetail(consumableListEntity, conn);
		if (list.size() > 0) {
			ConsumableListForm returnForm = list.get(0);
			listResponse.put("returnForm", returnForm);
		}

		listResponse.put("errors", errors);

		returnJsonResponse(response, listResponse);
		log.info("ConsumableListAction.edit end");
	}

	/**
	 * 消耗品库存设置更新
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doupdateConsumableManage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("ConsumableListAction.doupdateConsumableManage start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		// 检查表单验证信息
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			service.updateConsumableManage(request, form, request.getSession(), conn, errors);
		}

		callbackResponse.put("errors", errors);

		returnJsonResponse(response, callbackResponse);
		log.info("ConsumableListAction.doupdateConsumableManage end");
	}

	/**
	 * 移出消耗品库存
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void dodelConsumable(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("ConsumableListAction.dodelConsumable start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		// 检查表单验证信息
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			service.delConsumable(request, form, request.getSession(), conn, errors);
		}

		callbackResponse.put("errors", errors);

		returnJsonResponse(response, callbackResponse);
		log.info("ConsumableListAction.dodelConsumable end");
	}
	
	/**
	 * 消耗品计量单位设置
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void getMeasuring(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("ConsumableListAction.getMeasuring start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		ConsumableListEntity consumableListEntity = new ConsumableListEntity();
		/* 表单复制到Bean */
		BeanUtil.copyToBean(form, consumableListEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		/* 查询 */
		List<ConsumableListForm> list = service.searchMeasuringUnit(consumableListEntity, conn);
		if (list.size() > 0) {
			ConsumableListForm returnForm = list.get(0);
			listResponse.put("returnForm", returnForm);
		}

		listResponse.put("errors", errors);

		returnJsonResponse(response, listResponse);
		log.info("ConsumableListAction.getMeasuring end");
	}
	
	/**
	 * 消耗品计量单位更新
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doupdateMeasurementUnit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("ConsumableListAction.doupdateMeasurementUnit start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		// 检查表单验证信息
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		ConsumableListEntity consumableListEntity = new ConsumableListEntity();
		/* 表单复制到Bean */
		BeanUtil.copyToBean(form, consumableListEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		// 消耗品计量单位存在check
		Integer iCnt = service.getMeasurementUnitCntByPid(consumableListEntity, conn);

		if (errors.size() == 0) {
			// 已经存在时，更新
			if (iCnt == 1) {
				service.updateMeasurementUnit(request, form, request.getSession(), conn, errors);
			// 不存在时，插入
			} else {
				service.insertMeasurementUnit(request, form, request.getSession(), conn, errors);
			}
		}

		callbackResponse.put("errors", errors);

		returnJsonResponse(response, callbackResponse);
		log.info("ConsumableListAction.doupdateMeasurementUnit end");
	}


	public void postClipboard(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("ConsumableListAction.search start");
	
		Map<String, Object> cbResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		cbResponse.put("clipboardObject", service.getPostSheet(conn));

		cbResponse.put("errors", errors);
	
		returnJsonResponse(response, cbResponse);
		log.info("ConsumableListAction.search end");
	}

	/**
	 * 消耗品仓库库存一览查询
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void searchDownload(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("ConsumableListAction.search start");

		ConsumableListForm consumableListForm = (ConsumableListForm) form;

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		ConsumableListEntity consumableListEntity = new ConsumableListEntity();
		if(consumableListForm.getSearch_count_period_start()=="" || consumableListForm.getSearch_count_period_start()==null){
			Calendar cal_1=Calendar.getInstance();
			cal_1.set(Calendar.DAY_OF_MONTH,1);
			consumableListForm.setSearch_count_period_start(DateUtil.toString(cal_1.getTime(), DateUtil.DATE_PATTERN));
		}
		if(consumableListForm.getSearch_count_period_end()=="" || consumableListForm.getSearch_count_period_end()==null){
			consumableListForm.setSearch_count_period_end(DateUtil.toString(new Date(), DateUtil.DATE_PATTERN));
		}
		
		/* 表单复制到Bean */
		BeanUtil.copyToBean(consumableListForm, consumableListEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		if(consumableListEntity.getConsumed_rate_alarmline() == null){
			consumableListEntity.setConsumed_rate_alarmline(0);
		}
		/* 查询 */
		List<ConsumableListForm> list = service.searchConsumableListWithQuota(consumableListEntity, conn);

		String fileName ="消耗品仓库库存一览.xlsx";

		String filePath = service.createConsumableListRecord(consumableListForm, list);
		
		listResponse.put("fileName", fileName);
		listResponse.put("filePath", filePath);

		
		listResponse.put("errors", errors);

		returnJsonResponse(response, listResponse);
		log.info("ConsumableListAction.searchDownload end");
	}

	/**
	 * 剪裁长度设置
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void getHeatshrinkableLength(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("ConsumableListAction.getHeatshrinkableLength start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		ConsumableListEntity consumableListEntity = new ConsumableListEntity();
		/* 表单复制到Bean */
		BeanUtil.copyToBean(form, consumableListEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		String partialId = "" + consumableListEntity.getPartial_id();
		/* 查询 */
		String content = service.searchHeatshrinkableLength(partialId , conn);
		ConsumableListForm returnForm = new ConsumableListForm();
		returnForm.setPartial_id(partialId);
		returnForm.setContent(content);
		listResponse.put("returnForm", returnForm);

		listResponse.put("errors", errors);

		returnJsonResponse(response, listResponse);
		log.info("ConsumableListAction.getHeatshrinkableLength end");
	}
	public void doSetHeatshrinkableLength(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("ConsumableListAction.setHeatshrinkableLength start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		service.setHeatshrinkableLength(request , conn, errors);

		listResponse.put("errors", errors);

		returnJsonResponse(response, listResponse);
		log.info("ConsumableListAction.setHeatshrinkableLength end");
	}
}
