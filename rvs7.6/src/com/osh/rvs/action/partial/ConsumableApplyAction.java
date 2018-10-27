/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：消耗品仓库管理记录<br>
 * @author 龚镭敏
 * @version 0.01
 */
package com.osh.rvs.action.partial;

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
import com.osh.rvs.bean.partial.ConsumableApplicationEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.partial.ConsumableApplicationForm;
import com.osh.rvs.service.partial.ConsumableApplicationDetailService;
import com.osh.rvs.service.partial.ConsumableApplyService;
import com.osh.rvs.service.partial.ConsumableListService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

public class ConsumableApplyAction extends BaseAction {

	private Logger log = Logger.getLogger(this.getClass());

	/**
	 * 
	 * 申请领用记录检索
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form,HttpServletRequest req, HttpServletResponse res,SqlSession conn) throws Exception {
		log.info("ConsumableApplyAction.search start");

		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> msgs = new ArrayList<MsgInfo>();

		ConsumableApplyService service = new ConsumableApplyService();
		List<ConsumableApplicationForm> apply_list = service.search(form, conn);// 申请领用记录一览

		listResponse.put("apply_list", apply_list);
		listResponse.put("errors", msgs);

		returnJsonResponse(res, listResponse);
		
		log.info("ConsumableApplyAction.search end");
	}

	/**
	 * 消耗品申请单详细
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void getDetail(ActionMapping mapping, ActionForm form,HttpServletRequest req, HttpServletResponse res,SqlSession conn)throws Exception{
		log.info("ConsumableApplyAction.getDetail start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> msgs = new ArrayList<MsgInfo>();
		
		
		ConsumableApplyService service = new ConsumableApplyService();
		ConsumableApplicationForm consumableApplicationForm = service.getConsumableApplication(form, conn);//消耗品订购单
		
		
		ConsumableApplicationDetailService consumableApplicationDetailService = new ConsumableApplicationDetailService();	
		List<ConsumableApplicationForm> list = consumableApplicationDetailService.search(form, conn);//消耗品订购单明细
		
		
		listResponse.put("errors", msgs);
		listResponse.put("consumableApplicationForm", consumableApplicationForm);
		listResponse.put("list", list);

		
		returnJsonResponse(res, listResponse);
		
		log.info("ConsumableApplyAction.getDetail end");
	}
	
	/**
	 * 发放
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdate(ActionMapping mapping, ActionForm form,HttpServletRequest req, HttpServletResponse res,SqlSessionManager conn)throws Exception{
		log.info("ConsumableApplyAction.doUpdate start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		
		Map<String,String[]> map = (Map<String,String[]>)req.getParameterMap();
		List<ConsumableApplicationForm> formList=new AutofillArrayList<>(ConsumableApplicationForm.class);

		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
		// 整理提交数据
		for (String parameterKey : map.keySet()) {
			 Matcher m = p.matcher(parameterKey);
			 if (m.find()) {
				 String entity = m.group(1);
				 if("consumable_application".equals(entity)){
					 String column = m.group(2);
					 int icounts = Integer.parseInt(m.group(3));
					 String[] value = map.get(parameterKey);
					 
					 if("consumable_application_key".equals(column)){
						 formList.get(icounts).setConsumable_application_key(value[0]);
					 }else if("partial_id".equals(column)){
						 formList.get(icounts).setPartial_id(value[0]);
					 }else if("available_inventory".equals(column)){
						 formList.get(icounts).setAvailable_inventory(value[0]);
					 }else if("waitting_quantity".equals(column)){
						 formList.get(icounts).setWaitting_quantity(value[0]);
					 }else if("supply_quantity".equals(column)){
						 formList.get(icounts).setSupply_quantity(value[0]);
					 }else if("openflg".equals(column)){
						 formList.get(icounts).setOpenflg(value[0]);
					 } else if("code".equals(column)){
						 formList.get(icounts).setCode(value[0]);
					 }else if("db_supply_quantity".equals(column)){
						 formList.get(icounts).setDb_supply_quantity(value[0]);
					 }else if("petitioner_id".equals(column)){
						 formList.get(icounts).setPetitioner_id(value[0]);
					 }else if("type".equals(column)){
						 formList.get(icounts).setType(value[0]);
					 }
				 }
			 }
		}
		
		for(ConsumableApplicationForm tempForm:formList){
			
			String str_supply_quantity = tempForm.getSupply_quantity();//发放数量
			if(CommonStringUtil.isEmpty(str_supply_quantity)){
				MsgInfo msg = new MsgInfo();
				msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "消耗品代码为: " + tempForm.getCode() + "的发放数量"));
				errors.add(msg);
				
				break;
			}

			Integer available_inventory = Integer.valueOf(tempForm.getAvailable_inventory());//有效库存
			Integer supply_quantity = Integer.valueOf(str_supply_quantity);
			
			if(supply_quantity > available_inventory){//发放数量 > 有效库存
				MsgInfo msg = new MsgInfo();
				msg.setErrmsg("消耗品代码为: " + tempForm.getCode() +"的发放数量大于有效库存");
				errors.add(msg);
				
				break;
			}

		}
		
		if(errors.size()==0){
			ConsumableApplyService service = new ConsumableApplyService();
			boolean success =  service.update(form,formList,conn,errors,req);
			listResponse.put("success", success);
		}
		
		listResponse.put("errors", errors);
		
		returnJsonResponse(res, listResponse);
		
		log.info("ConsumableApplyAction.doUpdate end");
	}

	/**
	 * 编辑画面初始化
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res, SqlSession conn)
			throws Exception {

		log.info("ConsumableApplyAction.edit start");
		String consumable_application_key = req.getParameter("consumable_application_key");

		// 取得登录者信息
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		String current_section_id = user.getSection_id();
		String current_line_id = user.getLine_id();

		ConsumableApplyService service = new ConsumableApplyService();

		// 取得申请单
		ConsumableApplicationEntity cmf = null;
		if (consumable_application_key == null) {
			cmf = service
					.getConsumableApplicationByLine(current_section_id, current_line_id, conn);
			if (cmf.getConsumable_application_key() == null) {
				cmf.setApplication_no(cmf.getApplication_no() + "(临时)");
			}

			// 取得申请工程
			req.setAttribute("line_name", user.getSection_name() + CommonStringUtil.nullToAlter(user.getLine_name(), ""));

		} else {
			List<ConsumableApplicationEntity> cmfs = service.searchEntities(form, conn);
			if (cmfs.size() > 0) {
				cmf = cmfs.get(0);

				// 取得申请工程
				req.setAttribute("line_name", cmf.getLine_name());
			}
		}

		req.setAttribute("cmf", cmf);

		actionForward = mapping.findForward("edit");

		log.info("ConsumableApplyAction.edit end");
	}

	/**
	 * 消耗品申请单读入
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void editInit(ActionMapping mapping, ActionForm form,HttpServletRequest req, HttpServletResponse res,SqlSession conn)throws Exception{
		log.info("ConsumableApplyAction.editInit start");

		// 取得登录者信息
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<MsgInfo> msgs = new ArrayList<MsgInfo>();

		String current_section_id = user.getSection_id();
		String current_position_id = user.getPosition_id();

		// 取得针对维修对象
		ConsumableApplyService service = new ConsumableApplyService();
		List<MaterialForm> performanceList = service.getPerformanceList(current_section_id, current_position_id, conn);
		for (MaterialForm material : performanceList) {
			material.setLevel(
					CodeListUtils.getValue("material_level", material.getLevel()));
		}
		result.put("performanceList", performanceList);

		ConsumableListService clService = new ConsumableListService();
		// 取得常用消耗品
		List<String[]> popularItems = clService.getPopularItems(conn);
		result.put("popularItems", CodeListUtils.getReferChooser(popularItems));

		// 取得已登录消耗品
		String operatorId = user.getOperator_id();
		// 判断自身是否有线长权限
		List<Integer> privacies = user.getPrivacies();
		if (privacies.contains(RvsConsts.PRIVACY_LINE)
				|| privacies.contains(RvsConsts.PRIVACY_QA_MANAGER)
				|| privacies.contains(RvsConsts.PRIVACY_RECEPT_EDIT)) {
			operatorId = "00000000000"; // 线长都可以修改
		}
		service.getConsumableList(req.getParameter("consumable_application_key"), result, operatorId, conn);

		LoginData loginData = (LoginData)req.getSession().getAttribute(RvsConsts.SESSION_USER);
		result.put("supplyOrderPrivacy", service.getSupplyOrderPrivacy(loginData));

		result.put("errors", msgs);

		returnJsonResponse(res, result);
		log.info("ConsumableApplyAction.editInit end");
	}

	public void getPartialForEdit(ActionMapping mapping, ActionForm form,HttpServletRequest req, HttpServletResponse res,SqlSession conn)throws Exception{
		log.info("ConsumableApplyAction.getPartialForEdit start");

		Map<String, Object> result = new HashMap<String, Object>();
		
		List<MsgInfo> msgs = new ArrayList<MsgInfo>();

		ConsumableApplyService service = new ConsumableApplyService();
		service.getDetailForEditByPartial(form, result, conn);

		result.put("errors", msgs);

		returnJsonResponse(res, result);
		log.info("ConsumableApplyAction.getPartialForEdit end");
	}


	/**
	 * 编辑/提交订购单
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doEdit(ActionMapping mapping, ActionForm form,HttpServletRequest req, HttpServletResponse res,SqlSessionManager conn)throws Exception{
		log.info("ConsumableApplyAction.doEdit start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		v.delete("flg");
		List<MsgInfo> errors = v.validate();

		Map<String,String[]> map = (Map<String,String[]>)req.getParameterMap();
		List<ConsumableApplicationForm> formList=new AutofillArrayList<>(ConsumableApplicationForm.class);
		
		if(errors.size()==0){
			Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
			// 整理提交数据
			for (String parameterKey : map.keySet()) {
				 Matcher m = p.matcher(parameterKey);
				 if (m.find()) {
					 String entity = m.group(1);
					 if("consumable_application".equals(entity)){
						String column = m.group(2);
						int icounts = Integer.parseInt(m.group(3));
						String[] value = map.get(parameterKey);
	
						if("partial_id".equals(column)){
							 formList.get(icounts).setPartial_id(value[0]);
						 }else if("petitioner_id".equals(column)){
							 formList.get(icounts).setPetitioner_id(value[0]);
						 }else if("apply_method".equals(column)){
							 formList.get(icounts).setApply_method(value[0]);
						 }else if("apply_quantity".equals(column)){
							 formList.get(icounts).setApply_quantity(value[0]);
						 }else if("pack_method".equals(column)){
							 formList.get(icounts).setPack_method(value[0]);
						 }else if("flg".equals(column)){ // 处理标记
							 formList.get(icounts).setFlg(value[0]);
						 }else if("cut_length".equals(column)){
							 formList.get(icounts).setCut_length(value[0]);
						 }
					 }
				 }
			}
	
			// check detail
			for(ConsumableApplicationForm tempForm : formList){
				Validators vDetail = BeanUtil.createBeanValidators(tempForm, BeanUtil.CHECK_TYPE_ALL);
				vDetail.only("partial_id", "apply_method", "apply_quantity", "pack_method");
				errors.addAll(vDetail.validate());
			}
		}

		if(errors.size()==0){
			ConsumableApplyService service = new ConsumableApplyService();
			service.edit(form,formList,conn,errors,req);
		}
		
		listResponse.put("errors", errors);
		
		returnJsonResponse(res, listResponse);
		
		log.info("ConsumableApplyAction.doEdit end");
	}

}
