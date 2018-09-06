package com.osh.rvs.form.infect;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class ToolsDistributeForm extends ActionForm implements Serializable {

	/**
	 * 治具分布
	 */
	private static final long serialVersionUID = -4710747302936537284L;
	@BeanField(title="管理编号",name="manage_code",type=FieldType.String)
	private String manage_code;
	@BeanField(title="治具NO.",name="tools_no",type=FieldType.String)
	private String tools_no;
	@BeanField(title="治具名称",name="tools_name",type=FieldType.String)
	private String tools_name;
	@BeanField(title="治具名称ID",name="tools_type_id",type=FieldType.String)
	private String tools_type_id;
	@BeanField(title="分发课室",name="section_name",type=FieldType.String)
	private String section_name;
	@BeanField(title="分发课室ID",name="section_id",type=FieldType.String)
	private String section_id;
	@BeanField(title="责任工程",name="line_name",type=FieldType.String)
	private String line_name;
	@BeanField(title="责任工程ID",name="line_id",type=FieldType.String)
	private String line_id;
	@BeanField(title="责任工位",name="process_code",type=FieldType.String)
	private String process_code;
	@BeanField(title="责任工位ID",name="position_id",type=FieldType.String)
	private String position_id;
	@BeanField(title="操作者",name="operator",type=FieldType.String)
	private String operator;
	@BeanField(title="操作者ID",name="responsible_operator_id",type=FieldType.String)
	private String responsible_operator_id;
	@BeanField(title="发放日期",name="provide_date",type=FieldType.Date)
	private String provide_date;
	@BeanField(title="发放者",name="provider",type=FieldType.String)
	private String provider;
	@BeanField(title="更新人",name="updated_by",type=FieldType.String)
	private String updated_by;
	@BeanField(title="更新时间",name="updated_time",type=FieldType.TimeStamp)
	private String updated_time;
	@BeanField(title="状态",name="status",type=FieldType.String)
	private String status;
	@BeanField(title="备注",name="comment",type=FieldType.String)
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
	public String getUpdated_time() {
		return updated_time;
	}
	public void setUpdated_time(String updated_time) {
		this.updated_time = updated_time;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
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
	public String getProvide_date() {
		return provide_date;
	}
	public void setProvide_date(String provide_date) {
		this.provide_date = provide_date;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
}
