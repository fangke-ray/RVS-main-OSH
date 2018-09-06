package com.osh.rvs.form.report;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class RemainTimeReportForm extends ActionForm implements Serializable {

	private static final long serialVersionUID = -2068108313908645382L;

	@BeanField(title = "作业开始时间", name = "start_date", type = FieldType.String, notNull = true)
	private String start_date;

	@BeanField(title = "作业结束时间", name = "end_date", type = FieldType.String, notNull = true)
	private String end_date;

	private String svg;

	private String position_id;
	private String omr_notifi_no;

	@BeanField(title = "判定时间", name = "contrast_time", type = FieldType.DateTime)
	private String contrast_time;
	@BeanField(title = "判定时间", name = "contrast_time_start", type = FieldType.Date)
	private String contrast_time_start;
	@BeanField(title = "判定时间", name = "contrast_time_end", type = FieldType.Date)
	private String contrast_time_end;

	@BeanField(title = "进度代码", name = "process_code", type = FieldType.String, length = 3)
	private String process_code;

	@BeanField(title = "判定理由", name = "main_cause", type = FieldType.Integer)
	private String main_cause;

	// 管理人员意见
	private String comment;
	private String operator_name;
	private String model_name;

	private String countdown_key;

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

	public String getSvg() {
		return svg;
	}

	public void setSvg(String svg) {
		this.svg = svg;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}

	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}

	public String getContrast_time() {
		return contrast_time;
	}

	public void setContrast_time(String contrast_time) {
		this.contrast_time = contrast_time;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getContrast_time_start() {
		return contrast_time_start;
	}

	public void setContrast_time_start(String contrast_time_start) {
		this.contrast_time_start = contrast_time_start;
	}

	public String getContrast_time_end() {
		return contrast_time_end;
	}

	public void setContrast_time_end(String contrast_time_end) {
		this.contrast_time_end = contrast_time_end;
	}

	public String getMain_cause() {
		return main_cause;
	}

	public void setMain_cause(String main_cause) {
		this.main_cause = main_cause;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

	public String getCountdown_key() {
		return countdown_key;
	}

	public void setCountdown_key(String countdown_key) {
		this.countdown_key = countdown_key;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}
}
