package com.osh.rvs.form.infect;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class PeripheralInfectDeviceForm extends ActionForm implements Serializable {

	/**
	 * 周边设备点检关系
	 */
	private static final long serialVersionUID = 9171598652124262473L;

	@BeanField(title="周边设备型号 ID",name="model_id",type=FieldType.String)
	private String model_id;
	@BeanField(title="周边设备型号",name="model_type_name",type=FieldType.String)
	private String model_type_name;
	@BeanField(title="周边设备机种",name="category_name",type=FieldType.String)
	private String category_name;
	@BeanField(title="序号",name="seq",type=FieldType.Integer)
	private String seq;
	@BeanField(title="设备类别 ID",name="device_type_id",type=FieldType.String)
	private String device_type_id;
	@BeanField(title="设备类别",name="device_type_name",type=FieldType.String)
	private String device_type_name;
	@BeanField(title="型号",name="model_name",type=FieldType.String)
	private String model_name;
	@BeanField(title="更新人",name="updated_by",type=FieldType.String)
	private String updated_by;
	@BeanField(title="更新时间",name="updated_time",type=FieldType.TimeStamp)
	private String updated_time;

	/**
	 * @return the model_id
	 */
	public String getModel_id() {
		return model_id;
	}
	/**
	 * @param model_id the model_id to set
	 */
	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}
	/**
	 * @return the model_type_name
	 */
	public String getModel_type_name() {
		return model_type_name;
	}
	/**
	 * @param model_type_name the model_type_name to set
	 */
	public void setModel_type_name(String model_type_name) {
		this.model_type_name = model_type_name;
	}
	/**
	 * @return the seq
	 */
	public String getSeq() {
		return seq;
	}
	/**
	 * @param seq the seq to set
	 */
	public void setSeq(String seq) {
		this.seq = seq;
	}
	/**
	 * @return the device_type_id
	 */
	public String getDevice_type_id() {
		return device_type_id;
	}
	/**
	 * @param device_type_id the device_type_id to set
	 */
	public void setDevice_type_id(String device_type_id) {
		this.device_type_id = device_type_id;
	}
	/**
	 * @return the device_type_name
	 */
	public String getDevice_type_name() {
		return device_type_name;
	}
	/**
	 * @param device_type_name the device_type_name to set
	 */
	public void setDevice_type_name(String device_type_name) {
		this.device_type_name = device_type_name;
	}
	/**
	 * @return the model_name
	 */
	public String getModel_name() {
		return model_name;
	}
	/**
	 * @param model_name the model_name to set
	 */
	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}
	/**
	 * @return the updated_by
	 */
	public String getUpdated_by() {
		return updated_by;
	}
	/**
	 * @param updated_by the updated_by to set
	 */
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}
	/**
	 * @return the updated_time
	 */
	public String getUpdated_time() {
		return updated_time;
	}
	/**
	 * @param updated_time the updated_time to set
	 */
	public void setUpdated_time(String updated_time) {
		this.updated_time = updated_time;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
}
