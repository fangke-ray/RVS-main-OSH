package com.osh.rvs.form.inline;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class SnoutForm extends ActionForm implements Serializable {

	private static final long serialVersionUID = 5214594711555127313L;

	private String model_name;

	@BeanField(title = "先端头型号", name = "model_id", type = FieldType.String, length = 11)
	private String model_id;

	@BeanField(title = "先端头序列号", name = "serial_no", type = FieldType.String, length = 8)
	private String serial_no;

	@BeanField(title = "完成时间", name = "finish_time", type = FieldType.DateTime)
	private String finish_time;

	private String operator_name;

	@BeanField(title = "作业者", name = "operator_id", type = FieldType.String, length = 11)
	private String operator_id;

	@BeanField(title = "检测时间", name = "confirm_time", type = FieldType.Date)
	private String confirm_time;

	private String confirmer_name;

	private String sorc_no;

	@BeanField(title = "状态", name = "status", type = FieldType.Integer, length = 1)
	private String status;

	@BeanField(title = "完成时间区间开始", name = "finish_time_from", type = FieldType.Date)
	private String finish_time_from;

	@BeanField(title = "完成时间区间终了", name = "finish_time_to", type = FieldType.Date)
	private String finish_time_to;

	@BeanField(title = "先端头来源修理单号", name = "origin_omr_notifi_no")
	private String origin_omr_notifi_no;

	@BeanField(title = "先端头来源机身号", name = "new_serial_no")
	private String origin_serial_no;

	@BeanField(title = "作业工位", name = "position_id")
	private String position_id;

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public String getSerial_no() {
		return serial_no;
	}

	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}

	public String getFinish_time() {
		return finish_time;
	}

	public void setFinish_time(String finish_time) {
		this.finish_time = finish_time;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getConfirm_time() {
		return confirm_time;
	}

	public void setConfirm_time(String confirm_time) {
		this.confirm_time = confirm_time;
	}

	public String getConfirmer_name() {
		return confirmer_name;
	}

	public void setConfirmer_name(String confirmer_name) {
		this.confirmer_name = confirmer_name;
	}

	public String getSorc_no() {
		return sorc_no;
	}

	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFinish_time_from() {
		return finish_time_from;
	}

	public void setFinish_time_from(String finish_time_from) {
		this.finish_time_from = finish_time_from;
	}

	public String getFinish_time_to() {
		return finish_time_to;
	}

	public void setFinish_time_to(String finish_time_end) {
		this.finish_time_to = finish_time_end;
	}

	public String getOrigin_omr_notifi_no() {
		return origin_omr_notifi_no;
	}

	public void setOrigin_omr_notifi_no(String origin_omr_notifi_no) {
		this.origin_omr_notifi_no = origin_omr_notifi_no;
	}

	public String getOrigin_serial_no() {
		return origin_serial_no;
	}

	public void setOrigin_serial_no(String origin_serial_no) {
		this.origin_serial_no = origin_serial_no;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}
}
