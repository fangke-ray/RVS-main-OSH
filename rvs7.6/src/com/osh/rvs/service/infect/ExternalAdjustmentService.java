package com.osh.rvs.service.infect;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.infect.ExternalAdjustmentEntity;
import com.osh.rvs.bean.master.DevicesTypeEntity;
import com.osh.rvs.bean.master.ToolsTypeEntity;
import com.osh.rvs.form.infect.ExternalAdjustmentForm;
import com.osh.rvs.form.master.DevicesTypeForm;
import com.osh.rvs.form.master.ToolsTypeForm;
import com.osh.rvs.mapper.infect.ExternalAdjustmentMapper;
import com.osh.rvs.mapper.master.DevicesTypeMapper;
import com.osh.rvs.mapper.master.ToolsTypeMapper;

import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.service
 * @ClassName: ExternalAdjustmentService
 * @Description:检查机器校正Service
 * @author lxb
 * @date 2014-9-6 下午7:20:56
 * 
 */
public class ExternalAdjustmentService {
	/**
	 * 检索
	 * 
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<ExternalAdjustmentForm> search(ActionForm form, SqlSession conn) {
		ExternalAdjustmentEntity entity = new ExternalAdjustmentEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		String id=entity.getDevices_type_id();
		if(!CommonStringUtil.isEmpty(id)){
			entity.setDevices_type_id(id.split("-")[0]);
			entity.setObject_type(Integer.valueOf(id.split("-")[1]));
		}

		ExternalAdjustmentMapper dao = conn.getMapper(ExternalAdjustmentMapper.class);
		List<ExternalAdjustmentEntity> list = dao.search(entity);

		List<ExternalAdjustmentForm> retutrFormLists = new ArrayList<ExternalAdjustmentForm>();
		if (list.size() > 0) {
			// 复制数据到表单对象
			BeanUtil.copyToFormList(list, retutrFormLists, CopyOptions.COPYOPTIONS_NOEMPTY, ExternalAdjustmentForm.class);
			return retutrFormLists;
		} else {
			return null;
		}
	}

	/**
	 * 管理编号下拉框
	 * 
	 * @param conn
	 * @return
	 */
	public String getManageCodeReferChooser(SqlSession conn) {
		// 取得权限下拉框信息
		List<String[]> dList = new ArrayList<String[]>();
		ExternalAdjustmentMapper dao = conn.getMapper(ExternalAdjustmentMapper.class);
		List<ExternalAdjustmentEntity> list = dao.getDeviceManageCodeReferChooser();//设备

		// 建立页面返回表单
		List<ExternalAdjustmentForm> ldf = new ArrayList<ExternalAdjustmentForm>();

		String pReferChooser="";
		
		if (list != null && list.size() > 0) {
			// 数据对象复制到表单
			BeanUtil.copyToFormList(list, ldf, null, ExternalAdjustmentForm.class);
			for (ExternalAdjustmentForm form : ldf) {
				String[] dline = new String[4];
				dline[0] = form.getDevices_manage_id()+"-1";
				dline[1] = form.getManage_code();
				dline[2] = form.getName();
				dline[3] = "设备";
				dList.add(dline);
			}
		}
		
		List<ExternalAdjustmentEntity> list2 = dao.getToolsManageCodeReferChooser();//治具
		// 建立页面返回表单
		List<ExternalAdjustmentForm> ldf2 = new ArrayList<ExternalAdjustmentForm>();
		
		if (list2 != null && list2.size() > 0) {
			// 数据对象复制到表单
			BeanUtil.copyToFormList(list2, ldf2, null, ExternalAdjustmentForm.class);
			for (ExternalAdjustmentForm form : ldf2) {
				String[] dline = new String[4];
				dline[0] = form.getTools_manage_id()+"-2";
				dline[1] = form.getManage_code();
				dline[2] = form.getName();
				dline[3] = "治具";
				dList.add(dline);
			}
		}
		
		
		pReferChooser = CodeListUtils.getReferChooser(dList);
		
		return pReferChooser;
	}

	/**
	 * 管理编号chang事件
	 * 
	 * @param form
	 * @param conn
	 * @return
	 */
	public ExternalAdjustmentForm searchBaseInfo(ActionForm form, SqlSession conn) {
		ExternalAdjustmentEntity entity = new ExternalAdjustmentEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		ExternalAdjustmentMapper dao = conn.getMapper(ExternalAdjustmentMapper.class);

		if (CommonStringUtil.isEmpty(entity.getDevices_manage_id())) {
			return null;
		} else {
			String id=entity.getDevices_manage_id();
			entity.setDevices_manage_id(id.split("-")[0]);
			int object_type=Integer.valueOf(id.split("-")[1]);
			
			ExternalAdjustmentEntity returnEntity;
			if(object_type==1){//设备
				returnEntity = dao.searchDeviceBaseInfo(entity);//设备
			}else{//治具
				returnEntity = dao.searchToolsBaseInfo(entity);//治具
			}
			
			ExternalAdjustmentForm retutrForm = new ExternalAdjustmentForm();
			// 复制数据到表单对象
			BeanUtil.copyToForm(returnEntity, retutrForm, CopyOptions.COPYOPTIONS_NOEMPTY);
			return retutrForm;
		}
	}

