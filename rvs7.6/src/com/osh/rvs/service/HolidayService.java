package com.osh.rvs.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;

import com.osh.rvs.mapper.master.HolidayMapper;

import framework.huiqing.common.util.copy.DateUtil;

public class HolidayService {

	private static HashMap<String, List<String>> holidaysOnMonth = new HashMap<String, List<String>>();

	/**
	 * 更新休假标记
	 * @param date
	 * @param conn
	 * @throws Exception
	 */
	public void update(Date date, SqlSessionManager conn) throws Exception {
		synchronized (holidaysOnMonth) {
			HolidayMapper dao = conn.getMapper(HolidayMapper.class);
			if (dao.existsHoliday(date)) {
				dao.removeHoliday(date);
			} else {
				dao.createHoliday(date);
			}

			holidaysOnMonth.clear();
		}
	}

	/**
	 * 查询指定月的有休假标记日期
	 * @param date
	 * @param conn
	 * @throws Exception
	 */
	public List<String> search(String month, SqlSession conn) {
		synchronized (holidaysOnMonth) {
			if (!holidaysOnMonth.containsKey(month)) {
				HolidayMapper dao = conn.getMapper(HolidayMapper.class);
				holidaysOnMonth.put(month, dao.searchHolidayOfMonth(month));
			}
			return holidaysOnMonth.get(month);
		}
	}

	public String addWorkdays(Date date, int interval, SqlSession conn) throws Exception {
		HolidayMapper mapper = conn.getMapper(HolidayMapper.class);
		Map<String, Object> cond = new HashMap<String, Object>();
		cond.put("date", date);
		cond.put("interval", interval);
		Date counted = mapper.addWorkdays(cond);
		return DateUtil.toString(counted, DateUtil.DATE_PATTERN);
	}

	public static String getHolidaysOnMonthAsJson() {
		return JSON.encode(holidaysOnMonth);
	}

	public static boolean checkTodayHoliday(SqlSession conn) {
		Calendar today = Calendar.getInstance();
		int weekday = today.get(Calendar.DAY_OF_WEEK);
		boolean isWeekday =  weekday == Calendar.SATURDAY || weekday == Calendar.SUNDAY;
		String sMonth = DateUtil.toString(today.getTime(), "yyyy/MM"); 
		String sDate = DateUtil.toString(today.getTime(), "dd"); 

		HolidayService thisService = new HolidayService();
		List<String> holidays = thisService.search(sMonth, conn);
		boolean setHoli = holidays.contains(sDate);

		return (isWeekday && !setHoli) || (!isWeekday && setHoli);
	}
	
	public static boolean checkHoliday(Date date,SqlSession conn) {
		Calendar today = Calendar.getInstance();
		today.setTime(date);
		int weekday = today.get(Calendar.DAY_OF_WEEK);
		boolean isWeekday =  weekday == Calendar.SATURDAY || weekday == Calendar.SUNDAY;
		String sMonth = DateUtil.toString(date, "yyyy/MM"); 
		String sDate = DateUtil.toString(date, "dd"); 

		HolidayService thisService = new HolidayService();
		List<String> holidays = thisService.search(sMonth, conn);
		boolean setHoli = holidays.contains(sDate);

		return (isWeekday && !setHoli) || (!isWeekday && setHoli);
	}
}
