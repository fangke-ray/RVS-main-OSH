package com.osh.rvs.action.infect;

import java.io.Serializable;
import java.math.BigDecimal;
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

import com.osh.rvs.form.infect.ExternalAdjustmentForm;
import com.osh.rvs.service.LineService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.infect.ExternalAdjustmentService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.RequiredValidator;
import framework.huiqing.common.util.validator.Validators;

/**
 * 
* @Project rvs
* @Package com.osh.rvs.action.infect 
* @ClassName: ExternalCheckAction
* @Description: 检查机器校正Action
* @author lxb
* @date 2014-9-6 下午4:21:25
*
 */
public class ExternalAdjustmentAction extends BaseAction implements Serializable{
	private Logger log=Logger.getLogger(getClass());
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3856559201791116977L;
	
	/**
	 * 页面初始化
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void init(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("ExternalAdjustmentAction.init start");
		
		//品名
		ExternalAdjustmentService service=new ExternalAdjustmentService();
		String nReferChooser=service.geNameReferChooser(conn);
		request.setAttribute("nReferChooser", nReferChooser);
		
		SectionService sectionService = new SectionService();
		// 分发课室
		String sectionOptions = sectionService.getAllOptions(conn);
		request.setAttribute("sectionOptions", sectionOptions);
		
		LineService lineService = new LineService();
		// 责任工程
		String lineOptions = lineService.getOptions(conn);
		request.setAttribute("lineOptions", lineOptions);
		
		//有效期
		request.setAttribute("goEffectInterval", CodeListUtils.getSelectOptions("effect_interval", null, ""));
		request.setAttribute("sEffectInterval", CodeListUtils.getGridOptions("effect_interval"));
		
		//校验单位
		request.setAttribute("goOrganizationType", CodeListUtils.getSelectOptions("organization_type",null,""));
		request.setAttribute("goOrganizationType2", CodeListUtils.getSelectOptions("organization_type",""));
		request.setAttribute("sOrganizationType", CodeListUtils.getGridOptions("organization_type"));
		
		Map<String, BigDecimal> map=service.getTotalCost(conn);
		
		request.setAttribute("institution",map.get("institution")==null ? 0:map.get("institution"));//校验机构 校验费用总计 
		request.setAttribute("domestic",map.get("domestic")==null ? 0:map.get("domestic"));//国内厂商校验 校验费用总计 
		request.setAttribute("abroad",map.get("abroad")==null ? 0:map.get("abroad"));//国外厂商校验 校验费用总计 
		
		
		
		actionForward=mapping.findForward(FW_INIT);
		
		log.info("ExternalAdjustmentAction.init end");
	}
	
	/**
	 * 检索
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void search(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("ExternalAdjustmentAction.search start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		ExternalAdjustmentService service=new ExternalAdjustmentService();
		List<ExternalAdjustmentForm> finished=service.search(form, conn);
		
		listResponse.put("errors", errors);
		listResponse.put("finished", finished);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		
		log.info("ExternalAdjustmentAction.search end");
	}
	
	/**
	 * 管理编号下拉框
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void manageCodeReferChooser(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("ExternalAdjustmentAction.manageCodeReferChooser start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		//管理编号
		ExternalAdjustmentService service=new ExternalAdjustmentService();
		String manageCodeReferChooser=service.getManageCodeReferChooser(conn);
		
		listResponse.put("errors", errors);
		listResponse.put("finished", manageCodeReferChooser);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("ExternalAdjustmentAction.manageCodeReferChooser end");
	}
	
	
	/**
	 *  管理编号chang事件 根据管理编号查询基本信息  
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void searchBaseInfo(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("ExternalAdjustmentAction.searchBaseInfo start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		ExternalAdjustmentService service=new ExternalAdjustmentService();
		ExternalAdjustmentForm finished=service.searchBaseInfo(form, conn);;
		
		listResponse.put("errors", errors);
		listResponse.put("finished", finished);
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("ExternalAdjustmentAction.searchBaseInfo end");
	}
	
	
	
	/**
	 * 新建
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doInsert(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("ExternalAdjustmentAction.doInsert start");
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		
		ExternalAdjustmentForm externalAdjustmentForm=(ExternalAdjustmentForm)form;
		
		if(!CommonStringUtil.isEmpty(externalAdjustmentForm.getCheck_cost())){
			v.add("check_cost", new RequiredValidator("校验费用"));
		}
		
		// 错误信息集合
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		
		
		String organization_type=externalAdjustmentForm.getOrganization_type();
		if("1".equals(organization_type)){//校验单位选择了校验机构
			if(CommonStringUtil.isEmpty(externalAdjustmentForm.getInstitution_name())){//校验机构名称为空
				MsgInfo error = new MsgInfo();
				error.setComponentid("add_institution_name");
				error.setErrcode("validator.required");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required","校验机构名称"));
				errors.add(error);
			}
		}
		
		if(errors.size()==0){
			ExternalAdjustmentService service=new ExternalAdjustmentService();
			service.insert(externalAdjustmentForm, conn);
		}
		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("ExternalAdjustmentAction.doInsert end");
	}
	
	/**
	 * 送检
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doChecking(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("ExternalAdjustmentAction.doChecking start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		// 错误信息集合
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		if(errors.size()==0){
			ExternalAdjustmentService service=new ExternalAdjustmentService();
			service.checking(form, conn);
		}
		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("ExternalAdjustmentAction.doChecking end");
	}
	
	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void searchDetailById(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("ExternalAdjustmentAction.searchDetailById start");
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		ExternalAdjustmentService service=new ExternalAdjustmentService();
		ExternalAdjustmentForm finished=service.getDetailById(form, conn);;

		listResponse.put("errors", errors);
		listResponse.put("finished", finished);
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("ExternalAdjustmentAction.searchDetailById end");
	}
	
	/**
	 * 更新
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("ExternalAdjustmentAction.doUpdateCheckedDate start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		ExternalAdjustmentForm externalAdjustmentForm=(ExternalAdjustmentForm)form;
		
		
		if(!CommonStringUtil.isEmpty(externalAdjustmentForm.getCheck_cost())){
			v.add("check_cost", new RequiredValidator("校验费用"));
		}
		
		String checking_flg=externalAdjustmentForm.getChecking_flg();//校验状态
		if("1".equals(checking_flg)){//校验中
			if(!CommonStringUtil.isEmpty(externalAdjustmentForm.getChecked_date())){
				v.add("checked_date", new RequiredValidator("校验日期"));
			}
		}
		
		// 错误信息集合
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		String organization_type=externalAdjustmentForm.getOrganization_type();
		if("1".equals(organization_type)){//校验单位选择了校验机构
			if(CommonStringUtil.isEmpty(externalAdjustmentForm.getInstitution_name())){//校验机构名称为空
				MsgInfo error = new MsgInfo();
				error.setComponentid("add_institution_name");
				error.setErrcode("validator.required");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required","校验机构名称"));
				errors.add(error);
			}
		}
		
		
		if(errors.size()==0){
			ExternalAdjustmentService service=new ExternalAdjustmentService();
			service.update(externalAdjustmentForm, conn);
		}
		
		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
				
		log.info("ExternalAdjustmentAction.doUpdateCheckedDate end");
	}
	
	/**
	 * 停止校验
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doStop(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("ExternalAdjustmentAction.doStop start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		ExternalAdjustmentService service=new ExternalAdjustmentService();
		service.stopChecking(form, conn);
		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("ExternalAdjustmentAction.doStop end");
	}
	

}
