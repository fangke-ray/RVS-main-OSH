package com.osh.rvs.service.manage;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;

import com.osh.rvs.bean.manage.AttendanceRecordEntity;
import com.osh.rvs.bean.manage.AttendanceReportEntity;
import com.osh.rvs.mapper.manage.AttendanceMapper;

import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.copy.Converter;
import framework.huiqing.common.util.copy.IntegerConverter;

public class AttendanceService {

	public void getAttendances(String section_id, String line_id,
			SqlSession conn, Map<String, Object> callbackResponse) {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);

		AttendanceReportEntity conditionReport = new AttendanceReportEntity();
		conditionReport.setReport_date(today.getTime());
		conditionReport.setSection_id(section_id);
		conditionReport.setLine_id(line_id);

		AttendanceMapper mapper = conn.getMapper(AttendanceMapper.class);
		callbackResponse.put("headcounts", mapper.getAttendanceRecords(0));
		callbackResponse.put("today_attends", mapper.getAttendanceRecords(1));

		callbackResponse.put("today_remarks", mapper.searchAttendanceReport(conditionReport));
	}

	public void updateAttendance(HttpServletRequest request, SqlSessionManager conn) {

		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		Converter<Integer> iCov = IntegerConverter.getInstance();
		List<AttendanceRecordEntity> recordEntities = new AutofillArrayList<AttendanceRecordEntity>(AttendanceRecordEntity.class);
		List<AttendanceReportEntity> reportEntities = new AutofillArrayList<AttendanceReportEntity>(AttendanceReportEntity.class);

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
						recordEntities.get(icounts).setLine_id(value[0]);
					} else if ("px".equals(column)) {
						recordEntities.get(icounts).setPx(iCov.getAsObject(value[0]));
					} else if ("record_type".equals(column)) {
						recordEntities.get(icounts).setRecord_type(iCov.getAsObject(value[0]));
					} else if ("section_id".equals(column)) {
						recordEntities.get(icounts).setSection_id(value[0]);
					} else if ("attendance".equals(column)){
						recordEntities.get(icounts).setClue_count(new BigDecimal(value[0]));
					}
				} else if ("remarks".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameterMap.get(parameterKey);

					if ("line_id".equals(column)) {
						reportEntities.get(icounts).setLine_id(value[0]);
					} else if ("px".equals(column)) {
						reportEntities.get(icounts).setPx(iCov.getAsObject(value[0]));
					} else if ("section_id".equals(column)) {
						reportEntities.get(icounts).setSection_id(value[0]);
					} else if ("remark".equals(column)){
						reportEntities.get(icounts).setAttendance_comment(value[0]);
					}
				}
			}
		}

		Date today = new Date();
		AttendanceMapper mapper = conn.getMapper(AttendanceMapper.class);

		for (AttendanceRecordEntity recordEntity : recordEntities) {
			recordEntity.setRecord_date(today);
			mapper.insertAttendanceRecord(recordEntity);
		}

		for (AttendanceReportEntity reportEntity : reportEntities) {
			reportEntity.setReport_date(today);
			mapper.insertAttendanceReport(reportEntity);
		}
	}

}
