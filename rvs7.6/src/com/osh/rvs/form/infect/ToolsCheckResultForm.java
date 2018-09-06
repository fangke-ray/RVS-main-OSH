package com.osh.rvs.form.infect;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class ToolsCheckResultForm extends ActionForm {

	/**
	 * 治具点检结果
	 */
	private static final long serialVersionUID = 5777122242850845512L;
	//合格台数
	@BeanField(title="合格台数",name="qualified",type=FieldType.Integer)
	private Integer qualified;
	
	//不合格台数
	@BeanField(title="不合格台数",name="unqualified",type=FieldType.Integer)
	private Integer unqualified;
	
	/* 治具管理 */
	@BeanField(title="治具管理ID",name="tools_manage_id",type=FieldType.String)
	private String tools_manage_id;
	@BeanField(title="管理编号",name="manage_code",type=FieldType.String)
	private String manage_code;
	@BeanField(title="治具NO.",name="tools_no",type=FieldType.String)
	private String tools_no;
	@BeanField(title="治具品名ID",name="tools_type_id",type=FieldType.String)
	private String tools_type_id;
	@BeanField(title="治具名称",name="tools_name",type=FieldType.String)
	private String tools_name;
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
	@BeanField(title="责任工位名称",name="position_name",type=FieldType.String)
	private String position_name;
	@BeanField(title="责任工位ID",name="position_id",type=FieldType.String)
	private String position_id;

	/*设备工具点检记录*/
	@BeanField(title="管理ID",name="manage_id",type=FieldType.String)
	private String manage_id;
	@BeanField(title="点检表管理ID",name="check_file_manage_id",type=FieldType.String)
	private String check_file_manage_id;
	@BeanField(title="项目序号",name="item_seq",type=FieldType.String)
	private String item_seq;
	@BeanField(title="点检人员",name="operator",type=FieldType.String)
	private String operator;
	@BeanField(title="点检人员",name="operator_id",type=FieldType.String)
	private String operator_id;
	@BeanField(title="点检时间",name="check_confirm_time",type=FieldType.String)
	private String check_confirm_time;
	@BeanField(title="数值",name="digit",type=FieldType.String)
	private String digit;
	@BeanField(title="结果",name="checked_status",type=FieldType.String)
	private String checked_status;
	
	/*12个月份*/
	@BeanField(title="四月",name="april",type=FieldType.String)
    private String april;
	@BeanField(title="五月",name="may",type=FieldType.String)
	private String may;
	@BeanField(title="六月",name="june",type=FieldType.String)
	private String june;
	@BeanField(title="七月",name="july",type=FieldType.String)
	private String july;
	@BeanField(title="八月",name="august",type=FieldType.String)
	private String august;
	@BeanField(title="九月",name="september",type=FieldType.String)
	private String september;
	@BeanField(title="十月",name="october",type=FieldType.String)
	private String october;
	@BeanField(title="十一月",name="november",type=FieldType.String)
	private String november;
	@BeanField(title="十二月",name="december",type=FieldType.String)
	private String december;
	@BeanField(title="一月",name="january",type=FieldType.String)
	private String january;
	@BeanField(title="二月",name="february",type=FieldType.String)
	private String february;
	@BeanField(title="三月",name="march",type=FieldType.String)
	private String march;
	
	private String manager_operator_id;
	private String responsible_operator_id;

	public String getPosition_name() {
		return position_name;
	}
	public void setPosition_name(String position_name) {
		this.position_name = position_name;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public Integer getQualified() {
		return qualified;
	}
	public void setQualified(Integer qualified) {
		this.qualified = qualified;
	}
	public Integer getUnqualified() {
		return unqualified;
	}
	public void setUnqualified(Integer unqualified) {
		this.unqualified = unqualified;
	}
	public String getTools_manage_id() {
		return tools_manage_id;
	}
	public void setTools_manage_id(String tools_manage_id) {
		this.tools_manage_id = tools_manage_id;
	}
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
	public String getTools_type_id() {
		return tools_type_id;
	}
	public void setTools_type_id(String tools_type_id) {
		this.tools_type_id = tools_type_id;
	}
	public String getTools_name() {
		return tools_name;
	}
	public void setTools_name(String tools_name) {
		this.tools_name = tools_name;
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
	public String getPosition_id() {
		return position_id;
	}
	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}
	public String getManage_id() {
		return manage_id;
	}
	public void setManage_id(String manage_id) {
		this.manage_id = manage_id;
	}
	public String getCheck_file_manage_id() {
		return check_file_manage_id;
	}
	public void setCheck_file_manage_id(String check_file_manage_id) {
		this.check_file_manage_id = check_file_manage_id;
	}
	public String getItem_seq() {
		return item_seq;
	}
	public void setItem_seq(String item_seq) {
		this.item_seq = item_seq;
	}
	public String getOperator_id() {
		return operator_id;
	}
	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}
	public String getCheck_confirm_time() {
		return check_confirm_time;
	}
	public void setCheck_confirm_time(String check_confirm_time) {
		this.check_confirm_time = check_confirm_time;
	}
	public String getDigit() {
		return digit;
	}
	public void setDigit(String digit) {
		this.digit = digit;
	}
	public String getChecked_status() {
		return checked_status;
	}
	public void setChecked_status(String checked_status) {
		this.checked_status = checked_status;
	}
	public String getApril() {
		return april;
	}
	public void setApril(String april) {
		this.april = april;
	}
	public String getMay() {
		return may;
	}
	public void setMay(String may) {
		this.may = may;
	}
	public String getJune() {
		return june;
	}
	public void setJune(String june) {
		this.june = june;
	}
	public String getJuly() {
		return july;
	}
	public void setJuly(String july) {
		this.july = july;
	}
	public String getAugust() {
		return august;
	}
	public void setAugust(String august) {
		this.august = august;
	}
	public String getSeptember() {
		return september;
	}
	public void setSeptember(String september) {
		this.september = september;
	}
	public String getOctober() {
		return october;
	}
	public void setOctober(String october) {
		this.october = october;
	}
	public String getNovember() {
		return november;
	}
	public void setNovember(String november) {
		this.november = november;
	}
	public String getDecember() {
		return december;
	}
	public void setDecember(String december) {
		this.december = december;
	}
	public String getJanuary() {
		return january;
	}
	public void setJanuary(String january) {
		this.january = january;
	}
	public String getFebruary() {
		return february;
	}
	public void setFebruary(String february) {
		this.february = february;
	}
	public String getMarch() {
		return march;
	}
	public void setMarch(String march) {
		this.march = march;
	}
	public String getResponsible_operator_id() {
		return responsible_operator_id;
	}
	public void setResponsible_operator_id(String responsible_operator_id) {
		this.responsible_operator_id = responsible_operator_id;
	}
	public String getManager_operator_id() {
		return manager_operator_id;
	}
	public void setManager_operator_id(String manager_operator_id) {
		this.manager_operator_id = manager_operator_id;
	}
	
	
}
