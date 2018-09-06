package com.osh.rvs.form.manage;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class PcsFixOrderForm extends ActionForm {

	static final long serialVersionUID = 8645982635102774265L;

	@BeanField(title = "Key", name = "pcs_fix_order_key", primaryKey = true, length = 11)
	private String pcs_fix_order_key;
	@BeanField(title = "维修对象 ID", name = "material_id", notNull = true, length = 11)
	private String material_id;
	@BeanField(title = "修理单号", name = "sorc_no")
	private String sorc_no;
	@BeanField(title = "发送者 ID", name = "sender_id", length = 11)
	private String sender_id;
	@BeanField(title = "发送者名", name = "sender_name")
	private String sender_name;
	@BeanField(title = "修正需求", name = "comment", notNull = true, length = 200)
	private String comment;
	@BeanField(title = "修正状态", name = "status", type = FieldType.Integer, length = 1)
	private String status;
	@BeanField(title = "更新时间", name = "update_time", type = FieldType.DateTime)
	private String update_time;
	@BeanField(title = "更新时间", name = "update_time_start", type = FieldType.Date)
	private String update_time_start;
	@BeanField(title = "更新时间", name = "update_time_end", type = FieldType.Date)
	private String update_time_end;
	@BeanField(title = "工位 ID", name = "position_id", length = 11)
	private String position_id;
	@BeanField(title = "工程 ID", name = "line_id", length = 11)
	private String line_id;
	@BeanField(title = "工位代码", name = "process_code")
	private String process_code;

	public String getPcs_fix_order_key() {
		return pcs_fix_order_key;
	}
	public void setPcs_fix_order_key(String pcs_fix_order_key) {
		this.pcs_fix_order_key = pcs_fix_order_key;
	}
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
	public String getSender_id() {
		return sender_id;
	}
	public void setSender_id(String sender_id) {
		this.sender_id = sender_id;
	}
	public String getSender_name() {
		return sender_name;
	}
	public void setSender_name(String sender_name) {
		this.sender_name = sender_name;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public String getUpdate_time_start() {
		return update_time_start;
	}
	public void setUpdate_time_start(String update_time_start) {
		this.update_time_start = update_time_start;
	}
	public String getUpdate_time_end() {
		return update_time_end;
	}
	public void setUpdate_time_end(String update_time_end) {
		this.update_time_end = update_time_end;
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
	public String getLine_id() {
		return line_id;
	}
	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}
}
