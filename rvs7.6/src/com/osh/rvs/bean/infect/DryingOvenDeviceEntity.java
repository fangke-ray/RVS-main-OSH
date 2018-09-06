package com.osh.rvs.bean.infect;

import java.io.Serializable;

public class DryingOvenDeviceEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4079982714288376360L;

	/** 设备管理 ID **/
	private String device_manage_id;

	/** 设定温度 **/
	private Integer setting_temperature;

	/** 库位数 **/
	private Integer slot;

	/** 管理编号 **/
	private String manage_code;

	/** 设备名称 **/
	private String device_name;

	/** 型号名称 **/
	private String model_name;

	/** 工位代码 **/
	private String process_code;

	/** 课室名称 **/
	private String section_name;

	public String getDevice_manage_id() {
		return device_manage_id;
	}

	public void setDevice_manage_id(String device_manage_id) {
		this.device_manage_id = device_manage_id;
	}

	public Integer getSetting_temperature() {
		return setting_temperature;
	}

	public void setSetting_temperature(Integer setting_temperature) {
		this.setting_temperature = setting_temperature;
	}

	public Integer getSlot() {
		return slot;
	}

	public void setSlot(Integer slot) {
		this.slot = slot;
	}

	public String getManage_code() {
		return manage_code;
	}

	public void setManage_code(String manage_code) {
		this.manage_code = manage_code;
	}

	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getSection_name() {
		return section_name;
	}

	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}

}
