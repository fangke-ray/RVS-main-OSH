package com.osh.rvs.action.infect;


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

import com.osh.rvs.form.infect.DryingJobForm;
import com.osh.rvs.service.inline.DryingJobService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 * 
 * @Title DryingJobAction.java
 * @Project rvs
 * @Package com.osh.rvs.action.infect
 * @ClassName: DryingJobAction
 * @Description: 烘干作业
 * @author lxb
 * @date 2016-8-3 下午4:49:20
 */
public class DryingJobAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());

	
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
		log.info("DryingJobAction.search start");
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		DryingJobService service = new DryingJobService();
		List<DryingJobForm> returnList = service.serarch(form, conn);

		listResponse.put("errors", errors);
		listResponse.put("finished", returnList);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("DryingJobAction.search end");
	}
	
	/**
	 * 查询烘箱设备
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void searchDryingOvenDevice(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("DryingJobAction.searchDryingOvenDevice start");
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		DryingJobService service = new DryingJobService();
		
		//烘干作业使用设备
		String dryingOvenDeviceReferChooser = service.getDryingOvenDeviceReferChooser(conn);
		
		listResponse.put("errors", errors);
		listResponse.put("dryingOvenDeviceReferChooser", dryingOvenDeviceReferChooser);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("DryingJobAction.searchDryingOvenDevice end");
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
		log.info("DryingJobAction.doInsert start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		DryingJobForm dryingJobForm = (DryingJobForm)form;
		String hardening_condition = dryingJobForm.getHardening_condition();
		
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.delete("drying_job_id");
		if(!CommonStringUtil.isEmpty(hardening_condition) && !"0".equals(hardening_condition)){
			v.add("device_manage_id", v.required("使用设备"));
		}
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		if(errors.size()==0){
			DryingJobService service = new DryingJobService();
			service.insert(dryingJobForm, request, conn);
		}
		
		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("DryingJobAction.doInsert end");
	}
	
	public void getDetail(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("DryingJobAction.getDetail start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		if(errors.size()==0){
			DryingJobService service = new DryingJobService();
			DryingJobForm dryingJobDetail = service.getDryingJobDetail(form, conn);
			//烘干作业使用设备
			String dryingOvenDeviceReferChooser = service.getDryingOvenDeviceReferChooser(conn);
			List<DryingJobForm> categorys = service.getDryingJobOfCategory(form, conn);
			
			listResponse.put("dryingJobDetail", dryingJobDetail);
			listResponse.put("dryingOvenDeviceReferChooser", dryingOvenDeviceReferChooser);
			listResponse.put("categorys", categorys);
		}
		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("DryingJobAction.getDetail end");
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
		log.info("DryingJobAction.doUpdate start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		DryingJobForm dryingJobForm = (DryingJobForm)form;
		String hardening_condition = dryingJobForm.getHardening_condition();
		
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		if(!CommonStringUtil.isEmpty(hardening_condition) && !"0".equals(hardening_condition)){
			v.add("device_manage_id", v.required("使用设备"));
		}
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		if(errors.size()==0){
			DryingJobService service = new DryingJobService();
			service.update(dryingJobForm, request, conn);
		}
		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("DryingJobAction.doUpdate end");
	}
	
	public void doDelete(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("DryingJobAction.doDelete start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		if(errors.size()==0){
			DryingJobService service = new DryingJobService();
			service.delete(form, conn);
		}
		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("DryingJobAction.doDelete end");
	}

}
