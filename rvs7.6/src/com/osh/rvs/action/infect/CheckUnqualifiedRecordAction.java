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

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.infect.CheckUnqualifiedRecordForm;
import com.osh.rvs.service.infect.CheckUnqualifiedRecordService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.action.infect
 * @ClassName: CheckUnqualifiedRecordAction
 * @Description: 点检不合格记录
 * @author lxb
 * @date 2014-8-13 上午11:22:29
 * 
 */
public class CheckUnqualifiedRecordAction extends BaseAction {
	private Logger log=Logger.getLogger(getClass());
	
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
		log.info("CheckUnqualifiedRecordAction.init start");
		
		//工位对处  	线长操作
		request.setAttribute("goPositionHandle", CodeListUtils.getSelectOptions("position_handle", null, ""));
		request.setAttribute("goPositionHandle2", CodeListUtils.getSelectOptions("position_handle"));//详细画面
		request.setAttribute("sPositionHandle", CodeListUtils.getGridOptions("position_handle"));
		
		//产品处理内容  经理操作
		request.setAttribute("goProductContent", CodeListUtils.getSelectOptions("product_content", null, ""));
		request.setAttribute("sProductContent", CodeListUtils.getGridOptions("product_content"));
		
		//管理对象对处(设备对处)    经理操作
		request.setAttribute("goObjectHandle",CodeListUtils.getSelectOptions("object_handle", null, ""));
		request.setAttribute("sObjectHandle", CodeListUtils.getGridOptions("object_handle"));
		
		//管理对象对处结果(设备对处结果) 设备管理员操作
		request.setAttribute("goObjectFinalHandleResult", CodeListUtils.getSelectOptions("object_final_handle_result", null, ""));
		request.setAttribute("sObjectFinalHandleResult",CodeListUtils.getGridOptions("object_final_handle_result"));
		
		LoginData loginData=(LoginData)request.getSession().getAttribute(RvsConsts.SESSION_USER);

		
		String role_id=loginData.getRole_id();//角色ID
		if(role_id.equals(RvsConsts.ROLE_MANAGER) || role_id.equals(RvsConsts.ROLE_QA_MANAGER)){//经理
			request.setAttribute("role", "manage");
		}else if(role_id.equals(RvsConsts.ROLE_DT_MANAGER)){//设备管理员
			request.setAttribute("role", "dtTechnology");
		}else{
			request.setAttribute("role", "other");
		}
		
		
		actionForward=mapping.findForward(FW_INIT);
		
		log.info("CheckUnqualifiedRecordAction.init end");
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
		log.info("CheckUnqualifiedRecordAction.search start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		CheckUnqualifiedRecordService service=new CheckUnqualifiedRecordService();
		List<CheckUnqualifiedRecordForm> finished=service.search(form, conn);

		listResponse.put("errors", errors);
		listResponse.put("finished", finished);
		
		
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("CheckUnqualifiedRecordAction.search end");
	}
	
	
	/**
	 * 详细信息
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void detail(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("CheckUnqualifiedRecordAction.detail start");

		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		CheckUnqualifiedRecordService service=new CheckUnqualifiedRecordService();
		String nameReferChooser=service.getNameReferChooser(form, conn);
		
		
		CheckUnqualifiedRecordForm tempForm=service.getById(form, conn);
		String line_leader_handle_time=tempForm.getLine_leader_handle_time();//线长处理时间
		String line_leader_id=tempForm.getLine_leader_id();//线长ID
		String product_content=tempForm.getProduct_content();//产品处理内容
		
		String borrow_object_id=tempForm.getBorrow_object_id();//借用物品ID
		String object_type=tempForm.getObject_type();//对象类型
		
		String sectionAndLine=service.getBorrowSectionAndLine(borrow_object_id,Integer.valueOf(object_type),conn);
		
		LoginData loginData=(LoginData)request.getSession().getAttribute(RvsConsts.SESSION_USER);
		String operator_id= loginData.getOperator_id();//登录者ID
		
		
		String role_id=loginData.getRole_id();//角色ID
		if(role_id.equals(RvsConsts.ROLE_DT_MANAGER)){//设备管理员
			listResponse.put("isDTTechnology", "dtTechnology");
		}else{
			listResponse.put("isDTTechnology", "");
		}
		
		if(role_id.equals(RvsConsts.ROLE_MANAGER) || role_id.equals(RvsConsts.ROLE_QA_MANAGER)){//经理
			listResponse.put("isManage", "manage");
		}else{
			listResponse.put("isManage", "");
		}
		
		listResponse.put("login_id", operator_id);
		listResponse.put("nameReferChooser", nameReferChooser);
		listResponse.put("line_leader_handle_time", line_leader_handle_time==null ?"":line_leader_handle_time);
		listResponse.put("line_leader_id", line_leader_id==null ?"":line_leader_id);
		listResponse.put("product_content", product_content);
		listResponse.put("tempForm", tempForm);
		listResponse.put("sectionAndLine", sectionAndLine);
		
		listResponse.put("errors", errors);
	
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);

		log.info("CheckUnqualifiedRecordAction.detail end");
	}
	
	/**
	 * 线长确认
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doUpdateByLineLeader(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("CheckUnqualifiedRecordAction.doUpdateByLineLeader start");
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		// 错误信息集合
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		if(errors.size()==0){
			CheckUnqualifiedRecordService service=new CheckUnqualifiedRecordService();
			service.updateByLineLeader(form, request, conn);
		}
		
		listResponse.put("errors", errors);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("CheckUnqualifiedRecordAction.doUpdateByLineLeader end");
	}
	
	/**
	 * 经理确认
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doUpdateByManage(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("CheckUnqualifiedRecordAction.doUpdateByManage start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		// 错误信息集合
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		if(errors.size()==0){
			CheckUnqualifiedRecordService service=new CheckUnqualifiedRecordService();
			service.updateByManage(form, request, conn);
		}
		
		listResponse.put("errors", errors);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("CheckUnqualifiedRecordAction.doUpdateByManage end");
	}
	
	
	/**
	 * 设备管理员确认
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdateByTechnology(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("CheckUnqualifiedRecordAction.doUpdateByTechnology start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		// 错误信息集合
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		if(errors.size()==0){
			CheckUnqualifiedRecordService service=new CheckUnqualifiedRecordService();
			service.updateByTechnology(form, request, conn);
		}
		
		listResponse.put("errors", errors);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		
		log.info("CheckUnqualifiedRecordAction.doUpdateByTechnology end");
	}
	
	
}
