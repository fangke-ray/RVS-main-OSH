package com.osh.rvs.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.DevicesTypeEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.master.DevicesTypeForm;
import com.osh.rvs.mapper.master.DevicesTypeMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.FileUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

public class DevicesTypeService {

	public static final int SPECIALIZED_FOR_DISINFECT_DEVICE = 3; //消毒设备
	public static final int SPECIALIZED_FOR_STERILIZE_DEVICE = 4;//灭菌设备

	public static Set<String> freeAlterTypes = null;
	private static Set<String> safetyGuideSet = null;
	/**
	 * 设备工具品名 详细
	 * 
	 * @param toolsCheckEntity
	 * @param conn
	 * @return
	 */
	public List<DevicesTypeForm> searchDevicesType(DevicesTypeEntity devicesTypeEntity, SqlSession conn) {

		DevicesTypeMapper dao = conn.getMapper(DevicesTypeMapper.class);
		List<DevicesTypeForm> devicesTypeForms = new ArrayList<DevicesTypeForm>();

		List<DevicesTypeEntity> devicesTypeEntities = dao.searchDeviceType(devicesTypeEntity);

		for(DevicesTypeEntity entity : devicesTypeEntities) {
			DevicesTypeForm devicesTypeForm = new DevicesTypeForm();
			BeanUtil.copyToForm(entity, devicesTypeForm, CopyOptions.COPYOPTIONS_NOEMPTY);
			
			if (getSafetyGuideSet().contains(entity.getDevices_type_id())) {
				devicesTypeForm.setSafety_guide("1");
			}
			devicesTypeForms.add(devicesTypeForm);
		}

		return devicesTypeForms;
	}

	/**
	 * 插入设备工具品名
	 * 
	 * @param form
	 * @param conn
	 * @param errors
	 * @throws Exception
	 */
	public void insertDevicesType(ActionForm form, SqlSessionManager conn,HttpSession session,List<MsgInfo> errors) throws Exception {

		DevicesTypeForm devicesTypeForm = (DevicesTypeForm) form;
		DevicesTypeEntity devicesTypeEntity = new DevicesTypeEntity();

		BeanUtil.copyToBean(devicesTypeForm, devicesTypeEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//当前操作者ID
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		devicesTypeEntity.setUpdated_by(user.getOperator_id());
		
		DevicesTypeMapper dao = conn.getMapper(DevicesTypeMapper.class);
		
		dao.insertDevicesType(devicesTypeEntity);
	}

	/**
	 * 删除设备工具品名
	 * 
	 * @param form
	 * @param conn
	 * @param errors
	 * @throws Exception
	 */
	public void deleteDevicesType(ActionForm form, SqlSessionManager conn,HttpSession session,List<MsgInfo> errors) throws Exception {

		DevicesTypeForm devicesTypeForm = (DevicesTypeForm) form;
		DevicesTypeEntity devicesTypeEntity = new DevicesTypeEntity();

		BeanUtil.copyToBean(devicesTypeForm, devicesTypeEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		//当前操作者ID
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		devicesTypeEntity.setUpdated_by(user.getOperator_id());
		
		DevicesTypeMapper dao = conn.getMapper(DevicesTypeMapper.class);
		dao.deleteDevicesType(devicesTypeEntity);
	}

	/**
	 * 更新设备工具品名
	 * 
	 * @param form
	 * @param conn
	 * @param errors
	 * @throws Exception
	 */
	public void updateDevicesType(ActionForm form, SqlSessionManager conn,HttpSession session,List<MsgInfo> errors) throws Exception {

		DevicesTypeForm devicesTypeForm = (DevicesTypeForm) form;
		DevicesTypeEntity devicesTypeEntity = new DevicesTypeEntity();

		BeanUtil.copyToBean(devicesTypeForm, devicesTypeEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//当前操作者ID
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		devicesTypeEntity.setUpdated_by(user.getOperator_id());
		
		DevicesTypeMapper dao = conn.getMapper(DevicesTypeMapper.class);
		dao.updateDevicesType(devicesTypeEntity);
	}

	/**
	 * 使用设备工具品名下拉列表
	 * 
	 * @param conn
	 * @return
	 */
	public String getDevicesTypeReferChooser(SqlSession conn) {
		// 取得权限下拉框信息
		List<String[]> dList = new ArrayList<String[]>();

		DevicesTypeMapper dao = conn.getMapper(DevicesTypeMapper.class);
		List<DevicesTypeEntity> list = dao.getAllDeviceName();

		// 建立页面返回表单
		List<DevicesTypeForm> ldf = new ArrayList<DevicesTypeForm>();
		if (list != null && list.size() > 0) {
			// 数据对象复制到表单
			BeanUtil.copyToFormList(list, ldf, null, DevicesTypeForm.class);
			for (DevicesTypeForm form : ldf) {
				String[] dline = new String[2];
				dline[0] = form.getDevices_type_id();
				dline[1] = form.getName();
				dList.add(dline);
			}
			String pReferChooser = CodeListUtils.getReferChooser(dList);
			return pReferChooser;
		} else {
			return "";
		}
	}
	
	/**
	 * 设定安全操作守则
	 * @param manage_id
	 * @param photo_file_name
	 */
	public void copyPhoto(String type_id, String photo_file_name) {
		// 把图片拷贝到目标文件夹下
		String today = DateUtil.toString(new Date(), "yyyyMM");
		String tempFilePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + today + "\\" + photo_file_name;
		String targetPath = PathConsts.BASE_PATH + PathConsts.PHOTOS + "\\safety_guide\\" + type_id;
		File confFile = new File(tempFilePath);
		if (confFile.exists()) {
			FileUtils.copyFile(tempFilePath, targetPath, true);
		}
		safetyGuideSet.add(type_id);
	}
	
	public static Set<String> getSafetyGuideSet() {
		if (safetyGuideSet == null) {
			safetyGuideSet = new HashSet<String>();
			File path = new File(PathConsts.BASE_PATH + PathConsts.PHOTOS + "\\safety_guide\\");
			if (path.exists()) {
				for (File file : path.listFiles()) {
					if (file.isFile()) safetyGuideSet.add(file.getName());
				}
			} else {
				path.mkdirs();
			}
		}
		return safetyGuideSet;
	}
}
