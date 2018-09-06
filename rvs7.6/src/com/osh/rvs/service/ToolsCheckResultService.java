package com.osh.rvs.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.ibatis.session.SqlSession;

import com.osh.rvs.bean.infect.ToolsCheckResultEntity;
import com.osh.rvs.form.infect.ToolsCheckResultForm;
import com.osh.rvs.mapper.infect.ToolsCheckResultMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;


public class ToolsCheckResultService {
	/**
	 * 治具点检一览详细
	 * @param toolsManageForm
	 * @param conn
	 * @param quantityForm
	 * @return
	 * @throws ParseException 
	 */
	public List<ToolsCheckResultForm> searchToolsCheckResult(ToolsCheckResultForm toolsCheckResultForm,SqlSession conn) {
		List<ToolsCheckResultForm>  toolsCheckResultForms= new ArrayList<ToolsCheckResultForm>();
		ToolsCheckResultMapper dao = conn.getMapper(ToolsCheckResultMapper.class);
		
		ToolsCheckResultEntity toolsCheckResultEntity = new ToolsCheckResultEntity();
		//form-->bean
		BeanUtil.copyToBean(toolsCheckResultForm, toolsCheckResultEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		String responsibleOperatorId = toolsCheckResultEntity.getResponsible_operator_id();
		
		//检索后的数据
		List<ToolsCheckResultEntity> retultEntitys = null;
		
		ToolsCheckResultEntity returnEntity = null;
		
		Map<String,ToolsCheckResultEntity> map = new TreeMap<String,ToolsCheckResultEntity>();
		
		int[] months = { 4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 2, 3 };
		Calendar cal = Calendar.getInstance();
		int nowYear = cal.get(Calendar.YEAR);// 当前年
		int nowMonth = cal.get(Calendar.MONTH) + 1;// 当前月
		if (nowMonth < 4) {// 月份小于四月
			nowYear--;
		}
		
		
		
		//4月份到当前月的所有点检状态
		for(int i=0;i<months.length;i++){
			
			cal.set(Calendar.YEAR, nowYear);
			
			Calendar endcal = Calendar.getInstance();
			endcal.set(Calendar.YEAR, nowYear);
			
			if (months[i] <= 3) {
				cal.set(Calendar.YEAR, nowYear+1);
				endcal.set(Calendar.YEAR, nowYear+1);
			}
			
			cal.set(Calendar.MONTH, months[i] - 1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			
		
			endcal.set(Calendar.MONTH, months[i] - 1);
			endcal.add(Calendar.MONTH, 1);
			endcal.set(Calendar.DAY_OF_MONTH, 1);
			endcal.set(Calendar.HOUR_OF_DAY, 0);
			endcal.set(Calendar.MINUTE, 0);
			endcal.set(Calendar.SECOND, 0);
			endcal.set(Calendar.MILLISECOND, 0);
			
	        
	        //当前年份的每个月的第一天和最后一天
	        toolsCheckResultEntity.setFirstDate(DateUtil.toString(cal.getTime(), DateUtil.DATE_PATTERN));
	        toolsCheckResultEntity.setLastDate(DateUtil.toString(endcal.getTime(), DateUtil.DATE_PATTERN));
	        
	        //检索的数据
	        retultEntitys= dao.searchToolsCheckResult(toolsCheckResultEntity);
	        if (retultEntitys != null && retultEntitys.size() > 0) {
				if (i == 0) {//
					for (int j = 0; j < retultEntitys.size(); j++) {
						ToolsCheckResultEntity tempEntity = retultEntitys.get(j);
						returnEntity = new ToolsCheckResultEntity();
						returnEntity.setManage_id(tempEntity.getManage_id());
						returnEntity.setManage_code(tempEntity.getManage_code());
						returnEntity.setTools_no(tempEntity.getTools_no());
						returnEntity.setTools_name(tempEntity.getTools_name());
						returnEntity.setChecked_status(tempEntity.getChecked_status());
						if (responsibleOperatorId == null || !responsibleOperatorId.equals(tempEntity.getResponsible_operator_id())) {
							returnEntity.setResponsible_operator_id(tempEntity.getResponsible_operator_id());
						}
						
						map.put(returnEntity.getManage_id(), returnEntity);
					}
				} // else {
					for (int j = 0; j < retultEntitys.size(); j++) {
						ToolsCheckResultEntity tempEntity = retultEntitys.get(j);
						String manage_id = tempEntity.getManage_id();
						
						if (!map.isEmpty() && map.containsKey(manage_id)) {
							switch(months[i])
							{
								case 4 :map.get(manage_id).setApril(tempEntity.getChecked_status());break;       
								case 5 :map.get(manage_id).setMay(tempEntity.getChecked_status());break;       
								case 6 :map.get(manage_id).setJune(tempEntity.getChecked_status());break;      
								case 7 :map.get(manage_id).setJuly(tempEntity.getChecked_status());break;       
								case 8 :map.get(manage_id).setAugust(tempEntity.getChecked_status());break;      
								case 9 :map.get(manage_id).setSeptember(tempEntity.getChecked_status());break;       
								case 10:map.get(manage_id).setOctober(tempEntity.getChecked_status());break;     
								case 11:map.get(manage_id).setNovember(tempEntity.getChecked_status());break;     
								case 12:map.get(manage_id).setDecember(tempEntity.getChecked_status());break; 
								case 1 :map.get(manage_id).setJanuary(tempEntity.getChecked_status());break;         
								case 2 :map.get(manage_id).setFebruary(tempEntity.getChecked_status());break;      
								case 3 :map.get(manage_id).setMarch(tempEntity.getChecked_status());break; 
								default:
									break;
							}
						} else {
							returnEntity = new ToolsCheckResultEntity();
							returnEntity.setManage_id(tempEntity.getManage_id());
							returnEntity.setManage_code(tempEntity.getManage_code());
							returnEntity.setTools_no(tempEntity.getTools_no());
							returnEntity.setTools_name(tempEntity.getTools_name());

							switch (months[i]) {
							case 4 :returnEntity.setApril(tempEntity.getChecked_status());break;     
							case 5 :returnEntity.setMay(tempEntity.getChecked_status());break;       
							case 6 :returnEntity.setJune(tempEntity.getChecked_status());break;      
							case 7 :returnEntity.setJuly(tempEntity.getChecked_status());break;      
							case 8 :returnEntity.setAugust(tempEntity.getChecked_status());break;    
							case 9 :returnEntity.setSeptember(tempEntity.getChecked_status());break; 
							case 10:returnEntity.setOctober(tempEntity.getChecked_status());break;   
							case 11:returnEntity.setNovember(tempEntity.getChecked_status());break;  
							case 12:returnEntity.setDecember(tempEntity.getChecked_status());break;  
							case 1 :returnEntity.setJanuary(tempEntity.getChecked_status());break;   
							case 2 :returnEntity.setFebruary(tempEntity.getChecked_status());break;  
							case 3 :returnEntity.setMarch(tempEntity.getChecked_status());break;     
							default:
								break;
						 }
							map.put(returnEntity.getManage_id(), returnEntity);
						}
				//	}
				}
			}
		}
		
		Set<String> keys = map.keySet();
		Iterator<String> iter = keys.iterator();
		retultEntitys.clear();
		while (iter.hasNext()) {
			String key = iter.next();
			ToolsCheckResultEntity mapEntity = map.get(key);
			retultEntitys.add(mapEntity);
		}
		
		//formList-->beanList
		BeanUtil.copyToFormList(retultEntitys,toolsCheckResultForms ,CopyOptions.COPYOPTIONS_NOEMPTY, ToolsCheckResultForm.class);
		
		//按照当前月的状态进行排序--×状态在前排序
		List<ToolsCheckResultForm> changeForm1List = new ArrayList<ToolsCheckResultForm>();
		List<ToolsCheckResultForm> changeForm2List = new ArrayList<ToolsCheckResultForm>();
		ToolsCheckResultForm  changeForm = null;
		for(int j=0;j<toolsCheckResultForms.size();j++){
			changeForm = toolsCheckResultForms.get(j);
			if("2".equals(changeForm.getApril()) || "2".equals(changeForm.getMay())
			    ||"2".equals(changeForm.getJune())||"2".equals(changeForm.getJuly())
			    ||"2".equals(changeForm.getAugust())||"2".equals(changeForm.getSeptember())
			    ||"2".equals(changeForm.getOctober())||"2".equals(changeForm.getNovember())
			    ||"2".equals(changeForm.getDecember())||"2".equals(changeForm.getJanuary())
			    ||"2".equals(changeForm.getFebruary())||"2".equals(changeForm.getMarch())){
				changeForm1List.add(changeForm);
			}else{
				changeForm2List.add(changeForm);
			}
		}
		
		changeForm1List.addAll(changeForm2List);
		
		return changeForm1List;
	}
	
	/**
	 * 治具点检结果--课室+工程+工位
	 * @param ToolsCheckResultEntity
	 * @param conn
	 * @return
	 */
	public ToolsCheckResultForm searchSectionLinePosition(ToolsCheckResultEntity toolsCheckResultEntity, SqlSession conn) {
		ToolsCheckResultForm toolsCheckResultForm = new ToolsCheckResultForm();
		ToolsCheckResultMapper dao = conn.getMapper(ToolsCheckResultMapper.class);
		//课室+工程+工位
		ToolsCheckResultEntity resultEntity = dao.searchSectionLinePosition(toolsCheckResultEntity);

		BeanUtil.copyToForm(resultEntity, toolsCheckResultForm, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		return toolsCheckResultForm;
	}
	/**
	 * 治具点检记录
	 * @param toolsCheckResultEntity
	 * @param conn
	 * @return
	 */
	public List<ToolsCheckResultForm> detailCheckResult(ToolsCheckResultEntity toolsCheckResultEntity,SqlSession conn){
		List<ToolsCheckResultForm> toolsCheckResultForms = new ArrayList<ToolsCheckResultForm>() ;
		ToolsCheckResultMapper dao = conn.getMapper(ToolsCheckResultMapper.class);
		
		Calendar calendar = Calendar.getInstance();
		
		//当前年
		int year  =calendar.get(Calendar.YEAR);
		
		int month = 4;
		
		//今年的四月
		String firstDate = year+"-0"+month;
		
		//明年的四月
		String lastDate = (year+1)+"-0"+month;
		
		toolsCheckResultEntity.setFirstDate(firstDate);
		toolsCheckResultEntity.setLastDate(lastDate);
		
		//设备工具点检记录
		List<ToolsCheckResultEntity> resultEntitys = dao.searchCheckResult(toolsCheckResultEntity);
		
		BeanUtil.copyToFormList(resultEntitys,toolsCheckResultForms,CopyOptions.COPYOPTIONS_NOEMPTY,ToolsCheckResultForm.class);
		
		return toolsCheckResultForms;
	}
}
