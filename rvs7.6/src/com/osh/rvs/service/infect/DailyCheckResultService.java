package com.osh.rvs.service.infect;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.ibatis.session.SqlSession;

import com.osh.rvs.bean.infect.DailyCheckResultEntity;
import com.osh.rvs.form.infect.DailyCheckResultForm;
import com.osh.rvs.mapper.infect.DailyCheckResultMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

public class DailyCheckResultService {
  
	/**
	 * 日常点检结果
	 * @param dailyCheckResultEntity
	 * @param conn
	 * @return
	 */
	public List<DailyCheckResultForm> searchDailyCheckResult(DailyCheckResultForm dailyCheckResultForm,Calendar calendar,SqlSession conn){
		
		List<DailyCheckResultForm> dailyCheckResultForms = new ArrayList<DailyCheckResultForm>() ;
		
		DailyCheckResultMapper dao = conn.getMapper(DailyCheckResultMapper.class);
		
		DailyCheckResultEntity yearMonthEntity=new DailyCheckResultEntity();
		
		//全新的日常点检结果集合
		List<DailyCheckResultEntity> resultEntities = new ArrayList<DailyCheckResultEntity>();
	     
		DailyCheckResultEntity returnEntity = null;
		//当前年
		int year  =calendar.get(Calendar.YEAR);
		//当前月
		int month = calendar.get(Calendar.MONTH)+1;
		//当前几号
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		//当前月的所有日期
		String year_month="";
		//查询当前月的某一天的详细
		List<DailyCheckResultEntity> dailyCheckResults=null;
		
		Map<String,DailyCheckResultEntity> map = new TreeMap<String,DailyCheckResultEntity>();
		
		for(int i=1;i<=day;i++){
			//月日期
			year_month =year+"/"+""+month+"/"+i;
			//设置点检时间--为当前月的所有日期
			dailyCheckResultForm.setCheck_confirm_time(year_month);
			
			BeanUtil.copyToBean(dailyCheckResultForm, yearMonthEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
			
			dailyCheckResults= dao.searchDailyCheckResult(yearMonthEntity);
			
			if (dailyCheckResults != null && dailyCheckResults.size() > 0) {
				if (i == 0) {//
					for (int j = 0; j < dailyCheckResults.size(); j++) {
						DailyCheckResultEntity tempEntity = dailyCheckResults.get(j);
						returnEntity = new DailyCheckResultEntity();
						returnEntity.setManage_id(tempEntity.getManage_id());
						returnEntity.setManage_code(tempEntity.getManage_code());
						returnEntity.setName(tempEntity.getName());
						returnEntity.setModel_name(tempEntity.getModel_name());
						returnEntity.setChecked_status(tempEntity.getChecked_status());
						
						map.put(returnEntity.getManage_id(), returnEntity);
					}
				} else {
					for (int j = 0; j < dailyCheckResults.size(); j++) {
						DailyCheckResultEntity tempEntity = dailyCheckResults.get(j);
						String manage_id = tempEntity.getManage_id();
						
						if (map.containsKey(manage_id)) {
							switch(i)
							{
								case 1 :map.get(manage_id).setOne(tempEntity.getChecked_status());break;         
								case 2 :map.get(manage_id).setTwo(tempEntity.getChecked_status());break;      
								case 3 :map.get(manage_id).setThree(tempEntity.getChecked_status());break;       
								case 4 :map.get(manage_id).setFour(tempEntity.getChecked_status());break;       
								case 5 :map.get(manage_id).setFive(tempEntity.getChecked_status());break;       
								case 6 :map.get(manage_id).setSix(tempEntity.getChecked_status());break;      
								case 7 :map.get(manage_id).setSeven(tempEntity.getChecked_status());break;       
								case 8 :map.get(manage_id).setEight(tempEntity.getChecked_status());break;      
								case 9 :map.get(manage_id).setNine(tempEntity.getChecked_status());break;       
								case 10:map.get(manage_id).setTen(tempEntity.getChecked_status());break;     
								case 11:map.get(manage_id).setEleven(tempEntity.getChecked_status());break;     
								case 12:map.get(manage_id).setTwelve(tempEntity.getChecked_status());break;  
								case 13:map.get(manage_id).setThirteen(tempEntity.getChecked_status()); break;   
								case 14:map.get(manage_id).setFourteen(tempEntity.getChecked_status()); break;   
								case 15:map.get(manage_id).setFiveteen(tempEntity.getChecked_status());break;    
								case 16:map.get(manage_id).setSixteen(tempEntity.getChecked_status());break; 
								case 17:map.get(manage_id).setSeventeen(tempEntity.getChecked_status());break;
								case 18:map.get(manage_id).setEighteen(tempEntity.getChecked_status());break;    
								case 19:map.get(manage_id).setNineteen(tempEntity.getChecked_status());break;    
								case 20:map.get(manage_id).setTwenty(tempEntity.getChecked_status());break;
								case 21:map.get(manage_id).setTwenty_one(tempEntity.getChecked_status());break;  
								case 22:map.get(manage_id).setTwenty_two(tempEntity.getChecked_status());break;
								case 23:map.get(manage_id).setTwenty_three(tempEntity.getChecked_status());break;
								case 24:map.get(manage_id).setTwenty_four(tempEntity.getChecked_status());break;
								case 25:map.get(manage_id).setTwenty_five(tempEntity.getChecked_status());break; 
								case 26:map.get(manage_id).setTwenty_six(tempEntity.getChecked_status());break;  
								case 27:map.get(manage_id).setTwenty_seven(tempEntity.getChecked_status());break;
								case 28:map.get(manage_id).setTwenty_eight(tempEntity.getChecked_status());break;
								case 29:map.get(manage_id).setTwenty_nine(tempEntity.getChecked_status());break;
								case 30:map.get(manage_id).setThirty(tempEntity.getChecked_status());break;      
								case 31:map.get(manage_id).setThirty_one(tempEntity.getChecked_status());break;
								default:
									break;
							}
						} else {
							returnEntity = new DailyCheckResultEntity();
							returnEntity.setManage_id(tempEntity.getManage_id());
							returnEntity.setManage_code(tempEntity.getManage_code());
							returnEntity.setName(tempEntity.getName());
							returnEntity.setModel_name(tempEntity.getModel_name());

							switch (i) {
							case 1 :returnEntity.setOne(tempEntity.getChecked_status());break;         
							case 2 :returnEntity.setTwo(tempEntity.getChecked_status());break;      
							case 3 :returnEntity.setThree(tempEntity.getChecked_status());break;       
							case 4 :returnEntity.setFour(tempEntity.getChecked_status());break;       
							case 5 :returnEntity.setFive(tempEntity.getChecked_status());break;       
							case 6 :returnEntity.setSix(tempEntity.getChecked_status());break;      
							case 7 :returnEntity.setSeven(tempEntity.getChecked_status());break;       
							case 8 :returnEntity.setEight(tempEntity.getChecked_status());break;      
							case 9 :returnEntity.setNine(tempEntity.getChecked_status());break;       
							case 10:returnEntity.setTen(tempEntity.getChecked_status());break;     
							case 11:returnEntity.setEleven(tempEntity.getChecked_status());break;     
							case 12:returnEntity.setTwelve(tempEntity.getChecked_status());break;  
							case 13:returnEntity.setThirteen(tempEntity.getChecked_status()); break;   
							case 14:returnEntity.setFourteen(tempEntity.getChecked_status()); break;   
							case 15:returnEntity.setFiveteen(tempEntity.getChecked_status());break;    
							case 16:returnEntity.setSixteen(tempEntity.getChecked_status());break; 
							case 17:returnEntity.setSeventeen(tempEntity.getChecked_status());break;
							case 18:returnEntity.setEighteen(tempEntity.getChecked_status());break;    
							case 19:returnEntity.setNineteen(tempEntity.getChecked_status());break;    
							case 20:returnEntity.setTwenty(tempEntity.getChecked_status());break;
							case 21:returnEntity.setTwenty_one(tempEntity.getChecked_status());break;  
							case 22:returnEntity.setTwenty_two(tempEntity.getChecked_status());break;
							case 23:returnEntity.setTwenty_three(tempEntity.getChecked_status());break;
							case 24:returnEntity.setTwenty_four(tempEntity.getChecked_status());break;
							case 25:returnEntity.setTwenty_five(tempEntity.getChecked_status());break; 
							case 26:returnEntity.setTwenty_six(tempEntity.getChecked_status());break;  
							case 27:returnEntity.setTwenty_seven(tempEntity.getChecked_status());break;
							case 28:returnEntity.setTwenty_eight(tempEntity.getChecked_status());break;
							case 29:returnEntity.setTwenty_nine(tempEntity.getChecked_status());break;
							case 30:returnEntity.setThirty(tempEntity.getChecked_status());break;      
							case 31:returnEntity.setThirty_one(tempEntity.getChecked_status());break;
							default:
								break;
						 }
							map.put(returnEntity.getManage_id(), returnEntity);
						}
					}
				}
			}
	     }
		
		Set<String> keys = map.keySet();
		Iterator<String> iter = keys.iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			DailyCheckResultEntity mapEntity = map.get(key);
			resultEntities.add(mapEntity);
		}
		
		BeanUtil.copyToFormList(resultEntities,dailyCheckResultForms,CopyOptions.COPYOPTIONS_NOEMPTY,DailyCheckResultForm.class);
		
		return dailyCheckResultForms;
	}
	
