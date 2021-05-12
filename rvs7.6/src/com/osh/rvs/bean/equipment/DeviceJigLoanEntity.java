package com.osh.rvs.bean.equipment;

import java.io.Serializable;
import java.util.Date;

/**
 * 设备工具借调
 * 
 * @author gonglm
 * 
 */
public class DeviceJigLoanEntity implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8060318038968528356L;

	private String manage_id;

	private Integer reason;

	private String device_jig_loan_key;

	private Integer object_type;

	private Date on_loan_time;

	private Date revent_time;

	private String manage_code;

	private String type_name;

	private String model_name;

	private String position_id;

	private String process_code;

	private String material_id;

	private String omr_notifi_no;

	private String operator_id;

	private String operator_name;

	private Integer rework;

	public String getManage_id() {
		return manage_id;
	}

	public void setManage_id(String manage_id) {
		this.manage_id = manage_id;
	}

	public String getDevice_jig_loan_key() {
		return device_jig_loan_key;
	}

	public void setDevice_jig_loan_key(String device_jig_loan_key) {
		this.device_jig_loan_key = device_jig_loan_key;
	}

	public Integer getObject_type() {
		return object_type;
	}

	public void setObject_type(Integer object_type) {
		this.object_type = object_type;
	}

	public Date getOn_loan_time() {
		return on_loan_time;
	}

	public void setOn_loan_time(Date on_loan_time) {
		this.on_loan_time = on_loan_time;
	}

	public String getManage_code() {
		return manage_code;
	}

	public void setManage_code(String manage_code) {
		this.manage_code = manage_code;
	}

	public String getType_name() {
		return type_name;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
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

	public Integer getReason() {
		return reason;
	}

	public void setReason(Integer reason) {
		this.reason = reason;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public Date getRevent_time() {
		return revent_time;
	}

	public void setRevent_time(Date revent_time) {
		this.revent_time = revent_time;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

	public Integer getRework() {
		return rework;
	}

	public void setRework(Integer rework) {
		this.rework = rework;
	}

	
}
