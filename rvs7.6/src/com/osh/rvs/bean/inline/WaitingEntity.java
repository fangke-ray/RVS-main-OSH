package com.osh.rvs.bean.inline;

import java.io.Serializable;

import com.osh.rvs.form.inline.DryingProcessForm;

/**
 * 
 */
public class WaitingEntity implements Serializable {

	private static final long serialVersionUID = 3151697709388335872L;

	private String material_id;
	private String sorc_no;
	private String model_id;
	private String model_name;
	private String category_name;
	private String serial_no;
	private Integer expedited;
	private String waitingat;
	private Integer pause_reason;
	private Integer today;
	private Integer reworked;
	private Integer direct_flg;
	private Integer light_fix;
	private String in_place_time;
	private Integer block_status;
	private DryingProcessForm drying_process;
	private Integer overtime;
	private Integer imbalance;
	private String scheduled_monthday;

	private String position_id;
	private String process_code;

	public String getMaterial_id() {
		return material_id;
	}
	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}
	public String getSorc_no() {
		return sorc_no;
	}
	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
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
	public String getSerial_no() {
		return serial_no;
	}
	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}
	public Integer getExpedited() {
		return expedited;
	}
	public void setExpedited(Integer expedited) {
		this.expedited = expedited;
	}
	public String getWaitingat() {
		return waitingat;
	}
	public void setWaitingat(String waitingat) {
		this.waitingat = waitingat;
	}
	public Integer getPause_reason() {
		return pause_reason;
	}
	public void setPause_reason(Integer pause_reanson) {
		this.pause_reason = pause_reanson;
	}
	public Integer getToday() {
		return today;
	}
	public void setToday(Integer today) {
		this.today = today;
	}
	/**
	 * @return the category_name
	 */
	public String getCategory_name() {
		return category_name;
	}
	/**
	 * @param category_name the category_name to set
	 */
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	/**
	 * @return the reworked
	 */
	public Integer getReworked() {
		return reworked;
	}
	/**
	 * @param reworked the reworked to set
	 */
	public void setReworked(Integer reworked) {
		this.reworked = reworked;
	}
	public Integer getDirect_flg() {
		return direct_flg;
	}
	public void setDirect_flg(Integer direct_flg) {
		this.direct_flg = direct_flg;
	}
	public Integer getLight_fix() {
		return light_fix;
	}
	public void setLight_fix(Integer light_fix) {
		this.light_fix = light_fix;
	}
	public String getIn_place_time() {
		return in_place_time;
	}
	public void setIn_place_time(String in_place_time) {
		this.in_place_time = in_place_time;
	}
	public Integer getBlock_status() {
		return block_status;
	}
	public void setBlock_status(Integer block_status) {
		this.block_status = block_status;
	}
	public DryingProcessForm getDrying_process() {
		return drying_process;
	}
	public void setDrying_process(DryingProcessForm drying_process) {
		this.drying_process = drying_process;
	}
	public Integer getOvertime() {
		return overtime;
	}
	public void setOvertime(Integer overtime) {
		this.overtime = overtime;
	}
	public Integer getImbalance() {
		return imbalance;
	}
	public void setImbalance(Integer imbalance) {
		this.imbalance = imbalance;
	}
	public String getScheduled_monthday() {
		return scheduled_monthday;
	}
	public void setScheduled_monthday(String scheduled_monthday) {
		this.scheduled_monthday = scheduled_monthday;
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
}