	/**
	 * 日常点检结果--课室+工程+工位
	 * @param dailyCheckResultEntity
	 * @param conn
	 * @return
	 */
	public DailyCheckResultForm searchSectionLinePosition(DailyCheckResultEntity dailyCheckResultEntity, SqlSession conn) {
		DailyCheckResultForm dailyCheckResultForm = new DailyCheckResultForm();
		DailyCheckResultMapper dao = conn.getMapper(DailyCheckResultMapper.class);
		//课室+工程+工位
		DailyCheckResultEntity resultEntity = dao.searchSectionLinePosition(dailyCheckResultEntity);

		BeanUtil.copyToForm(resultEntity, dailyCheckResultForm, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		return dailyCheckResultForm;
	}
	/**
	 * 设备工具点检记录
	 * @param dailyCheckResultEntity
	 * @param conn
	 * @return
	 */
	public List<DailyCheckResultForm> detailCheckResult(DailyCheckResultEntity dailyCheckResultEntity,SqlSession conn){
		List<DailyCheckResultForm> dailyCheckResultForms = new ArrayList<DailyCheckResultForm>() ;
		DailyCheckResultMapper dao = conn.getMapper(DailyCheckResultMapper.class);
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(calendar.getTime());
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		
		//当前月的第一天
		String firstDate = DateUtil.toString(calendar.getTime(), "yyyy/MM/dd");
		
		/*calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);*/
		//下个月
		int month = calendar.get(Calendar.MONTH)+2;
		//下个月的第一天
		String lastDate = "2014/"+month+"/01";//TODO DateUtil.toString(calendar.getTime(), "yyyy/MM/dd");
		
		dailyCheckResultEntity.setFirstDate(firstDate);
		dailyCheckResultEntity.setLastDate(lastDate);
		
		//设备工具点检记录
		List<DailyCheckResultEntity> resultEntitys = dao.searchCheckResult(dailyCheckResultEntity);
		
		BeanUtil.copyToFormList(resultEntitys,dailyCheckResultForms,CopyOptions.COPYOPTIONS_NOEMPTY,DailyCheckResultForm.class);
		
		return dailyCheckResultForms;
	}
}
