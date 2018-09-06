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

import com.osh.rvs.form.infect.DryingOvenDeviceForm;
import com.osh.rvs.service.CategoryService;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.inline.DryingOvenDeviceService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 * 
 * @Title DryingOvenDeviceAction.java
 * @Project rvs
 * @Package com.osh.rvs.action.infect
 * @ClassName: DryingOvenDeviceAction
 * @Description: 烘箱管理
 * @author lxb
 * @date 2016-8-2 下午4:09:30
 */
public class DryingOvenDeviceAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());
	
	/**
	 * 初始化
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("DryingOvenDeviceAction.init start");
		
		DryingOvenDeviceService service=new DryingOvenDeviceService();
		
		//管理编号
		String referChooser = service.getReferChooser(conn);
		request.setAttribute("referChooser",referChooser);
		
		// 设定温度
		request.setAttribute("goDryingOvenSettingTemperature", CodeListUtils.getSelectOptions("drying_oven_setting_temperature", null, ""));
		request.setAttribute("sDryingOvenSettingTemperature", CodeListUtils.getGridOptions("drying_oven_setting_temperature"));
		
		// 硬化条件
		request.setAttribute("goDryingHardeningCondition", CodeListUtils.getSelectOptions("drying_hardening_condition", null, ""));
		request.setAttribute("sDryingHardeningCondition", CodeListUtils.getGridOptions("drying_hardening_condition"));
		
		//烘干作业工位
		PositionService positionService = new PositionService();
		String pReferChooser = positionService.getOptions(conn);
		request.setAttribute("pReferChooser",pReferChooser);
		
		//机种
		CategoryService categoryService = new CategoryService();
		String cReferChooser = categoryService.getEndoscopeOptions(conn);
		request.setAttribute("cReferChooser",cReferChooser);
		
		String role = request.getParameter("role");
		request.setAttribute("role",role);
		
		actionForward=mapping.findForward(FW_INIT);
		
		log.info("DryingOvenDeviceAction.init end");
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
		log.info("DryingOvenDeviceAction.search start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		DryingOvenDeviceService service=new DryingOvenDeviceService();
		List<DryingOvenDeviceForm> finished=service.search(form, conn);
		
		listResponse.put("errors", errors);
		listResponse.put("finished", finished);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("DryingOvenDeviceAction.search end");
	}
	
	/**
	 * 新建
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doInsert(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("DryingOvenDeviceAction.doInsert start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		if(errors.size()==0){
			DryingOvenDeviceService service=new DryingOvenDeviceService();
			service.insert(form, conn, errors);
			
		}
		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("DryingOvenDeviceAction.doInsert end");
	}
	
	
	/**
	 * 更新
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doUpdate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("DryingOvenDeviceAction.doUpdate start");
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.delete("device_manage_id","");
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		if(errors.size()==0){
			DryingOvenDeviceService service=new DryingOvenDeviceService();
			service.update(form, conn);
		}
		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("DryingOvenDeviceAction.doUpdate end");
	}
	
	/**
	 * 删除
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doDelete(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("DryingOvenDeviceAction.doDelete start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.only("device_manage_id");
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		if(errors.size()==0){
			DryingOvenDeviceService service=new DryingOvenDeviceService();
			service.delete(form, conn);
		}
		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("DryingOvenDeviceAction.doDelete end");
	}
	
	
}
