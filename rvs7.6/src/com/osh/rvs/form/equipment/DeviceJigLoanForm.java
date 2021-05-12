package com.osh.rvs.form.equipment;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 设备工具借调
 * 
 * @author gonglm
 * 
 */
public class DeviceJigLoanForm extends ActionForm implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 614239189128951032L;

	@BeanField(title = "KEY", name = "device_jig_loan_key", type = FieldType.String, primaryKey=true)
	private String device_jig_loan_key;

	@BeanField(title = "对象类别", name = "object_type", type = FieldType.Integer, length = 1)
	private String object_type;

	@BeanField(title = "管理 ID", name = "manage_id", type = FieldType.String)
	private String manage_id;

	@BeanField(title = "品名", name = "type_name", type = FieldType.String)
	private String type_name;

	@BeanField(title = "型号", name = "model_name", type = FieldType.String)
	private String model_name;

	@BeanField(title = "管理编号", name = "manage_code", type = FieldType.String)
	private String manage_code;

	@BeanField(title = "治具号", name = "jig_no", type = FieldType.String)
	private String jig_no;

	@BeanField(title = "工位代码", name = "process_code", type = FieldType.String)
	private String process_code;

	@BeanField(title = "开始借用时间", name = "on_loan_time", type = FieldType.DateTime)
	private String on_loan_time;

	@BeanField(title = "修理单号", name = "omr_notifi_no", type = FieldType.String)
	private String omr_notifi_no;

	@BeanField(title = "点检状态", name = "check_status", type = FieldType.String)
	private String check_status;

	@BeanField(title = "清洗返还时间", name = "revent_time", type = FieldType.DateTime)
	private String revent_time;

	@BeanField(title = "操作者名", name = "operator_name", type = FieldType.String)
	private String operator_name;

	public String getObject_type() {
		return object_type;
	}

	public void setObject_type(String object_type) {
		this.object_type = object_type;
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

	public String getManage_code() {
		return manage_code;
	}

	public void setManage_code(String manage_code) {
		this.manage_code = manage_code;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getOn_loan_time() {
		return on_loan_time;
	}

	public void setOn_loan_time(String on_loan_time) {
		this.on_loan_time = on_loan_time;
	}

	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}

	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}

	public String getCheck_status() {
		return check_status;
	}

	public void setCheck_status(String check_status) {
		this.check_status = check_status;
	}

	public String getDevice_jig_loan_key() {
		return device_jig_loan_key;
	}

	public void setDevice_jig_loan_key(String device_jig_loan_key) {
		this.device_jig_loan_key = device_jig_loan_key;
	}

	public String getManage_id() {
		return manage_id;
	}

	public void setManage_id(String manage_id) {
		this.manage_id = manage_id;
	}

	public String getJig_no() {
		return jig_no;
	}

	public void setJig_no(String jig_no) {
		this.jig_no = jig_no;
	}

	public String getRevent_time() {
		return revent_time;
	}

	public void setRevent_time(String revent_time) {
		this.revent_time = revent_time;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}
	
}
