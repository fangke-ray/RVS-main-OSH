package com.osh.rvs.bean.infect;

import java.io.Serializable;

/***
 * 
 * @Title DryingJobEntity.java
 * @Project rvs
 * @Package com.osh.rvs.bean.infect
 * @ClassName: DryingJobEntity
 * @Description: 烘干作业
 * @author lxb
 * @date 2016-8-3 下午5:38:08
 */
public class DryingJobEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 22412628416193956L;

	/** 烘干作业 ID **/
	private String drying_job_id;

	/** 作业内容 **/
	private String content;

	/** 干燥时间 **/
	private Integer drying_time;

	/** 硬化条件 **/
	private Integer hardening_condition;

	/** 使用设备 ID **/
	private String device_manage_id;

	/** 指定库位 **/
	private String slots;

	/** 工位ID **/
	private String position_id;

	/** 工位代码 **/
	private String process_code;

	/** 管理编号 **/
	private String manage_code;

	/** 型号名称 **/
	private String model_name;

	/** 机种名称 **/
	private String category_name;

	/** 机种ID **/
	private String category_id;

	/** 工位名称 **/
	private String position_name;

	/** 库位数 **/
	private Integer slot;
	
	/*** 设备名称 */
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

	public Integer getDrying_time() {
		return drying_time;
	}

	public void setDrying_time(Integer drying_time) {
		this.drying_time = drying_time;
	}

	public Integer getHardening_condition() {
		return hardening_condition;
	}

	public void setHardening_condition(Integer hardening_condition) {
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

	public Integer getSlot() {
		return slot;
	}

	public void setSlot(Integer slot) {
		this.slot = slot;
	}

	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}

}
