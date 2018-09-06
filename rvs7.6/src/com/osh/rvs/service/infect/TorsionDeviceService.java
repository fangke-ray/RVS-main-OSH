package com.osh.rvs.service.infect;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.infect.TorsionDeviceEntity;
import com.osh.rvs.form.infect.TorsionDeviceForm;
import com.osh.rvs.mapper.infect.TorsionDeviceMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;


/**
 * 
 * @author lliwanyun
 *
 */
public class TorsionDeviceService {

	/**
	 * 查询力矩设备详细
	 * @param form 表单
	 * @param conn 数据库会话
	 * @param errors 
	 * @return
	 */
	public List<TorsionDeviceForm> search(ActionForm form,SqlSession conn,List<MsgInfo> errors){
		//returnFormList
		List<TorsionDeviceForm> torsionDeviceForms = new ArrayList<TorsionDeviceForm>();

		TorsionDeviceMapper dao = conn.getMapper(TorsionDeviceMapper.class);
		
		//condition
		TorsionDeviceEntity conditionEntity = new TorsionDeviceEntity();
		
		// 数据对象复制到表单
		BeanUtil.copyToBean(form, conditionEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		List<TorsionDeviceEntity> torsionDeviceEntitys = dao.searchTorsionDevice(conditionEntity);
		// 数据对象复制到表单
		BeanUtil.copyToFormList(torsionDeviceEntitys, torsionDeviceForms, CopyOptions.COPYOPTIONS_NOEMPTY, TorsionDeviceForm.class);
		return torsionDeviceForms;
	}
	
	/**
	 * 验证主键不重复
	 * @param form 表单
	 * @param conn 数据库会话
	 * @param errors 
	 */
	public void customValidate(ActionForm form,SqlSessionManager conn,List<MsgInfo> errors){
		TorsionDeviceEntity conditionEntity = new TorsionDeviceEntity();
		// 数据对象复制到表单
		BeanUtil.copyToBean(form,conditionEntity,CopyOptions.COPYOPTIONS_NOEMPTY);
		
		TorsionDeviceMapper dao = conn.getMapper(TorsionDeviceMapper.class);
		
		//当输入个位数字时补0
		String fullSeq = this.addZero(Integer.parseInt(conditionEntity.getSeq()));
		conditionEntity.setSeq(fullSeq);
		List<String> manageIds =dao.searchManageCode(conditionEntity);
		if(manageIds != null && manageIds.size() > 0){
			MsgInfo error = new MsgInfo();
			error.setComponentid("manage_id");
			error.setErrcode("dbaccess.columnNotUnique");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "管理编号",
					conditionEntity.getManage_code(),"力矩点检序号为"+conditionEntity.getSeq()));
			errors.add(error);
		}
	}
	
	public void insertTorsionDevice(TorsionDeviceForm conditionForm,SqlSessionManager conn){
		TorsionDeviceMapper dao = conn.getMapper(TorsionDeviceMapper.class);
		TorsionDeviceEntity conditionEntity = new TorsionDeviceEntity();
		// 数据对象复制到表单
		BeanUtil.copyToBean(conditionForm, conditionEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//当输入个位数字时补0
		String fullSeq = this.addZero(Integer.parseInt(conditionEntity.getSeq()));
		conditionEntity.setSeq(fullSeq);
		
		dao.insertTorsionDevice(conditionEntity);
	}
	
	//力矩点检序号补齐成两位
	public String addZero(Integer seq){
		 DecimalFormat df = new DecimalFormat("00");
		 String fullSeq = df.format(seq);
		 return fullSeq;
	}
	
	/**
	 * referchoose--管理编号
	 * @param conn 数据库会话
	 * @return
	 */
	public String getManageCodes(SqlSession conn){
		List<String[]> lst = new ArrayList<String[]>();

		TorsionDeviceMapper dao = conn.getMapper(TorsionDeviceMapper.class);
		List<TorsionDeviceEntity> mcEntitys = dao.searchManageCodes();

		List<TorsionDeviceForm> mcForms = new ArrayList<TorsionDeviceForm>();
		if(mcEntitys !=null && mcEntitys.size()>0){
			// 数据对象复制到表单
			BeanUtil.copyToFormList(mcEntitys, mcForms, null, TorsionDeviceForm.class);
			for (TorsionDeviceForm resultForm : mcForms) {
				String[] p = new String[2];
				p[0] = resultForm.getManage_id();
				p[1] = resultForm.getManage_code();
				lst.add(p);
			}
			String mcReferChooser = CodeListUtils.getReferChooser(lst);
			return mcReferChooser;
		}else{
			return "";
		}
	}

	/**
	 * 力矩设备双击修改
	 * @param conditionForm 表单
	 * @param conn 数据库会话
	 */
	public void updateTorsionDevice(TorsionDeviceForm conditionForm,SqlSessionManager conn) {
		TorsionDeviceMapper dao = conn.getMapper(TorsionDeviceMapper.class);
		
		TorsionDeviceEntity conditionEntity = new TorsionDeviceEntity();		
		// 数据对象复制到表单
		BeanUtil.copyToBean(conditionForm, conditionEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//当输入个位数字时补0
		String fullSeq = this.addZero(Integer.parseInt(conditionEntity.getSeq()));
		conditionEntity.setSeq(fullSeq);
		
		dao.updateTorsionDevice(conditionEntity);
	}

	/**
	 * 删除力矩设备
	 * @param form 表单
	 * @param conn 数据库会话
	 * @param session
	 * @param errors
	 */
	public void deleteToolsManage(ActionForm form,SqlSessionManager conn,List<MsgInfo> errors) {
		TorsionDeviceMapper dao = conn.getMapper(TorsionDeviceMapper.class);
		
		TorsionDeviceEntity conditionEntity = new TorsionDeviceEntity();	
		// 数据对象复制到表单
		BeanUtil.copyToBean(form, conditionEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		//删除
		dao.deleteTorsionDevice(conditionEntity);
	}

}
