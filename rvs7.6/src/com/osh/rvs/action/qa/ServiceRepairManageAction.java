package com.osh.rvs.action.qa;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

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
import com.osh.rvs.bean.qa.ServiceRepairManageEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.qa.ServiceRepairManageForm;
import com.osh.rvs.service.qa.ServiceRepairManageService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.validator.Validators;

public class ServiceRepairManageAction extends BaseAction{
	private Logger log = Logger.getLogger(getClass());
	
	private ServiceRepairManageService service=new ServiceRepairManageService();
	
	public void init(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("ServiceRepairManageAction.init start");
		ServiceRepairManageForm serviceRepairManageForm=(ServiceRepairManageForm)form;
		
		ServiceRepairManageEntity serviceRepairManageEntity=new ServiceRepairManageEntity();
		//复制表单到数据对象
		BeanUtil.copyToBean(serviceRepairManageForm, serviceRepairManageEntity,CopyOptions.COPYOPTIONS_NOEMPTY);
		
		String strDate=service.getMinReceptionTime(conn);
		request.setAttribute("minQaReceptionTime", strDate);//最小QA受理日
		
		//有无偿
		request.setAttribute("sServiceFreeFlg", CodeListUtils.getSelectOptions("service_free_flg",null,""));
		request.setAttribute("goServiceFreeFlg",CodeListUtils.getGridOptions("service_free_flg"));
		
		//产品分类
		request.setAttribute("sQaCategoryKind", CodeListUtils.getSelectOptions("qa_category_kind", null, ""));
		request.setAttribute("goQaCategoryKind", CodeListUtils.getGridOptions("qa_category_kind"));
		
		//维修类别
		request.setAttribute("sQaMaterialServiceRepair", CodeListUtils.getSelectOptions("qa_material_service_repair", null, ""));
		request.setAttribute("goQaMaterialServiceRepair",CodeListUtils.getGridOptions("qa_material_service_repair"));
		
		//维修站
		request.setAttribute("sWorkshop", CodeListUtils.getSelectOptions("workshop_new", null, ""));
		request.setAttribute("goWorkshop", CodeListUtils.getGridOptions("workshop"));
		
		//分析结果
		request.setAttribute("sAnalysis_result", CodeListUtils.getSelectOptions("analysis_result", null, ""));
		
		//责任分区
		request.setAttribute("sLiability_flg", CodeListUtils.getSelectOptions("liability_flg", null, ""));
		request.setAttribute("goLiabilityFlg",CodeListUtils.getGridOptions("liability_flg"));

		//处理方式
		request.setAttribute("sQis_corresponse_flg", CodeListUtils.getSelectOptions("qis_corresponse_flg", null, ""));
		
		//实物处理
		request.setAttribute("sQis_entity_send_flg", CodeListUtils.getSelectOptions("qis_entity_send_flg", null, ""));
		
		//质量判定
		request.setAttribute("sQuality_judgment", CodeListUtils.getSelectOptions("quality_judgment", null, ""));
		request.setAttribute("goQuality_judgment",CodeListUtils.getGridOptions("quality_judgment"));
		
		//发行QIS
		request.setAttribute("sQis_isuse", CodeListUtils.getSelectOptions("qis_isuse", null, ""));
		request.setAttribute("goQis_isuse",CodeListUtils.getGridOptions("qis_isuse"));
		
		LoginData user = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		String privacy = (!user.getPrivacies().contains(RvsConsts.PRIVACY_QUALITY_ASSURANCE)
				&& !user.getPrivacies().contains(RvsConsts.PRIVACY_QA_MANAGER)) ? "view" : "qa";

		// 经理导出报表
		if (user.getPrivacies().contains(RvsConsts.PRIVACY_PROCESSING)) {
			privacy = "manager";
		}
		
		//当是品保管理员登陆进行操作时---点击分析表弹出 样式改变
		if(user.getPrivacies().contains(RvsConsts.PRIVACY_QA_MANAGER)){
			privacy="privacy_qa_manager";
		}

		request.setAttribute("privacy", privacy);

		actionForward = mapping.findForward(FW_INIT);
		log.info("ServiceRepairManageAction.init end");

	}
	
