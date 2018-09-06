package com.osh.rvs.service.infect;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.infect.DeviceRegularlyCheckResultEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.form.infect.DeviceRegularlyCheckResultForm;
import com.osh.rvs.mapper.infect.DeviceRegularlyCheckResultMapper;
import com.osh.rvs.service.CheckResultService;
import com.osh.rvs.service.PositionService;

import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.service
 * @ClassName: DeviceRegularlyCheckResultService
 * @Description: 设备工具定期点检Service
 * @author lxb
 * @date 2014-8-19 上午10:25:20
 * 
 */
public class DeviceRegularlyCheckResultService {

	/**
	 * 设备工具定期点检一览 按月
	 * 
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<DeviceRegularlyCheckResultForm> searchByMonth(ActionForm form, SqlSession conn) {
		DeviceRegularlyCheckResultEntity entity = new DeviceRegularlyCheckResultEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		entity.setCycle_type(2);
		
		Map<String, DeviceRegularlyCheckResultEntity> map = new TreeMap<String, DeviceRegularlyCheckResultEntity>();

		DeviceRegularlyCheckResultEntity returnEntity = null;
		int[] months = { 4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 2, 3 };

		Calendar cal = Calendar.getInstance();
		int nowYear = cal.get(Calendar.YEAR);// 当前年
		int nowMonth = cal.get(Calendar.MONTH) + 1;// 当前月
		if (nowMonth < 4) {// 月份小于四月
			nowYear--;
		}
				
				
		for (int i = 0; i < months.length; i++) {
			/*if (months[i] == nowMonth) {// 当前月跳出循环
				break;
			}*/
			cal.set(Calendar.YEAR, nowYear);

			if (months[i] <= 3) {
				cal.set(Calendar.YEAR, nowYear+1);
			}

			cal.set(Calendar.MONTH, months[i] - 1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			entity.setDate_start(cal.getTime());
			entity.setInterval(1);

			DeviceRegularlyCheckResultMapper dao = conn.getMapper(DeviceRegularlyCheckResultMapper.class);
			List<DeviceRegularlyCheckResultEntity> list = dao.search(entity);

			if (list != null && list.size() > 0) {
				for (int j = 0; j < list.size(); j++) {
					DeviceRegularlyCheckResultEntity tempEntity = list.get(j);
					String manage_id = tempEntity.getManage_id();

					if (map.containsKey(manage_id)) {
						switch (months[i]) {
						case 1:// 一月
							map.get(manage_id).setJanurary_checked_status(tempEntity.getChecked_status());
							break;
						case 2:// 二月
							map.get(manage_id).setFebruary_checked_status(tempEntity.getChecked_status());
							break;
						case 3:// 三月
							map.get(manage_id).setMarch_checked_status(tempEntity.getChecked_status());
							break;
						case 4:// 四月
							map.get(manage_id).setApril_checked_status(tempEntity.getChecked_status());
							break;
						case 5:// 五月
							map.get(manage_id).setMay_checked_status(tempEntity.getChecked_status());
							break;
						case 6:// 六月
							map.get(manage_id).setJune_checked_status(tempEntity.getChecked_status());
							break;
						case 7:// 七月
							map.get(manage_id).setJuly_checked_status(tempEntity.getChecked_status());
							break;
						case 8:// 八月
							map.get(manage_id).setAugust_checked_status(tempEntity.getChecked_status());
							break;
						case 9:// 九月
							map.get(manage_id).setSeptember_checked_status(tempEntity.getChecked_status());
							break;
						case 10:// 十月
							map.get(manage_id).setOctober_checked_status(tempEntity.getChecked_status());
							break;
						case 11:// 十一月
							map.get(manage_id).setNovember_checked_status(tempEntity.getChecked_status());
							break;
						case 12:// 十二月
							map.get(manage_id).setDecember_checked_status(tempEntity.getChecked_status());
							break;
						default:
							break;
						}
					} else {
						returnEntity = new DeviceRegularlyCheckResultEntity();
						returnEntity.setManage_id(tempEntity.getManage_id());
						returnEntity.setManage_code(tempEntity.getManage_code());
						returnEntity.setName(tempEntity.getName());
						returnEntity.setModel_name(tempEntity.getModel_name());

						switch (months[i]) {
						case 1:// 一月
							returnEntity.setJanurary_checked_status(tempEntity.getChecked_status());
							break;
						case 2:// 二月
							returnEntity.setFebruary_checked_status(tempEntity.getChecked_status());
							break;
						case 3:// 三月
							returnEntity.setMarch_checked_status(tempEntity.getChecked_status());
							break;
						case 4:// 四月
							returnEntity.setApril_checked_status(tempEntity.getChecked_status());
							break;
						case 5:// 五月
							returnEntity.setMay_checked_status(tempEntity.getChecked_status());
							break;
						case 6:// 六月
							returnEntity.setJune_checked_status(tempEntity.getChecked_status());
							break;
						case 7:// 七月
							returnEntity.setJuly_checked_status(tempEntity.getChecked_status());
							break;
						case 8:// 八月
							returnEntity.setAugust_checked_status(tempEntity.getChecked_status());
							break;
						case 9:// 九月
							returnEntity.setSeptember_checked_status(tempEntity.getChecked_status());
							break;
						case 10:// 十月
							returnEntity.setOctober_checked_status(tempEntity.getChecked_status());
							break;
						case 11:// 十一月
							returnEntity.setNovember_checked_status(tempEntity.getChecked_status());
							break;
						case 12:// 十二月
							returnEntity.setDecember_checked_status(tempEntity.getChecked_status());
							break;
						default:
							break;
						}

						map.put(returnEntity.getManage_id(), returnEntity);
					}

				}
			}

			if (months[i] == nowMonth) {// 当前月跳出循环
				break;
			}

		}

