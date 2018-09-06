package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.OperatorEntity;
import com.osh.rvs.bean.master.OperatorNamedEntity;
import com.osh.rvs.bean.master.ToolsManageEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.XlsUtil;
import com.osh.rvs.form.master.ToolsManageForm;
import com.osh.rvs.mapper.master.OperatorMapper;
import com.osh.rvs.mapper.master.ToolsManageMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class ToolsManageService {

	/**
	 * 治具管理一览详细
	 * 
	 * @param form
	 * @param conn
	 * @param errors
	 * @return
	 */
	public List<ToolsManageForm> searchToolsManage(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		ToolsManageEntity toolsManageEntity = new ToolsManageEntity();
	
		BeanUtil.copyToBean(form, toolsManageEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<ToolsManageForm> toolsManageForms = new ArrayList<ToolsManageForm>();

		ToolsManageMapper dao = conn.getMapper(ToolsManageMapper.class);

		List<ToolsManageEntity>  toolsManageEntities= dao.searchToolsManage(toolsManageEntity);

		BeanUtil.copyToFormList(toolsManageEntities, toolsManageForms, CopyOptions.COPYOPTIONS_NOEMPTY,
				ToolsManageForm.class);

		return toolsManageForms;
	}

	/**
	 * 修改治具管理详细
	 * 
	 * @param form
	 * @param conn
	 * @param errors
	 */
	public void updateToolsManage(String compare_status,ToolsManageForm toolsManageForm, SqlSessionManager conn, HttpSession session,
			List<MsgInfo> errors) {
		ToolsManageMapper dao = conn.getMapper(ToolsManageMapper.class);
		ToolsManageEntity toolsManageEntity = new ToolsManageEntity();

		Calendar calendar = Calendar.getInstance();

		//修改前状态和修改后状态相比较--如果修改成状态值为1(使用中)，则发放日期是当前日期
		if ("false".equals(compare_status)) {
			if("1".equals(toolsManageForm.getStatus())){
				toolsManageForm.setWaste_date(null);
				String provide_date = DateUtil.toString(calendar.getTime(), "yyyy/MM/dd");
				toolsManageForm.setProvide_date(provide_date);
			}else if("4".equals(toolsManageForm.getStatus())){
				toolsManageForm.setWaste_date(null);
			}			
		}
		
		//如果状态是废弃或者遗失--必须填写废弃日期
		if("2".equals(toolsManageForm.getStatus()) || "3".equals(toolsManageForm.getStatus())){
			if(CommonStringUtil.isEmpty(toolsManageForm.getWaste_date())){
				MsgInfo error = new MsgInfo();
				error.setComponentid("waste_date");
				error.setErrcode("validator.required");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "废弃日期",
						toolsManageForm.getWaste_date(), "废弃日期"));
				errors.add(error);
			}			
		}
		
		BeanUtil.copyToBean(toolsManageForm, toolsManageEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		// 当前操作者ID
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		toolsManageEntity.setUpdated_by(user.getOperator_id());

		dao.updateToolsManage(toolsManageEntity);
	}
	
	/* 验证管理编号不能重复 */
	public void customValidate(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		ToolsManageMapper dao = conn.getMapper(ToolsManageMapper.class);
		ToolsManageEntity toolsManageEntity = new ToolsManageEntity();
		
		/* 数据复制 */
		BeanUtil.copyToBean(form, toolsManageEntity ,(new CopyOptions()).include("tools_manage_id", "manage_code"));

		List<String> resultBean = dao.searchManageCode(toolsManageEntity);
		if (resultBean != null && resultBean.size() > 0) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("manage_code");
			error.setErrcode("dbaccess.columnNotUnique");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "管理编号",
					toolsManageEntity.getManage_code(), "管理编号"));
			errors.add(error);
		}
	}
	
	/*验证选择状态是保管中时，工程和工位必须2选1*/
	public void validateStatus(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		ToolsManageEntity toolsManageEntity = new ToolsManageEntity();
		/* 数据复制 */
		BeanUtil.copyToBean(form, toolsManageEntity,CopyOptions.COPYOPTIONS_NOEMPTY);

		//管理等级是A或者B
		if(toolsManageEntity.getManage_level()==1 ||toolsManageEntity.getManage_level()==2){
			//如果状态是使用中，则工程和工位必须2选1
			if ("1".equals(toolsManageEntity.getStatus()) && CommonStringUtil.isEmpty(toolsManageEntity.getPosition_id()) && CommonStringUtil.isEmpty(toolsManageEntity.getResponsible_operator_id())){
					MsgInfo error = new MsgInfo();
					error.setComponentid("devices_manage_id");
					error.setErrcode("info.tools.choosePositionOperator");
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.tools.choosePositionOperator", "",
							toolsManageEntity.getPosition_id(), ""));
					errors.add(error);		
			} 		
		}
	}
	/**
	 * 新建治具管理
	 * @param form
	 * @param conn
	 * @param errors
	 */
	public void insertToolsManage(ToolsManageForm toolsManageForm, SqlSessionManager conn, HttpSession session,
			List<MsgInfo> errors) {
		ToolsManageMapper dao = conn.getMapper(ToolsManageMapper.class);
		ToolsManageEntity toolsManageEntity = new ToolsManageEntity();

		Calendar calendar = Calendar.getInstance();
		//如果新建状态值为1(使用中)，则发放日期是当前日期
		String provide_date= "";
		if ("1".equals(toolsManageForm.getStatus())) {
			provide_date = DateUtil.toString(calendar.getTime(), "yyyy/MM/dd");
		}
		toolsManageForm.setProvide_date(provide_date);
		//如果状态是废弃或者遗失--必须填写废弃日期
		if("2".equals(toolsManageForm.getStatus()) || "3".equals(toolsManageForm.getStatus())){
			if(CommonStringUtil.isEmpty(toolsManageForm.getWaste_date())){
				MsgInfo error = new MsgInfo();
				error.setComponentid("waste_date");
				error.setErrcode("validator.required");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "废弃日期",
						toolsManageForm.getWaste_date(), "废弃日期"));
				errors.add(error);
				return;
			}
		}
		
		BeanUtil.copyToBean(toolsManageForm, toolsManageEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		// 当前操作者ID
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		toolsManageEntity.setUpdated_by(user.getOperator_id());

		dao.insertToolsManage(toolsManageEntity);
	}
	
	/**
	 * 删除治具管理
	 * 
	 * @param form
	 * @param conn
	 * @param session
	 * @param errors
	 */
	public void deleteToolsManage(ActionForm form, SqlSessionManager conn, HttpSession session, List<MsgInfo> errors) {
		ToolsManageMapper dao = conn.getMapper(ToolsManageMapper.class);
		ToolsManageEntity toolsManageEntity = new ToolsManageEntity();
		BeanUtil.copyToBean(form, toolsManageEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 当前操作者ID
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		toolsManageEntity.setUpdated_by(user.getOperator_id());

		dao.deleteToolsManage(toolsManageEntity);
	}
	
	// 取得责任人员
	public String getResponseOperator(SqlSession conn, OperatorEntity operatorEntity) {
		List<String[]> lst = new ArrayList<String[]>();
		OperatorMapper dao = conn.getMapper(OperatorMapper.class);

		List<OperatorNamedEntity> list = dao.searchOperator(operatorEntity);

		for (OperatorNamedEntity entity : list) {
			String[] o = new String[3];
			o[0] = entity.getOperator_id();
			o[1] = entity.getName();
			o[2] = entity.getRole_name();
			lst.add(o);
		}
		String oReferChooser = CodeListUtils.getReferChooser(lst);
		return oReferChooser;
	}
	
	//查询最大管理编号
	public String searchMaxManageCode(ActionForm form,SqlSession conn){
		ToolsManageEntity toolsManageEntity = new ToolsManageEntity();
		//复制表单对象到数据
		BeanUtil.copyToBean(form, toolsManageEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		String tempManageCode=toolsManageEntity.getManage_code();
		if(tempManageCode.lastIndexOf("-")!=-1){
			tempManageCode=tempManageCode.substring(0, tempManageCode.lastIndexOf("-"));
			toolsManageEntity.setManage_code(tempManageCode);
		}
		
		
		ToolsManageMapper dao = conn.getMapper(ToolsManageMapper.class);
		List<String> list=dao.searchMaxManageCode(toolsManageEntity);
		String manage_code=list.get(0);
		
		String last_name="";
		if(manage_code.lastIndexOf("-")!=-1){
			last_name=manage_code.substring(manage_code.lastIndexOf("-")+1, manage_code.length());
			Integer seq=XlsUtil.getExcelRowSeq(last_name);
			seq++;
			String str=XlsUtil.getExcelColCode(seq);
			manage_code=manage_code.substring(0,manage_code.lastIndexOf("-")+1)+str;
		}else{
			last_name="A";
			manage_code=manage_code+"-"+last_name;
		}
		
		return manage_code;
	}
	
	//替换新品
	public void replace(String compare_status,String old_manage_code,ToolsManageForm toolsManageForm, SqlSessionManager conn, HttpServletRequest request){
		ToolsManageMapper dao = conn.getMapper(ToolsManageMapper.class);
		ToolsManageEntity toolsManageEntity = new ToolsManageEntity();

		Calendar calendar = Calendar.getInstance();

		/*课室是技术课时，发放日期是空白的;其他课室时，发放日期是当前日期;*/
		
		//如果状态该变成使用中,发放日期是当前日期
		String provide_date = "";
		if ("false".equals(compare_status) && !"1".equals(toolsManageForm.getStatus())) {
			provide_date = DateUtil.toString(calendar.getTime(), "yyyy/MM/dd");
		}
		toolsManageForm.setProvide_date(provide_date);
		
		BeanUtil.copyToBean(toolsManageForm, toolsManageEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		// 当前操作者ID
		LoginData user = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		toolsManageEntity.setUpdated_by(user.getOperator_id());

		dao.replace(toolsManageEntity);
		
		//同时废弃掉旧品--选择是(1)--则进行废弃旧品操作
		toolsManageEntity = new ToolsManageEntity();
		BeanUtil.copyToBean(toolsManageForm, toolsManageEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		if("1".equals(toolsManageForm.getWaste_old_products())){
			toolsManageEntity.setUpdated_by(user.getOperator_id());
			dao.disband(toolsManageEntity);
		}
				
	}

	//批量交付
	public void deliverToolsManage(ToolsManageForm toolsManageForm,SqlSessionManager conn, HttpSession session, List<MsgInfo> errors,HttpServletRequest request) {
		ToolsManageMapper dao  = conn.getMapper(ToolsManageMapper.class);
		ToolsManageEntity conditionEntity = new ToolsManageEntity();	
		
		BeanUtil.copyToBean(toolsManageForm, conditionEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		// 当前操作者ID
		LoginData user = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		//多选--key
		List<String> keys = this.getPostKeys(request.getParameterMap());
		MsgInfo error = new MsgInfo();
		if (keys.size()>0) {
			//判断交付条件是否改
			if("true".equals(toolsManageForm.getCompare_section_id()) && "true".equals(toolsManageForm.getCompare_line_id()) && "true".equals(toolsManageForm.getCompare_position_id()) && "true".equals(toolsManageForm.getCompare_responsible_operator_id())&&"true".equals(toolsManageForm.getCompare_manager_operator_id()) ){
				error.setComponentid("tools_manage_id");
				error.setErrmsg("交付条件未改变");
				errors.add(error);
			}else{
				for(String toolsManageId :keys){
					conditionEntity.setTools_manage_id(toolsManageId);
					conditionEntity.setUpdated_by(user.getOperator_id());
					dao.deliverToolsManage(conditionEntity);
				}
			}
			
		} else {
			error.setComponentid("tools_manage_id");
			error.setErrcode("validator.required.multidetail");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required.multidetail", "要交付的治具工具",
					null, "要交付的治具工具"));
			errors.add(error);
		}
	}
	
	// 获取被选择的多个治具
	public List<String> getPostKeys(Map<String, String[]> parameters) {

		List<String> keys = new AutofillArrayList<String>(String.class);
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		// 整理提交数据
		for (String parameterKey : parameters.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("keys".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameters.get(parameterKey);

					if ("tools_manage_id".equals(column)) {
						keys.set(icounts, value[0]);
					}
				}
			}
		}

		return keys;
	}
}
