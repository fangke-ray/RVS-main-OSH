package com.osh.rvs.action.qa;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

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
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.inline.SoloProductionFeatureEntity;
import com.osh.rvs.bean.qa.ServiceRepairManageEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.qa.ServiceRepairManageForm;
import com.osh.rvs.service.PauseFeatureService;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.equipment.DeviceJigLoanService;
import com.osh.rvs.service.inline.PositionPanelService;
import com.osh.rvs.service.qa.ServiceRepairManageService;
import com.osh.rvs.service.qa.ServiceRepairRefereeService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

/**
 * 待判断维修品
 * @author lxb
 *
 */
public class ServiceRepairRefereeAction extends BaseAction{
	private Logger log=Logger.getLogger(getClass());
	private ServiceRepairRefereeService service=new ServiceRepairRefereeService();
	private ServiceRepairManageService manageService = new ServiceRepairManageService();

	/**初始化
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("ServiceRepairRefereeAction.init start");

		// 取得用户信息
		HttpSession session = request.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 取得待点检信息
		if (user.getPosition_id() == null) {
			user.setProcess_code("601");
			user.setPosition_id(RvsConsts.POSITION_QA_601);

			user.setLine_id("00000000015");
		}

		//产品分类
		request.setAttribute("sKind", CodeListUtils.getSelectOptions("qa_category_kind", null, ""));
		
		//维修类别
		request.setAttribute("sQaMaterialServiceRepair", CodeListUtils.getSelectOptions("qa_material_service_repair", null, ""));
		request.setAttribute("goQaMaterialServiceRepair",CodeListUtils.getGridOptions("qa_material_service_repair"));
		//有无偿
		request.setAttribute("sServiceFreeFlg", CodeListUtils.getSelectOptions("service_free_flg",null,""));
		
		//维修站
		request.setAttribute("sWorkshop", CodeListUtils.getSelectOptions("workshop_new", null, ""));
		request.setAttribute("goQaMaterialServiceRepair",CodeListUtils.getGridOptions("qa_material_service_repair"));

		//质量判定
		request.setAttribute("sQuality_judgment", CodeListUtils.getSelectOptions("quality_judgment", null, ""));
		
		//发行QIS
		request.setAttribute("sQis_isuse", CodeListUtils.getSelectOptions("qis_isuse", null, ""));

		// 判断是否动物实验用维修品工位
		if (PositionService.getPositionUnitizeds(conn).containsKey(user.getPosition_id())) {
			request.setAttribute("unitizeds", "true");
		}

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("ServiceRepairRefereeAction.init end");
	}

	/**
	 * 数据一览
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void jsinit(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("ServiceRepairRefereeAction.jsinit start");

		// Ajax响应对象
		Map<String,Object>callbackResponse=new HashMap<String,Object>();

		// 取得用户信息
		HttpSession session = request.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		String operator_id = user.getOperator_id();

		// 取得待点检信息
		String section_id = "00000000009";
		if (user.getPosition_id() == null) {
			user.setProcess_code("601");
			user.setPosition_id(RvsConsts.POSITION_QA_601);

			user.setLine_id("00000000015");
		}
		String position_id = user.getPosition_id();
		String line_id = user.getLine_id();
		boolean isUnitizeds = PositionService.getPositionUnitizeds(conn).containsKey(position_id);

		// 未着手数据
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();
		// 分种类
		String kind = null;
		if (!isUnitizeds) {
			kind = ("601".equals(user.getProcess_code()) ? "0" : "7");			
		}
		List<ServiceRepairManageForm> sList=service.searchServiceRepair(conn, kind,
				isUnitizeds, null);
		callbackResponse.put("errors", infoes);
		callbackResponse.put("serviceRepairList", sList);

		// 暂停/中断中数据
		List<ServiceRepairManageForm> sListPaused = service.searchServiceRepairPaused(user.getPosition_id(), conn);
		callbackResponse.put("serviceRepairPausedList", sListPaused);

		PositionPanelService ppservice = new PositionPanelService();

		// 设定待点检信息
		String infectString = ppservice.checkPositionInfectWorkOnPass(
				section_id, position_id, line_id, operator_id, conn, callbackResponse);
		infectString += ppservice.getAbnormalWorkStateByOperator(operator_id, conn);

		callbackResponse.put("infectString", infectString);
		if (infectString.indexOf("限制工作") >= 0) {
			callbackResponse.put("workstauts", "-1");
		} else {
			// 判断是否有进行中作业
			ServiceRepairManageForm resultForm = service.checkWorkingServiceRepair(operator_id, conn, infoes);
	
			callbackResponse.put("resultForm", resultForm);
	
			ServiceRepairManageService serviceRepairManageService=new ServiceRepairManageService();
			List<String> ranklist=serviceRepairManageService.getRankAutoCompletes(conn);//等级集合
			callbackResponse.put("sRank", ranklist.toArray());
	
			// 设定暂停选项
			String pauseOptions = "";
	
			pauseOptions += PauseFeatureService.getPauseReasonSelectOptions();
			callbackResponse.put("pauseOptions", pauseOptions);
			callbackResponse.put("pauseComments", PauseFeatureService.getPauseReasonSelectComments());

			// 工步
			String stepOptions = service.getSteps();
			callbackResponse.put("stepOptions", stepOptions);

			// 判断作业者是否借用了设备工具
			if (!isUnitizeds) {
				if (session.getAttribute("DJ_LOANING") != null) {
					DeviceJigLoanService djlService = new DeviceJigLoanService();
					String loaning = djlService.checkLoaningNowText(user.getOperator_id(), conn);
					if (loaning != null) {
						callbackResponse.put("djLoaning", loaning);
					}
				}
			}
		}

		returnJsonResponse(response, callbackResponse);

		log.info("ServiceRepairRefereeAction.jsinit end");
	}

	/**
	 * 数据一览
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("ServiceRepairRefereeAction.search start");
		
		// Ajax响应对象
		Map<String,Object>listResponse=new HashMap<String,Object>();

		// 取得用户信息
		HttpSession session = request.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 未着手数据
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();
		// 分种类
		String kind = null;
		boolean isUnitizeds = PositionService.getPositionUnitizeds(conn).containsKey(user.getPosition_id());
		if (!isUnitizeds) {
			kind = ("601".equals(user.getProcess_code()) ? "0" : "7");			
		}
		List<ServiceRepairManageForm> sList=service.searchServiceRepair(conn, kind,
				isUnitizeds, null);
		listResponse.put("errors", infoes);
		listResponse.put("serviceRepairList", sList);

		// 暂停/中断中数据
		List<ServiceRepairManageForm> sListPaused = service.searchServiceRepairPaused(user.getPosition_id(), conn);
		listResponse.put("serviceRepairPausedList", sListPaused);
		returnJsonResponse(response, listResponse);

		log.info("ServiceRepairRefereeAction.search end");
	}

	/**
	 * 扫描
	 * @param mapping
	 * @param form 接受表单传值
	 * @param request
	 * @param response
	 * @param conn
	 */
	public void doscan(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("ServiceRepairRefereeAction.doscan start");
		
		// Ajax响应对象
		Map<String,Object>listResponse=new HashMap<String,Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		//检查合法性
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		msgInfos = v.validate();

		// 取得用户信息
		HttpSession session = request.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 如果检查输入没有问题
		if(msgInfos.size()==0){
			//根据material_id找出扫描对象
			ServiceRepairManageForm servicerepairManageForm=(ServiceRepairManageForm)form;
			String material_id = servicerepairManageForm.getMaterial_id();
			ServiceRepairManageEntity resultEntity=service.checkServiceRepairManageExist(material_id, conn, msgInfos);

			if (user.getPosition_id() == null) {
				user.setProcess_code("601");
				user.setPosition_id(RvsConsts.POSITION_QA_601);

				user.setLine_id("00000000015");
			}

			//如果扫描对象存在
			if(msgInfos.size()==0){
				// 建立页面返回表单
				ServiceRepairManageForm resultForm = new ServiceRepairManageForm();
				// 复制对象数据到表单
				BeanUtil.copyToForm(resultEntity, resultForm, CopyOptions.COPYOPTIONS_NOEMPTY);

				// 分种类
				String kind = null;
				boolean isUnitizeds = PositionService.getPositionUnitizeds(conn).containsKey(user.getPosition_id());
				if (!isUnitizeds) {
					kind = ("601".equals(user.getProcess_code()) ? "0" : "7");			
				}
				List<ServiceRepairManageForm> sList=service.searchServiceRepair(conn, kind, isUnitizeds, material_id);
				//如果扫描对象不在等待区域
				if(sList.size()<=0){
					// 判断在暂停区存在
					List<ServiceRepairManageEntity> sWList = service.searchPausedServiceRepair(conn,material_id);

					if(sWList.size()==0){
						MsgInfo info=new MsgInfo();
						info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.notInWaiting", "待判定维修品"));
						info.setComponentid("scanner_inputer");
						info.setErrcode("info.linework.notInWaiting");
						msgInfos.add(info);
					} else {
						service.createProductionFeature(conn, resultEntity, user.getPosition_id(), user.getOperator_id(), user.getSection_id(), RvsConsts.OPERATE_RESULT_WORKING);
						resultForm.setMention(service.getWorkedSteps(resultEntity, conn));
						listResponse.put("resultForm", resultForm);
					}
				}else{
					//更新受理时间
					service.createProductionFeature(conn, resultEntity, user.getPosition_id(), user.getOperator_id(), user.getSection_id(), RvsConsts.OPERATE_RESULT_WORKING);
					service.updateQareceptionTime(conn, material_id);
					listResponse.put("resultForm", resultForm);
				}

				if (PositionService.getPositionUnitizeds(conn).containsKey(user.getPosition_id())) {
					DeviceJigLoanService djlService = new DeviceJigLoanService();

					ProductionFeatureEntity waitingPf = new ProductionFeatureEntity();
					waitingPf.setMaterial_id(material_id);
					waitingPf.setRework(0);
					waitingPf.setPosition_id(user.getPosition_id());
					// 现在已借用的设备治具未登记给维修品
					List<String> loaningUnregisting = djlService.getLoaningUnregisting(waitingPf,
							user.getOperator_id(), conn);

					// 在借用的前提下
					djlService.registToMaterial(waitingPf, loaningUnregisting, conn);
				}

			}
		}
		
		// 暂停/中断中数据
		List<ServiceRepairManageForm> sListPaused = service.searchServiceRepairPaused(user.getPosition_id(), conn);
		listResponse.put("serviceRepairPausedList", sListPaused);

		listResponse.put("errors", msgInfos);
		returnJsonResponse(response, listResponse);

		log.info("ServiceRepairRefereeAction.doscan end");
	}
	
