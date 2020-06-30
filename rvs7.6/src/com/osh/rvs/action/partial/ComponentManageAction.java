package com.osh.rvs.action.partial;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.inline.SoloProductionFeatureEntity;
import com.osh.rvs.bean.partial.ComponentManageEntity;
import com.osh.rvs.bean.partial.ComponentSettingEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.partial.ComponentManageForm;
import com.osh.rvs.form.partial.ComponentSettingForm;
import com.osh.rvs.form.partial.PremakePartialForm;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.partial.ComponentManageService;
import com.osh.rvs.service.partial.ComponentSettingService;
import com.osh.rvs.service.partial.PremakePartialService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

public class ComponentManageAction extends BaseAction{
	private Logger log = Logger.getLogger(getClass());
	private ComponentManageService service = new ComponentManageService();
	private ComponentSettingService settingService = new ComponentSettingService();
	private PremakePartialService premakeService = new PremakePartialService();
	
	/**
	 * NS组件库存管理画面初始化处理
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("ComponentManageAction.init start");
		
		ModelService modelService = new ModelService();
		String mReferChooser = modelService.getOptions(conn);
		req.setAttribute("mReferChooser", mReferChooser);// 维修对象型号集合
		
		String models = settingService.getComponentSettings(conn);
		req.setAttribute("models", models);// component_setting表里所有型号
		Calendar   cal_1=Calendar.getInstance();
		cal_1.set(Calendar.DAY_OF_MONTH,1);
		req.setAttribute("first",DateUtil.toString(cal_1.getTime(), DateUtil.DATE_PATTERN));
		/* 分类 */
		req.setAttribute("Steps", CodeListUtils.getSelectOptions("component_step",null,""));
		req.setAttribute("gqOptions",CodeListUtils.getGridOptions("component_step"));

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
		
		if (privacies.contains(RvsConsts.PRIVACY_PROCESSING)) {
			req.setAttribute("is105", true);
		} else {
			req.setAttribute("is105", false);
		}
		
		if (privacies.contains(RvsConsts.PRIVACY_POSITION)) {
			req.setAttribute("is107", true);
		} else {
			req.setAttribute("is107", false);
		}
		
