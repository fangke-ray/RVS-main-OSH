package com.osh.rvs.bean.infect;

import java.io.Serializable;
import java.util.Date;

public class CheckResultFilingEntity implements Serializable {

	/**
	 * 点检结果归档
	 */
	private static final long serialVersionUID = 7814306723856702990L;
	
	private Integer branch;//文件从属
	
	private String check_file_manage_id;// 点检表管理ID
	private String check_manage_code;// 点检表管理编号
	private String sheet_file_name;// 点检表文件
	private String devices_type_id;// 设备工具品名ID
	private Integer access_place;// 类型
	private Integer cycle_type;// 归档周期
	private Integer delete_flg;// 删除标记
	private String updated_by;// 最后更新人ID
	private Date updated_time;// 最后更新时间

	private String name;// 使用设备工具品名；
	private String update_name;// 最后更新人名称
	
	private Date filing_date;
	private String devices_manage_id;
	private Date start_record_date;
	private String storage_file_name;
	private Date filing_date_start;
	private Date filing_date_end;
	
	//SORC财年
	private String work_period;
	
	public Integer getBranch() {
		return branch;
	}
	public void setBranch(Integer branch) {
		this.branch = branch;
	}
	public String getWork_period() {
		return work_period;
	}
	public void setWork_period(String work_period) {
		this.work_period = work_period;
	}
	public Date getFiling_date() {
		return filing_date;
	}
	public void setFiling_date(Date filing_date) {
		this.filing_date = filing_date;
	}
	public Date getStart_record_date() {
		return start_record_date;
	}
	public void setStart_record_date(Date start_record_date) {
		this.start_record_date = start_record_date;
	}
	public String getDevices_manage_id() {
		return devices_manage_id;
	}
	public void setDevices_manage_id(String devices_manage_id) {
		this.devices_manage_id = devices_manage_id;
	}
	public String getStorage_file_name() {
		return storage_file_name;
	}
	public void setStorage_file_name(String storage_file_name) {
		this.storage_file_name = storage_file_name;
	}
	public String getCheck_file_manage_id() {
		return check_file_manage_id;
	}
	public void setCheck_file_manage_id(String check_file_manage_id) {
		this.check_file_manage_id = check_file_manage_id;
	}
	public String getCheck_manage_code() {
		return check_manage_code;
	}
	public void setCheck_manage_code(String check_manage_code) {
		this.check_manage_code = check_manage_code;
	}
	public String getSheet_file_name() {
		return sheet_file_name;
	}
	public void setSheet_file_name(String sheet_file_name) {
		this.sheet_file_name = sheet_file_name;
	}
	public String getDevices_type_id() {
		return devices_type_id;
	}
	public void setDevices_type_id(String devices_type_id) {
		this.devices_type_id = devices_type_id;
	}
	public Integer getAccess_place() {
		return access_place;
	}
	public void setAccess_place(Integer access_place) {
		this.access_place = access_place;
	}
	public Integer getCycle_type() {
		return cycle_type;
	}
	public void setCycle_type(Integer cycle_type) {
		this.cycle_type = cycle_type;
	}
	public Integer getDelete_flg() {
		return delete_flg;
	}
	public void setDelete_flg(Integer delete_flg) {
		this.delete_flg = delete_flg;
	}
	public String getUpdated_by() {
		return updated_by;
	}
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}
	public Date getUpdated_time() {
		return updated_time;
	}
	public void setUpdated_time(Date updated_time) {
		this.updated_time = updated_time;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUpdate_name() {
		return update_name;
	}
	public void setUpdate_name(String update_name) {
		this.update_name = update_name;
	}
	public Date getFiling_date_start() {
		return filing_date_start;
	}
	public void setFiling_date_start(Date filing_date_start) {
		this.filing_date_start = filing_date_start;
	}
	public Date getFiling_date_end() {
		return filing_date_end;
	}
	public void setFiling_date_end(Date filing_date_end) {
		this.filing_date_end = filing_date_end;
	}	
}