	/**
	 * QIS请款信息
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	/*public void searchQisPayout(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("ServiceRepairRefereeAction.searchQisPayout start");
		
		// Ajax响应对象
		Map<String,Object>listResponse=new HashMap<String,Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();
		
		ServiceRepairManageForm qisPayoutReslut=service.searchQisPayout(form,conn);
		
		listResponse.put("qisPayoutReslut", qisPayoutReslut);
		listResponse.put("errors", msgInfos);
		returnJsonResponse(response, listResponse);
		
		log.info("ServiceRepairRefereeAction.searchQisPayout end");
	}
	*/
	
	public void doUpdate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("ServiceRepairRefereeAction.doupdate start");

		// Ajax响应对象
		Map<String,Object>listResponse=new HashMap<String,Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();
		//检查合法性
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		msgInfos = v.validate();

		ServiceRepairManageForm serviceRepairManageForm=(ServiceRepairManageForm)form;

		//ETQ单号
		String etq_no = serviceRepairManageForm.getEtq_no();
		//QIS发送日期
		String qis_invoice_date = serviceRepairManageForm.getQis_invoice_date();
		
		// 当填写了QIS发送日期，则ETQ单号必须填写
		if(!CommonStringUtil.isEmpty(qis_invoice_date) && CommonStringUtil.isEmpty(etq_no)){
			MsgInfo info=new MsgInfo();
			info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required","ETQ单号"));
			info.setErrcode("validator.required");
			msgInfos.add(info);
		}

