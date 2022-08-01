package com.osh.rvs.bean.manage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 出勤人数

 */
public class AttendanceRecordEntity implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2445201068938334779L;
	
	// 课室 ID
	private String section_id;
	// 工程 ID
	private String line_id;
	// 记录类型
	private Integer record_type;
	// 人数记录日
	private Date record_date;
	// 分线
	private Integer px;
	// 出勤人数
	private BigDecimal clue_count;

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
	public Integer getRecord_type() {
		return record_type;
	}
	public void setRecord_type(Integer record_type) {
		this.record_type = record_type;
	}
	public Date getRecord_date() {
		return record_date;
	}
	public void setRecord_date(Date record_date) {
		this.record_date = record_date;
	}
	public Integer getPx() {
		return px;
	}
	public void setPx(Integer px) {
		this.px = px;
	}
	public BigDecimal getClue_count() {
		return clue_count;
	}
	public void setClue_count(BigDecimal clue_count) {
		this.clue_count = clue_count;
	}

}
