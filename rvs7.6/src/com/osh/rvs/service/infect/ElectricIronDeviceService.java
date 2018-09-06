package com.osh.rvs.service.infect;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.infect.ElectricIronDeviceEntity;
import com.osh.rvs.form.infect.ElectricIronDeviceForm;
import com.osh.rvs.mapper.infect.ElectricIronDeviceMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class ElectricIronDeviceService {

	/**
	 * 初始查询
	 * @param conditionEntity 条件
	 * @param conn 数据库连接
	 * @param errors
	 */
	public List<ElectricIronDeviceForm> searchElectricIronDevice(ElectricIronDeviceEntity conditionEntity, SqlSession conn,List<MsgInfo> errors) {
		List<ElectricIronDeviceForm> returnForms = new ArrayList<ElectricIronDeviceForm>();
		ElectricIronDeviceMapper dao =conn.getMapper(ElectricIronDeviceMapper.class);
		
		//电烙铁工具管理详细
		List<ElectricIronDeviceEntity> returnEntitys  = dao.searchElectricIronDevice(conditionEntity);
		BeanUtil.copyToFormList(returnEntitys,returnForms,CopyOptions.COPYOPTIONS_NOEMPTY,ElectricIronDeviceForm.class);
		
		return returnForms;
	}
	
	//验证电烙铁工具数据是否已经存在
	public void customValidate(ActionForm form,SqlSessionManager conn,List<MsgInfo> errors){
		ElectricIronDeviceEntity conditionEntity = new ElectricIronDeviceEntity();
		// 数据对象复制到表单
		BeanUtil.copyToBean(form,conditionEntity,CopyOptions.COPYOPTIONS_NOEMPTY);
		ElectricIronDeviceMapper dao =conn.getMapper(ElectricIronDeviceMapper.class);
		
		//当输入个位数字时补0
		String fullSeq = this.addZero(Integer.parseInt(conditionEntity.getSeq()));
		conditionEntity.setSeq(fullSeq);
		
		List<ElectricIronDeviceEntity> returnEntitys  = dao.ElectricIronDeviceisExist(conditionEntity);
		if(returnEntitys !=null && returnEntitys.size()>0){
			MsgInfo error = new MsgInfo();
			error.setComponentid("manage_id");
			error.setErrcode("dbaccess.columnNotUnique");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "管理编号",
					conditionEntity.getManage_code(),"温度点检序号为"+conditionEntity.getSeq()));
			errors.add(error);
		}
	}
	
	/**
	 * 新建电烙铁工具
	 * @param conditionEntity 条件
	 * @param conn 数据库连接
	 * @param errors
	 */
	public void insertElectricIronDevice(ElectricIronDeviceEntity conditionEntity,SqlSessionManager conn,List<MsgInfo> errors){
		ElectricIronDeviceMapper dao =conn.getMapper(ElectricIronDeviceMapper.class); 
		
		//当输入个位数字时补0
		String fullSeq = this.addZero(Integer.parseInt(conditionEntity.getSeq()));
		conditionEntity.setSeq(fullSeq);
		
		dao.insertElectricIronDevice(conditionEntity);
	}
	
	//温度点检序号补齐成两位
	public String addZero(Integer seq){
		 DecimalFormat df = new DecimalFormat("00");
		 String fullSeq = df.format(seq);
		 return fullSeq;
	}
	
	/**
	 * 管理编号+品名下拉选择
	 * @param conn
	 * @return
	 */
	public String getMCReferChooser(SqlSession conn){
		
		List<String[]> list = new ArrayList<String[]>();
		
		ElectricIronDeviceMapper dao = conn.getMapper(ElectricIronDeviceMapper.class);
		List<ElectricIronDeviceEntity> manageCodeNames = dao.searchManageCodes();
		for(ElectricIronDeviceEntity returnBean:manageCodeNames){
			String[] str = new String[3];
			str[0] = returnBean.getDevices_manage_id();
			str[1] = returnBean.getManage_code();
			str[2] = returnBean.getDevice_name()==null ? "":returnBean.getDevice_name();
			
			list.add(str);
		}
		
		String mcreferChooser = CodeListUtils.getReferChooser(list);
		return mcreferChooser;
	}
	
	/**
	 * 设备品名下拉选择
	 * @param conn
	 * @return
	 */
	public String getDNReferChooser(SqlSession conn){
		List<String[]> list = new ArrayList<String[]>();
		ElectricIronDeviceMapper dao = conn.getMapper(ElectricIronDeviceMapper.class);
		List<ElectricIronDeviceEntity> deviceNames = dao.searchDeviceNames();
		for(ElectricIronDeviceEntity returnBean:deviceNames){
			String[] str = new String[2];
			str[0] = returnBean.getDevices_type_id();
			str[1] = returnBean.getDevice_name();
			
			list.add(str);
		}
		
		String dnReferChooder = CodeListUtils.getReferChooser(list);
		return dnReferChooder;
	}

	/**
	 * 双击修改
	 * @param conditionEntity
	 * @param conn
	 * @param errors
	 */
	public void updateElectricIronDevice(ElectricIronDeviceEntity conditionEntity, SqlSessionManager conn,List<MsgInfo> errors) {
		ElectricIronDeviceMapper dao = conn.getMapper(ElectricIronDeviceMapper.class);
		dao.updateElectricIronDevice(conditionEntity);
	}

	/**
	 * 删除
	 * @param conditionEntity 条件s
	 * @param conn 数据库
	 * @param errors
	 */
	public void deleteElectricIronDevice(ElectricIronDeviceEntity conditionEntity, SqlSessionManager conn,List<String> errors) {
		ElectricIronDeviceMapper dao = conn.getMapper(ElectricIronDeviceMapper.class);
		dao.deleteElectricIronDevice(conditionEntity);
	}
}
