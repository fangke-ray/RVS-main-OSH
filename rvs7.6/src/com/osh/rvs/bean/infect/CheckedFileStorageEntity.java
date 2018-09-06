package com.osh.rvs.bean.infect;

import java.io.Serializable;
import java.util.Date;

public class CheckedFileStorageEntity implements Serializable{

	private static final long serialVersionUID = -2407731915589455731L;
	private String check_file_manage_id;
	private Date filing_date;
	private String devices_manage_id;
	private Date start_record_date;
	private String storage_file_name;
	private String template_file_name;

	private String section_id;
	private String line_id;
	private String position_id;

	public String getCheck_file_manage_id() {
		return check_file_manage_id;
	}
	public void setCheck_file_manage_id(String check_file_manage_id) {
		this.check_file_manage_id = check_file_manage_id;
	}
	public Date getFiling_date() {
		return filing_date;
	}
	public void setFiling_date(Date filing_date) {
		this.filing_date = filing_date;
	}
	public String getDevices_manage_id() {
		return devices_manage_id;
	}
	public void setDevices_manage_id(String devices_manage_id) {
		this.devices_manage_id = devices_manage_id;
	}
	public Date getStart_record_date() {
		return start_record_date;
	}
	public void setStart_record_date(Date start_record_date) {
		this.start_record_date = start_record_date;
	}
	public String getStorage_file_name() {
		return storage_file_name;
	}
	public void setStorage_file_name(String storage_file_name) {
		this.storage_file_name = storage_file_name;
	}
	public String getSection_id() {
		return section_id;
	}
	public void setSection_id(String section_id) {
		this.section_id = section_id;
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
	public String getTemplate_file_name() {
		return template_file_name;
	}
	public void setTemplate_file_name(String template_file_name) {
		this.template_file_name = template_file_name;
	}
}
