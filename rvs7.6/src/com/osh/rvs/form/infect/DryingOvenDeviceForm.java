package com.osh.rvs.form.infect;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 
 * @Title DryingOvenDeviceForm.java
 * @Project rvs
 * @Package com.osh.rvs.form.infect
 * @ClassName: DryingOvenDeviceForm
 * @Description: 烘箱管理
 * @author lxb
 * @date 2016-8-2 下午4:11:00
 */
public class DryingOvenDeviceForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8411928183590570184L;

	@BeanField(title = " 管理编号", name = "device_manage_id", type = FieldType.String, length = 11, notNull = true)
	private String device_manage_id;

	@BeanField(title = " 设定温度", name = "setting_temperature", type = FieldType.Integer, length = 3, notNull = true)
	private String setting_temperature;

	@BeanField(title = " 库位数", name = "slot", type = FieldType.Integer, length = 2, notNull = true)
	private String slot;

	@BeanField(title = " 管理编号", name = "manage_code", type = FieldType.String, length = 9)
	private String manage_code;

	@BeanField(title = " 型号名称 ", name = "model_name", type = FieldType.String, length = 32)
	private String model_name;

	/** 实测温度下线 **/
	private String lower_limit;

	/** 实测温度上线 **/
	private String upper_limit;

	public String getDevice_manage_id() {
		return device_manage_id;
	}

	public void setDevice_manage_id(String device_manage_id) {
		this.device_manage_id = device_manage_id;
	}

	public String getSetting_temperature() {
		return setting_temperature;
	}

	public void setSetting_temperature(String setting_temperature) {
		this.setting_temperature = setting_temperature;
	}

	public String getSlot() {
		return slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}

	public String getLower_limit() {
		return lower_limit;
	}

	public void setLower_limit(String lower_limit) {
		this.lower_limit = lower_limit;
	}

	public String getUpper_limit() {
		return upper_limit;
	}

	public void setUpper_limit(String upper_limit) {
		this.upper_limit = upper_limit;
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

}