		// 取得用户信息
		HttpSession session = request.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		SoloProductionFeatureEntity org = service.checkWorkingPfServiceRepair(user.getOperator_id(), conn, msgInfos);
		if (org == null) {
			MsgInfo info=new MsgInfo();
			info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.workingLost"));
			info.setErrcode("info.linework.workingLost");
			msgInfos.add(info);
		}

		if(msgInfos.size()==0){
			service.updateServiceRepair(form,conn);
			ServiceRepairManageForm tempForm=(ServiceRepairManageForm)form;
			//if("2".equals(tempForm.getService_repair_flg())){
				String quality_info_no = tempForm.getQuality_info_no();
				if(isEmpty(quality_info_no) && isEmpty(tempForm.getQis_invoice_no()) && isEmpty(tempForm.getQis_invoice_date())){
					//删除QIS请款信息
					service.deleteQisPayout(tempForm, conn);
				}else{
					//更新QIS请款信息
					service.updateQisPayout(tempForm, org.getJudge_date(), conn);
				}
			//}

			String operator_id = user.getOperator_id();

			String pcs_comments = request.getParameter("pcs_comments");
			// 结束工时计算 
			service.finishWorkingServiceRepair(tempForm, operator_id, pcs_comments, conn, msgInfos);	// qa_referee_time

			String orgDate = DateUtil.toString(org.getJudge_date(), DateUtil.DATE_PATTERN);
			if (!orgDate.equals(tempForm.getRc_mailsend_date())) {
				manageService.updateRelationRcMailsendDate(tempForm, org.getJudge_date(), conn);
			}
		}

