package com.osh.rvs.bean.manage;

import java.io.Serializable;
import java.util.Date;


public class NewPhenomenonEntity implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 5200256589911210021L;

	private String omr_notifi_no; // SAPRepairNotificationNo
	private String material_id;
	private String key; // RVSDetailNo
	private String location_group_desc;
	private String location_desc;
	private String description;
	private Date last_determine_date;
	private String return_status; // DetermineTime
	private String last_sent_message_number;

	private String job_no; // DeterminePerson
	private String operator_id;
	private String determine_operator_id;
	private Integer kind;
	private String category_name;
	private String serial_no;
	private Date occur_time;
	private String line_id;
	private String line_name;
	private String operator_name;
	private String determine_operator_name;
	private Date occur_time_start;
	private Date occur_time_end;
	private Date last_determine_date_start;
	private Date last_determine_date_end;

	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}
	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}
	public String getMaterial_id() {
		return material_id;
	}
	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getLocation_group_desc() {
		return location_group_desc;
	}
	public void setLocation_group_desc(String location_group_desc) {
		this.location_group_desc = location_group_desc;
	}
	public String getLocation_desc() {
		return location_desc;
	}
	public void setLocation_desc(String location_desc) {
		this.location_desc = location_desc;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getJob_no() {
		return job_no;
	}
	public void setJob_no(String job_no) {
		this.job_no = job_no;
	}
	public String getOperator_id() {
		return operator_id;
	}
	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}
	public String getReturn_status() {
		return return_status;
	}
	public void setReturn_status(String return_status) {
		this.return_status = return_status;
	}
	public String getLast_sent_message_number() {
		return last_sent_message_number;
	}
	public void setLast_sent_message_number(String last_sent_message_number) {
		this.last_sent_message_number = last_sent_message_number;
	}
	public Date getLast_determine_date() {
		return last_determine_date;
	}
	public void setLast_determine_date(Date last_determine_date) {
		this.last_determine_date = last_determine_date;
	}
	public Integer getKind() {
		return kind;
	}
	public void setKind(Integer kind) {
		this.kind = kind;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	public String getSerial_no() {
		return serial_no;
	}
	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}
	public Date getOccur_time() {
		return occur_time;
	}
	public void setOccur_time(Date occur_time) {
		this.occur_time = occur_time;
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
	public String getOperator_name() {
		return operator_name;
	}
	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}
	public String getDetermine_operator_name() {
		return determine_operator_name;
	}
	public void setDetermine_operator_name(String determine_operator_name) {
		this.determine_operator_name = determine_operator_name;
	}
	public Date getOccur_time_start() {
		return occur_time_start;
	}
	public void setOccur_time_start(Date occur_time_start) {
		this.occur_time_start = occur_time_start;
	}
	public Date getOccur_time_end() {
		return occur_time_end;
	}
	public void setOccur_time_end(Date occur_time_end) {
		this.occur_time_end = occur_time_end;
	}
	public Date getLast_determine_date_start() {
		return last_determine_date_start;
	}
	public void setLast_determine_date_start(Date last_determine_date_start) {
		this.last_determine_date_start = last_determine_date_start;
	}
	public Date getLast_determine_date_end() {
		return last_determine_date_end;
	}
	public void setLast_determine_date_end(Date last_determine_date_end) {
		this.last_determine_date_end = last_determine_date_end;
	}
	public String getDetermine_operator_id() {
		return determine_operator_id;
	}
	public void setDetermine_operator_id(String determine_operator_id) {
		this.determine_operator_id = determine_operator_id;
	}

}
