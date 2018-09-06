package com.osh.rvs.bean.report;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @Title RemainTimeReportEntity.java
 * @Project rvs
 * @Package com.osh.rvs.bean.report
 * @ClassName: RemainTimeReportEntity
 * @Description: 倒计时达成率
 * @author houp
 * @date 2017-02-22 下午1:56:26
 */
public class RemainTimeReportEntity implements Serializable {

	private static final long serialVersionUID = -1411522207849281228L;

	/** 作业开始时间 **/
	private String start_date;

	/** 作业结束时间 **/
	private String end_date;

	private String material_id;

	private String omr_notifi_no;

	private String line_id;

	private String line_name;

	private Integer level;

	private String model_name;

	private String category_name;

	private Date action_time;

	private Date finish_time;

	private Date expected_finish_time;

	private Integer factwork_minutes;

	private Integer expected_minutes;

	private Integer bo_flg;

	private Integer rework;

	private String finish_month;

	private BigDecimal finish_rate;

	private Integer finish_cnt;

	private Integer reached_cnt;

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}

	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public Date getAction_time() {
		return action_time;
	}

	public void setAction_time(Date action_time) {
		this.action_time = action_time;
	}

	public Date getFinish_time() {
		return finish_time;
	}

	public void setFinish_time(Date finish_time) {
		this.finish_time = finish_time;
	}

	public Date getExpected_finish_time() {
		return expected_finish_time;
	}

	public void setExpected_finish_time(Date expected_finish_time) {
		this.expected_finish_time = expected_finish_time;
	}

	public Integer getFactwork_minutes() {
		return factwork_minutes;
	}

	public void setFactwork_minutes(Integer factwork_minutes) {
		this.factwork_minutes = factwork_minutes;
	}

	public Integer getExpected_minutes() {
		return expected_minutes;
	}

	public void setExpected_minutes(Integer expected_minutes) {
		this.expected_minutes = expected_minutes;
	}

	public Integer getBo_flg() {
		return bo_flg;
	}

	public void setBo_flg(Integer bo_flg) {
		this.bo_flg = bo_flg;
	}

	public Integer getRework() {
		return rework;
	}

	public void setRework(Integer rework) {
		this.rework = rework;
	}

	public String getFinish_month() {
		return finish_month;
	}

	public void setFinish_month(String finish_month) {
		this.finish_month = finish_month;
	}

	public BigDecimal getFinish_rate() {
		return finish_rate;
	}

	public void setFinish_rate(BigDecimal finish_rate) {
		this.finish_rate = finish_rate;
	}

	public Integer getFinish_cnt() {
		return finish_cnt;
	}

	public void setFinish_cnt(Integer finish_cnt) {
		this.finish_cnt = finish_cnt;
	}

	public Integer getReached_cnt() {
		return reached_cnt;
	}

	public void setReached_cnt(Integer reached_cnt) {
		this.reached_cnt = reached_cnt;
	}
}
