package com.osh.rvs.action.master;

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

import com.osh.rvs.form.master.CustomerForm;
import com.osh.rvs.service.CustomerService;

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
 * @Title CustomerAction.java
 * @Project rvs
 * @Package com.osh.rvs.action.master
 * @ClassName: CustomerAction
 * @Description: 客户管理Action
 * @author lxb
 * @date 2014-12-3 上午11:07:00
 */
public class CustomerAction extends BaseAction{
	private Logger log=Logger.getLogger(getClass());
	
	/**
	 * 页面初始化
	 * @param mapping Mapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void init(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("CustomerAction.init start");
		
		request.setAttribute("goMaterial_ocm", CodeListUtils.getSelectOptions("material_ocm", null, ""));//分室
		request.setAttribute("sMaterial_ocm", CodeListUtils.getGridOptions("material_ocm"));
		
		actionForward=mapping.findForward(FW_INIT);
		
		log.info("CustomerAction.init end");
	}
	
	/**
	 * 检索
	 * @param mpping Mapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void search(ActionMapping mpping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("CustomerAction.search start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		CustomerService service=new CustomerService();
		List<CustomerForm> returnList=service.search(form, conn);
		
		listResponse.put("errors", errors);
		listResponse.put("finished", returnList);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("CustomerAction.search end");
	}
	
	/**
	 * 新建客户
	 * @param mapping Mapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doInsert(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("CustomerAction.doInsert start");
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		v.add("name",  new RequiredValidator("客户名称"));
		v.add("vip", new RequiredValidator("优先对应客户"));
		
		// 错误信息集合
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		if(errors.size()==0){
			CustomerService service=new CustomerService();
			service.checkNameIsExist(form, conn, errors);
			if(errors.size()==0){
				service.insert(form, conn, request);
			}
		}
		
		listResponse.put("errors", errors);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		log.info("CustomerAction.doInsert end");
	}
	
	/**
	 * 更新
	 * @param mapping Mapping
	 * @param form 表单
	 * @param rerquest 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doUpdate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("CustomerAction.doUpdate start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		v.add("customer_id", new RequiredValidator("客户ID"));
		v.add("name",  new RequiredValidator("客户名称"));
		v.add("vip", new RequiredValidator("优先对应客户"));
		// 错误信息集合
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		if(errors.size()==0){
			CustomerService service=new CustomerService();
			service.checkIdIsCurrent(form, conn, errors);
			if(errors.size()==0){
				service.update(form,conn,request);
			}
		}
		
		listResponse.put("errors", errors);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		log.info("CustomerAction.doUpdate end");
	}
	
	/**
	 * 查询归并目标
	 * @param mapping Mapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void searchMergeTarget(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("CustomerAction.searchMergeTarget start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		CustomerForm customerForm=(CustomerForm)form;
		String name=customerForm.getName();
		if(CommonStringUtil.isEmpty(name)){//客户名称为空
			MsgInfo error = new MsgInfo();
			error.setComponentid("merge_search_name");
			error.setErrcode("validator.required");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "客户名称"));
			errors.add(error);
		}
		
		if(errors.size()==0){
			CustomerService service=new CustomerService();
			List<CustomerForm> returnList=service.searchMergeTarget(customerForm, conn);
			listResponse.put("finished", returnList);
		}
		
		listResponse.put("errors", errors);
		
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("CustomerAction.searchMergeTarget end");
	}
	
	
	/**
	 * 实行归并
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doMerge(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("CustomerAction.doMerge start");
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		
		// 错误信息集合
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		if(errors.size()==0){
			CustomerService service=new CustomerService();
			service.merge(form, conn, request);
		}
		
		listResponse.put("errors", errors);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		log.info("CustomerAction.doMerge end");
	}
}
