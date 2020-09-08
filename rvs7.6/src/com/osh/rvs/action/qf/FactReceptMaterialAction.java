package com.osh.rvs.action.qf;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.common.ReverseResolution;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.master.ModelForm;
import com.osh.rvs.form.qf.AfProductionFeatureForm;
import com.osh.rvs.form.qf.FactMaterialForm;
import com.osh.rvs.form.qf.FactReceptMaterialForm;
import com.osh.rvs.form.qf.MaterialTagForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.service.AcceptFactService;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.ProductionFeatureService;
import com.osh.rvs.service.qf.AcceptanceService;
import com.osh.rvs.service.qf.FactMaterialService;
import com.osh.rvs.service.qf.FactReceptMaterialService;
import com.osh.rvs.service.qf.MaterialTagService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

/**
 * 维修品实物受理/测漏
 * 
 * @Description
 * @author dell
 * @date 2020-8-24 上午10:06:20
 */
public class FactReceptMaterialAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());
	/** 间接人员作业信息 **/
	private AcceptFactService acceptFactService = new AcceptFactService();
	/** 现品维修对象 **/
	private FactMaterialService factMaterialService = new FactMaterialService();
	/** 维修对象 **/
	private MaterialService materialService = new MaterialService();
	/** 维修对象属性标签 **/
	private MaterialTagService materialTagService = new MaterialTagService();
	private FactReceptMaterialService factReceptMaterialService = new FactReceptMaterialService();
	private ProductionFeatureService featureService = new ProductionFeatureService();
	private AcceptanceService acceptanceService = new AcceptanceService();
	
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
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("FactReceptMaterialAction.init end");

		// 取得登录用户权限
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();

		if (privacies.contains(RvsConsts.PRIVACY_LINE)) {
			req.setAttribute("role", "lineleader");
		} else {
			req.setAttribute("role", "none");
		}

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("FactReceptMaterialAction.init end");
	}

	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("FactReceptMaterialAction.search start");

		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		List<FactReceptMaterialForm> materialList = factReceptMaterialService.searchReceptMaterial(conn);
		callbackResponse.put("materialList", materialList);
		
		List<FactReceptMaterialForm> tempList = factReceptMaterialService.searchFactReceptMaterialTemp(form, conn);
		callbackResponse.put("tempList", tempList);
		
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();
		
		Calendar start = Calendar.getInstance();
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		start.set(Calendar.MILLISECOND, 0);
		
		Calendar end = Calendar.getInstance();
		end.add(Calendar.DATE, 1);
		end.set(Calendar.HOUR_OF_DAY, 0);
		end.set(Calendar.MINUTE, 0);
		end.set(Calendar.SECOND, 0);
		end.set(Calendar.MILLISECOND, 0);
		
		FactMaterialForm factMaterialForm = new FactMaterialForm();
		factMaterialForm.setProduction_type("102");
		factMaterialForm.setAction_time_start(DateUtil.toString(start.getTime(), DateUtil.DATE_PATTERN));
		factMaterialForm.setAction_time_end(DateUtil.toString(end.getTime(), DateUtil.DATE_PATTERN));
		
		if (!privacies.contains(RvsConsts.PRIVACY_LINE)) {
			factMaterialForm.setOperator_id(user.getOperator_id());
		}
		//当天完成单数
		int todayFinishedNum = factMaterialService.countFinished(factMaterialForm, conn);
		callbackResponse.put("todayFinishedNum", todayFinishedNum);
		
		String nextDay = RvsUtils.getNextWorkday(conn);
		callbackResponse.put("nextDay", nextDay);
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);

		log.info("FactReceptMaterialAction.search end");
	}
	
	/**
	 * 更新
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("FactReceptMaterialAction.doUpdate start");
		
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		// 当前登录者
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		// 根据操作者ID查找未结束作业信息
		AfProductionFeatureForm productionForm = acceptFactService.getUnFinish(user.getOperator_id(), conn);
		
		if(productionForm == null){
			MsgInfo error = new MsgInfo();
			error.setErrcode("info.linework.workingLost");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.workingLost"));
			errors.add(error);
		} else if(!"102".equals(productionForm.getProduction_type())){
			MsgInfo error = new MsgInfo();
			error.setErrcode("info.linework.productionTypeNotMatch");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.productionTypeNotMatch",CodeListUtils.getValue("qf_production_type", productionForm.getProduction_type())));
			errors.add(error);
		}
		
		if(errors.size() == 0){
			FactReceptMaterialForm factReceptMaterialForm = (FactReceptMaterialForm)form;
			String materialID = factReceptMaterialForm.getMaterial_id();
			
			//现品维修对象作业
			FactMaterialForm factMaterialForm = new FactMaterialForm();
			factMaterialForm.setMaterial_id(materialID);
			factMaterialForm.setProduction_type("102");
			//查询当前维修对象有没有做过（维修品实物受理/测漏）
			List<FactMaterialForm> fMaterialList =  factMaterialService.search(factMaterialForm, conn);
			if(fMaterialList.size() == 0){
				//新建现品维修对象作业
				factMaterialForm.setAf_pf_key(productionForm.getAf_pf_key());
				factMaterialService.insert(factMaterialForm, conn);
			}
			
			Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
			List<MaterialTagForm> pageList = new AutofillArrayList<MaterialTagForm>(MaterialTagForm.class);
			Map<String, String[]> parameters = req.getParameterMap();
			String tagTypes = "";
			
			for (String parameterKey : parameters.keySet()) {
				Matcher m = p.matcher(parameterKey);
				if (m.find()) {
					String entity = m.group(1);
					if ("material_tag".equals(entity)) {
						String column = m.group(2);
						int icounts = Integer.parseInt(m.group(3));
						String[] value = parameters.get(parameterKey);
						if ("tag_type".equals(column)) {
							pageList.get(icounts).setTag_type(value[0]);
							tagTypes += value[0] + ",";
						}
						pageList.get(icounts).setMaterial_id(materialID);
					}
				}
			}
			
			//维修对象属性标签
			materialTagService.deleteByMaterialId(materialID, conn);
			for(MaterialTagForm materialTagForm : pageList){
				materialTagService.insert(materialTagForm, conn);
			}
			
			//维修对象属性标签
			if(tagTypes.contains("4")){//消毒
				// 发送至消毒
				ProductionFeatureEntity entity = new ProductionFeatureEntity();
				entity.setMaterial_id(materialID);
				entity.setPosition_id("10");
				entity.setSection_id(user.getSection_id());
				entity.setPace(0);
				entity.setOperate_result(0);
				entity.setRework(0);
				entity.setOperator_id(user.getOperator_id());
				featureService.insert(entity, conn);
				// 发送完毕后，受理时间覆盖导入时间
				acceptanceService.updateFormalReception(new String[]{materialID}, conn);
			} else if(tagTypes.contains("5")){//灭菌
				ProductionFeatureEntity entity = new ProductionFeatureEntity();
				entity.setMaterial_id(materialID);
				entity.setPosition_id("11");
				entity.setPace(0);
				entity.setSection_id(user.getSection_id());
				entity.setOperate_result(0);
				entity.setRework(0);
				entity.setOperator_id(user.getOperator_id());
				featureService.insert(entity, conn);
				// 发送完毕后，受理时间覆盖导入时间
				acceptanceService.updateFormalReception(new String[]{materialID}, conn);
			}
		}
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("FactReceptMaterialAction.doUpdate end");
	}
	
	/**
	 * 维修品实物受理/测漏 临时表更新
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doTempUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn)throws Exception {
		log.info("FactReceptMaterialAction.doTempUpdate start");
		
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		// 当前登录者
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		// 根据操作者ID查找未结束作业信息
		AfProductionFeatureForm productionForm = acceptFactService.getUnFinish(user.getOperator_id(), conn);
		
		if(productionForm == null){
			MsgInfo error = new MsgInfo();
			error.setErrcode("info.linework.workingLost");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.workingLost"));
			errors.add(error);
		} else if(!"102".equals(productionForm.getProduction_type())){
			MsgInfo error = new MsgInfo();
			error.setErrcode("info.linework.productionTypeNotMatch");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.productionTypeNotMatch",CodeListUtils.getValue("qf_production_type", productionForm.getProduction_type())));
			errors.add(error);
		}
		
		if(errors.size() == 0){
			//表单合法性检查
			Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
			v.add("serial_no", v.required("机身号"));
			errors = v.validate();
			
			if(errors.size() == 0){
				FactReceptMaterialForm factReceptMaterialForm = (FactReceptMaterialForm)form;
				String factReceptId = factReceptMaterialForm.getFact_recept_id();
				
				if(CommonStringUtil.isEmpty(factReceptMaterialForm.getModel_id())){
					////找不到型号，型号ID设为00000000000
					factReceptMaterialForm.setModel_id("00000000000");
				}
				
				//material重复check
				String existId1 = materialService.checkModelSerialNo(factReceptMaterialForm, conn);
				if (existId1 != null) {
					MsgInfo info = new MsgInfo();
					info.setErrcode("dbaccess.columnNotUnique");
					info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "型号 + 机身号", factReceptMaterialForm.getModel_id() +","+ factReceptMaterialForm.getSerial_no(), "维修对象"));
					errors.add(info);
				}
				
				if(errors.size() == 0){
					if(!"00000000000".equals(factReceptMaterialForm.getModel_id()) && !factReceptMaterialForm.getSerial_no().startsWith("临")){
						//临时表重复check
						List<FactReceptMaterialForm> list = factReceptMaterialService.searchFactReceptMaterialTemp(factReceptMaterialForm, conn);
						//型号机身号不能重复
						if(list.size() == 1 && !factReceptId.equals(list.get(0).getFact_recept_id())){
							MsgInfo info = new MsgInfo();
							info.setErrcode("dbaccess.columnNotUnique");
							info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "型号 + 机身号", factReceptMaterialForm.getModel_name() +","+ factReceptMaterialForm.getSerial_no(), "维修对象"));
							errors.add(info);
						}
					}
					
					if(errors.size() == 0){
						//更新临时表fact_recept_material_temp
						factReceptMaterialService.updateFactReceptMaterialTemp(factReceptMaterialForm, conn);
					}
				}
			}
		}
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("FactReceptMaterialAction.doTempUpdate end");
	}
	
	
	/**
	 * 型号检查
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void checkModelName(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("FactReceptMaterialAction.checkModelName start");
		
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		ModelService modelService = new ModelService();
		
		FactReceptMaterialForm factReceptMaterialForm = (FactReceptMaterialForm)form;
		//型号名称
		String modelName = factReceptMaterialForm.getModel_name();
		
		//型号不为空
		if(!CommonStringUtil.isEmpty(modelName)){
			String modelId = ReverseResolution.getModelByName(modelName, conn);
			
			//型号不存在
			if(CommonStringUtil.isEmpty(modelId)){
				MsgInfo error = new MsgInfo();
				error.setErrcode("dbaccess.recordNotExist");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist","型号【" + modelName  + "】"));
				errors.add(error);
			} else {
				ModelForm modelForm = modelService.getDetail(modelId, conn);
				callbackResponse.put("modelForm", modelForm);
			}
		}
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("FactReceptMaterialAction.checkModelName end");
	}
	
	/**
	 * 新建
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doInsert(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("FactReceptMaterialAction.doInsert start");
		
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		// 当前登录者
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		// 根据操作者ID查找未结束作业信息
		AfProductionFeatureForm productionForm = acceptFactService.getUnFinish(user.getOperator_id(), conn);
		
		if(productionForm == null){
			MsgInfo error = new MsgInfo();
			error.setErrcode("info.linework.workingLost");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.workingLost"));
			errors.add(error);
		} else if(!"102".equals(productionForm.getProduction_type())){
			MsgInfo error = new MsgInfo();
			error.setErrcode("info.linework.productionTypeNotMatch");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.productionTypeNotMatch",CodeListUtils.getValue("qf_production_type", productionForm.getProduction_type())));
			errors.add(error);
		}
		
		if(errors.size() == 0){
			FactReceptMaterialForm factReceptMaterialForm = (FactReceptMaterialForm)form;
			String flag = factReceptMaterialForm.getFlag();
			
			factReceptMaterialForm.setAf_pf_key(productionForm.getAf_pf_key());
			
			//表单合法性检查
			Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
			v.add("serial_no", v.required("机身号"));
			if("1".equals(flag)){//周边
				v.add("model_name", v.required("型号"));
			}
			errors = v.validate();
			
			if(errors.size() == 0){
				if("0".equals(factReceptMaterialForm.getFlag())){//维修品
					if(CommonStringUtil.isEmpty(factReceptMaterialForm.getModel_id())){
						////找不到型号，型号ID设为00000000000
						factReceptMaterialForm.setModel_id("00000000000");
					}
					
					//material重复check
					String existId1 = materialService.checkModelSerialNo(factReceptMaterialForm, conn);
					if (existId1 != null) {
						MsgInfo info = new MsgInfo();
						info.setErrcode("dbaccess.columnNotUnique");
						info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "型号 + 机身号", factReceptMaterialForm.getModel_id() +","+ factReceptMaterialForm.getSerial_no(), "维修对象"));
						errors.add(info);
					}
					
					if(errors.size() == 0){
						if(!"00000000000".equals(factReceptMaterialForm.getModel_id()) && !factReceptMaterialForm.getSerial_no().startsWith("临")){
							//临时表重复check
							List<FactReceptMaterialForm> list = factReceptMaterialService.searchFactReceptMaterialTemp(factReceptMaterialForm, conn);
							//型号机身号不能重复
							if(list.size() == 1){
								MsgInfo info = new MsgInfo();
								info.setErrcode("dbaccess.columnNotUnique");
								info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "型号 + 机身号", factReceptMaterialForm.getModel_name() +","+ factReceptMaterialForm.getSerial_no(), "维修对象"));
								errors.add(info);
							}
						}
					}
					
					if(errors.size() == 0){
						//插入临时表fact_recept_material_temp
						factReceptMaterialService.insertFactReceptMaterialTemp(factReceptMaterialForm, conn);
					}
				} else {//备品（其他）
					//material重复check
					String existId1 = materialService.checkModelSerialNo(factReceptMaterialForm, conn);
					if (existId1 != null) {
						MsgInfo info = new MsgInfo();
						info.setErrcode("dbaccess.columnNotUnique");
						info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "型号 + 机身号", factReceptMaterialForm.getModel_id() +","+ factReceptMaterialForm.getSerial_no(), "维修对象"));
						errors.add(info);
					}
					
					if(errors.size() == 0){
						materialService.insert(factReceptMaterialForm, conn);
						CommonMapper cDao = conn.getMapper(CommonMapper.class);
						String insertId = cDao.getLastInsertID();
						
						//新建现品维修对象作业
						FactMaterialForm factMaterialForm = new FactMaterialForm();
						factMaterialForm.setAf_pf_key(productionForm.getAf_pf_key());
						factMaterialForm.setMaterial_id(insertId);
						factMaterialService.insert(factMaterialForm, conn);
						
						//新建维修对象属性标签
						String [] arrTag = factReceptMaterialForm.getTag_types().split(",");
						for(int i = 0;i<arrTag.length;i++){
							MaterialTagForm materialTagForm = new MaterialTagForm();
							materialTagForm.setMaterial_id(insertId);
							materialTagForm.setTag_type(arrTag[i]);
							materialTagService.insert(materialTagForm, conn);
						}
					}
				}
			}
		}
		
		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("FactReceptMaterialAction.doInsert end");
	}
}