	/**
	 * 新建
	 * 
	 * @param form
	 * @param conn
	 */
	public void insert(ActionForm form, SqlSessionManager conn) {
		ExternalAdjustmentEntity entity = new ExternalAdjustmentEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		String id=entity.getDevices_manage_id();
		entity.setDevices_manage_id(id.split("-")[0]);
		entity.setObject_type(Integer.valueOf(id.split("-")[1]));

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Date checked_date = entity.getChecked_date();// 校验日期
		String strDate = DateUtil.toString(checked_date, DateUtil.ISO_DATE_PATTERN);
		String date[] = strDate.split("-");
		int year = Integer.parseInt(date[0]);// 年
		int month = Integer.parseInt(date[1]);// 月
		int day = Integer.parseInt(date[2]);// 日
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.DAY_OF_MONTH, day);

		Integer effect_interval = entity.getEffect_interval();// 有效期

		if (effect_interval == 1) {// 半年
			cal.add(Calendar.MONTH, 6);
		} else if (effect_interval == 2) {// 1年
			cal.add(Calendar.YEAR, 1);
		} else if (effect_interval == 4) {// 2年
			cal.add(Calendar.YEAR, 2);
		} else if (effect_interval == 6) {// 3年
			cal.add(Calendar.YEAR, 3);
		} else if (effect_interval == 12) {// 6年
			cal.add(Calendar.YEAR, 6);
		}
		cal.add(Calendar.DAY_OF_MONTH, -1);

