package com.osh.rvs.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.log4j.Logger;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.mapper.master.HolidayMapper;
import com.osh.rvs.mapper.master.ModelMapper;

import framework.huiqing.common.mybatis.SqlSessionFactorySingletonHolder;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.validator.IntegerTypeValidator;

public class RvsUtils {

	protected static final Logger logger = Logger.getLogger("Production");

	private static Map<String, String> SNOUT_MODELS = null;
	private static Map<String, Integer> SNOUT_MODEL_BENCHMARKS = null;
	private static Map<String, String> SNOUT_MODEL_LOCATION = null;
	private static Set<String> SNOUT_SAVETIME_341_MODELS = null;
	private static Set<String> CCD_LINE_MODELS = null;
	private static Set<String> CCD_MODELS = null;

	/**
	 * 取得N个工作日前/后的日期
	 * 
	 * @param startDate
	 *            坐标时间
	 * @param interval
	 *            N个工作日
	 * @return N个工作日前/后的日期
	 */
	public static Date switchWorkDate(Date startDate, int interval) {
		return switchWorkDate(startDate, interval, null);
	}
	public static Date switchWorkDate(Date startDate, int interval, SqlSession conn) {
		if (startDate == null)
			return null;
		if (interval == 0)
			return new Date(startDate.getTime());

		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		startDate.setTime(cal.getTime().getTime());

		cal.add(Calendar.DATE, interval);

		boolean conncreated = false;
		try {
			if (conn == null) {
				SqlSessionFactory factory = SqlSessionFactorySingletonHolder.getFactory();
//				if (factory == null) { // for TEST
//					factory = SqlSessionFactorySingletonHolder.getInstance().getFactory();
//				}
				conn = factory.openSession(TransactionIsolationLevel.READ_COMMITTED);
				conncreated = true;
			}
			if (interval > 0) {
	
				HolidayMapper dao = conn.getMapper(HolidayMapper.class);
				Calendar wCal = Calendar.getInstance();
				wCal.setTime(startDate);
				wCal.add(Calendar.SECOND, 1);
				List<Date> jDates = dao.searchHoliday(wCal.getTime(), cal.getTime());
				int holis = getHolis(wCal.getTime(), cal.getTime(), jDates);
				if (holis > 0) {
					return switchWorkDate(cal.getTime(), holis, conn);
				} else {
					return cal.getTime();
				}
			} else {
	
				HolidayMapper dao = conn.getMapper(HolidayMapper.class);
				Calendar wCal = Calendar.getInstance();
				wCal.setTime(startDate);
				wCal.add(Calendar.SECOND, -1);
				List<Date> jDates = dao.searchHoliday(cal.getTime(), wCal.getTime());
				int holis = getHolisRev(cal.getTime(), startDate, jDates);
				if (holis > 0) {
					return switchWorkDate(cal.getTime(), -holis, conn);
				} else {
					return cal.getTime();
				}
			}
		} catch(Exception e) {
			return null;
		} finally {
			if (conncreated && conn != null) conn.close();
		}
	}

