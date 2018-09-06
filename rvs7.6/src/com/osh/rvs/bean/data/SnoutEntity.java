package com.osh.rvs.bean.data;

import java.io.Serializable;
import java.util.Date;

public class SnoutEntity implements Serializable {

	private static final long serialVersionUID = 7465275087987481598L;

	private String model_name;

	private String model_id;

	private String serial_no;

	private Date finish_time;

	private String operator_name;

	private String operator_id;

	private Date confirm_time;

	private String confirmer_name;

	private String sorc_no;

	private Integer status;

	private Date finish_time_from;

	private Date finish_time_to;

	private String new_model_id;

	private String new_serial_no;

	private String origin_omr_notifi_no;

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

	public Date getFinish_time() {
		return finish_time;
	}

	public void setFinish_time(Date finish_time) {
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

	public Date getConfirm_time() {
		return confirm_time;
	}

	public void setConfirm_time(Date confirm_time) {
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getFinish_time_from() {
		return finish_time_from;
	}

	public void setFinish_time_from(Date finish_time_start) {
		this.finish_time_from = finish_time_start;
	}

	public Date getFinish_time_to() {
		return finish_time_to;
	}

	public void setFinish_time_to(Date finish_time_end) {
		this.finish_time_to = finish_time_end;
	}

	public String getNew_model_id() {
		return new_model_id;
	}

	public void setNew_model_id(String new_model_id) {
		this.new_model_id = new_model_id;
	}

	public String getNew_serial_no() {
		return new_serial_no;
	}

	public void setNew_serial_no(String new_serial_no) {
		this.new_serial_no = new_serial_no;
	}

	public String getOrigin_omr_notifi_no() {
		return origin_omr_notifi_no;
	}

	public void setOrigin_omr_notifi_no(String origin_omr_notifi_no) {
		this.origin_omr_notifi_no = origin_omr_notifi_no;
	}

}
