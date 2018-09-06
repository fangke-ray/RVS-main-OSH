package com.osh.rvs.action.partial;

import java.math.BigDecimal;
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

import com.osh.rvs.bean.partial.ConsumableListEntity;
import com.osh.rvs.bean.partial.ConsumableSupplyRecordEntity;
import com.osh.rvs.form.partial.ConsumableSupplyRecordForm;
import com.osh.rvs.service.partial.ConsumableListService;
import com.osh.rvs.service.partial.ConsumableSupplyRecordService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.validator.DoubleScaleValidator;
import framework.huiqing.common.util.validator.DoubleTypeValidator;
import framework.huiqing.common.util.validator.MaxlengthValidator;
import framework.huiqing.common.util.validator.Validators;

/**
 * @Description: 消耗品发放记录
 * @author liuxb
 * @date 2018-5-17 下午5:12:32
 */
public class ConsumableSupplyRecordAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());
	
	/**
	 * 画面初始表示处理
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("ConsumableSupplyRecordAction.init start");
		
		// 申请方式
		String strApplyMethod = CodeListUtils.getSelectOptions("consumable_apply_method",null,"");
		strApplyMethod += "<option value=\"5\">替代</option>";
		
		req.setAttribute("sApplyMethod", strApplyMethod);
		req.setAttribute("goApplyMethod",CodeListUtils.getGridOptions("consumable_apply_method") + ";5:替代");
		
		// 消耗品分类
		req.setAttribute("sConsumableType", CodeListUtils.getSelectOptions("consumable_type",null,""));
		req.setAttribute("goConsumableType",CodeListUtils.getGridOptions("consumable_type"));
		
		Calendar cal = Calendar.getInstance();
		
		// 发放开始时间
		cal.set(Calendar.DAY_OF_MONTH, 1);
		req.setAttribute("startDate", DateUtil.toString(cal.getTime(), DateUtil.DATE_PATTERN));
		
		// 发放结束时间
		cal.set(Calendar.DAY_OF_MONTH, cal.getMaximum(Calendar.DAY_OF_MONTH));
		req.setAttribute("endDate", DateUtil.toString(cal.getTime(), DateUtil.DATE_PATTERN));
		
		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);
		
		log.info("ConsumableSupplyRecordAction.init end");
	}
	
	/**
	 * 检索
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn)throws Exception{
		log.info("ConsumableSupplyRecordAction.search start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		v.add("supply_time_start", v.required("发放开始时间"));
		v.add("supply_time_end", v.required("发放结束时间"));
		
		List<MsgInfo> errors = v.validate();
		if(errors.size() == 0){
			ConsumableSupplyRecordService service = new ConsumableSupplyRecordService();
			List<ConsumableSupplyRecordForm> list = service.search(form, conn,req);
			
			listResponse.put("finished", list);
		}
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		log.info("ConsumableSupplyRecordAction.search end");
	}
	
	/**
	 * 下载
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void report(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("ConsumableSupplyRecordAction.report start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		String fileName ="消耗品发放记录一览.xlsx";
		
		ConsumableSupplyRecordService service = new ConsumableSupplyRecordService();
		String filePath = service.createConsumableSupplyRecord(request);
		
		listResponse.put("fileName", fileName);
		listResponse.put("filePath", filePath);
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("ConsumableSupplyRecordAction.report end");
	}
	
	/**
	 * 消耗量Top10导出
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void reportTopTen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("ConsumableSupplyRecordAction.reportTopTen start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String isContinue = request.getParameter("continue");
		
		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		v.add("supply_time_start", v.required("发放开始时间"));
		v.add("supply_time_end", v.required("发放结束时间"));
		errors = v.validate();
		
		if(errors.size() == 0){
			ConsumableSupplyRecordService service = new ConsumableSupplyRecordService();
			
			service.consumableTopTenValidate(form,request,conn, isContinue, errors);
			
			if(errors.size() == 0){
				//存在消耗目标不存在的消耗品信息
				
				@SuppressWarnings("unchecked")
				List<ConsumableSupplyRecordEntity> consumptQuotaEmptyList = (List<ConsumableSupplyRecordEntity>)request.getAttribute("consumptQuotaEmptyList");
				
				if(consumptQuotaEmptyList!=null && consumptQuotaEmptyList.size() > 0){
					listResponse.put("consumptQuotaEmptyList", consumptQuotaEmptyList);
				}else{
					String fileName ="消耗量Top10.xlsx";
					String filePath = service.createConsumableTopTen((ConsumableSupplyRecordForm)form,request);
					
					listResponse.put("fileName", fileName);
					listResponse.put("filePath", filePath);
				}
			}
		}
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("ConsumableSupplyRecordAction.reportTopTen end");
	}
	
	/**
	 * 更新消耗品目标值
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdateConsumptQuota(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("ConsumableSupplyRecordAction.doUpdateConsumptQuota start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		Map<String,String[]> map = (Map<String,String[]>)request.getParameterMap();
		List<ConsumableSupplyRecordForm> formList=new AutofillArrayList<>(ConsumableSupplyRecordForm.class);
		
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
		// 整理提交数据
		for (String parameterKey : map.keySet()) {
			 Matcher m = p.matcher(parameterKey);
			 if (m.find()) {
				 String entity = m.group(1);
				 if("consumable_manage".equals(entity)){
					 String column = m.group(2);
					 int icounts = Integer.parseInt(m.group(3));
					 String[] value = map.get(parameterKey);
					 
					 if("partial_id".equals(column)){
						 formList.get(icounts).setPartial_id(value[0]);
					 }else if("consumpt_quota".equals(column)){
						 formList.get(icounts).setConsumpt_quota(value[0]);
					 }else if("partial_code".equals(column)){
						 formList.get(icounts).setPartial_code(value[0]);
					 }
				 }
			 }
		}
		
		DoubleTypeValidator dtv = new DoubleTypeValidator("消耗目标");
		MaxlengthValidator mv = new MaxlengthValidator("消耗目标", 5);
		DoubleScaleValidator dsv = new DoubleScaleValidator(5, 2, "消耗目标");
		for(ConsumableSupplyRecordForm f:formList){
			// 检索条件表单合法性检查
			Validators v = BeanUtil.createBeanValidators(f, BeanUtil.CHECK_TYPE_PASSEMPTY);
			v.add("consumpt_quota", dtv, mv, dsv);
			
			errors.addAll(v.validate());
		}

		if(errors.size() == 0){
			ConsumableListService consumableListService = new ConsumableListService();
			for(ConsumableSupplyRecordForm f:formList){
				if (CommonStringUtil.isEmpty(f.getConsumpt_quota())) continue;

				ConsumableListEntity consumableListEntity = new ConsumableListEntity();
				// 消耗品 ID
				consumableListEntity.setPartial_id(Integer.valueOf(f.getPartial_id()));
				
				// 目标值
				consumableListEntity.setConsumpt_quota(new BigDecimal(f.getConsumpt_quota()));
				
				consumableListService.updateConsumptQuota(consumableListEntity, conn);
			}
		}
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
				
		log.info("ConsumableSupplyRecordAction.doUpdateConsumptQuota end");
	}
	
}
