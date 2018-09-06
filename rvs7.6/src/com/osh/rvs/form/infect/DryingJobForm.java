package com.osh.rvs.form.infect;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 
 * @Title DryingJobForm.java
 * @Project rvs
 * @Package com.osh.rvs.form.infect
 * @ClassName: DryingJobForm
 * @Description: 烘干作业
 * @author lxb
 * @date 2016-8-4 上午9:10:35
 */
public class DryingJobForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 853783186746851405L;

	@BeanField(title = "烘干作业 ID", name = "drying_job_id", type = FieldType.String, length = 11, notNull = true)
	private String drying_job_id;

	@BeanField(title = "作业内容", name = "content", type = FieldType.String, length = 20, notNull = true)
	private String content;

	@BeanField(title = "干燥时间", name = "drying_time", type = FieldType.Integer, length = 3, notNull = true)
	private String drying_time;

	@BeanField(title = "硬化条件", name = "hardening_condition", type = FieldType.Integer, length = 3, notNull = true)
	private String hardening_condition;

	@BeanField(title = "使用设备", name = "device_manage_id", type = FieldType.String, length = 11)
	private String device_manage_id;

	@BeanField(title = "指定库位 ", name = "slots", type = FieldType.String, length = 45)
	private String slots;

	@BeanField(title = "工位", name = "position_id", type = FieldType.String, length = 11, notNull = true)
	private String position_id;

	@BeanField(title = "工位代码", name = "process_code", type = FieldType.String, length = 3)
	private String process_code;

	@BeanField(title = "管理编号", name = "manage_code", type = FieldType.String, length = 9)
	private String manage_code;

	@BeanField(title = "型号名称", name = "model_name", type = FieldType.String, length = 32)
	private String model_name;

	@BeanField(title = "机种名称", name = "category_name", type = FieldType.String, length = 100)
	private String category_name;

	@BeanField(title = "机种ID", name = "category_id", type = FieldType.String, length = 11)
	private String category_id;

	@BeanField(title = "工位名称", name = "position_name", type = FieldType.String, length = 45)
	private String position_name;

	@BeanField(title = "库位数", name = "slot", type = FieldType.Integer, length = 2)
	private String slot;
	
	@BeanField(title = "设备名称", name = "device_name", type = FieldType.String, length = 32)
	private String device_name;

	public String getDrying_job_id() {
		return drying_job_id;
	}

	public void setDrying_job_id(String drying_job_id) {
		this.drying_job_id = drying_job_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDrying_time() {
		return drying_time;
	}

	public void setDrying_time(String drying_time) {
		this.drying_time = drying_time;
	}

	public String getHardening_condition() {
		return hardening_condition;
	}

	public void setHardening_condition(String hardening_condition) {
		this.hardening_condition = hardening_condition;
	}

	public String getDevice_manage_id() {
		return device_manage_id;
	}

	public void setDevice_manage_id(String device_manage_id) {
		this.device_manage_id = device_manage_id;
	}

	public String getSlots() {
		return slots;
	}

	public void setSlots(String slots) {
		this.slots = slots;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
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

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getPosition_name() {
		return position_name;
	}

	public void setPosition_name(String position_name) {
		this.position_name = position_name;
	}

	public String getSlot() {
		return slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}

	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}

}
