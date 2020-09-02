package com.osh.rvs.service.qa;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.PcsUtils;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.mapper.master.HolidayMapper;
import com.osh.rvs.mapper.qa.QualityAssuranceMapper;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.partial.ComponentManageService;
import com.osh.rvs.service.partial.ComponentSettingService;

import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

public class QualityAssuranceService {
	/**
	 * 得到维修对象信息 For 品保
	 * 
	 * @param material_id
	 * @param conn
	 * @return
	 */
	public MaterialForm getMaterialInfo(String material_id, SqlSession conn) {
		MaterialForm materialForm = new MaterialForm();
		QualityAssuranceMapper dao = conn.getMapper(QualityAssuranceMapper.class);
		MaterialEntity materialEntity = dao.getMaterialDetail(material_id);
		BeanUtil.copyToForm(materialEntity, materialForm, CopyOptions.COPYOPTIONS_NOEMPTY);

		return materialForm;
	}
	private static Logger _log = Logger.getLogger(QualityAssuranceService.class);

	private static String TARGET_RATE_146P = "99.8%";

	/**
	 * 工程检查票Pdf制造
	 * 
	 * @param mform
	 * @param folderPath 
	 * @param conn
	 * @throws IOException
	 */
	public void makePdf(MaterialForm mform, String folderPath, boolean getHistory, SqlSession conn) throws IOException {
		String[] showLines = new String[6];
		showLines[0] = "检查卡";
		showLines[1] = "最终检验";
		showLines[2] = "分解工程";
		showLines[3] = "NS 工程";
		showLines[4] = "总组工程";
		showLines[5] = "外科硬镜修理工程";

		MaterialService mService = new MaterialService();

		for (String showLine : showLines) {
			Map<String, String> fileTempl = PcsUtils.getXlsContents(showLine, mform.getModel_name(), null, 
					mform.getMaterial_id(), RvsUtils.isLightFix(mform.getLevel()), getHistory, conn);

			if ("NS 工程".equals(showLine))
				mService.filterSolo(fileTempl, mform.getMaterial_id(), conn);

			String retEmpty = PcsUtils.toPdf(fileTempl, mform.getMaterial_id(), mform.getSorc_no(), mform.getModel_name(),
					mform.getSerial_no(), mform.getLevel(), null, folderPath, conn);
			if (retEmpty!= null && retEmpty.length() > 0) {
				Logger logger = Logger.getLogger("Download");
				logger.info(retEmpty + " MODEL: " + mform.getModel_name() + " PAT:" + mform.getPat_id());
			}
		}

		// 组件
		if ("1".equals(mform.getLevel())) {
			Set<String> nsCompModels = ComponentSettingService.getNsCompModels(conn);
			boolean isNsCompModel = nsCompModels.contains(mform.getModel_id());

			// 生成NS组件检查票
			if (isNsCompModel) {
				ComponentManageService cmService = new ComponentManageService();
				String serialNos = cmService.getSerialNosForTargetMaterial(mform.getMaterial_id(), conn);
				if (!isEmpty(serialNos)) {
					String[] serialNoArray = serialNos.split(",");
					Map<String, String> fileTempl = new HashMap<String, String>();
					fileTempl.put("NS组件组装", PathConsts.BASE_PATH + PathConsts.PCS_TEMPLATE + "\\excel\\NS 工程\\NS组件组装\\" + mform.getModel_name() + ".xls"); // TODO

					for (int i = 0;i < serialNoArray.length; i++) {
						PcsUtils.toPdfSnout(fileTempl, mform.getModel_id(), serialNoArray[i], folderPath, conn);
					}
					
				}
			}
		}
	}

