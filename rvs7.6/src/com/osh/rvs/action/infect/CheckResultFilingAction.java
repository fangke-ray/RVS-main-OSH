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
import org.apache.struts.upload.FormFile;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.infect.CheckResultFilingForm;
import com.osh.rvs.service.infect.CheckResultFilingService;
import com.osh.rvs.service.DevicesTypeService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.RequiredValidator;
import framework.huiqing.common.util.validator.Validators;

public class CheckResultFilingAction extends BaseAction {
	private Logger log=Logger.getLogger(getClass());
	
	private DevicesTypeService devicesTypeService = new DevicesTypeService();
	private CheckResultFilingService service=new CheckResultFilingService();
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
		log.info("CheckResultFilingAction.init start");
		
		//使用设备工具品名
		String nReferChooser=devicesTypeService.getDevicesTypeReferChooser(conn);
		request.setAttribute("nReferChooser", nReferChooser);
		
		//上传附表--点检表名称
		String sfnReferChooser = service.searchCheckFileNames(conn);
		request.setAttribute("sfnReferChooser", sfnReferChooser);
		
		//上传附表--设备名称
		String dnReferChooser = service.searchDeviceNames(conn);
		request.setAttribute("dnReferChooser", dnReferChooser);
		
		//类型
		request.setAttribute("sAccessPlace", CodeListUtils.getGridOptions("access_place"));
		request.setAttribute("goAccessPlace", CodeListUtils.getSelectOptions("access_place", null, ""));
		//归档周期
		request.setAttribute("sCycleType", CodeListUtils.getGridOptions("cycle_type"));	
		request.setAttribute("goCycleType", CodeListUtils.getSelectOptions("cycle_type", null, ""));
				
		//文件从属
		request.setAttribute("sBranch",CodeListUtils.getGridOptions("checked_file_storage_branch"));
		request.setAttribute("goBranch", CodeListUtils.getSelectOptions("checked_file_storage_branch", null, ""));
		
		LoginData user = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		
		String privacy="";
		//设备分派管理(上传附表)
		if (user.getPrivacies().contains(RvsConsts.PRIVACY_TECHNOLOGY)) {
			privacy = "privacy_technology";
		}
		request.setAttribute("privacyTechnology", privacy);
		
		actionForward=mapping.findForward(FW_INIT);
		
		log.info("CheckResultFilingAction.init end");
	}
	
	/**
	 * 检索--点检结果归档一览
	 * @param mapping ActionMapping
	 * @param form 表单
 	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void search(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("CheckResultFilingAction.search start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
	
		List<CheckResultFilingForm> returnList=service.searchCheckResultFiling(form, conn);
		
		listResponse.put("errors", errors);
		listResponse.put("finished", returnList);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("CheckResultFilingAction.search end");
	}
	
	/**
	 * 双击--点检结果文档一览
	 * @param mapping
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void detail(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn) throws Exception{
		log.info("CheckResultFilingAction.detail start");
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		//双击点检结果文档详细
		List<CheckResultFilingForm> returnList=service.searchCheckedFileStorage(form, conn);
		
		listResponse.put("finished", returnList);
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("CheckResultFilingAction.detail end");
	}
	
	/**
	 * 新建附表数据
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doInsert(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("CheckResultFilingAction.doinsert start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		v.add("check_file_manage_id", new RequiredValidator("点检表管理编号"));
		v.add("devices_manage_id",  new RequiredValidator("对象管理编号"));
		v.add("filing_date", new RequiredValidator("归档日期"));
		// 错误信息集合
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		CheckResultFilingForm checkResultFilingForm = (CheckResultFilingForm)form;
		
		FormFile file = checkResultFilingForm.getFile();
		if (file == null || CommonStringUtil.isEmpty(file.getFileName())) {
			MsgInfo error = new MsgInfo();
			error.setErrcode("file.notExist");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.notExist"));
			errors.add(error);
		}
		String fileName = file.getFileName();
		//判断上传附表是否是pdf格式的
		if(!fileName.endsWith(".pdf")){
			MsgInfo error = new MsgInfo();
			error.setErrcode("file.invalidType");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidType"));
			errors.add(error);
		}
		
		if(errors.size()==0){
			//上传附表文件(pdf文件)
			service.uploadSchedule(file, checkResultFilingForm);
			//新建点检归档记录
			service.insertCheckedFileStorage(file,checkResultFilingForm,conn);
		}	
		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("CheckResultFilingAction.doinsert end");
	}
}
