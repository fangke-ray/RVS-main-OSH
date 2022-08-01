package com.osh.rvs.bean.manage;

import java.io.Serializable;
import java.util.Date;

public class AttendanceReportEntity implements Serializable {

	/**
	 * 出勤情况
	 */
	private static final long serialVersionUID = 574379008402756604L;
	
	//出勤日期开始
	private Date report_date;
	//分发课室
	private String section_id;
	//工程
	private String line_id;
	//出勤情况
	private String attendance_comment;
	// 分线
	private Integer px;
	
	public Date getReport_date() {
		return report_date;
	}
	public void setReport_date(Date report_date) {
		this.report_date = report_date;
	}
	public String getSection_id() {
		return section_id;
	}
	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}
	public String getLine_id() {
		return line_id;
	}
	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}
	public String getAttendance_comment() {
		return attendance_comment;
	}
	public void setAttendance_comment(String attendance_comment) {
		this.attendance_comment = attendance_comment;
	}
	public Integer getPx() {
		return px;
	}
	public void setPx(Integer px) {
		this.px = px;
	}
}