	/**
	 * 得到作业信息 For 品保
	 * 
	 * @param material_id
	 * @param conn
	 * @return
	 */
	public boolean getProccessingData(Map<String, Object> listResponse, String material_id, ProductionFeatureEntity pf,
			LoginData user, SqlSession conn) throws Exception {
		// 取得维修对象信息。
		MaterialForm mform = getMaterialInfo(material_id, conn);
		listResponse.put("mform", mform);
		if (mform.getQa_check_time() == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 品保结果一览更新
	 * 
	 * @param material_id
	 * @param conn
	 * @return
	 */
	public void listRefresh(Map<String, Object> listResponse, String position_id, SqlSession conn) {
		QualityAssuranceMapper qDao = conn.getMapper(QualityAssuranceMapper.class);
		// 取得待品保处理对象一览 611
		List<MaterialEntity> waitings = qDao.getWaitings(position_id);

		// 取得今日已完成处理对象一览
		List<MaterialEntity> finished = qDao.getFinished(position_id);

		List<MaterialForm> waitingsForms = new ArrayList<MaterialForm>();
		List<MaterialForm> finishedForm = new ArrayList<MaterialForm>();

		for (MaterialEntity waiting : waitings) {
			MaterialForm waitingsForm = new MaterialForm();
			BeanUtil.copyToForm(waiting, waitingsForm, CopyOptions.COPYOPTIONS_NOEMPTY);
			String comment = "";
			if (waiting.getDirect_flg() != null && waiting.getDirect_flg()==1) {
				comment += "直送";
			}
			if (waiting.getService_repair_flg() != null) {
				comment += CodeListUtils.getValue("material_service_repair", ""+waiting.getService_repair_flg());
			}
			waitingsForm.setStatus(comment);
			waitingsForms.add(waitingsForm);
		}

		BeanUtil.copyToFormList(finished, finishedForm, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialForm.class);

		listResponse.put("waitings", waitingsForms);
		listResponse.put("finished", finishedForm);

	}

	/**
	 * 返回品保展示月份
	 * 
	 * @param selectedMonth
	 * @return
	 */
	public Map<String, Object> getYearMonthByDate(Calendar selectedMonth) {
		Map<String, Object> ret = new HashMap<String, Object>();
		int year = selectedMonth.get(Calendar.YEAR);

		ret.put("yOptions", "<option value=''/><option value='" + (year - 1) + "'>" + (year - 1)
				+ "</option><option value='" + year + "'>" + year + "</option>");

		List<String> years = new ArrayList<String>();
		List<String> months = new ArrayList<String>();
		RvsUtils.getMonthAxisInNatualYear(selectedMonth.getTime(), true, true, years, months);

		String mOptions = "<option value=''/>";
		for (String month : months) {
			mOptions += "<option value='" + month + "'>" + month + "月</option>";
		}
		ret.put("mOptions", mOptions);

		int sMonth = (selectedMonth.get(Calendar.MONTH) + 1);
		ret.put("sMonth", year + "年" + sMonth + "月（"
						+ RvsUtils.getBussinessHalfYearString(selectedMonth) + "）");
		ret.put("yearMonthValue", year + (sMonth >= 10 ? ""+sMonth : "0"+sMonth) );

		return ret;
	}

    SimpleDateFormat objSimpleDateFormat = new SimpleDateFormat("M月");

    /**
	 * 返回品保展示数据
	 * 
	 * @param selectedMonth 月底
	 * @return
	 */
	public Map<String, Object> getData(Calendar selectedMonth, SqlSessionManager conn) {
		Map<String, Object> ret = new HashMap<String, Object>();

		List<String> axisTexts = new ArrayList<String>();
		List<String> years = new ArrayList<String>();
		List<String> months = new ArrayList<String>();
		RvsUtils.getMonthAxisInHalfBussinessYear(selectedMonth.getTime(), true, true, axisTexts, years, months);

		// 本月文字
		String sSelectedMonth = objSimpleDateFormat.format(selectedMonth.getTime());
		
		int yearMonthIndex = 0;
		QualityAssuranceMapper qaMapper = conn.getMapper(QualityAssuranceMapper.class);
		List<HashMap<String, Object>> tLines = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < axisTexts.size();i++) {
			String axisText = axisTexts.get(i);
			if (!sSelectedMonth.equals(axisText)) {
				HashMap<String, Object> tLine = qaMapper.getQualityAssuranceDataForMonth(years.get(yearMonthIndex)
						+ months.get(yearMonthIndex));
				tLine.put("axisText", axisText);
				tLine.put("targetRate", TARGET_RATE_146P);
				tLines.add(tLine);
				yearMonthIndex++;
			} else {
				List<HashMap<String, Object>> tLineWeeks = qaMapper.getQualityAssuranceDataForWeek(years.get(yearMonthIndex)
						+ months.get(yearMonthIndex));
//				selectedMonth
				// 未登记
				if (tLineWeeks == null || tLineWeeks.size() == 0) {
					insertFirstAtMonth(selectedMonth, conn);

					tLineWeeks = qaMapper.getQualityAssuranceDataForWeek(years.get(yearMonthIndex)
							+ months.get(yearMonthIndex));
				} 

				sSelectedMonth = sSelectedMonth.replaceAll("月", "/");
				for (HashMap<String, Object> tLine : tLineWeeks) {

					tLine.put("axisText", sSelectedMonth + tLine.get("start_date") + "～" + sSelectedMonth + tLine.get("end_date"));
					tLine.put("targetRate", TARGET_RATE_146P);
					tLines.add(tLine);

					
//					if (i >= axisTexts.size())
//						break;
				}

				yearMonthIndex++;
			}
		}

		ret.put("tLines", tLines);

		return ret;
	}

    SimpleDateFormat dfYm = new SimpleDateFormat("yyyyMM");

    private void insertFirstAtMonth(Calendar selectedMonth, SqlSession conn) {
		Calendar firstDate = Calendar.getInstance();
		firstDate.setTimeInMillis(selectedMonth.getTimeInMillis());
		firstDate.set(Calendar.DATE, 1);
		
		List<WeekBean> weeks = insertWeekPart(firstDate, conn);
		QualityAssuranceMapper dao = conn.getMapper(QualityAssuranceMapper.class);
		int igz = 1;
		for (WeekBean week : weeks) {
			dao.updateQualityAssuranceDataForWeek(dfYm.format(selectedMonth.getTime()), "" + igz, "0", "0", week.start_date, week.end_date);
			igz ++;
		}
	}

	@SuppressWarnings("unchecked")
	public void setMonthData(Map<String, String[]> parameterMap, SqlSessionManager conn) {

		@SuppressWarnings("rawtypes")
		List<HashMap> qaStatictisInMonth = new AutofillArrayList<HashMap>(HashMap.class);
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("weeks".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameterMap.get(parameterKey);

					if ("process_count".equals(column)) {
						qaStatictisInMonth.get(icounts).put("process_count", value[0]);
					} else if ("fail_count".equals(column)) {
						qaStatictisInMonth.get(icounts).put("fail_count", value[0]);
					} else if ("start_date".equals(column)) {
						qaStatictisInMonth.get(icounts).put("start_date", value[0]);
					} else if ("end_date".equals(column)) {
						qaStatictisInMonth.get(icounts).put("end_date", value[0]);
					}
				}
			}
		}

