package com.osh.rvs.form.master;

import java.io.Serializable;

import com.osh.rvs.form.UploadForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.form.master
 * @ClassName: CheckFileManageForm
 * @Description: 点检表管理Form
 * @author lxb
 * @date 2014-8-11 上午11:40:10
 * 
 */
public class CheckFileManageForm extends UploadForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 898555571921816878L;

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

	@BeanField(title = "归档方式", name = "filing_means", type = FieldType.Integer, length = 1, notNull = true)
	private String filing_means;// 归档方式

	@BeanField(title = "特定机型", name = "specified_model_name", type = FieldType.String, length = 100)
	private String specified_model_name;// 特定机型

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

	public String getFiling_means() {
		return filing_means;
	}

	public void setFiling_means(String filing_means) {
		this.filing_means = filing_means;
	}

	public String getSpecified_model_name() {
		return specified_model_name;
	}

	public void setSpecified_model_name(String specified_model_name) {
		this.specified_model_name = specified_model_name;
	}

}