		List<DeviceRegularlyCheckResultEntity> retutnEntityist = new ArrayList<DeviceRegularlyCheckResultEntity>();
		List<DeviceRegularlyCheckResultForm> retutnFormList = new ArrayList<DeviceRegularlyCheckResultForm>();

		Set<String> keys = map.keySet();
		Iterator<String> iter = keys.iterator();

		while (iter.hasNext()) {
			String key = iter.next();
			DeviceRegularlyCheckResultEntity deviceRegularlyCheckResultEntity = map.get(key);
			retutnEntityist.add(deviceRegularlyCheckResultEntity);
		}

		// 复制数据到表单对象
		BeanUtil.copyToFormList(retutnEntityist, retutnFormList, CopyOptions.COPYOPTIONS_NOEMPTY, DeviceRegularlyCheckResultForm.class);

		return retutnFormList;
	}
	
	
	
	/**
	 * 设备工具定期点检一览 按年
	 * 
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<DeviceRegularlyCheckResultForm> searchByYear(ActionForm form, SqlSession conn) {
		DeviceRegularlyCheckResultEntity entity = new DeviceRegularlyCheckResultEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		entity.setCycle_type(3);
		
		Map<String, DeviceRegularlyCheckResultEntity> map = new TreeMap<String, DeviceRegularlyCheckResultEntity>();

		DeviceRegularlyCheckResultEntity returnEntity = null;

		Calendar cal = Calendar.getInstance();
		int nowYear = cal.get(Calendar.YEAR);// 当前年
		int nowMonth = cal.get(Calendar.MONTH);// 当前月

		if (nowMonth < 3) {
			nowYear--;
		}

		Calendar upper_date = Calendar.getInstance();

		upper_date.set(Calendar.YEAR, nowYear);
		upper_date.set(Calendar.MONTH, 3);
		upper_date.set(Calendar.DAY_OF_MONTH, 1);
		upper_date.set(Calendar.HOUR_OF_DAY, 0);
		upper_date.set(Calendar.MINUTE, 0);
		upper_date.set(Calendar.SECOND, 0);
		upper_date.set(Calendar.MILLISECOND, 0);

		Calendar lower_date = Calendar.getInstance();
		lower_date.set(Calendar.YEAR, nowYear);
		lower_date.set(Calendar.MONTH, 9);
		lower_date.set(Calendar.DAY_OF_MONTH, 1);
		lower_date.set(Calendar.HOUR_OF_DAY, 0);
		lower_date.set(Calendar.MINUTE, 0);
		lower_date.set(Calendar.SECOND, 0);
		lower_date.set(Calendar.MILLISECOND, 0);

		Date times[] = { upper_date.getTime(), lower_date.getTime() };
		for (int i = 0; i < times.length; i++) {
			entity.setDate_start(times[i]);
			entity.setInterval(6);
			DeviceRegularlyCheckResultMapper dao = conn.getMapper(DeviceRegularlyCheckResultMapper.class);
			List<DeviceRegularlyCheckResultEntity> list = dao.search(entity);

			if (list != null && list.size() > 0) {
				for (int j = 0; j < list.size(); j++) {
					DeviceRegularlyCheckResultEntity tempEntity = list.get(j);
					String manage_id = tempEntity.getManage_id();
					if (map.containsKey(manage_id)) {
						switch (i) {
						case 0:// 上半年
							map.get(manage_id).setUpper_half_year_status(tempEntity.getChecked_status());
							break;
						case 1:// 下半年
							map.get(manage_id).setLower_half_year_status(tempEntity.getChecked_status());
							break;
						default:
							break;
						}
					} else {
						returnEntity = new DeviceRegularlyCheckResultEntity();
						returnEntity.setManage_id(tempEntity.getManage_id());
						returnEntity.setManage_code(tempEntity.getManage_code());
						returnEntity.setName(tempEntity.getName());
						returnEntity.setModel_name(tempEntity.getModel_name());

						switch (i) {
						case 0:// 上半年
							returnEntity.setUpper_half_year_status(tempEntity.getChecked_status());
							break;
						case 1:// 下半年
							returnEntity.setLower_half_year_status(tempEntity.getChecked_status());
							break;
						default:
							break;
						}

						map.put(returnEntity.getManage_id(), returnEntity);
					}

				}
			}

		}

		List<DeviceRegularlyCheckResultEntity> retutnEntityist = new ArrayList<DeviceRegularlyCheckResultEntity>();
		List<DeviceRegularlyCheckResultForm> retutnFormList = new ArrayList<DeviceRegularlyCheckResultForm>();

		Set<String> keys = map.keySet();
		Iterator<String> iter = keys.iterator();

		while (iter.hasNext()) {
			String key = iter.next();
			DeviceRegularlyCheckResultEntity deviceRegularlyCheckResultEntity = map.get(key);
			
			
			

			/*
			 * Integer upper_half_year_status = deviceRegularlyCheckResultEntity.getUpper_half_year_status();// 上半年结果 //
			 * Integer lower_half_year_status = deviceRegularlyCheckResultEntity.getLower_half_year_status();// 下半年结果
			 * 
			 * if (nowMonth >= 3 && nowMonth <= 8) {// 上半年 } else {// 下半年 if (upper_half_year_status == null) {
			 * deviceRegularlyCheckResultEntity.setUpper_half_year_status(-1); } }
			 */

			retutnEntityist.add(deviceRegularlyCheckResultEntity);
		}

		// 复制数据到表单对象
		BeanUtil.copyToFormList(retutnEntityist, retutnFormList, CopyOptions.COPYOPTIONS_NOEMPTY, DeviceRegularlyCheckResultForm.class);

		return retutnFormList;
	}

	/**
	 * 设备工具定期点检一览 按周
	 * @param form
	 * @param conn
	 * @param cal 当前日期
	 * @param cur_week 当前是第几周
	 * @return
	 */
	public List<DeviceRegularlyCheckResultForm> searchByWeek(ActionForm form, SqlSession conn,Calendar cal,int cur_week) {
		DeviceRegularlyCheckResultEntity entity = new DeviceRegularlyCheckResultEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		entity.setCycle_type(1);
		
		Map<String, DeviceRegularlyCheckResultEntity> map = new TreeMap<String, DeviceRegularlyCheckResultEntity>();

		DeviceRegularlyCheckResultEntity returnEntity = null;
		 
		CheckResultService checkResultService=new CheckResultService();
		Date dates[][]=checkResultService.getWeekEndsOfMonth(cal);
		int weeks=dates.length;
		
		for (int i = 0; i < weeks; i++) {
			entity.setDate_start(DateUtil.toDate(DateUtil.toString(dates[i][0], DateUtil.ISO_DATE_PATTERN), DateUtil.ISO_DATE_PATTERN));
			entity.setDate_end(DateUtil.toDate(DateUtil.toString(dates[i][1], DateUtil.ISO_DATE_PATTERN), DateUtil.ISO_DATE_PATTERN));

			DeviceRegularlyCheckResultMapper dao = conn.getMapper(DeviceRegularlyCheckResultMapper.class);
			List<DeviceRegularlyCheckResultEntity> list = dao.searchByWeek(entity);
			if (list != null && list.size() > 0) {
				for (int j = 0; j < list.size(); j++) {
					DeviceRegularlyCheckResultEntity tempEntity = list.get(j);
					String manage_id = tempEntity.getManage_id();
					if (map.containsKey(manage_id)) {
						switch (i) {
						case 0:// 第一周
							map.get(manage_id).setOne_week_status(tempEntity.getChecked_status());
							break;
						case 1:// 第二周
							map.get(manage_id).setTwo_week_status(tempEntity.getChecked_status());
							break;
						case 2:// 第三周
							map.get(manage_id).setThree_week_status(tempEntity.getChecked_status());
							break;
						case 3:// 第四周
							map.get(manage_id).setFour_week_status(tempEntity.getChecked_status());
							break;
						case 4:// 第五周
							map.get(manage_id).setFive_week_status(tempEntity.getChecked_status());
							break;
						case 5:// 第六周
							map.get(manage_id).setSix_week_status(tempEntity.getChecked_status());
							break;
						}
					} else {
						returnEntity = new DeviceRegularlyCheckResultEntity();
						returnEntity.setManage_id(tempEntity.getManage_id());
						returnEntity.setManage_code(tempEntity.getManage_code());
						returnEntity.setName(tempEntity.getName());
						returnEntity.setModel_name(tempEntity.getModel_name());

						switch (i) {
						case 0:// 第一周
							returnEntity.setOne_week_status(tempEntity.getChecked_status());
							break;
						case 1:// 第二周
							returnEntity.setTwo_week_status(tempEntity.getChecked_status());
							break;
						case 2:// 第三周
							returnEntity.setThree_week_status(tempEntity.getChecked_status());
							break;
						case 3:// 第四周
							returnEntity.setFour_week_status(tempEntity.getChecked_status());
							break;
						case 4:// 第五周
							returnEntity.setFive_week_status(tempEntity.getChecked_status());
							break;
						case 5:// 第六周
							returnEntity.setSix_week_status(tempEntity.getChecked_status());
							break;
						}

						map.put(returnEntity.getManage_id(), returnEntity);

					}
				}
			}
			
			if((i+1)==cur_week){
				break;
			}
		}

		List<DeviceRegularlyCheckResultEntity> retutnEntityist = new ArrayList<DeviceRegularlyCheckResultEntity>();
		List<DeviceRegularlyCheckResultForm> retutnFormList = new ArrayList<DeviceRegularlyCheckResultForm>();

		Set<String> keys = map.keySet();
		Iterator<String> iter = keys.iterator();

		while (iter.hasNext()) {
			String key = iter.next();
			DeviceRegularlyCheckResultEntity deviceRegularlyCheckResultEntity = map.get(key);
			retutnEntityist.add(deviceRegularlyCheckResultEntity);
			
		}

		// 复制数据到表单对象
		BeanUtil.copyToFormList(retutnEntityist, retutnFormList, CopyOptions.COPYOPTIONS_NOEMPTY, DeviceRegularlyCheckResultForm.class);

		return retutnFormList;
	}

	/**
	 * 获取当前周在当月中是第几周
	 * @param cal
	 * @return
	 */
	public int getIndexOfCurrentWeek(Calendar cal){
		CheckResultService checkResultService=new CheckResultService();
		Date dates[][]=checkResultService.getWeekEndsOfMonth(cal);
		
		int index=0;
		for(int i=0;i<dates.length;i++){
			if(DateUtil.compareDate(cal.getTime(), dates[i][0])>=0 && DateUtil.compareDate(cal.getTime(), dates[i][1])<=0){
				index=i+1;
				break;
			}
		}
		
		return index;
	}
	

	/**
	 * 取得工位参照选择项
	 * 
	 * @param conn
	 * @return
	 */
	public String getOptions(SqlSession conn) {
		List<String[]> lst = new ArrayList<String[]>();
		PositionService positionService = new PositionService();
		List<PositionEntity> allPosition = positionService.getAllPosition(conn);

		for (PositionEntity position : allPosition) {
			String[] p = new String[3];
			p[0] = position.getPosition_id();
			p[1] = position.getProcess_code();
			p[2] = position.getName();

			lst.add(p);
		}

		String pReferChooser = CodeListUtils.getReferChooser(lst);

		return pReferChooser;
	}

	public List<DeviceRegularlyCheckResultForm> searchDetail(ActionForm form, SqlSession conn) {
		DeviceRegularlyCheckResultEntity entity = new DeviceRegularlyCheckResultEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<DeviceRegularlyCheckResultForm> retutnFormList = new ArrayList<DeviceRegularlyCheckResultForm>();
		DeviceRegularlyCheckResultMapper dao = conn.getMapper(DeviceRegularlyCheckResultMapper.class);
		List<DeviceRegularlyCheckResultEntity> list =null;
		
		if (entity.getType() == 2) {// 年或者月
			Calendar cal = Calendar.getInstance();
			int nowYear = cal.get(Calendar.YEAR);// 当前年
			int nowMonth = cal.get(Calendar.MONTH);// 当前月

			if (nowMonth < 3) {
				nowYear--;
			}

			Calendar cal_start = Calendar.getInstance();
			cal_start.set(Calendar.YEAR, nowYear);
			cal_start.set(Calendar.MONTH, 3);
			cal_start.set(Calendar.DAY_OF_MONTH, 1);
			cal_start.set(Calendar.HOUR_OF_DAY, 0);
			cal_start.set(Calendar.MINUTE, 0);
			cal_start.set(Calendar.SECOND, 0);
			cal_start.set(Calendar.MILLISECOND, 0);

			Calendar cal_end = Calendar.getInstance();
			cal_end.set(Calendar.YEAR, nowYear + 1);
			cal_end.set(Calendar.MONTH, 3);
			cal_end.set(Calendar.DAY_OF_MONTH, 1);
			cal_end.set(Calendar.HOUR_OF_DAY, 0);
			cal_end.set(Calendar.MINUTE, 0);
			cal_end.set(Calendar.SECOND, 0);
			cal_end.set(Calendar.MILLISECOND, 0);

			entity.setDate_start(cal_start.getTime());
			entity.setDate_end(cal_end.getTime());

			list= dao.searchDetail(entity);

		} else {
			Calendar cal_start = Calendar.getInstance();
			cal_start.set(Calendar.DAY_OF_MONTH, 1);
			cal_start.set(Calendar.HOUR_OF_DAY, 0);
			cal_start.set(Calendar.MINUTE, 0);
			cal_start.set(Calendar.SECOND, 0);
			cal_start.set(Calendar.MILLISECOND, 0);
			
			
			Calendar cal_end = Calendar.getInstance();
			cal_end.add(Calendar.MONTH,1);
			cal_end.set(Calendar.DAY_OF_MONTH, 1);
			cal_end.set(Calendar.HOUR_OF_DAY, 0);
			cal_end.set(Calendar.MINUTE, 0);
			cal_end.set(Calendar.SECOND, 0);
			cal_end.set(Calendar.MILLISECOND, 0);
			
			
			entity.setDate_start(cal_start.getTime());
			entity.setDate_end(cal_end.getTime());

			list = dao.searchDetail(entity);
		}
		
		if(list.size()>0){
			BeanUtil.copyToFormList(list, retutnFormList, CopyOptions.COPYOPTIONS_NOEMPTY, DeviceRegularlyCheckResultForm.class);
			return retutnFormList;
		}else{
			return null;
		}
		

		
	}

}