		actionForward = mapping.findForward(FW_INIT);
		log.info("ComponentManageAction.init end");
	}
	
	/**
	 * NS组件库存管理画面数据查询
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
		log.info("ComponentManageAction.search start");

		ComponentManageForm componentManageForm = (ComponentManageForm) form;

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		ComponentManageEntity componentManageEntity = new ComponentManageEntity();

		/* 表单复制到Bean */
		BeanUtil.copyToBean(componentManageForm, componentManageEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		/* NS组件库存管理情报查询 */
		List<ComponentManageForm> list = service.searchComponentManage(componentManageEntity, conn);
		listResponse.put("componentManage", list);
		listResponse.put("inlineDateCheck", RvsUtils.switchWorkDate(new Date(), -5));
		listResponse.put("errors", errors);

		returnJsonResponse(response, listResponse);
		log.info("ComponentManageAction.search end");
	}

	/**
	 * NS组件库存一览管理画面数据查询
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void searchSetting(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("ComponentManageAction.searchSetting start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		/* NS组件库存一览查询 */
		List<ComponentSettingForm> settingList = settingService.searchComponentSetting(conn);
		listResponse.put("componentSetting", settingList);
		
		listResponse.put("errors", errors);

		returnJsonResponse(response, listResponse);
		log.info("ComponentManageAction.searchSetting end");
	}
	
	/**
	 * 组件设置追加处理
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doinsertSetting(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("ComponentManageAction.doinsertSetting start");
		/* Ajax反馈对象 */
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		/* 表单合法性检查 */
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();
		
		// 型号是否存在判定
		int isExsistByModelId = settingService.isExitsByModelId(form, req.getSession(), conn);
		if (isExsistByModelId == 1) {
			MsgInfo msg = new MsgInfo();
			msg.setErrmsg("该型号已登录组件设置。");
			errors.add(msg);
		}
		
		// 识别代码判定是否存在
		int isExsistByIdentifyCode = settingService.isExitsByIdentifyCode(form, req.getSession(), conn);
		if (isExsistByIdentifyCode == 1) {
			MsgInfo msg = new MsgInfo();
			msg.setErrmsg("该识别代码已登录组件设置。");
			errors.add(msg);
		}
		
		// 预制对象零件List取得
		List<PremakePartialForm> premakes = null;
		if (errors.size() == 0) {
			premakes = this.premakePartialValidateEdit(req, conn, errors);
		}

		// 数据登录
		if (errors.size() == 0) {
			// 组件设置 (component_setting)数据插入
			settingService.insertSetting(form, req.getSession(), conn, errors);
			
			// 预制对象零件 (premake_partial)数据插入
			for (PremakePartialForm premake : premakes) {
				premakeService.insert(premake, errors, conn);
			}
		}

		/* 检查错误时报告错误信息 */
		callbackResponse.put("errors", errors);
		/* 返回Json格式响应信息 */
		returnJsonResponse(res, callbackResponse);

		log.info("ComponentManageAction.doinsertSetting end");
	}
	
	/**
	 * 组件代码check查询(autocomplete)
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
		log.info("ComponentManageAction.getAutocomplete start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		ComponentManageForm consumableManageForm = (ComponentManageForm) form;
		String code = consumableManageForm.getComponent_code();
		List<ComponentManageForm> list = service.getPartialAutoCompletes(code, conn);
		if(list.size() != 1 ){
			listResponse.put("reasult_flg", "0");
			
		}else{
			listResponse.put("reasult_flg", "1");
			ComponentManageForm result = new ComponentManageForm();
			result.setComponent_code(list.get(0).getComponent_code());
			result.setPartial_id(list.get(0).getPartial_id());
			result.setPartial_name(list.get(0).getPartial_name());
			listResponse.put("sPartialCode", result);// 零件集合
		}


		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("ComponentManageAction.getAutocomplete end");
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
	public void editSetting(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("ComponentManageAction.editSetting start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		ComponentSettingEntity settingEntity = new ComponentSettingEntity();
		/* 表单复制到Bean */
		BeanUtil.copyToBean(form, settingEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		/* 查询 */
		List<ComponentSettingForm> list = settingService.searchComponentSettingDetail(settingEntity, conn);
		if (list.size() > 0) {
			ComponentSettingForm returnForm = list.get(0);
			listResponse.put("returnForm", returnForm);
		}
		
		// NS 组件 子零件详细数据取得
		PremakePartialForm premakePartialForm = new PremakePartialForm();
		premakePartialForm.setModel_id(settingEntity.getModel_id());
		premakePartialForm.setStandard_flg("3");
		List<PremakePartialForm> premakePartials = premakeService.search(premakePartialForm, conn);
		listResponse.put("premakePartials", premakePartials);
		
		listResponse.put("errors", errors);

		returnJsonResponse(response, listResponse);
		log.info("ComponentManageAction.editSetting end");
	}

	/**
	 * 组件设置更新处理
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doUpdateSetting(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("ComponentManageAction.doUpdateSetting start");
		/* Ajax反馈对象 */
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		/* 表单合法性检查 */
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();
		
		// 型号是否存在判定
		int isExsistByModelId = settingService.isExitsByModelId(form, req.getSession(), conn);
		if (isExsistByModelId == 0) {
			MsgInfo msg = new MsgInfo();
			msg.setErrmsg("该型号组件设置已被删除。");
			errors.add(msg);
		}

		// 预制对象零件List取得
		List<PremakePartialForm> premakes = null;
		if (errors.size() == 0) {
			premakes = this.premakePartialValidateEdit(req, conn, errors);
		}

		// 数据登录
		if (errors.size() == 0) {
			// 组件设置 (component_setting)数据插入
			settingService.updateSetting(form, req.getSession(), conn, errors);
			
			// 预制对象零件 (premake_partial)数据删除
			premakeService.delete(premakes.get(0), conn);
			
			// 预制对象零件 (premake_partial)数据插入
			for (PremakePartialForm premake : premakes) {
				premakeService.insert(premake, errors, conn);
			}
		}

		/* 检查错误时报告错误信息 */
		callbackResponse.put("errors", errors);
		/* 返回Json格式响应信息 */
		returnJsonResponse(res, callbackResponse);

		log.info("ComponentManageAction.doUpdateSetting end");
	}

	/**
	 * 组件设置删除处理
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doDeleteSetting(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("ComponentManageAction.doDeleteSetting start");
		/* Ajax反馈对象 */
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		/* 表单合法性检查 */
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();
		
		// 型号是否存在判定
		int isExsistByModelId = settingService.isExitsByModelId(form, req.getSession(), conn);
		if (isExsistByModelId == 0) {
			MsgInfo msg = new MsgInfo();
			msg.setErrmsg("该型号组件设置已被删除。");
			errors.add(msg);
		}

		// 数据登录
		if (errors.size() == 0) {
			// 组件设置 (component_setting)数据插入
			settingService.deleteSetting(form, conn);
			
			// 预制对象零件 (premake_partial)数据删除
			premakeService.delete(form, conn);

		}

		/* 检查错误时报告错误信息 */
		callbackResponse.put("errors", errors);
		/* 返回Json格式响应信息 */
		returnJsonResponse(res, callbackResponse);

		log.info("ComponentManageAction.doDeleteSetting end");
	}

	/**
	 * 组件设置追加处理
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doInsertManage(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("ComponentManageAction.doInsertManage start");
		/* Ajax反馈对象 */
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		/* 表单合法性检查 */
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();
		
		// 数据登录
		if (errors.size() == 0) {
			// 组件库存管理 (component_manage)数据插入
			service.insertVirtualManage(form, req.getSession(), conn, errors);
		}

		/* 检查错误时报告错误信息 */
		callbackResponse.put("errors", errors);
		/* 返回Json格式响应信息 */
		returnJsonResponse(res, callbackResponse);

		log.info("ComponentManageAction.doInsertManage end");
	}

	/**
	 * 取得NS组件管理表内的所有库存编号
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void getNSempty(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("ComponentManageAction.getNSempty start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 执行检索
		List<String> stocks = service.getNSStock(conn);

		// 查询结果放入Ajax响应对象
		listResponse.put("heaps", stocks);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", new ArrayList<MsgInfo>());

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("ComponentManageAction.getNSempty end");
	}

	/**
	 * 子零件入库处理
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doPartialInstock(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("ComponentManageAction.doPartialInstock start");
		/* Ajax反馈对象 */
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		/* 表单合法性检查 */
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		// 数据登录
		if (errors.size() == 0) {
			
			ComponentManageEntity updateBean = new ComponentManageEntity();
			BeanUtil.copyToBean(form, updateBean, null);
			
			// 组件库存管理 (component_manage)数据更新
			// 子零件入库处理
			service.partialInstock(updateBean, req.getSession(), conn);
		}

		/* 检查错误时报告错误信息 */
		callbackResponse.put("errors", errors);
		/* 返回Json格式响应信息 */
		returnJsonResponse(res, callbackResponse);

		log.info("ComponentManageAction.doPartialInstock end");
	}

	/**
	 * 子零件出库处理
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doPartialOutstock(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("ComponentManageAction.doPartialOutstock start");

		/* Ajax反馈对象 */
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		/* 表单合法性检查 */
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		/* 查询 */
		ComponentSettingEntity settingEntity = new ComponentSettingEntity();
		BeanUtil.copyToBean(form, settingEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		List<ComponentSettingForm> list = settingService.searchComponentSettingDetail(settingEntity, conn);
		// 型号组件设置判定
		if (list.size() == 0) {
			MsgInfo msg = new MsgInfo();
			msg.setErrmsg("该型号组件设置已被删除。无法进行出库。");
			errors.add(msg);
		}

		// NS 组件流程取得
		String derivedId = service.getDerivedId(settingEntity.getModel_id(), conn);
		if (derivedId == null || derivedId.length() == 0) {
			MsgInfo msg = new MsgInfo();
			msg.setErrmsg("此型号的NS 组件流程不存在。无法进行出库。");
			errors.add(msg);
		}
		
		// 数据登录
		if (errors.size() == 0) {

			// 更新数据准备
			ComponentManageEntity updateBean = new ComponentManageEntity();
			BeanUtil.copyToBean(form, updateBean, null);
			
			// 序列号取得
			String getNewSerialNo = service.getNewSerialNo(list.get(0).getIdentify_code(), conn);
			updateBean.setSerial_no(getNewSerialNo);

			// 组件库存管理 (component_manage)数据更新
			// 子零件出库处理
			service.partialOutstock(updateBean, req.getSession(), conn);

			// 返回给前台一个消息：“子零件已经出库，作为序列号是”+ （刚才生成的序列号） + “的NS组件组装作业原料发放。”
			String resultMsg = "子零件已经出库，作为序列号是" + getNewSerialNo + "的NS组件组装作业原料发放。";
			callbackResponse.put("resultMsg", resultMsg);
			
			// PositionId 取得
			List<String> positionIds = service.getPositionIds(derivedId, conn);
			
			// 在（SOLO_PRODUCTION_FEATURE）表新建记录
			for (String positionId : positionIds) {
				SoloProductionFeatureEntity soloEntity = new SoloProductionFeatureEntity();
				soloEntity.setPosition_id(positionId);
				soloEntity.setModel_id(list.get(0).getModel_id());
				soloEntity.setModel_name(list.get(0).getModel_name());
				soloEntity.setSerial_no(getNewSerialNo);
				service.insertToSoloProductionFeature(soloEntity, conn);
			}
			
			// 在（POST_MESSAGE）表新建记录
			service.insertToPOST(resultMsg, positionIds, req.getSession(), conn);

		}
		
		/* 检查错误时报告错误信息 */
		callbackResponse.put("errors", errors);
		/* 返回Json格式响应信息 */
		returnJsonResponse(res, callbackResponse);

		log.info("ComponentManageAction.doPartialOutstock end");
	}

	/**
	 * 组件废弃处理
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doCancleManage(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("ComponentManageAction.doCancleManage start");
		/* Ajax反馈对象 */
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		/* 表单合法性检查 */
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		// 画面数据转换
		ComponentManageEntity componentBean = new ComponentManageEntity();
		BeanUtil.copyToBean(form, componentBean, null);
		
		// 独立工位操作记录处理条件设置
		SoloProductionFeatureEntity soloEntity = new SoloProductionFeatureEntity();
		soloEntity.setModel_id(componentBean.getModel_id());
		soloEntity.setSerial_no(componentBean.getSerial_no());
		
		// 如果step = 2时，查询是否存在独立工位操作记录
		if ("2".equals(componentBean.getStep())) {
			String positionId = service.getPosition(soloEntity, conn);
			if (positionId != null) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("content");
				error.setErrcode("info.linework.workingRemain");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.workingRemain", positionId));
				errors.add(error);
			}
		}
		
		// 数据更新
		if (errors.size() == 0) {
			if ("2".equals(componentBean.getStep())) {
				service.deleteSoloProductionFeature(soloEntity, conn);
			}
			// 组件库存管理 (component_manage)数据更新
			// 组件废弃处理
			service.cancleManage(componentBean, conn);
		}

		/* 检查错误时报告错误信息 */
		callbackResponse.put("errors", errors);
		/* 返回Json格式响应信息 */
		returnJsonResponse(res, callbackResponse);

		log.info("ComponentManageAction.doCancleManage end");
	}

	/**
	 * 重复性检查
	 * @param req
	 * @param conn
	 * @param errors
	 * @return 
	 */
	public List<PremakePartialForm> premakePartialValidateEdit(HttpServletRequest req, SqlSession conn,
			List<MsgInfo> errors) {
		List<PremakePartialForm> rets = new AutofillArrayList<>(PremakePartialForm.class);

		String modelId = req.getParameter("model_id");

		Map<String, String[]> parameterMap = req.getParameterMap();
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("partial".equals(entity)) {
					String column = m.group(2);
					Integer index = Integer.parseInt(m.group(3));
					rets.get(index).setModel_id(modelId);
					rets.get(index).setStandard_flg("3");
					
					String[] value = parameterMap.get(parameterKey);

					if ("partial_id".equals(column)) {
						rets.get(index).setPartial_id(value[0]);
					} else if ("partial_code".equals(column)) {
						rets.get(index).setCode(value[0]);
					} else if ("partial_name".equals(column)) {
						rets.get(index).setPartial_name(value[0]);
					} else if ("quantity".equals(column)) {
						rets.get(index).setQuantity(value[0]);
					}
				}
			}
		}

		if (rets.size() == 0) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("partial_id");
			error.setErrcode("validator.required.multidetail");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required.multidetail", "NS组件零件"));
			errors.add(error);
			return null;
		}

		// 比对稽核
		Set<String> checkSet = new HashSet<String>();
		for (PremakePartialForm ret : rets) {
			String checkKey = ret.getPartial_id();
			if (checkSet.contains(checkKey)) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("partial_id");
				error.setErrcode("info.peripheralInfectDevice.duplicatedInSeq");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.premake.partial.existed", ret.getCode()));
				errors.add(error);
				return null;
			}

			// 加入比对对象
			checkSet.add(checkKey);
		}

		return rets;
	}
	
	/**
	 * NS组件信息单打印
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void printNSInfoTicket(ActionMapping mapping, ActionForm form, HttpServletRequest req, 
			HttpServletResponse res, SqlSession conn) throws Exception{ 
		log.info("ComponentManageAction.printNSInfoTicket start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();
		// 画面数据转换
		ComponentManageEntity componentBean = new ComponentManageEntity();
		BeanUtil.copyToBean(form, componentBean, null);

		String filename = service.printNSInfoTicket(componentBean, conn);
		callbackResponse.put("tempFile", filename);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", infoes);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("ComponentManageAction.printNSInfoTicket end");
	}

	/**
	 * NS组件标签打印
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void printNSLabelTicket(ActionMapping mapping, ActionForm form, HttpServletRequest req, 
			HttpServletResponse res, SqlSession conn) throws Exception{ 
		log.info("ComponentManageAction.printNSLabelTicket start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();
		// 画面数据转换
		ComponentManageEntity componentBean = new ComponentManageEntity();
		BeanUtil.copyToBean(form, componentBean, null);

		String filename = service.printNsComponentIdTag(
				componentBean.getModel_name(),
				componentBean.getPartial_code(),
				componentBean.getSerial_no());
		
		callbackResponse.put("tempFile", filename);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", infoes);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("ComponentManageAction.printNSLabelTicket end");
	}
}
