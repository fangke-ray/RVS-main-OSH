package com.osh.rvs.bean.infect;

import java.io.Serializable;

public class ToolsCheckResultEntity implements Serializable {

	/**
	 * 治具点检结果
	 */
	private static final long serialVersionUID = 1628335626717502098L;
	//当前月的第一天
	private String firstDate;
	//当前月的最后一天
	private String lastDate;
	
	// 合格台数
	private Integer qualified;
	// 不合格台数
	private Integer unqualified;

	/* 治具管理 */
	private String tools_manage_id;
	private String manage_code;
	private String tools_no;
	private String tools_type_id;
	private String tools_name;
	private String section_name;
	private String section_id;
	private String line_name;
	private String line_id;
	private String position_name;
	private String process_code;
	private String position_id;

	/*设备工具点检记录*/
	private String manage_id;
	private String check_file_manage_id;
	private String item_seq;
	private String operator;
	private String operator_id;
	private String check_confirm_time;
	private String digit;
	private String checked_status;
	
	/*12个月份*/
    private String april;
	private String may;
	private String june;
	private String july;
	private String august;
	private String september;
	private String october;
	private String november;
	private String december;
	private String january;
	private String february;
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
	public String getFirstDate() {
		return firstDate;
	}
	public void setFirstDate(String firstDate) {
		this.firstDate = firstDate;
	}
	public String getLastDate() {
		return lastDate;
	}
	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
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
	public String getManager_operator_id() {
		return manager_operator_id;
	}
	public void setManager_operator_id(String manager_operator_id) {
		this.manager_operator_id = manager_operator_id;
	}
	public String getResponsible_operator_id() {
		return responsible_operator_id;
	}
	public void setResponsible_operator_id(String responsible_operator_id) {
		this.responsible_operator_id = responsible_operator_id;
	}  
	
	
}
