package com.osh.rvs.service.infect;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.AlarmMesssageSendationEntity;
import com.osh.rvs.bean.infect.CheckUnqualifiedRecordEntity;
import com.osh.rvs.bean.master.DevicesManageEntity;
import com.osh.rvs.bean.master.ToolsManageEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.infect.CheckUnqualifiedRecordForm;
import com.osh.rvs.form.master.DevicesManageForm;
import com.osh.rvs.form.master.ToolsManageForm;
import com.osh.rvs.mapper.data.AlarmMesssageMapper;
import com.osh.rvs.mapper.infect.CheckUnqualifiedRecordMapper;

import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.service
 * @ClassName: CheckUnqualifiedRecordService
 * @Description: 点检不合格记录Service
 * @author lxb
 * @date 2014-8-13 上午11:47:58
 * 
 */
public class CheckUnqualifiedRecordService {
	/**
	 * 一览
	 * 
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<CheckUnqualifiedRecordForm> search(ActionForm form, SqlSession conn) {
		CheckUnqualifiedRecordEntity entity = new CheckUnqualifiedRecordEntity();
		// 复制表单到数据对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		CheckUnqualifiedRecordMapper dao = conn.getMapper(CheckUnqualifiedRecordMapper.class);
		List<CheckUnqualifiedRecordEntity> list = dao.search(entity);

		List<CheckUnqualifiedRecordForm> formList = new ArrayList<CheckUnqualifiedRecordForm>();
		// 复制数据到表单对象
		BeanUtil.copyToFormList(list, formList, CopyOptions.COPYOPTIONS_NOEMPTY, CheckUnqualifiedRecordForm.class);

		return formList;
	}

	/**
	 * 借用设备下拉框 治具下来框
	 * 
	 * @param form
	 * @param conn
	 * @return
	 */
	public String getNameReferChooser(ActionForm form, SqlSession conn) {
		CheckUnqualifiedRecordMapper dao = conn.getMapper(CheckUnqualifiedRecordMapper.class);
		String pReferChooser = "";
		// 取得权限下拉框信息
		List<String[]> nList = new ArrayList<String[]>();
		CheckUnqualifiedRecordForm curForm = (CheckUnqualifiedRecordForm) form;

		String objecType = curForm.getObject_type();// 对象类型
		if ("1".equals(objecType)) {// 设备工具
			DevicesManageEntity entity = new DevicesManageEntity();
			entity.setDevices_type_id(curForm.getDevices_type_id());
			entity.setDevices_manage_id(curForm.getManage_id());
			List<DevicesManageEntity> list = dao.getDevicesNameReferChooser(entity);

			List<DevicesManageForm> dmf = new ArrayList<DevicesManageForm>();
			if (list != null && list.size() > 0) {
				BeanUtil.copyToFormList(list, dmf, null, DevicesManageForm.class);
				for (DevicesManageForm tempForm : dmf) {
					String[] dline = new String[4];
					dline[0] = tempForm.getDevices_manage_id();
					dline[1] = tempForm.getName();
					dline[2] = tempForm.getManage_code();
					dline[3] = tempForm.getModel_name();
					nList.add(dline);
				}
				pReferChooser = CodeListUtils.getReferChooser(nList);
			}
		} else if ("2".equals(objecType)) {// 治具
			ToolsManageEntity entity = new ToolsManageEntity();
			entity.setTools_type_id(curForm.getTools_type_id());
			entity.setTools_manage_id(curForm.getManage_id());
			entity.setTools_no(curForm.getTools_no());
			List<ToolsManageEntity> list = dao.getToolsNameReferChooser(entity);
			List<ToolsManageForm> tmf = new ArrayList<ToolsManageForm>();

			if (list != null && list.size() > 0) {
				BeanUtil.copyToFormList(list, tmf, null, ToolsManageForm.class);
				for (ToolsManageForm tempForm : tmf) {
					String[] dline = new String[5];
					dline[0] = tempForm.getTools_manage_id();
					dline[1] = tempForm.getTools_no();
					dline[2] = tempForm.getTools_name();
					dline[3] = tempForm.getManage_code();
					String sLocate = "";
					if (tempForm.getSection_name() != null) {
						sLocate += tempForm.getSection_name();
					}
					if (tempForm.getProcess_code() != null) {
						sLocate += " " + tempForm.getProcess_code() + "工位.";
					}
					dline[4] = sLocate;
					nList.add(dline);
				}
				pReferChooser = CodeListUtils.getReferChooser(nList);
			}
		}

		return pReferChooser;
	}

