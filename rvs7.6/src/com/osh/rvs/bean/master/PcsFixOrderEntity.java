package com.osh.rvs.bean.master;

import java.io.Serializable;
import java.util.Date;

public class PcsFixOrderEntity implements Serializable {

	private static final long serialVersionUID = 3291982959100726074L;

	private String pcs_fix_order_key;
	private String material_id;
	private String sorc_no;
	private String sender_id;
	private String sender_name;
	private String comment;
	private Integer status;
	private Date update_time;
	private Date update_time_start;
	private Date update_time_end;
	private String position_id;
	private String line_id;
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
	public String getSender_id() {
		return sender_id;
	}
	public void setSender_id(String sender_id) {
		this.sender_id = sender_id;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Date getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}
	public Date getUpdate_time_start() {
		return update_time_start;
	}
	public void setUpdate_time_start(Date update_time_start) {
		this.update_time_start = update_time_start;
	}
	public Date getUpdate_time_end() {
		return update_time_end;
	}
	public void setUpdate_time_end(Date update_time_end) {
		this.update_time_end = update_time_end;
	}
	public String getSorc_no() {
		return sorc_no;
	}
	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
	}
	public String getSender_name() {
		return sender_name;
	}
	public void setSender_name(String sender_name) {
		this.sender_name = sender_name;
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
