package com.osh.rvs.form.infect;

import java.io.Serializable;

import com.osh.rvs.form.UploadForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class CheckResultFilingForm extends UploadForm implements Serializable{

	/**
	 * 点检结果归档
	 */
	private static final long serialVersionUID = -4347567998236559438L;

	@BeanField(title = "文件从属", name = "branch", type = FieldType.Integer,length = 1, notNull = true)
	private String branch;//文件从属
	
	@BeanField(title = "点检表管理ID", name = "check_file_manage_id", type = FieldType.String, length = 11, primaryKey = true, notNull = true)
	private String check_file_manage_id;// 点检表管理ID

	@BeanField(title = "点检表管理编号", name = "check_manage_code", type = FieldType.String, length = 16, notNull = true)
	private String check_manage_code;// 点检表管理编号

	@BeanField(title = "点检表文件", name = "sheet_file_name", type = FieldType.String, length = 100)
	private String sheet_file_name;// 点检表文件

	@BeanField(title = "设备工具品名ID", name = "devices_type_id", length = 11, notNull = true)
	private String devices_type_id;// 设备工具品名ID

	@BeanField(title = "类型", name = "access_place", type = FieldType.Integer, length = 1, notNull = true)
	private String access_place;// 类型

	@BeanField(title = "归档周期", name = "cycle_type", type = FieldType.Integer, length = 1, notNull = true)
	private String cycle_type;// 归档周期

	@BeanField(title = "删除标记", name = "delete_flg", type = FieldType.Integer, length = 1, notNull = true)
	private String delete_flg;// 删除标记

	@BeanField(title = "最后更新人ID", name = "updated_by", type = FieldType.String, length = 11, notNull = true)
	private String updated_by;// 最后更新人ID

	@BeanField(title = "最后更新时间", name = "updated_time", type = FieldType.DateTime, notNull = true)
	private String updated_time;// 最后更新时间

	@BeanField(title = "使用设备工具品名", name = "name", type = FieldType.String)
	private String name;// 使用设备工具品名

	@BeanField(title = "最后更新人名称", name = "update_name", type = FieldType.String)
	private String update_name;// 最后更新人名称

	@BeanField(title = "点检表归档日期", name = "filing_date", type = FieldType.Date, notNull = true)
	private String filing_date;
	
	@BeanField(title = "对象管理ID", name = "devices_manage_id", type = FieldType.String, length = 11, notNull = true)
	private String devices_manage_id;
	
	@BeanField(title = "点检表初始时间", name = "start_record_date", type = FieldType.Date, notNull = true)
	private String start_record_date;
	
	@BeanField(title = "保存于文件", name = "storage_file_name", type = FieldType.String, length = 120, notNull = true)
	private String storage_file_name;

	@BeanField(title = "SORC财年", name = "work_period", type = FieldType.String)
	private String work_period;

	@BeanField(title = "归档日期开始", name = "filing_date_start", type = FieldType.Date, notNull = true)
	private String filing_date_start;

	@BeanField(title = "归档日期结束", name = "filing_date_end", type = FieldType.Date, notNull = true)
	private String filing_date_end;

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getWork_period() {
		return work_period;
	}

	public void setWork_period(String work_period) {
		this.work_period = work_period;
	}

	public String getFiling_date() {
		return filing_date;
	}

	public void setFiling_date(String filing_date) {
		this.filing_date = filing_date;
	}

	public String getDevices_manage_id() {
		return devices_manage_id;
	}

	public void setDevices_manage_id(String devices_manage_id) {
		this.devices_manage_id = devices_manage_id;
	}

	public String getStart_record_date() {
		return start_record_date;
	}

	public void setStart_record_date(String start_record_date) {
		this.start_record_date = start_record_date;
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

	public String getAccess_place() {
		return access_place;
	}

	public void setAccess_place(String access_place) {
		this.access_place = access_place;
	}

	public String getCycle_type() {
		return cycle_type;
	}

	public void setCycle_type(String cycle_type) {
		this.cycle_type = cycle_type;
	}

	public String getDelete_flg() {
		return delete_flg;
	}

	public void setDelete_flg(String delete_flg) {
		this.delete_flg = delete_flg;
	}

	public String getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}

	public String getUpdated_time() {
		return updated_time;
	}

	public void setUpdated_time(String updated_time) {
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

	public String getFiling_date_start() {
		return filing_date_start;
	}

	public void setFiling_date_start(String filing_date_start) {
		this.filing_date_start = filing_date_start;
	}

	public String getFiling_date_end() {
		return filing_date_end;
	}

	public void setFiling_date_end(String filing_date_end) {
		this.filing_date_end = filing_date_end;
	}
}
