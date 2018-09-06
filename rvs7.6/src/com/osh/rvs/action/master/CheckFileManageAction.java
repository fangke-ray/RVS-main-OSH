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
import org.apache.struts.upload.FormFile;

import com.osh.rvs.form.master.CheckFileManageForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.service.CheckFileManageService;
import com.osh.rvs.service.DevicesTypeService;

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
 * @Package com.osh.rvs.action.master
 * @ClassName: CheckFileManageAction
 * @Description:点检表管理
 * @author lxb
 * @date 2014-8-11 上午11:28:06
 * 
 */
public class CheckFileManageAction extends BaseAction {
	Logger log = Logger.getLogger(getClass());
	
	/**
	 * 页面初始化
	 * @param mapping ActionMapping
	 * @param forｍ 表单
	 * @param request 请求
	 * @param responsee 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void init(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse responsee,SqlSession conn)throws Exception{
		log.info("CheckFileManageAction.init start");
		
		request.setAttribute("sAccessPlace", CodeListUtils.getGridOptions("access_place"));//类型
		request.setAttribute("goAccessPlace", CodeListUtils.getSelectOptions("access_place", null, ""));
		request.setAttribute("updateAccessPlace", CodeListUtils.getSelectOptions("access_place"));
		
		request.setAttribute("sCycleType", CodeListUtils.getGridOptions("cycle_type"));//归档周期		
		request.setAttribute("goCycleType", CodeListUtils.getSelectOptions("cycle_type", null, ""));
		
		request.setAttribute("sCheck_file_filing_means", CodeListUtils.getGridOptions("check_file_filing_means"));//归档方式
		request.setAttribute("goCheck_file_filing_means", CodeListUtils.getSelectOptions("check_file_filing_means", null, ""));
		request.setAttribute("updateCheck_file_filing_means", CodeListUtils.getSelectOptions("check_file_filing_means"));
		
		DevicesTypeService service=new DevicesTypeService();
		String nameReferChooser=service.getDevicesTypeReferChooser(conn);
		
		request.setAttribute("nameReferChooser", nameReferChooser);
		
		actionForward=mapping.findForward(FW_INIT);
		
		log.info("CheckFileManageAction.init start");
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
		log.info("CheckFileManageAction.search start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
	
		CheckFileManageService service=new CheckFileManageService();
		List<CheckFileManageForm> returnList=service.search(form, conn);
		
		listResponse.put("errors", errors);
		listResponse.put("finished", returnList);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("CheckFileManageAction.search end");
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
		log.info("CheckFileManageAction.doDelete start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		// 错误信息集合
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		if(errors.size()==0){
			CheckFileManageService service=new CheckFileManageService();
			service.delete(form, conn);
		}
		
		listResponse.put("errors", errors);
		
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("CheckFileManageAction.doDelete end");
	}
	
	/**
	 * 新建点检表
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doInsert(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("CheckFileManageAction.doInsert start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		v.add("check_manage_code", new RequiredValidator("点检表管理编号"));
		v.add("devices_type_id",  new RequiredValidator("使用设备工具品名"));
		v.add("access_place", new RequiredValidator("类型"));
		v.add("filing_means",new RequiredValidator("归档方式"));
		// 错误信息集合
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		CheckFileManageForm checkFileManageForm=(CheckFileManageForm)form;
		FormFile file = checkFileManageForm.getFile();
		if (file == null || CommonStringUtil.isEmpty(file.getFileName())) {//点检表文件不存在
			MsgInfo error = new MsgInfo();
			error.setErrcode("file.notExist");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.notExist"));
			errors.add(error);
		}

		CheckFileManageService service=new CheckFileManageService();
		String access_place=checkFileManageForm.getAccess_place();//类型
		if("2".equals(access_place)){//定期
			if(CommonStringUtil.isEmpty(checkFileManageForm.getCycle_type())){//归档周期为空
				MsgInfo error = new MsgInfo();
				error.setErrcode("validator.required");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required","归档周期"));
				errors.add(error);
			}
		}else if("9".equals(access_place)){//使用前
			service.checkDataIsExist(checkFileManageForm, conn, errors);
		}
		
		if(errors.size()==0){
			service.checkManageCodeIsExist(form, conn, errors);
			if(errors.size()==0){
				service.insert(form, request,conn,"");
				if(errors.size()==0){
					CommonMapper cMapper = conn.getMapper(CommonMapper.class);
					String check_file_manage_id = cMapper.getLastInsertID();
					checkFileManageForm.setCheck_file_manage_id(check_file_manage_id);

					String fileName=service.getFile2Local(form, errors, conn);
					if(errors.size()==0){
						service.update(checkFileManageForm, request, conn, fileName);
					} else {
						conn.rollback();
					}
				}
			}
		}
		
		listResponse.put("errors", errors);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("CheckFileManageAction.doInsert end");
	}
	
	/**
	 * 修改点检表
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void doUpdate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("CheckFileManageAction.doUpdate start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
	
		v.add("check_manage_code", new RequiredValidator("点检表管理编号"));
		v.add("devices_type_id",  new RequiredValidator("使用设备工具品名"));
		v.add("access_place", new RequiredValidator("类型"));
		v.add("filing_means",new RequiredValidator("归档方式"));
	
		// 错误信息集合
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();

		CheckFileManageService service=new CheckFileManageService();
		CheckFileManageForm checkFileManageForm=(CheckFileManageForm)form;
		String access_place=checkFileManageForm.getAccess_place();//类型
		if("2".equals(access_place)){//定期
			if(CommonStringUtil.isEmpty(checkFileManageForm.getCycle_type())){//归档周期为空
				MsgInfo error = new MsgInfo();
				error.setErrcode("validator.required");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required","归档周期"));
				errors.add(error);
			}
		}else if("9".equals(access_place)){//使用前
			service.checkDataIsExist(checkFileManageForm, conn, errors);
		}

		if(errors.size()==0){
			boolean needRename = service.checkIdIsCurrent(form, conn, errors);
			if(errors.size()==0){
				String fileName=service.getFile2Local(form, errors, conn);
				if(errors.size()==0){
					service.update(form, request, conn,fileName);
					if (needRename && "".equals(fileName)) {
						// 修改管理号更名 TODO
					}
				}
			}
		}
		
		listResponse.put("errors", errors);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("CheckFileManageAction.doUpdate end");
	}
	
}
