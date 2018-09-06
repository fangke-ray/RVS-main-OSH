package com.osh.rvs.bean.report;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @Title WorkTimeAnalysisEntity.java
 * @Project rvs
 * @Package com.osh.rvs.bean.report
 * @ClassName: WorkTimeAnalysisEntity
 * @Description: 工时分析
 * @author liuxb
 * @date 2016-10-8 下午1:56:26
 */
public class WorkTimeAnalysisEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4051838096029228210L;

	/** 机种ID **/
	private String category_id;

	/** 机种名称 **/
	private String category_name;

	/** 型号ID **/
	private String model_id;

	/** 型号名称 **/
	private String model_name;

	/** 等级 **/
	private Integer level;

	/** 课室ID **/
	private String section_id;

	/** 课室名称 **/
	private String section_name;

	/** 工位ID **/
	private String position_id;

	/** 工位代码 **/
	private String process_code;

	/** 加急 **/
	private Integer scheduled_expedited;

	/** 是否包含返工 **/
	private Integer rework;

	/** 人员ID **/
	private String operator_id;

	/** 作业开始时间 **/
	private Date finish_time_start;

	/** 作业结束时间 **/
	private Date finish_time_end;

	private Integer avgWorkTime;

	private String yearMonth;

	private String svg;

	private String operator_name;

	/** 是否包含异常 **/
	private Integer abnormal;

	private Integer standardWorkTime;

	private Integer upper;

	private String omr_notifi_no;

	private String serial_no;

	private Date finish_time;

	private Integer use_seconds;

	private String line_name;

	private String line_id;

	private Date action_time;

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public Integer getScheduled_expedited() {
		return scheduled_expedited;
	}

	public void setScheduled_expedited(Integer scheduled_expedited) {
		this.scheduled_expedited = scheduled_expedited;
	}

	public Integer getRework() {
		return rework;
	}

	public void setRework(Integer rework) {
		this.rework = rework;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public Date getFinish_time_start() {
		return finish_time_start;
	}

	public void setFinish_time_start(Date finish_time_start) {
		this.finish_time_start = finish_time_start;
	}

	public Date getFinish_time_end() {
		return finish_time_end;
	}

	public void setFinish_time_end(Date finish_time_end) {
		this.finish_time_end = finish_time_end;
	}

	public Integer getAvgWorkTime() {
		return avgWorkTime;
	}

	public void setAvgWorkTime(Integer avgWorkTime) {
		this.avgWorkTime = avgWorkTime;
	}

	public String getYearMonth() {
		return yearMonth;
	}

	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getSvg() {
		return svg;
	}

	public void setSvg(String svg) {
		this.svg = svg;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

	public String getSection_name() {
		return section_name;
	}

	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}

	public Integer getAbnormal() {
		return abnormal;
	}

	public void setAbnormal(Integer abnormal) {
		this.abnormal = abnormal;
	}

	public Integer getStandardWorkTime() {
		return standardWorkTime;
	}

	public void setStandardWorkTime(Integer standardWorkTime) {
		this.standardWorkTime = standardWorkTime;
	}

	public Integer getUpper() {
		return upper;
	}

	public void setUpper(Integer upper) {
		this.upper = upper;
	}

	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}

	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}

	public String getSerial_no() {
		return serial_no;
	}

	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}

	public Date getFinish_time() {
		return finish_time;
	}

	public void setFinish_time(Date finish_time) {
		this.finish_time = finish_time;
	}

	public Integer getUse_seconds() {
		return use_seconds;
	}

	public void setUse_seconds(Integer use_seconds) {
		this.use_seconds = use_seconds;
	}

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public Date getAction_time() {
		return action_time;
	}

	public void setAction_time(Date action_time) {
		this.action_time = action_time;
	}

}
