package com.osh.rvs.bean.equipment;

import java.io.Serializable;

/**
 * 设备工具备品
 * 
 * @author gonglm
 * 
 */
public class DeviceBackupEntity implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2295507654761983825L;

	private String manage_id;

	private String backup_manage_id;

	private Integer free_displace_flg;

	private String manage_code;
	private String device_type_id;
	private String model_name;
	private String name;

	private String line_name;
	private String line_id;

	private String process_code;
	private String position_id;

	private String backup_in_manage;

	private Integer spare_available_inventory;

	private String corresponding;

	private Integer manage_level;

	private Integer status;

	private Integer evaluation;

	public String getManage_id() {
		return manage_id;
	}

	public void setManage_id(String manage_id) {
		this.manage_id = manage_id;
	}

	public String getBackup_manage_id() {
		return backup_manage_id;
	}

	public void setBackup_manage_id(String backup_manage_id) {
		this.backup_manage_id = backup_manage_id;
	}

	public Integer getFree_displace_flg() {
		return free_displace_flg;
	}

	public void setFree_displace_flg(Integer free_displace_flg) {
		this.free_displace_flg = free_displace_flg;
	}

	public String getManage_code() {
		return manage_code;
	}

	public void setManage_code(String manage_code) {
		this.manage_code = manage_code;
	}

	public String getDevice_type_id() {
		return device_type_id;
	}

	public void setDevice_type_id(String device_type_id) {
		this.device_type_id = device_type_id;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getBackup_in_manage() {
		return backup_in_manage;
	}

	public void setBackup_in_manage(String backup_in_line) {
		this.backup_in_manage = backup_in_line;
	}

	public Integer getSpare_available_inventory() {
		return spare_available_inventory;
	}

	public void setSpare_available_inventory(Integer spare_available_inventory) {
		this.spare_available_inventory = spare_available_inventory;
	}

	public String getCorresponding() {
		return corresponding;
	}

	public void setCorresponding(String corresponding) {
		this.corresponding = corresponding;
	}

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public Integer getManage_level() {
		return manage_level;
	}

	public void setManage_level(Integer manage_level) {
		this.manage_level = manage_level;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(Integer evaluation) {
		this.evaluation = evaluation;
	}
}
