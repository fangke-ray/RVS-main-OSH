package com.osh.rvs.bean.infect;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CheckResultEntity implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8162381798878300528L;
	// 管理ID
	private String manage_id;
	// 点检表管理ID
	private String check_file_manage_id;
	// 项目序号
	private String item_seq;
	// 点检人员
	private String operator_id;
	private String job_no;
	private String operator_name;
	private String manager_operator_id;             
	// 点检时间
	private Date check_confirm_time;
	private Date check_confirm_time_start;
	private Date check_confirm_time_end;
	// 数值
	private BigDecimal digit;
	// 结果
	private Integer checked_status;
	// 当时所属课室	
	private String section_id;
	// 当时所属工位	
	private String position_id;

	private String line_id;

	private String sheet_manage_no;
	private String sheet_file_name;
	private String manage_code;
	private String model_name;
	private String devices_type_id;
	private String comment;

	private Integer cycle_type;
	private String object_type;
	// 特定设备工具种类
	private Integer specialized;

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
	public Date getCheck_confirm_time() {
		return check_confirm_time;
	}
	public void setCheck_confirm_time(Date check_confirm_time) {
		this.check_confirm_time = check_confirm_time;
	}
	public BigDecimal getDigit() {
		return digit;
	}
	public void setDigit(BigDecimal digit) {
		this.digit = digit;
	}
	public Integer getChecked_status() {
		return checked_status;
	}
	public void setChecked_status(Integer checked_status) {
		this.checked_status = checked_status;
	}
	public String getJob_no() {
		return job_no;
	}
	public void setJob_no(String job_no) {
		this.job_no = job_no;
	}
	public Date getCheck_confirm_time_start() {
		return check_confirm_time_start;
	}
	public void setCheck_confirm_time_start(Date check_confirm_time_start) {
		this.check_confirm_time_start = check_confirm_time_start;
	}
	public Date getCheck_confirm_time_end() {
		return check_confirm_time_end;
	}
	public void setCheck_confirm_time_end(Date check_confirm_time_end) {
		this.check_confirm_time_end = check_confirm_time_end;
	}
	public String getSection_id() {
		return section_id;
	}
	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}
	public String getPosition_id() {
		return position_id;
	}
	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}
	public String getManager_operator_id() {
		return manager_operator_id;
	}
	public void setManager_operator_id(String manager_operator_id) {
		this.manager_operator_id = manager_operator_id;
	}
	public String getLine_id() {
		return line_id;
	}
	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}
	public String getSheet_manage_no() {
		return sheet_manage_no;
	}
	public void setSheet_manage_no(String sheet_manage_no) {
		this.sheet_manage_no = sheet_manage_no;
	}
	public String getManage_code() {
		return manage_code;
	}
	public void setManage_code(String manage_code) {
		this.manage_code = manage_code;
	}
	public String getModel_name() {
		return model_name;
	}
	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}
	public String getDevices_type_id() {
		return devices_type_id;
	}
	public void setDevices_type_id(String devices_type_id) {
		this.devices_type_id = devices_type_id;
	}
	public String getSheet_file_name() {
		return sheet_file_name;
	}
	public void setSheet_file_name(String sheet_file_name) {
		this.sheet_file_name = sheet_file_name;
	}
	public Integer getCycle_type() {
		return cycle_type;
	}
	public void setCycle_type(Integer cycle_type) {
		this.cycle_type = cycle_type;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getObject_type() {
		return object_type;
	}
	public void setObject_type(String object_type) {
		this.object_type = object_type;
	}
	public Integer getSpecialized() {
		return specialized;
	}
	public void setSpecialized(Integer specialized) {
		this.specialized = specialized;
	}

}
