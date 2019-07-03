package com.osh.rvs.service.equipment;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.equipment.DeviceJigRepairRecordEntity;
import com.osh.rvs.bean.master.DevicesManageEntity;
import com.osh.rvs.bean.master.ToolsManageEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.equipment.DeviceJigRepairRecordForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.equipment.DeviceJigRepairRecordMapper;
import com.osh.rvs.mapper.master.DevicesManageMapper;
import com.osh.rvs.mapper.master.ToolsManageMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.FileUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class DeviceJigRepairService {

	/**
	 * 检索
	 * @param form
	 * @param conn
	 * @param listResponse 
	 * @param errors
	 * @return
	 */
	public void search(ActionForm form,SqlSession conn, Map<String, Object> listResponse, List<MsgInfo> errors) {
		DeviceJigRepairRecordMapper mapper = conn.getMapper(DeviceJigRepairRecordMapper.class);
		DeviceJigRepairRecordEntity condition = new DeviceJigRepairRecordEntity();

		BeanUtil.copyToBean(form, condition, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<DeviceJigRepairRecordEntity> list = mapper.search(condition);
		List<DeviceJigRepairRecordForm> formlist = new ArrayList<DeviceJigRepairRecordForm>();

		for (DeviceJigRepairRecordEntity entity : list) {
			DeviceJigRepairRecordForm resForm = new DeviceJigRepairRecordForm();
			BeanUtil.copyToForm(entity, resForm, CopyOptions.COPYOPTIONS_NOEMPTY);

			// 照片
			String targetPath = PathConsts.BASE_PATH + PathConsts.PHOTOS + "\\dj_repair\\" + entity.getDevice_jig_repair_record_key();
			if (new File(targetPath).exists()) {
				resForm.setPhoto_flg("1");
			}

			formlist.add(resForm);
		}

		listResponse.put("recordList", formlist);
	}

	/**
	 * 管理编号Check + 提出权限Check
	 * 
	 * @param user
	 * @param form
	 * @param errors
	 * @param conn
	 */
	
	public void checkSubmitPrivacy(LoginData user, ActionForm form,List<MsgInfo> errors, SqlSession conn) {
		DeviceJigRepairRecordForm djrrForm = (DeviceJigRepairRecordForm) form;
		String objectType = djrrForm.getObject_type();
		String manageId = djrrForm.getManage_id();
		if (("1".equals(objectType) || "2".equals(objectType)) && CommonStringUtil.isEmpty(manageId)) {
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setComponentid("manage_id");
			msgInfo.setErrcode("validator.required");
			msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "管理编号"));
			errors.add(msgInfo);
			return;
		}
		
		List<Integer> privacies = user.getPrivacies();
		if (privacies.contains(RvsConsts.PRIVACY_TECHNOLOGY) || privacies.contains(RvsConsts.PRIVACY_PROCESSING)) {
			return;
		}


		if (!djrrForm.getLine_id().equals(user.getLine_id())) {
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setComponentid("line_id");
			msgInfo.setErrcode("privacy.objectOutOfDomain");
			msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("privacy.objectOutOfDomain", "工程"));
			errors.add(msgInfo);
		}

		if ("1".equals(objectType)) {
			DevicesManageMapper mapper = conn.getMapper(DevicesManageMapper.class);

			DevicesManageEntity entity = mapper.getByKey(manageId);
			if (!user.getOperator_id().equals(entity.getResponsible_operator_id())
					&& !user.getOperator_id().equals(entity.getManager_operator_id())) {
				MsgInfo msgInfo = new MsgInfo();
				msgInfo.setComponentid("manage_id");
				msgInfo.setErrcode("privacy.objectOutOfDomain");
				msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("privacy.objectOutOfDomain", "设备/一般工具"));
				errors.add(msgInfo);
			}
		} else if ("2".equals(objectType)) {
			ToolsManageMapper mapper = conn.getMapper(ToolsManageMapper.class);
			ToolsManageEntity entity = mapper.getByKey(manageId);
			if (!user.getOperator_id().equals(entity.getResponsible_operator_id())
					&& !user.getOperator_id().equals(entity.getManager_operator_id())) {
				MsgInfo msgInfo = new MsgInfo();
				msgInfo.setComponentid("manage_id");
				msgInfo.setErrcode("privacy.objectOutOfDomain");
				msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("privacy.objectOutOfDomain", "专用工具"));
				errors.add(msgInfo);
			}
		}
	}

	/**
	 * 报修处理--直接报修
	 * @param form
	 * @param user 
	 * @param conn
	 */
	public void sumbit(ActionForm form, LoginData user, SqlSessionManager conn) {
		DeviceJigRepairRecordEntity insertEntity = new DeviceJigRepairRecordEntity();

		BeanUtil.copyToBean(form, insertEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		DeviceJigRepairRecordMapper mapper = conn.getMapper(DeviceJigRepairRecordMapper.class);

		insertEntity.setSubmit_time(new Date());

		//新建设备治工具维修记录
		mapper.insertRecord(insertEntity);

		CommonMapper cMapper = conn.getMapper(CommonMapper.class);

		String key = cMapper.getLastInsertID();

		insertEntity.setDevice_jig_repair_record_key(key);
		insertEntity.setSubmitter_id(user.getOperator_id());
		
		//新建设备治工具申请记录
		mapper.insertSubmit(insertEntity);
	}

	/**
	 * 验收处理
	 * @param form
	 * @param user 
	 * @param conn
	 */
	public void confirm(ActionForm form, LoginData user, SqlSessionManager conn) {
		DeviceJigRepairRecordEntity confirmEntity = new DeviceJigRepairRecordEntity();

		BeanUtil.copyToBean(form, confirmEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		confirmEntity.setConfirmer_id(user.getOperator_id());

		DeviceJigRepairRecordMapper mapper = conn.getMapper(DeviceJigRepairRecordMapper.class);

		mapper.updateConfirm(confirmEntity);
	}

	/**
	 * 取得编辑详细
	 * @param key
	 * @param conn
	 * @return
	 */
	public DeviceJigRepairRecordForm detail(String key, SqlSession conn) {
		DeviceJigRepairRecordMapper mapper = conn.getMapper(DeviceJigRepairRecordMapper.class);
		DeviceJigRepairRecordEntity entity = mapper.getDetailForRepair(key);

		if (entity == null) {
			return null;
		} else {
			DeviceJigRepairRecordForm retForm = new DeviceJigRepairRecordForm();

			BeanUtil.copyToForm(entity, retForm, CopyOptions.COPYOPTIONS_NOEMPTY);

			return retForm;
		}
	}

	public void checkConfirmPrivacy(LoginData user, ActionForm form,List<MsgInfo> errors, SqlSession conn) {
		List<Integer> privacies = user.getPrivacies();
		if (privacies.contains(RvsConsts.PRIVACY_TECHNOLOGY)
			|| privacies.contains(RvsConsts.PRIVACY_PROCESSING)) {
			return;
		}

		DeviceJigRepairRecordForm djrrForm = (DeviceJigRepairRecordForm) form;

		String key = djrrForm.getDevice_jig_repair_record_key();

		DeviceJigRepairRecordMapper mapper = conn.getMapper(DeviceJigRepairRecordMapper.class);
		DeviceJigRepairRecordEntity entity = mapper.getDetailForRepair(key);
		
		String manageId = entity.getManage_id();

		if (entity.getObject_type() == 1) {
			DevicesManageMapper devMapper = conn.getMapper(DevicesManageMapper.class);

			DevicesManageEntity dmEntity = devMapper.getByKey(manageId);
			if (!user.getOperator_id().equals(dmEntity.getResponsible_operator_id())
					&& !user.getOperator_id().equals(dmEntity.getManager_operator_id())) {
				MsgInfo msgInfo = new MsgInfo();
				msgInfo.setComponentid("manage_id");
				msgInfo.setErrcode("privacy.objectOutOfDomain");
				msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("privacy.objectOutOfDomain", "设备/一般工具"));
				errors.add(msgInfo);
			}
		} else if (entity.getObject_type() == 2) {
			ToolsManageMapper toolsManageMapper = conn.getMapper(ToolsManageMapper.class);
			ToolsManageEntity toolsManageEntity = toolsManageMapper.getByKey(manageId);
			if (!user.getOperator_id().equals(toolsManageEntity.getResponsible_operator_id())
					&& !user.getOperator_id().equals(toolsManageEntity.getManager_operator_id())) {
				MsgInfo msgInfo = new MsgInfo();
				msgInfo.setComponentid("manage_id");
				msgInfo.setErrcode("privacy.objectOutOfDomain");
				msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("privacy.objectOutOfDomain", "专用工具"));
				errors.add(msgInfo);
			}
		}
	}

	/**
	 * 编辑数据检查和整理
	 * @param form
	 * @param conn
	 * @param errors
	 */
	public void repairEdit(ActionForm form, LoginData user,SqlSessionManager conn) {
		DeviceJigRepairRecordForm djrrForm = (DeviceJigRepairRecordForm) form;
		
		DeviceJigRepairRecordEntity editEntity = new DeviceJigRepairRecordEntity();
		BeanUtil.copyToBean(djrrForm, editEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		if(!CommonStringUtil.isEmpty(djrrForm.getRepair_complete_time())){
			editEntity.setRepair_complete_time(Calendar.getInstance().getTime());
		}
		
		DeviceJigRepairRecordMapper mapper = conn.getMapper(DeviceJigRepairRecordMapper.class);
		mapper.updateRecord(editEntity);

		// 插入维修者
		String existMaintainer = editEntity.getMaintainer_id();
		boolean insMaintainer = false;
		if (CommonStringUtil.isEmpty(existMaintainer)) {
			insMaintainer = true;
		} else {
			insMaintainer = true;
			String[] arrMaintainer = existMaintainer.split("/");
			for (String maintainer : arrMaintainer) {
				if (maintainer.equals(user.getOperator_id())) {
					insMaintainer = false;
					break;
				}
			}
		}

		if (insMaintainer) {
			editEntity.setMaintainer_id(user.getOperator_id());
			mapper.insertMaintainer(editEntity);
		}

	}

	public void copyPhoto(String device_jig_repair_record_key, String photo_file_name) {
		// 把图片拷贝到目标文件夹下
		String today = DateUtil.toString(new Date(), "yyyyMM");
		String tempFilePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + today + "\\" + photo_file_name;
		String targetPath = PathConsts.BASE_PATH + PathConsts.PHOTOS + "\\dj_repair\\" + device_jig_repair_record_key;
		File confFile = new File(tempFilePath);
		if (confFile.exists()) {
			FileUtils.copyFile(tempFilePath, targetPath, true);
		}
	}

	public void delPhoto(String device_jig_repair_record_key) {
		String targetPath = PathConsts.BASE_PATH + PathConsts.PHOTOS + "\\dj_repair\\" + device_jig_repair_record_key;
		File confFile = new File(targetPath);
		if (confFile.exists()) {
			confFile.delete();
		}
	}
}
