package com.osh.rvs.service.inline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.infect.DryingJobEntity;
import com.osh.rvs.bean.infect.DryingOvenDeviceEntity;
import com.osh.rvs.form.infect.DryingJobForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.infect.DryingJobMapper;
import com.osh.rvs.mapper.infect.DryingOvenDeviceMapper;

import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class DryingJobService {

	/**
	 * 检索
	 * 
	 * @param form
	 * @param conn
	 */
	public List<DryingJobForm> serarch(ActionForm form, SqlSession conn) {
		DryingJobEntity entity = new DryingJobEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		// 查询
		DryingJobMapper dao = conn.getMapper(DryingJobMapper.class);
		List<DryingJobEntity> list = dao.search(entity);

		List<DryingJobForm> respList = new ArrayList<DryingJobForm>();
		if (list != null && list.size() > 0) {
			BeanUtil.copyToFormList(list, respList,	CopyOptions.COPYOPTIONS_NOEMPTY, DryingJobForm.class);
		}

		return respList;

	}

	/**
	 * 烘箱设备
	 * 
	 * @param conn
	 * @return
	 */
	public String getDryingOvenDeviceReferChooser(SqlSession conn) {
		List<String[]> list = new ArrayList<String[]>();

		DryingOvenDeviceEntity entity = new DryingOvenDeviceEntity();
		DryingOvenDeviceMapper dao = conn.getMapper(DryingOvenDeviceMapper.class);
		// 检索结果
		List<DryingOvenDeviceEntity> resultList = dao.search(entity);
		for (DryingOvenDeviceEntity connd : resultList) {
			String[] str = new String[8];
			str[0] = connd.getDevice_manage_id();
			str[1] = connd.getManage_code();
			str[2] = connd.getDevice_name();
			str[3] = connd.getModel_name();
			str[4] = connd.getSection_name();
			str[5] = connd.getProcess_code();
			str[6] = CodeListUtils.getValue("drying_oven_setting_temperature", connd.getSetting_temperature().toString());
			str[7] = "库位数:"+connd.getSlot().toString();

			list.add(str);
		}

		String mcreferChooser = CodeListUtils.getReferChooser(list);
		return mcreferChooser;
	}

	/**
	 * 新建
	 * @param form
	 * @param request
	 * @param conn
	 */
	public void insert(ActionForm form,HttpServletRequest request,SqlSessionManager conn)throws Exception{
		DryingJobEntity entity = new DryingJobEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//硬化条件
		Integer hardening_condition = entity.getHardening_condition();
		if(hardening_condition==0){
			entity.setDevice_manage_id(null);
			entity.setSlots(null);
		}
		
		//新建烘干作业
		DryingJobMapper dao = conn.getMapper(DryingJobMapper.class);
		dao.insertDryingJob(entity);
		
		CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
		String lastInsertID = commonMapper.getLastInsertID();
		
		Map<String,String[]> map=(Map<String,String[]>)request.getParameterMap();
		List<DryingJobEntity> categoryIDs = new AutofillArrayList<DryingJobEntity>(DryingJobEntity.class);
		
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
		// 整理提交数据
		for (String parameterKey : map.keySet()) {
			 Matcher m = p.matcher(parameterKey);
			 if (m.find()) {
					String connd = m.group(1);
					if ("drying_job_of_category".equals(connd)) {
						String column = m.group(2);
						int icounts = Integer.parseInt(m.group(3));
						String[] value = map.get(parameterKey);
						if ("category_id".equals(column)) {
							categoryIDs.get(icounts).setCategory_id(value[0]);
							categoryIDs.get(icounts).setDrying_job_id(lastInsertID);
						}
					}
			 }
		}
		
		//新建烘干作业机种
		for(DryingJobEntity dryingJobEntity:categoryIDs){
			dao.insertDryingJobOfCategory(dryingJobEntity);
		}
	}
	
	/**
	 * 获取烘干作业详细信息
	 * @param form
	 * @param conn
	 * @return
	 */
	public DryingJobForm getDryingJobDetail(ActionForm form,SqlSession conn){
		DryingJobMapper dao = conn.getMapper(DryingJobMapper.class);
		
		DryingJobEntity entity = new DryingJobEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		DryingJobEntity dryingJobEntity = dao.getDryingJobDetail(entity);
		
		//复制数据到表单对象
		DryingJobForm dryingJobForm = new DryingJobForm();
		BeanUtil.copyToForm(dryingJobEntity, dryingJobForm, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		return dryingJobForm;
	}
	
	/**
	 * 烘干作业机种
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<DryingJobForm> getDryingJobOfCategory(ActionForm form,SqlSession conn){
		DryingJobEntity entity = new DryingJobEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		// 查询
		DryingJobMapper dao = conn.getMapper(DryingJobMapper.class);
		List<DryingJobEntity> list = dao.getDryingJobOfCategory(entity);

		List<DryingJobForm> respList = new ArrayList<DryingJobForm>();
		if (list != null && list.size() > 0) {
			BeanUtil.copyToFormList(list, respList,	CopyOptions.COPYOPTIONS_NOEMPTY, DryingJobForm.class);
		}

		return respList;
	}
	
	/**
	 * 更新烘干作业
	 * @param form
	 * @param request
	 * @param conn
	 * @throws Exception
	 */
	public void update(ActionForm form,HttpServletRequest request,SqlSessionManager conn) throws Exception{
		DryingJobEntity entity = new DryingJobEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//硬化条件
		Integer hardening_condition = entity.getHardening_condition();
		if(hardening_condition==0){
			entity.setDevice_manage_id(null);
			entity.setSlots(null);
		}
		
		//更新烘干作业
		DryingJobMapper dao = conn.getMapper(DryingJobMapper.class);
		dao.updateDryingJob(entity);
		
		String drying_job_id = entity.getDrying_job_id();
		dao.deleteDryingJobCategoryById(drying_job_id);
		
		Map<String,String[]> map=(Map<String,String[]>)request.getParameterMap();
		List<DryingJobEntity> categoryIDs = new AutofillArrayList<DryingJobEntity>(DryingJobEntity.class);
		
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
		// 整理提交数据
		for (String parameterKey : map.keySet()) {
			 Matcher m = p.matcher(parameterKey);
			 if (m.find()) {
					String connd = m.group(1);
					if ("drying_job_of_category".equals(connd)) {
						String column = m.group(2);
						int icounts = Integer.parseInt(m.group(3));
						String[] value = map.get(parameterKey);
						if ("category_id".equals(column)) {
							categoryIDs.get(icounts).setCategory_id(value[0]);
							categoryIDs.get(icounts).setDrying_job_id(drying_job_id);
						}
					}
			 }
		}
		
		//新建烘干作业机种
		for(DryingJobEntity dryingJobEntity:categoryIDs){
			dao.insertDryingJobOfCategory(dryingJobEntity);
		}
	}
	
	public void delete(ActionForm form,SqlSessionManager conn)throws Exception{
		DryingJobEntity entity = new DryingJobEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		DryingJobMapper dao = conn.getMapper(DryingJobMapper.class);
		dao.deleteDryingJobById(entity.getDrying_job_id());
		dao.deleteDryingJobCategoryById(entity.getDrying_job_id());
	}

}
