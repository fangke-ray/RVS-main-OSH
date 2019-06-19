package com.osh.rvs.service.infect;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.AlarmMesssageSendationEntity;
import com.osh.rvs.bean.data.PostMessageEntity;
import com.osh.rvs.bean.infect.CheckUnqualifiedRecordEntity;
import com.osh.rvs.bean.master.DevicesManageEntity;
import com.osh.rvs.bean.master.OperatorEntity;
import com.osh.rvs.bean.master.OperatorNamedEntity;
import com.osh.rvs.bean.master.ToolsManageEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.infect.CheckUnqualifiedRecordForm;
import com.osh.rvs.form.master.DevicesManageForm;
import com.osh.rvs.form.master.ToolsManageForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.data.PostMessageMapper;
import com.osh.rvs.mapper.infect.CheckUnqualifiedRecordMapper;
import com.osh.rvs.mapper.master.DevicesManageMapper;
import com.osh.rvs.mapper.master.OperatorMapper;
import com.osh.rvs.service.AlarmMesssageService;
import com.osh.rvs.service.PostMessageService;

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
	 * 借用设备(自由)下拉框/治具下拉框 借用设备(需确认)下拉框 备品件数
	 * 
	 * @param form
	 * @param listResponse 
	 * @param conn
	 * @return
	 */
	public void getAlterObjects(ActionForm form, LoginData user, Map<String, Object> listResponse, SqlSession conn) {
		CheckUnqualifiedRecordMapper dao = conn.getMapper(CheckUnqualifiedRecordMapper.class);
		CheckUnqualifiedRecordForm curForm = (CheckUnqualifiedRecordForm) form;

		String pFreeReferChooser = "";
		String pAcquireReferChooser = "";

		String objecType = curForm.getObject_type();// 对象类型

		if ("1".equals(objecType)) {// 设备工具
			DevicesManageEntity entity = new DevicesManageEntity();
			entity.setDevices_type_id(curForm.getDevices_type_id());
			entity.setDevices_manage_id(curForm.getManage_id());

			DevicesManageMapper dmMapper = conn.getMapper(DevicesManageMapper.class);
			DevicesManageEntity dmEntity = dmMapper.getByKey(curForm.getManage_id());

			entity.setLine_id(dmEntity.getLine_id());
			entity.setModel_name(dmEntity.getModel_name());

			// 可以自由替换他工程
			boolean canAlterOutOfLine = user.getPrivacies().contains(RvsConsts.PRIVACY_PROCESSING);

			List<DevicesManageEntity> acquireList = new ArrayList<DevicesManageEntity>();
			List<DevicesManageEntity> freeList = new ArrayList<DevicesManageEntity>();

			List<DevicesManageEntity> allList = dao.getDevicesNameReferChooser(entity);
			for (DevicesManageEntity alter : allList) {
				Integer freeDisplaceFlg = alter.getFree_displace_flg();
				if (freeDisplaceFlg == null) {
				} else if (freeDisplaceFlg == 0) { // △
					acquireList.add(alter);
				} else if (freeDisplaceFlg == 1) { // maru
					if (!canAlterOutOfLine && 
							(user.getLine_id() == null || !user.getLine_id().equals(alter.getLine_id()))) {
						acquireList.add(alter); // 线长身份不可替换他工程
					} else {
						freeList.add(alter);
					}
				}
			}

			if (freeList.size() + acquireList.size() == 0) {
				freeList = allList; // TODO type has set
			}

			// 取得权限下拉框信息
			List<String[]> nList = new ArrayList<String[]>();
			List<DevicesManageForm> dmf = new ArrayList<DevicesManageForm>();

			if (freeList != null && freeList.size() > 0) {
				BeanUtil.copyToFormList(freeList, dmf, CopyOptions.COPYOPTIONS_NOEMPTY, DevicesManageForm.class);
				for (DevicesManageForm tempForm : dmf) {
					String[] dline = new String[4];
					dline[0] = tempForm.getDevices_manage_id();
					dline[1] = tempForm.getManage_code();
					dline[2] = tempForm.getName();
					dline[3] = CommonStringUtil.nullToAlter(tempForm.getModel_name(), "-");
					nList.add(dline);
				}
				pFreeReferChooser = CodeListUtils.getReferChooser(nList);
			}

			dmf = new ArrayList<DevicesManageForm>();
			nList = new ArrayList<String[]>();

			if (acquireList != null && acquireList.size() > 0) {
				BeanUtil.copyToFormList(acquireList, dmf, CopyOptions.COPYOPTIONS_NOEMPTY, DevicesManageForm.class);
				for (DevicesManageForm tempForm : dmf) {
					String[] dline = new String[4];
					dline[0] = tempForm.getDevices_manage_id();
					dline[1] = tempForm.getManage_code();
					dline[2] = tempForm.getName();
					dline[3] = CommonStringUtil.nullToAlter(tempForm.getModel_name(), "-");
					nList.add(dline);
				}
				pAcquireReferChooser = CodeListUtils.getReferChooser(nList);
			}

		} else if ("2".equals(objecType)) {// 治具
			ToolsManageEntity entity = new ToolsManageEntity();
			entity.setTools_manage_id(curForm.getManage_id());
			entity.setTools_no(curForm.getTools_no());
			List<ToolsManageEntity> list = dao.getToolsNameReferChooser(entity);
			List<ToolsManageForm> tmf = new ArrayList<ToolsManageForm>();

			// 取得权限下拉框信息
			List<String[]> nList = new ArrayList<String[]>();

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
				pFreeReferChooser = CodeListUtils.getReferChooser(nList);
			}
		}

		listResponse.put("borrowFreeReferChooser", pFreeReferChooser);
		listResponse.put("borrowAcquireReferChooser", pAcquireReferChooser);

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
	 * @param triggerList 
	 * @param conn
	 * @throws Exception 
	 */
	public void updateByLineLeader(ActionForm form, HttpServletRequest request, List<String> triggerList, SqlSessionManager conn) throws Exception {
		CheckUnqualifiedRecordEntity entity = new CheckUnqualifiedRecordEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		LoginData loginData = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		String operator_id = loginData.getOperator_id();
		entity.setLine_leader_id(operator_id);

		int iBorrowStatus = 0;
		if (entity.getBorrow_status() != null) iBorrowStatus = entity.getBorrow_status();

		CheckUnqualifiedRecordMapper dao = conn.getMapper(CheckUnqualifiedRecordMapper.class);

		dao.updateByLineLeader(entity);

		if (iBorrowStatus == 1) {
			// 中断信息解除
			CheckUnqualifiedRecordEntity nowEntity = dao.getById(entity);
			String alarm_message_id = nowEntity.getAlarm_message_id();
		
			// 处理人处理信息
			AlarmMesssageSendationEntity sendation = new AlarmMesssageSendationEntity();
				sendation.setAlarm_messsage_id( alarm_message_id );
			sendation.setComment("点检不合格解除");
			sendation.setRed_flg(1);
			sendation.setSendation_id(loginData.getOperator_id());
			sendation.setResolve_time(new Date());

			AlarmMesssageService amService = new AlarmMesssageService();
			amService.replaceAlarmMessageSendation(sendation, conn, triggerList);
		} else if (iBorrowStatus == 2) {
			// 通知设备管理员
			// 发送给NS线长人员
			OperatorMapper oMapper = conn.getMapper(OperatorMapper.class);
			OperatorEntity operator = new OperatorEntity();
			operator.setRole_id(RvsConsts.ROLE_DT_MANAGER);

			List<OperatorNamedEntity> techs = oMapper.searchOperator(operator);

			// 推送需要信息制作信息
			PostMessageMapper pmMapper = conn.getMapper(PostMessageMapper.class);

			PostMessageEntity pmEntity = new PostMessageEntity();
			String checkUnqualifiedRecordKey = entity.getCheck_unqualified_record_key();
			checkUnqualifiedRecordKey = checkUnqualifiedRecordKey.substring(5);
			pmEntity.setContent(loginData.getName() + "对应" + entity.getManage_code() +  
					"的不合格(不合格记录流水号：" + checkUnqualifiedRecordKey + ")希望借用" + entity.getBorrow_manage_no() + "，请确认。");
			pmEntity.setSender_id(loginData.getOperator_id());
			pmEntity.setLevel(1);
			pmEntity.setReason(PostMessageService.BORROW_STATUS_CONFIRM);
			pmMapper.createPostMessage(pmEntity);

			CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
			String lastInsertId = commonMapper.getLastInsertID();

			List<String> fingerOperators = new ArrayList<String>();
			String noticeString = "http://localhost:8080/rvspush/trigger/postMessage/";

			for(OperatorNamedEntity techManager: techs){
				PostMessageEntity postMessageEntity = new PostMessageEntity();
				postMessageEntity.setPost_message_id(lastInsertId);
				postMessageEntity.setReceiver_id(techManager.getOperator_id());
				pmMapper.createPostMessageSendation(postMessageEntity);

				fingerOperators.add(techManager.getOperator_id());
			}
			if (fingerOperators.size() == 1) {
				noticeString += fingerOperators.get(0) + "/"  + fingerOperators.get(0) + "/" + fingerOperators.get(0) + "/"; // 重要的事情
			} else if (fingerOperators.size() == 2) {
				noticeString += fingerOperators.get(0) + "/"  + fingerOperators.get(0) + "/" + fingerOperators.get(1) + "/"; // 重要的事情
			} else {
				for (String fingerOperator : fingerOperators) {
					noticeString += fingerOperator + "/";
				}
			}

			triggerList.add(noticeString);
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
	 * @param triggerList 
	 * @throws Exception 
	 */
	public void updateByTechnology(ActionForm form, HttpServletRequest request, SqlSessionManager conn, List<String> triggerList) throws Exception {
		CheckUnqualifiedRecordEntity entity = new CheckUnqualifiedRecordEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		LoginData loginData = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		String operator_id = loginData.getOperator_id();
		entity.setTechnology_id(operator_id);

		CheckUnqualifiedRecordMapper dao = conn.getMapper(CheckUnqualifiedRecordMapper.class);

		// 确认借用
		Integer iBorrowStatus = entity.getBorrow_status();
		if (iBorrowStatus != null && iBorrowStatus == 3) { // 允许
			CheckUnqualifiedRecordEntity orgEntity = dao.getById(entity);
			if (orgEntity.getBorrow_status() != 3) {
				// 处理人处理信息
				AlarmMesssageSendationEntity sendation = new AlarmMesssageSendationEntity();
					sendation.setAlarm_messsage_id( orgEntity.getAlarm_message_id() );
				sendation.setComment("点检不合格解除");
				sendation.setRed_flg(1);
				sendation.setSendation_id(loginData.getOperator_id());
				sendation.setResolve_time(new Date());

				// 解除中断
				AlarmMesssageService amService = new AlarmMesssageService();
				amService.replaceAlarmMessageSendation(sendation, conn, triggerList);
			}
		}

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
