package com.osh.rvs.service.inline;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.infect.DryingOvenDeviceEntity;
import com.osh.rvs.form.infect.DryingOvenDeviceForm;
import com.osh.rvs.mapper.infect.DryingOvenDeviceMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

/**
 * 
 * @Title DryingOvenDeviceService.java
 * @Project rvs
 * @Package com.osh.rvs.service
 * @ClassName: DryingOvenDeviceService
 * @Description: 烘箱管理
 * @author lxb
 * @date 2016-8-2 下午5:16:51
 */
public class DryingOvenDeviceService {
	
	public List<DryingOvenDeviceForm> search(ActionForm form,SqlSession conn){
		DryingOvenDeviceEntity entity = new DryingOvenDeviceEntity();
		//复制表单到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		DryingOvenDeviceMapper dao = conn.getMapper(DryingOvenDeviceMapper.class);
		//检索结果
		List<DryingOvenDeviceEntity> list = dao.search(entity);
		
		List<DryingOvenDeviceForm> respForms = new ArrayList<DryingOvenDeviceForm>();
		
		if(list != null && list.size()>0){
			//复制数据到表单
			BeanUtil.copyToFormList(list, respForms, CopyOptions.COPYOPTIONS_NOEMPTY, DryingOvenDeviceForm.class);
			
			for(int i = 0;i < respForms.size();i++){
				DryingOvenDeviceForm dryingOvenDeviceForm = respForms.get(i);
				//设定温度
				String setting_temperature = dryingOvenDeviceForm.getSetting_temperature();
				respForms.get(i).setLower_limit(CodeListUtils.getValue("drying_oven_lower_limit", setting_temperature));
				respForms.get(i).setUpper_limit(CodeListUtils.getValue("drying_oven_upper_limit", setting_temperature));
			}
		}
		return respForms;
	}
	
	
	/**
	 * 管理编号+品名下拉选择
	 * @param conn
	 * @return
	 */
	public String getReferChooser(SqlSession conn){
		
		List<String[]> list = new ArrayList<String[]>();
		
		DryingOvenDeviceMapper dao = conn.getMapper(DryingOvenDeviceMapper.class);
		List<DryingOvenDeviceEntity> manageCodeNames = dao.searchAllDryingOvenDevice();
		for(DryingOvenDeviceEntity returnBean:manageCodeNames){
			String[] str = new String[6];
			str[0] = returnBean.getDevice_manage_id();
			str[1] = returnBean.getManage_code();
			str[2] = returnBean.getDevice_name();
			str[3] = returnBean.getModel_name() == null ? "" : returnBean.getModel_name();
			str[4] = returnBean.getSection_name();
			str[5] = returnBean.getProcess_code();
			
			list.add(str);
		}
		
		String mcreferChooser = CodeListUtils.getReferChooser(list);
		return mcreferChooser;
	}
	
	public void insert(ActionForm form,SqlSessionManager conn,List<MsgInfo> errors) throws Exception{
		DryingOvenDeviceEntity entity = new DryingOvenDeviceEntity();
		//复制表单到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		DryingOvenDeviceMapper dao = conn.getMapper(DryingOvenDeviceMapper.class);
		
		//查询烘箱是否存在
		DryingOvenDeviceEntity connd = dao.checkIsExist(entity);
		//烘箱存在
		if(connd != null){
			MsgInfo error = new MsgInfo();
			error.setErrcode("dbaccess.recordDuplicated");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordDuplicated","管理编号为:"+entity.getManage_code()));
			errors.add(error);
			
			return;
		}
		
		dao.insert(entity);
	}
	
	public void update(ActionForm form,SqlSessionManager conn)throws Exception{
		DryingOvenDeviceEntity entity = new DryingOvenDeviceEntity();
		//复制表单到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		DryingOvenDeviceMapper dao = conn.getMapper(DryingOvenDeviceMapper.class);
		dao.update(entity);
		
	}
	
	public void delete(ActionForm form,SqlSessionManager conn)throws Exception{
		DryingOvenDeviceEntity entity = new DryingOvenDeviceEntity();
		//复制表单到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		DryingOvenDeviceMapper dao = conn.getMapper(DryingOvenDeviceMapper.class);
		dao.delete(entity);
	}
}
