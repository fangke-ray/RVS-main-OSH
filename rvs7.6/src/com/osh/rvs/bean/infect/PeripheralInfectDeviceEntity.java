package com.osh.rvs.bean.infect;

import java.io.Serializable;
import java.sql.Timestamp;


public class PeripheralInfectDeviceEntity implements Serializable {

	/**
	 * 周边设备点检关系
	 */
	private static final long serialVersionUID = -1542974478096249398L;
	
	private String model_id;
	private String model_type_name;
	private Integer seq;
	private String device_type_id;
	private String device_type_name;
	private String model_name = "";
	private String category_name = "";
	private String updated_by;
	private Timestamp updated_time;
	private String manageCodeOptions;
	private String check_file_manage_id;
	private String check_manage_code;
	private String material_id;
	private String position_id;
	private Integer rework;
	private String device_manage_id;
	private String check_result;
	private Integer group;

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
	public Integer getSeq() {
		return seq;
	}
	/**
	 * @param seq the seq to set
	 */
	public void setSeq(Integer seq) {
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
	public Timestamp getUpdated_time() {
		return updated_time;
	}
	/**
	 * @param updated_time the updated_time to set
	 */
	public void setUpdated_time(Timestamp updated_time) {
		this.updated_time = updated_time;
	}
	/**
	 * @return the manageCodeOptions
	 */
	public String getManageCodeOptions() {
		return manageCodeOptions;
	}
	/**
	 * @param manageCodeOptions the manageCodeOptions to set
	 */
	public void setManageCodeOptions(String manageCodeOptions) {
		this.manageCodeOptions = manageCodeOptions;
	}
	/**
	 * @return the check_file_manage_id
	 */
	public String getCheck_file_manage_id() {
		return check_file_manage_id;
	}
	/**
	 * @param check_file_manage_id the check_file_manage_id to set
	 */
	public void setCheck_file_manage_id(String check_file_manage_id) {
		this.check_file_manage_id = check_file_manage_id;
	}
	/**
	 * @return the check_manage_code
	 */
	public String getCheck_manage_code() {
		return check_manage_code;
	}
	/**
	 * @param check_manage_code the check_manage_code to set
	 */
	public void setCheck_manage_code(String check_manage_code) {
		this.check_manage_code = check_manage_code;
	}
	/**
	 * @return the material_id
	 */
	public String getMaterial_id() {
		return material_id;
	}
	/**
	 * @param material_id the material_id to set
	 */
	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}
	/**
	 * @return the position_id
	 */
	public String getPosition_id() {
		return position_id;
	}
	/**
	 * @param position_id the position_id to set
	 */
	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}
	/**
	 * @return the rework
	 */
	public Integer getRework() {
		return rework;
	}
	/**
	 * @param rework the rework to set
	 */
	public void setRework(Integer rework) {
		this.rework = rework;
	}
	/**
	 * @return the device_manage_id
	 */
	public String getDevice_manage_id() {
		return device_manage_id;
	}
	/**
	 * @param device_manage_id the device_manage_id to set
	 */
	public void setDevice_manage_id(String device_manage_id) {
		this.device_manage_id = device_manage_id;
	}
	/**
	 * @return the check_result
	 */
	public String getCheck_result() {
		return check_result;
	}
	/**
	 * @param check_result the check_result to set
	 */
	public void setCheck_result(String check_result) {
		this.check_result = check_result;
	}
	public Integer getGroup() {
		return group;
	}
	public void setGroup(Integer group) {
		this.group = group;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
}