	public void getAutocomplete(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("ServiceRepairManageAction.getAutocomplete start");
		
		Map<String,Object>listResponse=new HashMap<String,Object>();
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		List<String> list=service.getModelNameAutoCompletes(conn);
		listResponse.put("sModelName", list.toArray());//型号集合
		
		List<String> ranklist=service.getRankAutoCompletes(conn);//等级集合
		listResponse.put("sRank", ranklist.toArray());
		
		listResponse.put("errors", infoes);
		returnJsonResponse(response, listResponse);
		
		log.info("ServiceRepairManageAction.getAutocomplete end");
	}
	

	public void search(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("ServiceRepairManageAction.search start");
		
		ServiceRepairManageForm  serviceRepairManageForm=(ServiceRepairManageForm)form;
		Map<String,Object>listResponse=new HashMap<String,Object>();
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		ServiceRepairManageEntity serviceRepairManageEntity=new ServiceRepairManageEntity();
		//复制表单到数据对象
		BeanUtil.copyToBean(serviceRepairManageForm, serviceRepairManageEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		List<ServiceRepairManageForm> sList=service.searchServiceRepair(serviceRepairManageEntity, conn);
		
		listResponse.put("serviceRepairList", sList);
		request.getSession().setAttribute("result", sList);
		listResponse.put("errors", infoes);
		returnJsonResponse(response, listResponse);
		
		log.info("ServiceRepairManageAction.search end");
	}
	
	
	public void recept(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("ServiceRepairManageAction.recept start");
		// Ajax响应对象
		Map<String,Object> listResponse=new HashMap<String,Object>();
		
		//检索条件表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors=v.validate();
		
		if(errors.size()==0){
			MaterialForm materialForm=service.getRecept(form, conn, errors);
			if(materialForm!=null){
				listResponse.put("materialForm", materialForm);
			}
		}
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("ServiceRepairManageAction.recept end");
	}
	
	
	
	public void doinsert(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("ServiceRepairManageAction.doinsert start");
		//Ajax响应对象
		Map<String,Object> listResponse=new HashMap<String,Object>();
		// 新建记录表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors=v.validate();
		
		//主建检查
		service.getPrimaryKey(form, conn, errors);
		
		if(errors.size()==0){
			service.insert(form, conn, errors);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);
		
		returnJsonResponse(response, listResponse);
		
		log.info("ServiceRepairManageAction.doinsert end");
	}
	
	public void report(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("ServiceRepairManageAction.report start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		String fileName ="保期内返品投线一览.xls";
		String filePath = service.createWorkReport(request);
		listResponse.put("fileName", fileName);
		listResponse.put("filePath", filePath);
			
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("ServiceRepairManageAction.report end");
	}
	
	/**
	 * 取得编辑前数据
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSession conn)throws Exception{
		log.info("ServiceRepairManageAction.edit start");
		//对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		ServiceRepairManageEntity entity=new ServiceRepairManageEntity();
		//复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		List<ServiceRepairManageForm> lLists=service.searchServiceRepair(entity, conn);

		if (lLists.size() > 0) {
			ServiceRepairManageForm returnForm=lLists.get(0);
			listResponse.put("returnForm", returnForm);
		}
		//request.setAttribute("sQaMaterialServiceRepair", CodeListUtils.getSelectOptions("qa_material_service_repair", null, ""));
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);
		listResponse.put("serviceRepairflgOption", CodeListUtils.getSelectOptions("qa_material_service_repair", null, ""));
		
		//产品分类
		listResponse.put("kind", CodeListUtils.getSelectOptions("qa_category_kind", null, ""));
		
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("ServiceRepairManageAction.edit end");
	}
	
	/**
	 * 取得QIS请款信息
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	/*public void searchQisPayout(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("ServiceRepairManageAction.searchQisPayout start");
		ServiceRepairRefereeService serviceRepairRefereeService=new ServiceRepairRefereeService();
		// Ajax响应对象
		Map<String,Object>listResponse=new HashMap<String,Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();
		
		ServiceRepairManageForm qisPayoutReslut=serviceRepairRefereeService.searchQisPayout(form,conn);
		
		listResponse.put("qisPayoutReslut", qisPayoutReslut);
		listResponse.put("errors", msgInfos);
		returnJsonResponse(response, listResponse);
		
		log.info("ServiceRepairManageAction.searchQisPayout end");
	}*/
	/**
	 * 执行编辑
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doEdit(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("ServiceRepairManageAction.doEdit start");
		
		// Ajax响应对象
		Map<String,Object>listResponse=new HashMap<String,Object>();
		
		//表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors=v.validate();
		
		service.validate(form, errors);
		
		//如果表单没有错误
		if(errors.size()==0){
			service.updateServiceRepairManage(form, conn);
			ServiceRepairManageForm tempForm=(ServiceRepairManageForm)form;
			// if("2".equals(tempForm.getService_repair_flg())){
				String quality_info_no = tempForm.getQuality_info_no();
				String qis_invoice_no = tempForm.getQis_invoice_no();
				String qis_invoice_date = tempForm.getQis_invoice_date();
				String include_month = tempForm.getInclude_month();
				String charge_amount = tempForm.getCharge_amount();
				if ((quality_info_no == null || quality_info_no == "")
						&& (qis_invoice_no == null || qis_invoice_no == "")
						&& (qis_invoice_date == null || qis_invoice_date == "")
						&& (include_month == null || include_month == "")
						&& (charge_amount == null || charge_amount == "")) {
					//删除QIS请款信息
					service.deleteQisPayout(tempForm, conn);
				}else{
					//更新QIS请款信息
					service.updateQisPayout(tempForm, conn);
				}
			// }
			
		}
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		
		log.info("ServiceRepairManageAction.doEdit end");
	}
	
	/**
	 * 受理
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doAccept(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("ServiceRepairManageAction.doAccept start");
		// Ajax响应对象
		Map<String,Object>listResponse=new HashMap<String,Object>();
		//表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		List<MsgInfo> errors=v.validate();

		//如果表单合法
		if(errors.size()==0){
			//主建检查
			service.getPrimaryKey(form, conn, errors);
			//如果主键不重复
			if(errors.size()==0){
				//插入数据
				service.insertServiceRpairManage(form, conn);
				ServiceRepairManageForm tempForm=(ServiceRepairManageForm)form;
				String quality_info_no=tempForm.getQuality_info_no();
				if(!isEmpty(quality_info_no)){
					service.updateQisPayout(form, conn);
				}
			}
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		
		log.info("ServiceRepairManageAction.doAccept end");
	}
	
	/**
	 * 删除
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doDelete(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("ServiceRepairManageAction.doDelete start");
		// Ajax响应对象
		Map<String,Object>listResponse=new HashMap<String,Object>();
		//表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);

		List<MsgInfo> errors=v.validate();

		//如果表单合法
		if(errors.size()==0){
			service.deleteServiceRepairManage(form, conn);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		
		log.info("ServiceRepairManageAction.doDelete end");
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
		log.info("ServiceRepairManageAction.detail start");
		//对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		ServiceRepairManageForm returnForm=service.searchServiceRepairAnalysis(form, conn,listResponse);

		listResponse.put("returnForm", returnForm);
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);
		
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("ServiceRepairManageAction.detail end");
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
		log.info("ServiceRepairManageAction.doupdateAnalysis start");
		// Ajax响应对象
		Map<String,Object>listResponse=new HashMap<String,Object>();

		//表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.add("analysis_no", v.required());
		v.delete("kind");
		List<MsgInfo> errors=v.validate();
		ServiceRepairManageForm serviceRepairManageForm = (ServiceRepairManageForm)form;
		//如果表单合法
		if(errors.size()==0){
			//更新保内QIS分析
			service.updateServiceRepairManageAnalysis(request,serviceRepairManageForm, conn);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		
		log.info("ServiceRepairManageAction.doupdateAnalysis end");
	}
	
	/**
	 * 更新提要内容
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doupdateMention(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("ServiceRepairManageAction.doupdateMention start");
		// Ajax响应对象
		Map<String,Object>listResponse=new HashMap<String,Object>();

		//表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors=v.validate();
		ServiceRepairManageForm serviceRepairManageForm = (ServiceRepairManageForm)form;
		
		//如果表单合法
		if(errors.size()==0){
			service.updateMention(serviceRepairManageForm, conn);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		
		log.info("ServiceRepairManageAction.doupdateMention end");
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
			service.deleteImage(serviceRepairManageForm, conn);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		
		log.info("ServiceRepairManageAction.doDeleteImage end");
	}

	/**
	 * 已开始判定任务退回等待
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={114, 0})
	public void doActionBack(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("ServiceRepairManageAction.doActionBack start");
		//Ajax响应对象
		Map<String,Object> listResponse=new HashMap<String,Object>();

		List<MsgInfo> errors=new ArrayList<MsgInfo>();
		
		service.actionBack(form, conn, errors);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);
		
		returnJsonResponse(response, listResponse);

		log.info("ServiceRepairManageAction.doActionBack end");
	}

}
