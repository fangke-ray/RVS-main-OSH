package com.osh.rvs.form.master;

import java.io.Serializable;

import com.osh.rvs.form.UploadForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class DevicesTypeForm extends UploadForm implements Serializable {
	/**
	 * 设备工具品名
	 */
	private static final long serialVersionUID = 7108345429538935178L;

	/* 设备工具品名ID */
	@BeanField(title = "设备工具品名ID", name = "devices_type_id", type = FieldType.String, primaryKey = true, length = 11)
	private String devices_type_id;

	/* 品名 */
	@BeanField(title = "品名", name = "name", length = 32, notNull = true)
	private String name;

	/* 删除标记 */
	@BeanField(title = "删除标记", name = "delete_flg", type = FieldType.Integer, length = 1)
	private String delete_flg;

	/* 最后更新人 */
	@BeanField(title = "最后更新人", name = "updated_by")
	private String updated_by;

	/* 最后更新时间 */
	@BeanField(title = "最后更新时间", name = "updated_time", type = FieldType.TimeStamp)
	private String updated_time;

	@BeanField(title = "特定设备工具种类", name = "specialized", type = FieldType.Integer, length = 1)
	private String specialized;
	
	private String safety_guide;

	public String getDevices_type_id() {
		return devices_type_id;
	}

	public void setDevices_type_id(String devices_type_id) {
		this.devices_type_id = devices_type_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getSpecialized() {
		return specialized;
	}

	public void setSpecialized(String specialized) {
		this.specialized = specialized;
	}

	public String getSafety_guide() {
		return safety_guide;
	}

	public void setSafety_guide(String safety_guide) {
		this.safety_guide = safety_guide;
	}

}