		entity.setAvailable_end_date(cal.getTime());
		ExternalAdjustmentMapper dao = conn.getMapper(ExternalAdjustmentMapper.class);
		dao.insert(entity);
	}

	/**
	 * 送检
	 * 
	 * @param form
	 * @param conn
	 */
	public void checking(ActionForm form, SqlSessionManager conn) {
		ExternalAdjustmentEntity entity = new ExternalAdjustmentEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		ExternalAdjustmentMapper dao = conn.getMapper(ExternalAdjustmentMapper.class);

		dao.checking(entity);
	}

	/**
	 * 费用总计
	 * 
	 * @param conn
	 * @return
	 */
	public Map<String, BigDecimal> getTotalCost(SqlSession conn) {
		ExternalAdjustmentMapper dao = conn.getMapper(ExternalAdjustmentMapper.class);
		List<ExternalAdjustmentEntity> list = dao.getTotalCost();

		Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
		map.put("institution", null);// 校验机构s
		map.put("domestic", null);// 国内厂商校验
		map.put("abroad", null);// 国外厂商校验

		for (int i = 0; i < list.size(); i++) {
			ExternalAdjustmentEntity entity = list.get(i);
			if (entity.getOrganization_type() == 1) {// 校验机构
				map.put("institution", entity.getTotal_check_cost());
			} else if (entity.getOrganization_type() == 2) {// 国内厂商校验
				map.put("domestic", entity.getTotal_check_cost());
			} else if (entity.getOrganization_type() == 3) {// 国外厂商校验
				map.put("abroad", entity.getTotal_check_cost());
			}
		}

		return map;
	}

	/**
	 * 详细信息
	 * @param form
	 * @param conn
	 * @return
	 */
	public ExternalAdjustmentForm getDetailById(ActionForm form, SqlSession conn) {
		ExternalAdjustmentEntity entity = new ExternalAdjustmentEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		ExternalAdjustmentMapper dao = conn.getMapper(ExternalAdjustmentMapper.class);

		ExternalAdjustmentEntity returnEntity = dao.getDetailById(entity);

		ExternalAdjustmentForm retutrForm = new ExternalAdjustmentForm();
		// 复制数据到表单对象
		BeanUtil.copyToForm(returnEntity, retutrForm, CopyOptions.COPYOPTIONS_NOEMPTY);

		return retutrForm;
	}
	
	/**
	 * 更新
	 * @param form
	 * @param conn
	 */
	public void update(ExternalAdjustmentForm externalAdjustmentForm, SqlSessionManager conn){
		String checking_flg=externalAdjustmentForm.getChecking_flg();//校验状态
		
		if("1".equals(checking_flg)){//校验中
			String check_date=externalAdjustmentForm.getChecked_date();//选择的校验日期
			String checked_date_start=externalAdjustmentForm.getChecked_date_start();//原来的校验日期
			
			int isover=DateUtil.compareDate(DateUtil.toDate(check_date, DateUtil.DATE_PATTERN), DateUtil.toDate(checked_date_start, DateUtil.DATE_PATTERN));//比较日期是否改变
			
			ExternalAdjustmentEntity entity = new ExternalAdjustmentEntity();
			// 复制表单数据到对象
			BeanUtil.copyToBean(externalAdjustmentForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
			
			if(isover!=0){//日期不一样
				isover=1;
			}
			entity.setIsover(isover);
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			Date checked_date = entity.getChecked_date();// 校验日期
			String strDate = DateUtil.toString(checked_date, DateUtil.ISO_DATE_PATTERN);
			String date[] = strDate.split("-");
			int year = Integer.parseInt(date[0]);// 年
			int month = Integer.parseInt(date[1]);// 月
			int day = Integer.parseInt(date[2]);// 日
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month-1);
			cal.set(Calendar.DAY_OF_MONTH, day);
			
			Integer effect_interval = entity.getEffect_interval();// 有效期

			if (effect_interval == 1) {// 半年
				cal.add(Calendar.MONTH, 6);
			} else if (effect_interval == 2) {// 1年
				cal.add(Calendar.YEAR, 1);
			} else if (effect_interval == 4) {// 2年
				cal.add(Calendar.YEAR, 2);
			} else if (effect_interval == 6) {// 3年
				cal.add(Calendar.YEAR, 3);
			} else if (effect_interval == 12) {// 6年
				cal.add(Calendar.YEAR, 6);
			}
			cal.add(Calendar.DAY_OF_MONTH, -1);

			entity.setAvailable_end_date(cal.getTime());//给过期日期赋值    
			
			ExternalAdjustmentMapper dao = conn.getMapper(ExternalAdjustmentMapper.class);
			dao.update(entity);
		}else{
			ExternalAdjustmentEntity entity = new ExternalAdjustmentEntity();
			// 复制表单数据到对象
			BeanUtil.copyToBean(externalAdjustmentForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
			ExternalAdjustmentMapper dao = conn.getMapper(ExternalAdjustmentMapper.class);
			
			dao.update(entity);
		}
		
	}
	
	/**
	 * 检索条件品名下拉框
	 * @param conn
	 * @return
	 */
	public String geNameReferChooser(SqlSession conn) {
		// 取得权限下拉框信息
		List<String[]> dList = new ArrayList<String[]>();

		DevicesTypeMapper devicesTypeDao = conn.getMapper(DevicesTypeMapper.class);
		List<DevicesTypeEntity> DevicesTypelist = devicesTypeDao.getAllDeviceName();
		// 建立页面返回表单
		List<DevicesTypeForm> ldf = new ArrayList<DevicesTypeForm>();
		
		String pReferChooser ="";
		
		if (DevicesTypelist != null && DevicesTypelist.size() > 0) {
			// 数据对象复制到表单
			BeanUtil.copyToFormList(DevicesTypelist, ldf, null, DevicesTypeForm.class);
			for (DevicesTypeForm form : ldf) {
				String[] dline = new String[2];
				dline[0] = form.getDevices_type_id()+"-1";
				dline[1] = form.getName();
				dList.add(dline);
			}
		} 
		
		ToolsTypeMapper toolsTypedao = conn.getMapper(ToolsTypeMapper.class);
		List<ToolsTypeEntity> toolsTypeEntities = toolsTypedao.getAllToolsName();

		List<ToolsTypeForm> toolsTypeForms = new ArrayList<ToolsTypeForm>();
		if (toolsTypeEntities != null && toolsTypeEntities.size() > 0) {
			BeanUtil.copyToFormList(toolsTypeEntities, toolsTypeForms, CopyOptions.COPYOPTIONS_NOEMPTY,ToolsTypeForm.class);
			for (ToolsTypeForm toolsTypeForm : toolsTypeForms) {
				String[] tline = new String[2];
				tline[0] = toolsTypeForm.getTools_type_id()+"-2";
				tline[1] = toolsTypeForm.getName();
				dList.add(tline);
			}
		}
		
		pReferChooser = CodeListUtils.getReferChooser(dList);
		return pReferChooser;
		
		
	}
	
	/**
	 * 停止校验
	 * @param form
	 * @param conn
	 */
	public void stopChecking(ActionForm form,SqlSessionManager conn){
		ExternalAdjustmentEntity entity = new ExternalAdjustmentEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		ExternalAdjustmentMapper dao = conn.getMapper(ExternalAdjustmentMapper.class);
		
		dao.stopChecking(entity);
		
	}
	
}