		// 未着手数据
		// 分种类
		String kind = null;
		boolean isUnitizeds = PositionService.getPositionUnitizeds(conn).containsKey(user.getPosition_id());
		if (!isUnitizeds) {
			kind = ("601".equals(user.getProcess_code()) ? "0" : "7");			
		}
		List<ServiceRepairManageForm> sList=service.searchServiceRepair(conn, kind,
				isUnitizeds, null);
		listResponse.put("serviceRepairList", sList);

		// 暂停/中断中数据
		List<ServiceRepairManageForm> sListPaused = service.searchServiceRepairPaused(user.getPosition_id(), conn);
		listResponse.put("serviceRepairPausedList", sListPaused);

		listResponse.put("errors", msgInfos);
		returnJsonResponse(response, listResponse);
		
		log.info("ServiceRepairRefereeAction.doupdate end");
	}
	
	public void doPause(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("ServiceRepairRefereeAction.doPause start");
		// Ajax响应对象

		Map<String,Object> callBackResponse=new HashMap<String,Object>();

		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		// 取得用户信息
		HttpSession session = request.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		String operator_id = user.getOperator_id();

		// 取得当前作业中作业信息
		SoloProductionFeatureEntity resultEntity = service.checkWorkingPfServiceRepair(operator_id, conn, msgInfos);

		// 作业信息状态改为，暂停

		// 制作暂停信息
		PauseFeatureService bfService = new PauseFeatureService();
		bfService.createPauseFeature(resultEntity, request.getParameter("reason"), request.getParameter("comments"), null, null, conn);

		// 根据作业信息生成新的等待作业信息－－有开始时间（仅作标记用，重开时需要覆盖掉），说明是操作者原因暂停，将由本人重开。
		service.pauseToSelf(resultEntity, conn);

		callBackResponse.put("errors", msgInfos);

		returnJsonResponse(response, callBackResponse);

		log.info("ServiceRepairRefereeAction.doPause end");
	}

	public void doEndpause(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("ServiceRepairRefereeAction.doEndpause start");

		Map<String,Object> callBackResponse=new HashMap<String,Object>();

		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		callBackResponse.put("errors", msgInfos);

		// 取得用户信息
		HttpSession session = request.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		String operator_id = user.getOperator_id();

		// 只要开始做，就结束掉本人所有的暂停信息。
		PauseFeatureService bfService = new PauseFeatureService();
		bfService.finishPauseFeature(null, null, null, user.getOperator_id(), conn);

		// 取得当前暂停中作业信息
		SoloProductionFeatureEntity resultEntity = service.checkWorkingPfServiceRepair(operator_id, conn, msgInfos);

		service.pauseToResume(resultEntity, conn);

		returnJsonResponse(response, callBackResponse);

		log.info("ServiceRepairRefereeAction.doEndpause end");
	}
	
	/**
	 * 保期内返品+QIS品分析 详细数据
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void detail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSession conn)throws Exception{
		log.info("ServiceRefereeManageAction.detail start");
		//对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		ServiceRepairManageService manageService = new ServiceRepairManageService();
		ServiceRepairManageForm returnForm=manageService.searchServiceRepairAnalysis(form, conn,listResponse);
		
		//当所有可修改编辑的内容框均有值时，分析按钮显示分析表(未完成)；否则显示，分析表(已完成)
		String judgeIsFull="false";
		if(!(CommonStringUtil.isEmpty(returnForm.getAnalysis_no()))&&!(CommonStringUtil.isEmpty(returnForm.getCustomer_name()))&&!(CommonStringUtil.isEmpty(returnForm.getLast_shipping_date()))&&!(CommonStringUtil.isEmpty(returnForm.getLast_sorc_no()))&&!(CommonStringUtil.isEmpty(returnForm.getLast_ocm_rank()))&&!(CommonStringUtil.isEmpty(returnForm.getLast_rank()))
				&&!(CommonStringUtil.isEmpty(returnForm.getLast_trouble_feature()))&&!(CommonStringUtil.isEmpty(returnForm.getFix_demand()))&&!(CommonStringUtil.isEmpty(returnForm.getTrouble_discribe()))&&!(CommonStringUtil.isEmpty(returnForm.getTrouble_cause()))&&!(CommonStringUtil.isEmpty(returnForm.getAnalysis_correspond_suggestion()))){
			judgeIsFull="true";
		}
		listResponse.put("isFull",judgeIsFull);
		listResponse.put("returnForm", returnForm);
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);
		
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("ServiceRefereeManageAction.detail end");
	}
	
	/**
	 * 更新分析表
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doupdateAnalysis(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("ServiceRefereeManageAction.doupdateAnalysis start");
		// Ajax响应对象
		Map<String,Object>listResponse=new HashMap<String,Object>();

		//表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		v.add("analysis_no", v.required());
		List<MsgInfo> errors=v.validate();
		
		ServiceRepairManageForm serviceRepairManageForm = (ServiceRepairManageForm)form;
		//如果表单合法
		if(errors.size()==0){
			//更新保内QIS分析
			manageService.updateServiceRepairManageAnalysis(request,serviceRepairManageForm, conn);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		
		log.info("ServiceRefereeManageAction.doupdateAnalysis end");
	}
	
	/**
	 * 分析表编辑--删除保内返品分析图像
	 * @param mapping ActionMapping
	 * @param form ActionForm
	 * @param request 页面请求
	 * @param response 页面响应
	 * @param conn 数据库会话
	 * @throws Exception 
	 */
	public void doDeleteImage(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("ServiceRepairManageAction.doDeleteImage start");
		// Ajax响应对象
		Map<String,Object>listResponse=new HashMap<String,Object>();

		//表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors=v.validate();
		
		ServiceRepairManageForm serviceRepairManageForm = (ServiceRepairManageForm)form;
		
		//如果表单合法
		if(errors.size()==0){
			manageService.deleteImage(serviceRepairManageForm, conn);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		
		log.info("ServiceRepairManageAction.doDeleteImage end");
	}
}
