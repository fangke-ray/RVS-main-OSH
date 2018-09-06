package com.osh.rvs.bean.infect;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;


public class ToolsDistributeEntity implements Serializable {

	/**
	 * 治具分布
	 */
	private static final long serialVersionUID = -1154977291809714971L;
	
	private String manage_code;
	private String tools_no;
	private String tools_name;
	private String tools_type_id;
	private String section_name;
	private String section_id;
	private String line_name;
	private String line_id;
	private String process_code;
	private String position_id;
	private String operator;
	private String responsible_operator_id;
	private Date provide_date;
	private String provider;
	private String updated_by;
	private Timestamp updated_time;
	private String status;
	private String comment;
	public String getManage_code() {
		return manage_code;
	}
	public void setManage_code(String manage_code) {
		this.manage_code = manage_code;
	}
	public String getTools_no() {
		return tools_no;
	}
	public void setTools_no(String tools_no) {
		this.tools_no = tools_no;
	}
	public String getTools_name() {
		return tools_name;
	}
	public void setTools_name(String tools_name) {
		this.tools_name = tools_name;
	}
	public String getTools_type_id() {
		return tools_type_id;
	}
	public void setTools_type_id(String tools_type_id) {
		this.tools_type_id = tools_type_id;
	}
	public String getSection_name() {
		return section_name;
	}
	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}
	public String getSection_id() {
		return section_id;
	}
	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}
	public String getLine_name() {
		return line_name;
	}
	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}
	public String getLine_id() {
		return line_id;
	}
	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}
	public String getProcess_code() {
		return process_code;
	}
	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}
	public String getResponsible_position_id() {
		return position_id;
	}
	public void setResponsible_position_id(String responsible_position_id) {
		this.position_id = responsible_position_id;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getResponsible_operator_id() {
		return responsible_operator_id;
	}
	public void setResponsible_operator_id(String responsible_operator_id) {
		this.responsible_operator_id = responsible_operator_id;
	}
	public String getUpdated_by() {
		return updated_by;
	}
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Timestamp getUpdated_time() {
		return updated_time;
	}
	public void setUpdated_time(Timestamp updated_time) {
		this.updated_time = updated_time;
	}
	public String getPosition_id() {
		return position_id;
	}
	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getProvide_date() {
		return provide_date;
	}
	public void setProvide_date(Date provide_date) {
		this.provide_date = provide_date;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
}