	private static int getHolis(Date startDate, Date endDate, List<Date> jDates) {
		int iHolis = 0;

		Calendar cal = Calendar.getInstance();
		int iDates = 0;
		cal.setTime(startDate);
		cal.add(Calendar.DATE, 1);
		for (;DateUtil.compareDate(cal.getTime(), endDate) <= 0;cal.add(Calendar.DATE, 1)) {
			boolean isHoli = false;
			// 是双休日
			if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				isHoli = true;
			}
			// 休日记录里有
			for (;iDates < jDates.size();) {
				int compare = DateUtil.compareDate(jDates.get(iDates), cal.getTime());
				if (compare == 0) {
					isHoli = !isHoli;
					iDates++;
					break;
				} else if (compare < 0) {
					iDates++;
					continue;
				} else if (compare > 0) {
					break;
				}
			}
			if (isHoli) iHolis++;
		}
		return iHolis;
	}

	private static int getHolisRev(Date startDate, Date endDate, List<Date> jDates) {
		int iHolis = 0;

		Calendar cal = Calendar.getInstance();
		int iDates = 0;
		cal.setTime(startDate);
		for (;DateUtil.compareDate(cal.getTime(), endDate) < 0;cal.add(Calendar.DATE, 1)) {
			boolean isHoli = false;
			// 是双休日
			if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				isHoli = true;
			}
			// 休日记录里有
			for (;iDates < jDates.size();) {
				int compare = DateUtil.compareDate(jDates.get(iDates), cal.getTime());
				if (compare == 0) {
					isHoli = !isHoli;
					iDates++;
					break;
				} else if (compare < 0) {
					iDates++;
					continue;
				} else if (compare > 0) {
					break;
				}
			}
			if (isHoli) iHolis++;
		}
		return iHolis;
	}
	public boolean isHoliday(Date date, SqlSession conn) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		boolean conncreated = false;
		try {
			if (conn == null) {
				SqlSessionFactory factory = SqlSessionFactorySingletonHolder.getFactory();
				conn = factory.openSession(TransactionIsolationLevel.READ_COMMITTED);
				conncreated = true;
			}

			HolidayMapper dao = conn.getMapper(HolidayMapper.class);
			boolean logged = dao.existsHoliday(date);
			// 是双休日
			if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				return logged ? false : true;
			} else {
				return logged ? true : false;
			}
		} catch(Exception e) {
			return false;
		} finally {
			if (conncreated && conn != null) conn.close();
		}
	}

	/**
	 * 用时(分钟)转换为小时+分格式。
	 * @param minutes 分钟数
	 */
	public static String formatMinutes(int minutes) {
		int hours = minutes / 60;
		int minutesRemain = minutes % 60;
		return (hours == 0 ? "" : "" + hours + "小时") + minutesRemain + "分";
	}

	/**
	 * 型号名正规化匹配取得
	 * @param modelName
	 * @return
	 */
	public static String regfy(String modelName) {
		if (modelName == null)
			return null;
		else {
			// UTF-8全角三字节
			return "^" + modelName.replaceAll("[\\{\\(\\[｛【「『（\\}\\)\\]｝】」』）]", ".{1,3}").replaceAll("[  　]", "[  　]{0,1}") + "$";
		}
	}

	private static Map<String, String> overLineCache = new HashMap<String, String>();

	public static String getZeroOverLine(String model_name, String category_name, LoginData user, String spprocess_code) throws Exception {
		// 取得用户信息
		String process_code;
		if (spprocess_code != null) {
			process_code = spprocess_code;
		} else {
			process_code = user.getProcess_code();
		}

		String sOverline = PathConsts.POSITION_SETTINGS.getProperty("overline.0." + process_code + "." + model_name);

		if (sOverline == null && category_name != null) {
			sOverline = PathConsts.POSITION_SETTINGS.getProperty("overline.0." + process_code + "." + category_name);
		}
		if (sOverline == null) {
			sOverline = PathConsts.POSITION_SETTINGS.getProperty("overline.0." + process_code + "._default");
		}
		if (sOverline == null) {
			logger.warn(process_code + "工位的标准作业时间没有定义。");
			sOverline = "-1";
		}

		return sOverline.trim();
	}
	
	public static String getLevelOverLine(String model_name, String category_name, String level, LoginData user, String spprocess_code) throws Exception {
		// 取得用户信息
		String process_code;
		if (spprocess_code != null) {
			process_code = spprocess_code;
		} else if(user != null){
			process_code = user.getProcess_code();
		}else{
			return null;
		}

		String cacheKey = "M[" + model_name + "]L[" + level + "]P[" + process_code + "]";
		if (overLineCache.containsKey(cacheKey)) {
			return overLineCache.get(cacheKey);
		}

		String sOverline = "";

		if (level != null) {
			if (level.length() > 1) level = level.substring(0,1);
			sOverline = PathConsts.POSITION_SETTINGS.getProperty("overline." + level + "." + process_code + "." + model_name);
	
			if (sOverline == null) {
				sOverline = PathConsts.POSITION_SETTINGS.getProperty("overline.0." + process_code + "." + model_name);
			}
			if (sOverline == null && category_name != null) {
				sOverline = PathConsts.POSITION_SETTINGS.getProperty("overline." + level + "." + process_code + "." + category_name);
			}
			if (sOverline == null && category_name != null) {
				sOverline = PathConsts.POSITION_SETTINGS.getProperty("overline.0." + process_code + "." + category_name);
			}
			if (sOverline == null) {
				sOverline = PathConsts.POSITION_SETTINGS.getProperty("overline." + level + "." + process_code + "._default");
			}
			if (sOverline == null) {
				sOverline = PathConsts.POSITION_SETTINGS.getProperty("overline.0." + process_code + "._default");
			}
		} else {
			sOverline = getZeroOverLine(model_name, category_name, user, process_code);
		}
		if (sOverline == null) {
			sOverline = getZeroOverLine(model_name, category_name, user, process_code);
		}

		Map<String, Object> checkMap = new HashMap<String, Object>();
		checkMap.put("lever", sOverline);
		if (IntegerTypeValidator.INSTANCE.validate(checkMap, "lever") != null) {
			sOverline = "0";
		}

		overLineCache.put(cacheKey, sOverline);

		return sOverline;
	}

	public static String getWaitingflow(String section, LoginData user, String spprocess_code) {
		// 取得用户信息
		String process_code = "0";
		if (spprocess_code != null) {
			process_code = spprocess_code;
		} else if (user != null) {
			process_code = user.getProcess_code();
		}

		String cacheKey = "S[" + section + "]P[" + process_code + "]";
		if (overLineCache.containsKey(cacheKey)) {
			return overLineCache.get(cacheKey);
		}

		String sOverline = "";

		if (section != null) {
			sOverline = PathConsts.POSITION_SETTINGS.getProperty("waitingflow." + section.substring(section.length() - 1) + "." + process_code);
			if (sOverline == null) {
				sOverline = PathConsts.POSITION_SETTINGS.getProperty("waitingflow.0." + process_code);
			}
		} else {
			sOverline = PathConsts.POSITION_SETTINGS.getProperty("waitingflow.0." + process_code);
		}
		if (sOverline == null) {
			sOverline = "0";
		}

		overLineCache.put(cacheKey, sOverline);

		return sOverline;
	}

	public static Map<String, String> reverseLinkedMap(Map<String, String> oldMap) {
		Map<String, String> newMap = new LinkedHashMap<String, String>();

		List<String> tmpKeyList = new ArrayList<String>();
		List<String> tmpValueList = new ArrayList<String>();
		for (String key : oldMap.keySet()) {
			tmpKeyList.add(key);
			tmpValueList.add(oldMap.get(key));
		}

		for (int i = tmpKeyList.size() - 1; i >= 0 ; i--) {
			newMap.put(tmpKeyList.get(i), tmpValueList.get(i));
		}
		return newMap;
	}

	public static Map<String, Integer> getSnoutModelBenchmarks(SqlSession conn) {
		if (SNOUT_MODEL_BENCHMARKS == null) {
			initSnoutModels(conn);
		}
		return SNOUT_MODEL_BENCHMARKS;
	}

	public static Map<String, String> getSnoutModelLocations(SqlSession conn) {
		if (SNOUT_MODEL_LOCATION == null) {
			initSnoutModels(conn);
		}
		return SNOUT_MODEL_LOCATION;
	}

	private static void initSnoutModels(SqlSession conn) {
		String sSnoutModels = PathConsts.POSITION_SETTINGS.getProperty("snout.models");
		String sSnoutSaveTime341Models = PathConsts.POSITION_SETTINGS.getProperty("snout.savetime.341.models");

		SNOUT_MODELS = new LinkedHashMap<String, String>();
		SNOUT_MODEL_BENCHMARKS = new LinkedHashMap<String, Integer>();
		SNOUT_MODEL_LOCATION = new TreeMap<String, String>();
		if (sSnoutModels != null) {
			String[] model_names = sSnoutModels.split(",");
			ModelMapper mdlDao = conn.getMapper(ModelMapper.class);
			for (String model_name : model_names) {
				String model_id = mdlDao.getModelByName(RvsUtils.regfy(model_name));
				if (model_id != null) {
					SNOUT_MODELS.put(model_id, model_name);

					String setting = PathConsts.POSITION_SETTINGS.getProperty("snout.model." + model_name);
					if (setting != null) {
						String[] splits = setting.split(":");
						if (splits.length >= 2) {
							int iBenchmark = Integer.parseInt(splits[0]);
							SNOUT_MODEL_BENCHMARKS.put(model_id, iBenchmark);
							SNOUT_MODEL_LOCATION.put(splits[1], model_id);
						}
					}
				} else {
					logger.error("不正确的型号设定：" + model_name);
				}
			}
		}

		SNOUT_SAVETIME_341_MODELS = new LinkedHashSet<String>(); 
		if (sSnoutSaveTime341Models != null) {
			String[] model_names = sSnoutSaveTime341Models.split(",");
			ModelMapper mdlDao = conn.getMapper(ModelMapper.class);
			for (String model_name : model_names) {
				String model_id = mdlDao.getModelByName(RvsUtils.regfy(model_name));
				if (model_id != null) {
					SNOUT_SAVETIME_341_MODELS.add(model_id);
				} else {
					logger.error("不正确的型号设定：" + model_name);
				}
			}
		}
	}

	public static Map<String, String> getSnoutModels(SqlSession conn) {
		if (SNOUT_MODELS == null) {
			initSnoutModels(conn);
		}
		return SNOUT_MODELS;
	}

	private static void initCcdModels(SqlSession conn) {
		String sCcdModels = PathConsts.POSITION_SETTINGS.getProperty("ccd.models");

		CCD_MODELS = new LinkedHashSet<String>();
		if (sCcdModels != null) {
			String[] model_names = sCcdModels.split(",");
			ModelMapper mdlDao = conn.getMapper(ModelMapper.class);
			for (String model_name : model_names) {
				String model_id = mdlDao.getModelByName(RvsUtils.regfy(model_name));
				if (model_id != null) {
					CCD_MODELS.add(model_id);
				} else {
					logger.error("不正确的型号设定：" + model_name);
				}
			}
		}

		String sCcdLineModels = PathConsts.POSITION_SETTINGS.getProperty("ccdline.models");
		CCD_LINE_MODELS = new LinkedHashSet<String>();
		if (sCcdLineModels != null) {
			String[] model_names = sCcdLineModels.split(",");
			ModelMapper mdlDao = conn.getMapper(ModelMapper.class);
			for (String model_name : model_names) {
				String model_id = mdlDao.getModelByName(RvsUtils.regfy(model_name));
				if (model_id != null) {
					CCD_LINE_MODELS.add(model_id);
				} else {
					logger.error("不正确的型号设定：" + model_name);
				}
			}
		}
	}

	public static Set<String> getCcdModels(SqlSession conn) {
		if (CCD_MODELS == null) {
			initCcdModels(conn);
		}
		return CCD_MODELS;
	}

	public static Set<String> getCcdLineModels(SqlSession conn) {
		if (CCD_LINE_MODELS == null) {
			initCcdModels(conn);
		}
		return CCD_LINE_MODELS;
	}

	public static Set<String> getSnoutSavetime341Models(SqlSession conn) {
		if (SNOUT_SAVETIME_341_MODELS == null) {
			initSnoutModels(conn);
		}
		return SNOUT_SAVETIME_341_MODELS;
	}

	public static String arrivalPlanDate2String(Date arrival_plan_date, String sPattern) {
		String sArrivalPlanDate = DateUtil.toString(arrival_plan_date, "yyyy");
		if (sArrivalPlanDate == null) {
			return "-";
		} else if ("9999".equals(sArrivalPlanDate)) {
			return "未定";
		} else {
			return DateUtil.toString(arrival_plan_date, sPattern);
		}
	}

	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat dfE = new SimpleDateFormat("M月", new Locale("EN"));
	private static SimpleDateFormat dfS = new SimpleDateFormat("M/d");

	/**
	 * 取得本期内月份
	 * 
	 * @param adjustDate 参照日期
	 * @param toEnd 包含年度内参照日期之后的结果
	 * @param years 返回：年文字列
	 * @param months 返回：月文字列
	 */
	public static int getMonthAxisInBussinessYear(Date adjustDate, boolean toEnd, boolean toNow, List<String> years, List<String> months) {
		if (adjustDate == null || years == null || months == null) {
			return -1;
		}

		Calendar cal = Calendar.getInstance();
		int nowYear = cal.get(Calendar.YEAR);
		int nowMonth = cal.get(Calendar.MONTH);

		cal.setTime(adjustDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		int adjustYear = cal.get(Calendar.YEAR);
		int adjustMonth = cal.get(Calendar.MONTH);

		if (adjustMonth < 3) {
			adjustYear--;
		}

		int currentIndex = -1;
		for (int i = 3; i < 15; i++) {
			int iYear = (adjustYear + (i >= 12 ? 1 : 0));
			years.add("" + iYear);
			months.add("" + (i % 12 + 1));
			if (toNow && nowYear == iYear && nowMonth == i % 12) return i - 3;
			if (i % 12 == adjustMonth) {
				if (toEnd) currentIndex = i - 3;
				else return i - 3;
			}
		}
		return currentIndex;
	}

	/**
	 * 取得本期内月份
	 * 
	 * @param adjustDate 参照日期
	 * @param toEnd 包含年度内参照日期之后的结果
	 * @param years 返回：年文字列
	 * @param months 返回：月文字列
	 */
	public static int getMonthAxisInNatualYear(Date adjustDate, boolean toEnd, boolean toNow, List<String> years, List<String> months) {
		if (adjustDate == null || years == null || months == null) {
			return -1;
		}

		Calendar cal = Calendar.getInstance();
		int nowYear = cal.get(Calendar.YEAR);
		int nowMonth = cal.get(Calendar.MONTH);

		cal.setTime(adjustDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		int adjustYear = cal.get(Calendar.YEAR);
		int adjustMonth = cal.get(Calendar.MONTH);

		int currentIndex = -1;
		for (int i = 0; i < 12; i++) {
			int iYear = adjustYear;
			years.add("" + iYear);
			months.add("" + (i + 1));
			if (toNow && nowYear == iYear && nowMonth == i) return i;
			if (i == adjustMonth) {
				if (toEnd) currentIndex = i;
				else return i;
			}
		}
		return currentIndex;
	}

	/**
	 * 取得月内的星期分割
	 * 
	 * @param adjustDate 参照日期
	 * @param toEnd 包含月内参照日期之后的结果
	 * @param startDates 返回：坐标区间开始日
	 * @param endDates 返回：坐标区间结束日
	 */
	public static void getWeekAxisInMonth(Date adjustDate, boolean toEnd, List<Date> startDates, List<Date> endDates) {
		if (adjustDate == null || startDates == null || endDates == null) {
			return;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(adjustDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		int adjustMonth = cal.get(Calendar.MONTH);

		cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
		cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);

		do {
			Calendar startCal = Calendar.getInstance();
			startCal.setTimeInMillis(cal.getTimeInMillis());
			startCal.add(Calendar.DATE, -6);
			if (toEnd) {
				if (startCal.get(Calendar.MONTH) != adjustMonth && cal.get(Calendar.MONTH) != adjustMonth) {
					return;
				}
			} else {
				if (startCal.getTime().after(adjustDate)) {
					return;
				}
			}
			if (startCal.get(Calendar.MONTH) != adjustMonth) {
				startCal.set(Calendar.MONTH, adjustMonth);
				startCal.set(Calendar.DATE, 1);
			}
			if (cal.get(Calendar.MONTH) != adjustMonth) {
				cal.set(Calendar.DATE, 1);
				cal.add(Calendar.DATE, -1);
			}

			startDates.add(startCal.getTime());
			endDates.add(cal.getTime());

			cal.add(Calendar.DATE, 7);
		} while (true);
	}

	public static String getBussinessYearString(Calendar date) {
		int adjustYear = date.get(Calendar.YEAR);
		int adjustMonth = date.get(Calendar.MONTH);
		
		if (adjustMonth < 3) {
			adjustYear--;
		}
		return (adjustYear - 1867) + "P";
	}

	public static int getBussinessYearStringRever(String bussinessYearString) {

		int iBussinessYear = 0;
		try {
			bussinessYearString = bussinessYearString.replaceAll("P", "");
			
			iBussinessYear = Integer.parseInt(bussinessYearString);
		} catch(Exception e) {
		}
		return iBussinessYear + 1867;
	}

	/**
	* 取得期起始时间
	*/
	public static String getBussinessStartYear(Calendar date) {
		int adjustYear = date.get(Calendar.YEAR);
		int adjustMonth = date.get(Calendar.MONTH);

		if (adjustMonth < 3) {
			adjustYear--;
		}
		return adjustYear + "";
	}
	/**
	* 取得期起始时间
	*/
	public static String getBussinessStartDate(Calendar date) {
		int adjustYear = date.get(Calendar.YEAR);
		int adjustMonth = date.get(Calendar.MONTH);

		if (adjustMonth < 3) {
			adjustYear--;
		}

		return adjustYear + "/04/01";
	}

	/**
	* 取得半期起始时间
	*/
	public static String getBussinessHalfStartDate(Calendar date) {
		int adjustYear = date.get(Calendar.YEAR);
		int adjustMonth = date.get(Calendar.MONTH);
		String sMonth = "";

		if (adjustMonth < 3) {
			adjustYear--;
			sMonth = "10";
		} else if (adjustMonth >= 9) {
			sMonth = "10";
		} else {
			sMonth = "04";
		}
		return adjustYear + "/" + sMonth + "/01";
	}

	public static String getBussinessHalfYearString(Calendar date) {
		int adjustMonth = date.get(Calendar.MONTH);
		
		if (adjustMonth < 3 || adjustMonth >= 9) {
			return getBussinessYearString(date) + "B";
		} else {
			return getBussinessYearString(date) + "A";
		}
	}

	public static Integer getYearByBussinessHalfYearStringAndMonth(String halfYearString, int month) {
		if (halfYearString == null) return null;

		int year = getBussinessYearStringRever(halfYearString.substring(0, halfYearString.length() - 1));
		if (month <= 3) {
			return (year + 1);
		} else {
			return year;
		}
	}

	/**
	 * 取得本期内月周
	 * @param adjustDate
	 * @param toEnd
	 * @param axisTexts
	 * @param startDatesoliday
	 * @param endDates
	 * @return
	 */
	public static int getMonthAndWeekAxisInHalfBussinessYear(Date adjustDate, boolean toEnd, boolean toNow, List<String> axisTexts) {
		return getMonthAndWeekAxisInHalfBussinessYear(adjustDate, toEnd, toNow, axisTexts ,null, null);
	}
	public static int getMonthAndWeekAxisInHalfBussinessYear(Date adjustDate, boolean toEnd, boolean toNow, List<String> axisTexts, List<String> years, List<String> months) {
		if (adjustDate == null || axisTexts == null) {
			return -1;
		}

		int retCd = -1;
		df.setTimeZone(TimeZone.getDefault());
		dfE.setTimeZone(TimeZone.getDefault());
		dfS.setTimeZone(TimeZone.getDefault());

		Calendar cal = Calendar.getInstance();
		String nowYear = "" + cal.get(Calendar.YEAR);
		String nowMonth = "" + (cal.get(Calendar.MONTH) + 1);

		cal.setTime(adjustDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		String currentYear = "" + cal.get(Calendar.YEAR);
		String currentMonth = "" + (cal.get(Calendar.MONTH) + 1);

		String adjustDateText = dfS.format(adjustDate.getTime());

		if (years == null)
			years = new ArrayList<String>();
		if (months == null)
			months = new ArrayList<String>();

		getMonthAxisInBussinessYear(adjustDate, toEnd, false, years, months);

		for (int i = 0; i < years.size(); i++) {
			if (years.get(i).equals(currentYear) && months.get(i).equals(currentMonth)) {
				// 本月

				List<Date> start_Dates = new ArrayList<Date>();
				List<Date> end_Dates = new ArrayList<Date>();
				getWeekAxisInMonth(adjustDate, toEnd, start_Dates, end_Dates);

				for (int j = 0; j < start_Dates.size(); j++) {
					Date startDate = start_Dates.get(j);
					Date endDate = end_Dates.get(j);

					String endDateText = dfS.format(endDate.getTime());
					if (endDateText.equals(adjustDateText)) {
						retCd = axisTexts.size();
					}

					axisTexts.add(dfS.format(startDate.getTime()) + "～" + endDateText);
				}

			} else {
				if (toNow && years.get(i).equals(nowYear) && Integer.parseInt(months.get(i)) > Integer.parseInt(nowMonth)) {
					break;
				}
				// 非本月
				Calendar startDate = Calendar.getInstance();
				startDate.set(Calendar.YEAR, Integer.parseInt(years.get(i)));
				startDate.set(Calendar.MONTH, Integer.parseInt(months.get(i)) - 1);
				startDate.set(Calendar.DATE, 1);

				Calendar endDate = Calendar.getInstance();
				endDate.set(Calendar.YEAR, Integer.parseInt(years.get(i)));
				endDate.set(Calendar.MONTH, Integer.parseInt(months.get(i)));
				endDate.set(Calendar.DATE, 1);
				endDate.add(Calendar.DATE, -1);

				axisTexts.add(dfE.format(startDate.getTime()));
			}
		}
		for (int i=0; i < months.size();i++) {
			String month = months.get(i);
			if (month.length() == 1) {
				month = "0" + month;
				months.set(i, month);
			}
		}
		return retCd;
	}

	/**
	 * 取得本期内月
	 * @param adjustDate
	 * @param toEnd
	 * @param axisTexts
	 * @param startDatesoliday
	 * @param endDates
	 * @return
	 */
	public static int getMonthAxisInHalfBussinessYear(Date adjustDate, boolean toEnd, boolean toNow, List<String> axisTexts) {
		return getMonthAxisInHalfBussinessYear(adjustDate, toEnd, toNow, axisTexts ,null, null);
	}
	public static int getMonthAxisInHalfBussinessYear(Date adjustDate, boolean toEnd, boolean toNow, List<String> axisTexts, List<String> years, List<String> months) {
		if (adjustDate == null || axisTexts == null) {
			return -1;
		}

		int retCd = -1;
		df.setTimeZone(TimeZone.getDefault());
		dfE.setTimeZone(TimeZone.getDefault());
		dfS.setTimeZone(TimeZone.getDefault());

		Calendar cal = Calendar.getInstance();
		String nowYear = "" + cal.get(Calendar.YEAR);
		String nowMonth = "" + (cal.get(Calendar.MONTH) + 1);

		cal.setTime(adjustDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		if (years == null)
			years = new ArrayList<String>();
		if (months == null)
			months = new ArrayList<String>();

		getMonthAxisInBussinessYear(adjustDate, toEnd, false, years, months);

		for (int i = 0; i < years.size(); i++) {
			if (toNow && years.get(i).equals(nowYear) && Integer.parseInt(months.get(i)) > Integer.parseInt(nowMonth)) {
				break;
			}
			Calendar startDate = Calendar.getInstance();
			startDate.set(Calendar.YEAR, Integer.parseInt(years.get(i)));
			startDate.set(Calendar.MONTH, Integer.parseInt(months.get(i)) - 1);
			startDate.set(Calendar.DATE, 1);
	
			Calendar endDate = Calendar.getInstance();
			endDate.set(Calendar.YEAR, Integer.parseInt(years.get(i)));
			endDate.set(Calendar.MONTH, Integer.parseInt(months.get(i)));
			endDate.set(Calendar.DATE, 1);
			endDate.add(Calendar.DATE, -1);
	
			axisTexts.add(dfE.format(startDate.getTime()));
		}
		for (int i=0; i < months.size();i++) {
			String month = months.get(i);
			if (month.length() == 1) {
				month = "0" + month;
				months.set(i, month);
			}
		}
		return retCd;
	}

	/**
	 * 根据文件前三个字节获取文件编码信息
	 * 
	 * @param filePath
	 * @return code
	 */
	public static String getFileCode(String filePath) {
		InputStream inputStream = null;
		String code = "";
		try {
			inputStream = new FileInputStream(filePath);// 读取文件
			byte[] head = new byte[3];// 存储文件前三个字节
			inputStream.read(head);
			code = "GBK";// 设置默认编码为GBK
			if (head[0] == -1 && head[1] == -2)
				code = "UTF-16";
			if (head[0] == -2 && head[1] == -1)
				code = "Unicode";
			if (head[0] == -17 && head[1] == -69 && head[2] == -65)
				code = "UTF-8";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return code;
	}
	
	 
    /**
	 * 获取一个月中第一天的日期
	 * 
	 * @param year
	 * @param month
	 * @return strDate
	 */
	public static Date getStartDate(String year, String month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.valueOf(year), Integer.valueOf(month) - 1, 1);
		String strDate = DateUtil.toString(calendar.getTime(), DateUtil.ISO_DATE_PATTERN);
		Date date = DateUtil.toDate(strDate, DateUtil.ISO_DATE_PATTERN);
		return date;
	}

	/**
	 * 获取一个月中最后一天的日期
	 * 
	 * @param year
	 * @param month
	 * @return strDate
	 */
	public static Date getEndDate(String year, String month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.valueOf(year), Integer.valueOf(month), 0);
		String strDate = DateUtil.toString(calendar.getTime(), DateUtil.ISO_DATE_PATTERN);
		Date date = DateUtil.toDate(strDate, DateUtil.ISO_DATE_PATTERN);
		return date;
	}
    
	public static SqlSessionManager getTempWritableConn() {
		@SuppressWarnings("static-access")
		SqlSessionFactory factory = SqlSessionFactorySingletonHolder.getInstance().getFactory();
		return SqlSessionManager.newInstance(factory);
	}

	public static void sendTrigger(List<String> triggerList) {
		try {  
			for (String trigger : triggerList) {
				// 通知
				HttpAsyncClient httpclient = new DefaultHttpAsyncClient();
				httpclient.start();
				try {  
					HttpGet request = new HttpGet(trigger);
					logger.info("finger:"+request.getURI());
					httpclient.execute(request, null);
	
				} catch (Exception e) {
				} finally {
					Thread.sleep(80);
					httpclient.shutdown();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	public static void sendTrigger(String trigger) {
		List<String> triggerList = new ArrayList<String>();
		triggerList.add(trigger);
		sendTrigger(triggerList);
	}

	private static final Map<String, Integer> countdownCache = new HashMap<String, Integer>();

	public static int getCountdown(String category_name, int kind, String level_name, String line_name) {

		String cacheKey = "C[" + category_name + "]R[" + level_name + "]L[" + line_name + "]";
		if (countdownCache.containsKey(cacheKey)) {
			return countdownCache.get(cacheKey);
		}

		String sCountdown = "";

		sCountdown = PathConsts.SCHEDULE_SETTINGS.getProperty("countdown." + category_name + "." + level_name + "." + line_name);

		if (sCountdown == null) {
			String kind_name = CodeListUtils.getValue("category_kind", "" + kind);
			sCountdown = PathConsts.SCHEDULE_SETTINGS.getProperty("countdown." + kind_name + "." + level_name + "." + line_name);
		}
		if (sCountdown == null && "S3".equals(level_name)) {
			int rankup = getCountdown(category_name, kind, "S2", line_name);
			countdownCache.put(cacheKey, rankup);
			return rankup;
		}
		if (sCountdown == null && !"S1".equals(level_name)) {
			int rankup = getCountdown(category_name, kind, "S1", line_name);
			countdownCache.put(cacheKey, rankup);
			return rankup;
		}

		if (sCountdown == null) {
			sCountdown = "0";
		}

		int iCountdown = 0;
		try {
			iCountdown = Integer.parseInt(sCountdown);
		} catch (NumberFormatException e) {
		}
		countdownCache.put(cacheKey, iCountdown);

		return iCountdown;
	}

	private static final Map<String, Integer> unproceedPermitCache = new HashMap<String, Integer>();
	public static int getUnproceedPermit(String section_name, String line_name) {
		String cacheKey = "S[" + section_name + "]L[" + line_name + "]";
		if (unproceedPermitCache.containsKey(cacheKey)) {
			return unproceedPermitCache.get(cacheKey);
		}

		String sUnproceedPermit = PathConsts.SCHEDULE_SETTINGS.getProperty("unproceed.permit." + section_name + "." + line_name);
		if (sUnproceedPermit == null) {
			sUnproceedPermit = "0";
		}

		int iUnproceedPermit = 0;
		try {
			iUnproceedPermit = Integer.parseInt(sUnproceedPermit);
		} catch (NumberFormatException e) {
		}
		unproceedPermitCache.put(cacheKey, iUnproceedPermit);

		return iUnproceedPermit;
	}
	
	/**
	 * 将SVG转换成PNG
	 * @param svg  SVG代码
	 * @param cacheName 临时文件名称
	 * @return
	 * @throws Exception
	 */
	public static String convertSVGToPng(String svgCode,String cacheName) throws Exception{
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM");
		
		File file = new File(cachePath);
		if(!file.exists()){
			file.mkdirs();
		}
		cachePath = cachePath + "\\" + cacheName;
		
		OutputStream outputStream = null;
		TranscoderInput transcoderInput = null;
		TranscoderOutput transcoderOutput = null;
		
		try{
			file = new File(cachePath);
			if(!file.exists()){
				file.createNewFile();
			}
			
			//文件输出地址
			outputStream = new FileOutputStream(file);
			
			byte[] bytes = svgCode.getBytes("utf-8");
			//输入转码流
			transcoderInput = new TranscoderInput(new ByteArrayInputStream(bytes));
			//输出转码流
			transcoderOutput = new TranscoderOutput(outputStream);
			//定义png转码器
			PNGTranscoder pngTranscoder = new PNGTranscoder();
			//转码
			pngTranscoder.transcode(transcoderInput, transcoderOutput);
			outputStream.flush();
		}catch(Exception e){
			throw e;
		}finally{
			if(outputStream!=null){
				outputStream.close();
			}
		}
		
		return cachePath;
	}

	/**
	 * 按照同意日取得纳期
	 * @param agreedDate
	 * @param level
	 * @param fixType
	 * @param scheduledExpedite
	 * @param conn
	 * @param needSub
	 * @return 0：纳期 ；1：分解完成纳期
	 */
	public static Date[] getTimeLimit(Date agreedDate, Integer level, Integer fixType,
			Integer scheduledExpedite, SqlSession conn, boolean needSub) {
		if (agreedDate == null || level == null || agreedDate.getTime() > 32503564800000l) return new Date[]{null};

		boolean lightFix = isLightFix(level);
		boolean peripheral = (level == 56 || level == 57 || level == 58 || level == 59);
		lightFix = lightFix && (fixType == 1);

		// 纳期限定
		Integer timeLimit = RvsConsts.TIME_LIMIT;
//		// 直送快速
//		if (scheduledExpedite != null && 2 == scheduledExpedite) {
//			if (1 == level) {
//				// S1同意日期+2个工作日
//				timeLimit = +2;
//			} else {
//				// S2/S3同意日期+4个工作日
//				timeLimit = +4;
//			}
//		}
		if (lightFix) {
			timeLimit = +2;
		}
		if (peripheral) { // 周边8个工作日 -> (151PB) 4个工作日
			timeLimit = +4;
		}

		Date workDate = RvsUtils.switchWorkDate(agreedDate, timeLimit);

		if (!needSub) {
			return new Date[]{workDate};
		}

		/** 设定产出安排时间为 同意日5个工作日后 */
		timeLimit--;

		Date workSubDate = RvsUtils.switchWorkDate(agreedDate, timeLimit);

		return new Date[]{workDate, workSubDate};
	}

	/**
	 * 判断是否为小修理或者中修理
	 * @param level
	 * @return boolean
	 */
	public static boolean isLightFix(String level) {
		boolean isLightFix = false;
		if (!CommonStringUtil.isEmpty(level)) {
			return isLightFix(Integer.parseInt(level));
		}
		return isLightFix;
	}
	public static boolean isLightFix(Integer level) {
		boolean isLightFix = (level != null) &&
				((level == 9 || level== 91 || level == 92 || level == 93) ||
						(level== 96 || level == 97 || level == 98 || level == 99));
		return isLightFix;
	}	
	public static boolean isMediumFix(String level) {
		boolean isMediumFix = false;
		if (!CommonStringUtil.isEmpty(level)) {
			return isMediumFix(Integer.parseInt(level));
		}
		return isMediumFix;
	}
	public static boolean isMediumFix(Integer level) {
		boolean isMediumFix = (level != null) &&
				((level== 96 || level == 97 || level == 98));
		return isMediumFix;
	}	

	public static void initAll(SqlSession conn) {
		overLineCache.clear();

		initSnoutModels(conn);
		initCcdModels(conn);

		countdownCache.clear();

		unproceedPermitCache.clear();;
	}
}