	public CheckUnqualifiedRecordForm getById(ActionForm form, SqlSession conn) {
		CheckUnqualifiedRecordEntity tempEntity = new CheckUnqualifiedRecordEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, tempEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		CheckUnqualifiedRecordMapper dao = conn.getMapper(CheckUnqualifiedRecordMapper.class);
		CheckUnqualifiedRecordEntity entity = dao.getById(tempEntity);

		CheckUnqualifiedRecordForm returnForm = new CheckUnqualifiedRecordForm();
		// 复制数据到表单对象
		BeanUtil.copyToForm(entity, returnForm, CopyOptions.COPYOPTIONS_NOEMPTY);

		return returnForm;
	}

	/**
	 * 线长确认
	 * 
	 * @param form
	 * @param request
	 * @param conn
	 * @throws Exception 
	 */
	public void updateByLineLeader(ActionForm form, HttpServletRequest request, SqlSessionManager conn) throws Exception {
		CheckUnqualifiedRecordEntity entity = new CheckUnqualifiedRecordEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		LoginData loginData = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		String operator_id = loginData.getOperator_id();
		entity.setLine_leader_id(operator_id);

		CheckUnqualifiedRecordMapper dao = conn.getMapper(CheckUnqualifiedRecordMapper.class);
		dao.updateByLineLeader(entity);

		// 中断信息解除 TODO
		entity = dao.getById(entity);
		
		// 处理人处理信息
		AlarmMesssageMapper amDao = conn.getMapper(AlarmMesssageMapper.class);
		AlarmMesssageSendationEntity sendation = new AlarmMesssageSendationEntity();
		sendation.setAlarm_messsage_id( entity.getAlarm_message_id() );
		sendation.setComment("点检不合格解除");
		sendation.setRed_flg(1);
		sendation.setSendation_id(loginData.getOperator_id());
		sendation.setResolve_time(new Date());

		int me = amDao.countAlarmMessageSendation(sendation);
		if (me <= 0) {
			// 没有发给处理者的信息时（代理线长），新建一条
			amDao.createAlarmMessageSendation(sendation);
		} else {
			amDao.updateAlarmMessageSendation(sendation);
		}
	}

	/**
	 * 经理确认
	 * 
	 * @param form
	 * @param request
	 * @param conn
	 */
	public void updateByManage(ActionForm form, HttpServletRequest request, SqlSessionManager conn) {
		CheckUnqualifiedRecordEntity entity = new CheckUnqualifiedRecordEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		LoginData loginData = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		String operator_id = loginData.getOperator_id();
		entity.setManager_id(operator_id);

		CheckUnqualifiedRecordMapper dao = conn.getMapper(CheckUnqualifiedRecordMapper.class);
		dao.updateByManage(entity);
	}

	/**
	 * 设备管理员确认
	 * 
	 * @param form
	 * @param request
	 * @param conn
	 */
	public void updateByTechnology(ActionForm form, HttpServletRequest request, SqlSessionManager conn) {
		CheckUnqualifiedRecordEntity entity = new CheckUnqualifiedRecordEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		LoginData loginData = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		String operator_id = loginData.getOperator_id();
		entity.setTechnology_id(operator_id);

		CheckUnqualifiedRecordMapper dao = conn.getMapper(CheckUnqualifiedRecordMapper.class);
		dao.updateByTechnology(entity);
		
		if(entity.getObject_final_handle_result()!= null && entity.getObject_final_handle_result()==1){//废弃
			dao.updateStatus(entity);
		}
		
	}
	
	//获取借用物品的科室和工位
	public String getBorrowSectionAndLine(String borrow_object_id,Integer object_type,SqlSession conn){
		if(CommonStringUtil.isEmpty(borrow_object_id)){
			return null;
		}
		CheckUnqualifiedRecordEntity entity = new CheckUnqualifiedRecordEntity();
		entity.setBorrow_object_id(borrow_object_id);
		entity.setObject_type(object_type);
		
		CheckUnqualifiedRecordMapper dao = conn.getMapper(CheckUnqualifiedRecordMapper.class);
		CheckUnqualifiedRecordEntity temp=dao.getSectionAndLine(entity);
		if (temp.getProcess_code()==null) {
			return ""+temp.getSection_name();
		}
		return ""+temp.getSection_name()+" "+temp.getProcess_code()+"工位";
	}
}
