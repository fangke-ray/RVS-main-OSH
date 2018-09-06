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

import com.osh.rvs.bean.infect.ElectricIronDeviceEntity;
import com.osh.rvs.form.infect.ElectricIronDeviceForm;
import com.osh.rvs.service.DevicesManageService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.infect.ElectricIronDeviceService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.validator.Validators;

public class ElectricIronDeviceAction extends BaseAction {
	
	private Logger log = Logger.getLogger(getClass());
	
	private ElectricIronDeviceService service = new ElectricIronDeviceService();
	
	private SectionService sectionService = new SectionService();
	
	private DevicesManageService devicesManageService = new DevicesManageService();
	/**
	 * 电烙铁工具数据页面初始化
	 * @param mapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @throws Exception
	 */
	public void init(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("ElectricIronDeviceAction.init start");
		
		//管理编号--referchooser
		String mcReferChooser = service.getMCReferChooser(conn);
		request.setAttribute("mcReferChooser",mcReferChooser);
		
		//设备品名--referchooser
		String dnReferChooser = service.getDNReferChooser(conn);
		request.setAttribute("dnReferChooser", dnReferChooser);
		
		//所在课室
		String sectionOptions = sectionService.getAllOptions(conn);
		request.setAttribute("sectionOptions", sectionOptions);
		
		//所在工位
		String pReferChooser = devicesManageService.getOptionPtions(conn);
		request.setAttribute("pReferChooser", pReferChooser);
		
		//种类
		String kOptions = CodeListUtils.getSelectOptions("electric_iron_kind", null, "");
		request.setAttribute("kOptions", kOptions);
		
		//种类无不选
		String kEOptions = CodeListUtils.getSelectOptions("electric_iron_kind");
		request.setAttribute("kEOptions", kEOptions);
		
		String kGridOptions = CodeListUtils.getGridOptions("electric_iron_kind_simple");
		request.setAttribute("kGridOptions", kGridOptions);
				
		actionForward=mapping.findForward(FW_INIT);
		
		log.info("ElectricIronDeviceAction.init end");
	}
	
	/**
	 * 初始查询
	 * @param mapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库
	 * @throws Exception
	 */
	public void search(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn) throws Exception{
		log.info("ElectricIronDeviceAction.search start");
		Map<String,Object> listResponse = new HashMap<String,Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		ElectricIronDeviceForm electricIronDeviceForm =(ElectricIronDeviceForm)form;
		ElectricIronDeviceEntity conditionEntity = new ElectricIronDeviceEntity();		
		BeanUtil.copyToBean(electricIronDeviceForm, conditionEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		List<ElectricIronDeviceForm> resultList = service.searchElectricIronDevice(conditionEntity,conn,errors);
		
		listResponse.put("errors", errors);
		listResponse.put("resultList",resultList);
		returnJsonResponse(response, listResponse);
		log.info("ElectricIronDeviceAction.search end");
	}
	
	/**
	 * 新建电烙铁工具
	 * @param mapping 
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库
	 * @throws Exception
	 */
	public void doInsert(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn) throws Exception{
		log.info("ElectricIronDeviceAction.doInsert start");
		Map<String,Object> listResponse = new HashMap<String,Object>();
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();
		
		ElectricIronDeviceForm electricIronDeviceForm =(ElectricIronDeviceForm)form;
		ElectricIronDeviceEntity conditionEntity = new ElectricIronDeviceEntity();		
		BeanUtil.copyToBean(electricIronDeviceForm, conditionEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//验证新建电烙铁工具是否已经存在
		if(errors.size()==0){
			service.customValidate(form, conn, errors);
			if(errors.size()==0){
				service.insertElectricIronDevice(conditionEntity,conn,errors);
			}
		}
		
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("ElectricIronDeviceAction.doInsert end");
	}
	
	/**
	 * 双击修改电烙铁
	 * @param mapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库
	 * @throws Exception
	 */
	public void doUpdate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn) throws Exception{
		log.info("ElectricIronDeviceAction.doUpdate start");
		Map<String,Object> listResponse = new HashMap<String,Object>();
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors = v.validate();
		
		ElectricIronDeviceForm electricIronDeviceForm =(ElectricIronDeviceForm)form;
		ElectricIronDeviceEntity conditionEntity = new ElectricIronDeviceEntity();		
		BeanUtil.copyToBean(electricIronDeviceForm, conditionEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		if(errors.size()==0){
			service.updateElectricIronDevice(conditionEntity,conn,errors);
		}
		
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("ElectricIronDeviceAction.doUpdate end");
	}
	
	/**
	 * 删除
	 * @param mapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库
	 * @throws Exception
	 */
	public void doDelete(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn) throws Exception{
		log.info("ElectricIronDeviceAction.doDelete start");
		Map<String,Object> listResponse = new HashMap<String,Object>();
		List<String> errors = new ArrayList<String>();
		
		ElectricIronDeviceForm electricIronDeviceForm =(ElectricIronDeviceForm)form;
		ElectricIronDeviceEntity conditionEntity = new ElectricIronDeviceEntity();		
		BeanUtil.copyToBean(electricIronDeviceForm, conditionEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		service.deleteElectricIronDevice(conditionEntity,conn,errors);
		
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);		
		log.info("ElectricIronDeviceAction.doDelete end");
	}
}