		// 年月
		String sYearMonth = parameterMap.get("year_month")[0];

		QualityAssuranceMapper mapper = conn.getMapper(QualityAssuranceMapper.class);

		int idex = 0;
		// 更新月品保数据
		for (HashMap<String, ?> qaStatictisInWeek : qaStatictisInMonth) {

			mapper.updateQualityAssuranceDataForWeek(sYearMonth, Integer.toString(++idex),
					qaStatictisInWeek.get("process_count").toString(), qaStatictisInWeek.get("fail_count").toString(), 
					qaStatictisInWeek.get("start_date").toString(), qaStatictisInWeek.get("end_date").toString());
		}
	}

	/**
	 * 取得周和工作日信息
	 * @param monthStart
	 * @param restDays
	 * @param conn
	 * @return
	 */
	private List<WeekBean> insertWeekPart(Calendar monthStart, SqlSession conn) {

		Calendar thisMonthStart = Calendar.getInstance();
		Calendar nextMonthStart = Calendar.getInstance();

		thisMonthStart.setTimeInMillis(monthStart.getTimeInMillis());
		nextMonthStart.setTimeInMillis(monthStart.getTimeInMillis());

		nextMonthStart.add(Calendar.MONTH, 1);

		// 得到本月休假及调整日
		HolidayMapper hdao = conn.getMapper(HolidayMapper.class);
		List<String> lholidays = hdao.searchHolidayOfMonth(DateUtil.toString(thisMonthStart.getTime(), "yyyy/MM"));
		Set<Integer> sHolidays = new HashSet<Integer>();
		for (String lholiday : lholidays) sHolidays.add(Integer.parseInt(lholiday));

		List<WeekBean> weekBeans = new ArrayList<WeekBean>();
		// 周分段
		WeekBean weekBean= new WeekBean();
		for (;thisMonthStart.before(nextMonthStart); thisMonthStart.add(Calendar.DATE, 1)){
			int dayOfWeek = thisMonthStart.get(Calendar.DAY_OF_WEEK);
			int cdate = thisMonthStart.get(Calendar.DATE);
			// 是否工作日
			boolean isWorkDay = (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) 
					!= sHolidays.contains(cdate);
			String textDate = DateUtil.toString(thisMonthStart.getTime(), "dd");
			if (isWorkDay) {
				if (weekBean.start_date == null)
					weekBean.start_date = textDate;
				weekBean.count_include_dates++;
				weekBean.end_date = textDate;
			}
			if (dayOfWeek == Calendar.THURSDAY && weekBean.count_include_dates >= 3) { // 小于3天的归入下一周
				// 周结束
				weekBeans.add(weekBean);
				weekBean = new WeekBean();
			}
		}
		// 最后一周
		if (weekBean.count_include_dates > 0) {
			if (weekBean.count_include_dates >= 3) {
				weekBeans.add(weekBean);
			} else {
				// 计入上一周
				WeekBean lastWeekBean = weekBeans.get(weekBeans.size() - 1); // myiqsi
				lastWeekBean.end_date = weekBean.end_date;
				lastWeekBean.count_include_dates += weekBean.count_include_dates;
			}
		}
		return weekBeans;
	}

	private class WeekBean {
		private String start_date = null;
		private String end_date = null;
		private int count_include_dates = 0;
	}


	/**
	 * 更新不通过件数
	 * @param conn
	 */
	public void updateCountForbid(SqlSessionManager conn) {
		Calendar now = Calendar.getInstance();
		String sYearMonth = dfYm.format(now.getTime());
		int date = now.get(Calendar.DATE);

		QualityAssuranceMapper qaMapper = conn.getMapper(QualityAssuranceMapper.class);
		List<HashMap<String, Object>> tLineWeeks = qaMapper.getQualityAssuranceDataForWeek(sYearMonth);

		if (tLineWeeks == null || tLineWeeks.size() == 0) {
			insertFirstAtMonth(now, conn);

			tLineWeeks = qaMapper.getQualityAssuranceDataForWeek(sYearMonth);
		}

		qaMapper.addQaWorkCount(sYearMonth, ""+date, "1", "1");
	}

	public void updateCountSucceed(SqlSessionManager conn) {
		Calendar now = Calendar.getInstance();
		String sYearMonth = dfYm.format(now.getTime());
		int date = now.get(Calendar.DATE);

		QualityAssuranceMapper qaMapper = conn.getMapper(QualityAssuranceMapper.class);
		List<HashMap<String, Object>> tLineWeeks = qaMapper.getQualityAssuranceDataForWeek(sYearMonth);

		if (tLineWeeks == null || tLineWeeks.size() == 0) {
			insertFirstAtMonth(now, conn);

			tLineWeeks = qaMapper.getQualityAssuranceDataForWeek(sYearMonth);
		}

		qaMapper.addQaWorkCount(sYearMonth, "" + date, "1", "0");
	}

	/**
	 * 触发SAP修理完成接口
	 * @param material_id
	 */
	public void notifiSapShipping(String material_id) {

		// SAP同步
		String urlString = "http://localhost:8080/rvsIf/shipping/" + material_id;
		try {
			URL url = new URL(urlString);
			url.getQuery();
			URLConnection urlconn = url.openConnection();
			urlconn.setReadTimeout(1); // 不等返回
			urlconn.connect();
			urlconn.getContentType(); // 这个就能触发
		} catch (Exception e) {
			_log.error("Failed", e);
		}
	}

	public void updateQaCheckTime(String material_id, SqlSessionManager conn) throws Exception {
		// 检查维修对象表单
		MaterialEntity bean = new MaterialEntity();
		// 进行中的维修对象
		bean.setMaterial_id(material_id);
		bean.setQa_check_time(new Date());

		// 更新维修对象。
		QualityAssuranceMapper dao = conn.getMapper(QualityAssuranceMapper.class);
		dao.updateMaterial(bean);
	}
}
