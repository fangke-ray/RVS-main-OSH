package com.osh.rvs.form.equipment;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 设备工具备品
 * 
 * @author gonglm
 * 
 */
public class DeviceBackupForm extends ActionForm implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3876343897522823536L;

	@BeanField(title = "被替换设备工具管理 ID", name = "manage_id", type = FieldType.String, length = 11, notNull = true)
	private String manage_id;

	@BeanField(title = "替换用设备工具管理 ID", name = "backup_manage_id", type = FieldType.String, length = 11, notNull = true)
	private String backup_manage_id;

	@BeanField(title = "可替换状况", name = "free_displace_flg", type = FieldType.Integer, length = 1, notNull = true)
	private String free_displace_flg;

	@BeanField(title = "设备工具管理编号", name = "manage_code", type = FieldType.String, length = 9)
	private String manage_code;
	@BeanField(title = "设备工具品名ID", name = "device_type_id", type = FieldType.String, length = 11)
	private String device_type_id;
	@BeanField(title = "型号", name = "model_name", type = FieldType.String)
	private String model_name;
	@BeanField(title = "品名", name = "name", type = FieldType.String)
	private String name;

	@BeanField(title = "使用工程", name = "line_name", type = FieldType.String)
	private String line_name;
	private String line_id;

	@BeanField(title = "责任工位", name = "process_code", type = FieldType.String)
	private String process_code;
	private String position_id;

	@BeanField(title = "管理中可替换品", name = "backup_in_manage", type = FieldType.String)
	private String backup_in_manage;
	
	@BeanField(title = "管理外可替换品", name = "backup_out_manage", type = FieldType.String)
	private String backup_out_manage;

	@BeanField(title = "同型号备品", name = "spare_available_inventory", type = FieldType.Integer)
	private String spare_available_inventory;

	@BeanField(title = "对应内容", name = "corresponding", type = FieldType.String, length = 250)
	private String corresponding;

	// 管理等级
	@BeanField(title = "管理等级", name = "manage_level", type = FieldType.Integer)
	private String manage_level;

	@BeanField(title = "状态", name = "status", type = FieldType.Integer)
	private String status;

	@BeanField(title = "评价", name = "evaluation", type = FieldType.Integer)
	private String evaluation;

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

	public String getFree_displace_flg() {
		return free_displace_flg;
	}

	public void setFree_displace_flg(String free_displace_flg) {
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

	public void setDevice_type_id(String devices_type_id) {
		this.device_type_id = devices_type_id;
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

	public void setBackup_in_manage(String backup_in_manage) {
		this.backup_in_manage = backup_in_manage;
	}

	public String getBackup_out_manage() {
		return backup_out_manage;
	}

	public void setBackup_out_manage(String backup_out_manage) {
		this.backup_out_manage = backup_out_manage;
	}

	public String getSpare_available_inventory() {
		return spare_available_inventory;
	}

	public void setSpare_available_inventory(String spare_available_inventory) {
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

	public String getManage_level() {
		return manage_level;
	}

	public void setManage_level(String manage_level) {
		this.manage_level = manage_level;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(String evaluation) {
		this.evaluation = evaluation;
	}
}
