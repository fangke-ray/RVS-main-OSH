package com.osh.rvs.bean.inline;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @Title DryingProcessEntity.java
 * @Project rvs
 * @Package com.osh.rvs.bean.inline
 * @ClassName: DryingProcessEntity
 * @Description: 烘干进程
 * @author lxb
 * @date 2016-8-17 下午2:59:18
 */
public class DryingProcessEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5319669826511442925L;

	/** 修理单号 **/
	private String omr_notifi_no;

	/*** 机身号 */
	private String serial_no;

	/** 型号ID **/
	private String model_id;

	/** 型号名称 **/
	private String model_name;

	/** 作业内容 **/
	private String content;

	/*** 管理编号 */
	private String manage_code;

	/** 设备ID **/
	private String device_manage_id;

	/** 工位ID **/
	private String position_id;

	/** 课室ID **/
	private String section_id;

	/** 库位号 **/
	private Integer slot;

	/** 开始时间 **/
	private Date start_time;

	/** 结束时间 **/
	private Date end_time;

	/** 进行状态 **/
	private Integer status;

	/** 课室名称 **/
	private String section_name;

	/** 工位代码 **/
	private String process_code;

	/** 烘干时间 **/
	private Integer drying_time;

	private String using_slots;
	private String drying_job_id;
	private String material_id;
	private Integer hardening_condition;

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

	public Integer getSlot() {
		return slot;
	}

	public void setSlot(Integer slot) {
		this.slot = slot;
	}

	public Date getStart_time() {
		return start_time;
	}

	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}

	public Date getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
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

	public Integer getDrying_time() {
		return drying_time;
	}

	public void setDrying_time(Integer drying_time) {
		this.drying_time = drying_time;
	}

	public String getUsing_slots() {
		return using_slots;
	}

	public void setUsing_slots(String using_slots) {
		this.using_slots = using_slots;
	}

	public String getDrying_job_id() {
		return drying_job_id;
	}

	public void setDrying_job_id(String drying_job_id) {
		this.drying_job_id = drying_job_id;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public Integer getHardening_condition() {
		return hardening_condition;
	}

	public void setHardening_condition(Integer hardening_condition) {
		this.hardening_condition = hardening_condition;
	}

}
