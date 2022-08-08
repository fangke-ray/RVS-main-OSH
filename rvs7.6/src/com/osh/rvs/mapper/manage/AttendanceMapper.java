package com.osh.rvs.mapper.manage;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.manage.AttendanceRecordEntity;
import com.osh.rvs.bean.manage.AttendanceReportEntity;

public interface AttendanceMapper {

	/*查询出勤情况详细一览*/
	public List<AttendanceReportEntity> searchAttendanceReport(AttendanceReportEntity conditionBean);

	/*判断是否已经存在过该条数据*/
	public AttendanceReportEntity checkIsExist(AttendanceReportEntity conditionBean);
	/*新建出勤情况*/
	public void insertAttendanceReport(AttendanceReportEntity conditionBean);
	
	/*双击修改出勤情况*/
	public void updateAttendanceReport(AttendanceReportEntity conditionBean);
	
	/*删除出勤情况*/
	public void deleteAttendanceReport(AttendanceReportEntity conditionBean);

	public List<AttendanceRecordEntity> getAttendanceRecords(@Param("record_type") int record_type);

	public void insertAttendanceRecord(AttendanceRecordEntity recordEntity);

}
