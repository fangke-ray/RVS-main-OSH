package com.osh.rvs.form.infect;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class UsageCheckForm extends ActionForm implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -8326831454890206894L;

	// 管理ID
	@BeanField(title="管理ID",name="manage_id",type=FieldType.String)
	private String manage_id;
	// 点检表管理ID
	@BeanField(title="点检表管理ID",name="check_file_manage_id",type=FieldType.String)
	private String check_file_manage_id;
	// 项目序号
	@BeanField(title="项目序号",name="item_seq",type=FieldType.String)
	private String item_seq;
	// 点检人员
	@BeanField(title="点检人员",name="operator_id",type=FieldType.String)
	private String operator_id;
	@BeanField(title="点检人员",name="operator_name",type=FieldType.String)
	private String operator_name;
	// 点检时间
	@BeanField(title="点检时间",name="check_confirm_time",type=FieldType.Integer)
	private String check_confirm_time;
	// 数值
	@BeanField(title="数值",name="digit",type=FieldType.Double)
	private String digit;
	// 结果
	@BeanField(title="结果",name="checked_status",type=FieldType.Integer)
	private String checked_status;

	private String object_type;

	private String name;
	private String model_name;
	private String manage_code;
	private String sheet_manage_no;
	private String section_id;
	private String position_id;
	private String process_code;
	private String check_proceed;
	private String devices_type_id;

	@BeanField(title="点检分类",name="cycle_type",type=FieldType.Integer)
	private String cycle_type;
	private String check_result;

	public String getManage_id() {
		return manage_id;
	}
	public void setManage_id(String manage_id) {
		this.manage_id = manage_id;
	}
	public String getCheck_file_manage_id() {
		return check_file_manage_id;
	}
	public void setCheck_file_manage_id(String check_file_manage_id) {
		this.check_file_manage_id = check_file_manage_id;
	}
	public String getItem_seq() {
		return item_seq;
	}
	public void setItem_seq(String item_seq) {
		this.item_seq = item_seq;
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
	public String getCheck_confirm_time() {
		return check_confirm_time;
	}
	public void setCheck_confirm_time(String check_confirm_time) {
		this.check_confirm_time = check_confirm_time;
	}
	public String getDigit() {
		return digit;
	}
	public void setDigit(String digit) {
		this.digit = digit;
	}
	public String getChecked_status() {
		return checked_status;
	}
	public void setChecked_status(String checked_status) {
		this.checked_status = checked_status;
	}
	public String getObject_type() {
		return object_type;
	}
	public void setObject_type(String object_type) {
		this.object_type = object_type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getModel_name() {
		return model_name;
	}
	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}
	public String getSheet_manage_no() {
		return sheet_manage_no;
	}
	public void setSheet_manage_no(String sheet_manage_no) {
		this.sheet_manage_no = sheet_manage_no;
	}
	public String getProcess_code() {
		return process_code;
	}
	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}
	public String getCheck_proceed() {
		return check_proceed;
	}
	public void setCheck_proceed(String check_proceed) {
		this.check_proceed = check_proceed;
	}
	public String getCheck_result() {
		return check_result;
	}
	public void setCheck_result(String check_result) {
		this.check_result = check_result;
	}
	public String getPosition_id() {
		return position_id;
	}
	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}
	public String getCycle_type() {
		return cycle_type;
	}
	public void setCycle_type(String cycle_type) {
		this.cycle_type = cycle_type;
	}
	public String getManage_code() {
		return manage_code;
	}
	public void setManage_code(String manage_code) {
		this.manage_code = manage_code;
	}
	public String getSection_id() {
		return section_id;
	}
	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}
	public String getDevices_type_id() {
		return devices_type_id;
	}
	public void setDevices_type_id(String devices_type_id) {
		this.devices_type_id = devices_type_id;
	}
}
