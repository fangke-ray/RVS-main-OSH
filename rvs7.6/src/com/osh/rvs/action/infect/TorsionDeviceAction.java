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

import com.osh.rvs.form.infect.TorsionDeviceForm;
import com.osh.rvs.service.infect.TorsionDeviceService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

public class TorsionDeviceAction extends BaseAction {
	private Logger log=Logger.getLogger(getClass());

	private TorsionDeviceService service = new TorsionDeviceService();
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
		log.info("TorsionDeviceAction.init start");
		
		//点检设备精度--search
		request.setAttribute("sHpScale", CodeListUtils.getSelectOptions("torsion_device_hp_scale", null, ""));
		
		//点检设备精度--grid
		request.setAttribute("goHpScale", CodeListUtils.getGridOptions("torsion_device_hp_scale"));
		
		//点检设备精度--edit
		request.setAttribute("eHpScale", CodeListUtils.getSelectOptions("torsion_device_hp_scale"));
		
		//管理编号--referchooser
		String sManageCode = service.getManageCodes(conn);
		request.setAttribute("mReferChooser",sManageCode);
		
		actionForward=mapping.findForward(FW_INIT);

		log.info("TorsionDeviceAction.init end");
	}
	
	/**
	 * 新建力矩设备
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doinsert(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn) throws Exception{
		log.info("TorsionDeviceAcion.doinsert start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 验证
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();
		
		TorsionDeviceForm conditionForm = (TorsionDeviceForm)form;
		service.customValidate(conditionForm, conn, errors);
		if(errors.size()==0){
			service.insertTorsionDevice(conditionForm, conn);
		} 
		
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("TorsionDeviceAction.doinsert end");
	}
	
	/**
	 *检索 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void search(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn) throws Exception{
		log.info("TorsionDeviceAction.search start");
		Map<String,Object> listResponse = new HashMap<String,Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		List<TorsionDeviceForm> resultList = service.search(form, conn, errors);
		listResponse.put("resultList",resultList);
		listResponse.put("errors",errors);
		
		returnJsonResponse(response, listResponse);
		log.info("TorsionDeviceAction.search end");
	}
	
	/**
	 * 力矩设备详细信息--双击修改
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doupdate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn) throws Exception{
		log.info("TorsionDeviceAction.doupdate start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		//表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors=v.validate();

		TorsionDeviceForm conditionForm = (TorsionDeviceForm)form;
		if (errors.size() == 0) {
			service.updateTorsionDevice(conditionForm,conn);
		}
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("TorsionDeviceAction.doupdate end");
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
    public void  dodelete(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn) throws Exception{
    	log.info("TorsionDeviceAction.dodelete start");
    	Map<String, Object> listResponse = new HashMap<String, Object>();
		// 表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		if(errors.size()==0){
			service.deleteToolsManage(form, conn,errors);
		}

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);  	
    	log.info("TorsionDeviceAction.dodelete end");
    }
}
