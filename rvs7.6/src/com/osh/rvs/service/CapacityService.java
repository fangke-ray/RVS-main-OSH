package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;

import com.osh.rvs.bean.inline.ScheduleEntity;
import com.osh.rvs.form.inline.ScheduleForm;
import com.osh.rvs.mapper.manage.CapacityMapper;

import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class CapacityService {

	/**
	 * 查询产能设定的课室
	 * @param conn 数据库会话
	 * @return
	 */
	public List<ScheduleForm> searchSectionName(SqlSession conn){
		List<ScheduleForm> resultForms = new ArrayList<ScheduleForm>();
		
		CapacityMapper dao = conn.getMapper(CapacityMapper.class);
		
		//查询
		List<ScheduleEntity> resultBeans = dao.searchSectionName();
		
		//copy--to--formList
		BeanUtil.copyToFormList(resultBeans,resultForms,CopyOptions.COPYOPTIONS_NOEMPTY,ScheduleForm.class);
		
		return resultForms;
	}
	
	/**
	 * 查询所有的最大产能
	 * @param conn
	 * @return
	 */
	public List<ScheduleForm> searchCapacitySetting(SqlSession conn){

		List<ScheduleForm> resultForms = new ArrayList<ScheduleForm>();
		
		CapacityMapper dao = conn.getMapper(CapacityMapper.class);
		
		//查询
		List<ScheduleEntity> resultBeans = dao.searchCapacitySetting();
		
		//copy--to--formList
		BeanUtil.copyToFormList(resultBeans,resultForms,CopyOptions.COPYOPTIONS_NOEMPTY,ScheduleForm.class);
		
		return resultForms;
	
	}
	
	/**
	 * 修改产能
	 * @param form 表单
	 * @param conn 数据库会话
	 */
	public void updateUpperLimit(HttpServletRequest request,SqlSessionManager conn){
		CapacityMapper dao = conn.getMapper(CapacityMapper.class);
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		List<ScheduleForm> pareForms = new AutofillArrayList<ScheduleForm>(ScheduleForm.class);
		Map<String,String[]> parameterMap = request.getParameterMap();
		for(String parameterKey : parameterMap.keySet()){
			Matcher m = p.matcher(parameterKey);
			if(m.find()){
				String entity = m.group(1);
				if ("update".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameterMap.get(parameterKey);

					if ("line_id".equals(column)) {
						pareForms.get(icounts).setLine_id(value[0]);
					} else if ("px".equals(column)) {
						pareForms.get(icounts).setPx(value[0]);
					} else if ("level".equals(column)) {
						pareForms.get(icounts).setLevel(value[0]);
					} else if ("section_id".equals(column)) {
						pareForms.get(icounts).setSection_id(value[0]);
					} else if ("upper_limit".equals(column)){
						pareForms.get(icounts).setUpper_limit(value[0]);
					}
				}
			}
		}

		for(ScheduleForm scheduleForm:pareForms){
			ScheduleEntity conditionEntity = new ScheduleEntity();
			BeanUtil.copyToBean(scheduleForm, conditionEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

			int upper_limit = 0;
			if (!CommonStringUtil.isEmpty(conditionEntity.getUpper_limit())) {
				upper_limit = Integer.parseInt(conditionEntity.getUpper_limit());
			}

			//判断数据是否已经存在
			if (!CommonStringUtil.isEmpty(dao.checkIsExist(conditionEntity))) {
				//修改最大产能
				if (upper_limit > 0) {
					dao.updateUpperLimit(conditionEntity);
				} else {
					dao.deleteCapacity(conditionEntity);
				}
			} else if (upper_limit > 0) {
				dao.insertCapacity(conditionEntity);
			}

		}
		
	}
}
