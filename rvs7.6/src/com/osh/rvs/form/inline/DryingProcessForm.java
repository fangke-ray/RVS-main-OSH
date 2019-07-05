package com.osh.rvs.form.inline;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 
 * @Title DryingProcessForm.java
 * @Project rvs
 * @Package com.osh.rvs.form.inline
 * @ClassName: DryingProcessForm
 * @Description: 烘干进程
 * @author lxb
 * @date 2016-8-17 下午3:00:01
 */
public class DryingProcessForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3429594551437751091L;

	@BeanField(title = "修理单号", name = "omr_notifi_no", type = FieldType.String, length = 14)
	private String omr_notifi_no;

	@BeanField(title = "机身号", name = "serial_no", type = FieldType.String)
	private String serial_no;

	@BeanField(title = "型号ID", name = "model_id", type = FieldType.String, length = 11)
	private String model_id;

	@BeanField(title = "型号名称", name = "model_name", type = FieldType.String, length = 50)
	private String model_name;

	@BeanField(title = "作业内容", name = "content", type = FieldType.String, length = 20)
	private String content;

	@BeanField(title = "管理编号", name = "manage_code", type = FieldType.String, length = 9)
	private String manage_code;

	@BeanField(title = "设备ID", name = "device_manage_id", type = FieldType.String, length = 11)
	private String device_manage_id;

	@BeanField(title = "工位ID", name = "position_id", type = FieldType.String, length = 11)
	private String position_id;

	@BeanField(title = "课室ID", name = "section_id", type = FieldType.String, length = 11)
	private String section_id;

	@BeanField(title = "库位号", name = "slot", type = FieldType.Integer, length = 2)
	private String slot;

	@BeanField(title = "开始时间 ", name = "start_time", type = FieldType.DateTime)
	private String start_time;

	@BeanField(title = "结束时间 ", name = "end_time", type = FieldType.DateTime)
	private String end_time;

	@BeanField(title = "进行状态", name = "status", type = FieldType.Integer, length = 1)
	private String status;

	@BeanField(title = "烘干作业 ID", name = "drying_job_id", type = FieldType.String, length = 11, notNull = true)
	private String drying_job_id;
	@BeanField(title = "硬化条件", name = "hardening_condition", type = FieldType.Integer, length = 3)
	private String hardening_condition;
	@BeanField(title = "可用库位", name = "slots", type = FieldType.String)
	private String slots;
	@BeanField(title = " 设定温度", name = "setting_temperature", type = FieldType.Integer, length = 3)
	private String setting_temperature;
	@BeanField(title = "课室名称", name = "section_name", type = FieldType.String, length = 11)
	private String section_name;

	@BeanField(title = "工位代码", name = "process_code", type = FieldType.String, length = 3)
	private String process_code;
	@BeanField(title = "已用库位", name = "using_slots", type = FieldType.String)
	private String using_slots;

	@BeanField(title = "烘干时间", name = "drying_time", type = FieldType.Integer, length = 3)
	private String drying_time;

	@BeanField(title = "维修对象ID", name = "material_id", type = FieldType.String, length = 11)
	private String material_id;

	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}

	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}

	public String getSerial_no() {
		return serial_no;
	}

	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getManage_code() {
		return manage_code;
	}

	public void setManage_code(String manage_code) {
		this.manage_code = manage_code;
	}

	public String getDevice_manage_id() {
		return device_manage_id;
	}

	public void setDevice_manage_id(String device_manage_id) {
		this.device_manage_id = device_manage_id;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public String getSlot() {
		return slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getDrying_job_id() {
		return drying_job_id;
	}

	public void setDrying_job_id(String drying_job_id) {
		this.drying_job_id = drying_job_id;
	}

	public String getHardening_condition() {
		return hardening_condition;
	}

	public void setHardening_condition(String hardening_condition) {
		this.hardening_condition = hardening_condition;
	}

	public String getSlots() {
		return slots;
	}

	public void setSlots(String slots) {
		this.slots = slots;
	}

	public String getSetting_temperature() {
		return setting_temperature;
	}

	public void setSetting_temperature(String setting_temperature) {
		this.setting_temperature = setting_temperature;
	}

	public String getSection_name() {
		return section_name;
	}

	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getDrying_time() {
		return drying_time;
	}

	public void setDrying_time(String drying_time) {
		this.drying_time = drying_time;
	}

	public String getUsing_slots() {
		return using_slots;
	}

	public void setUsing_slots(String using_slots) {
		this.using_slots = using_slots;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

}
