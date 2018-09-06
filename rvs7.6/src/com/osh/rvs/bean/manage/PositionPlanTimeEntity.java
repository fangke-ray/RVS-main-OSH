package com.osh.rvs.bean.manage;

import java.io.Serializable;
import java.util.Date;

public class PositionPlanTimeEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6697231484927085984L;

	private String material_id;
	private String line_id;
	private String position_id;
	private Date plan_start_time;
	private Date plan_end_time;
	private Integer seq;
	private String process_code;

	private String omr_notifi_no;
	private String line_name;
	private String operator_id;
	private String operator_name;
	// 判定主要原因
	private Integer main_cause;
	// 对照时间
	private Date contrast_time;
	private Date contrast_time_start;
	private Date contrast_time_end;
	// 管理人员意见
	private String comment;
	private String model_name;
	
	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public Date getPlan_start_time() {
		return plan_start_time;
	}

	public void setPlan_start_time(Date plan_start_time) {
		this.plan_start_time = plan_start_time;
	}

	public Date getPlan_end_time() {
		return plan_end_time;
	}

	public void setPlan_end_time(Date plan_end_time) {
		this.plan_end_time = plan_end_time;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}

	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

	public Integer getMain_cause() {
		return main_cause;
	}

	public void setMain_cause(Integer main_cause) {
		this.main_cause = main_cause;
	}

	public Date getContrast_time() {
		return contrast_time;
	}

	public void setContrast_time(Date contrast_time) {
		this.contrast_time = contrast_time;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public Date getContrast_time_start() {
		return contrast_time_start;
	}

	public void setContrast_time_start(Date contrast_time_start) {
		this.contrast_time_start = contrast_time_start;
	}

	public Date getContrast_time_end() {
		return contrast_time_end;
	}

	public void setContrast_time_end(Date contrast_time_end) {
		this.contrast_time_end = contrast_time_end;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

}
