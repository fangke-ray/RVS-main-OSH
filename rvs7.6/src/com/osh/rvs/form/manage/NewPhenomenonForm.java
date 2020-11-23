package com.osh.rvs.form.manage;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class NewPhenomenonForm extends ActionForm implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6637060531510930254L;

	@BeanField(title = "修理单号", name = "omr_notifi_no", type = FieldType.String)
	private String omr_notifi_no;
	@BeanField(title = "修理品 ID", name = "material_id", type = FieldType.String)
	private String material_id;
	@BeanField(title = "新现象 Key", name = "key", type = FieldType.String, primaryKey=true)
	private String key;
	@BeanField(title = "故障部件组名称", name = "location_group_desc", type = FieldType.String, length=45)
	private String location_group_desc;
	@BeanField(title = "故障部位名称", name = "location_desc", type = FieldType.String, length=45)
	private String location_desc;
	@BeanField(title = "新现象描述", name = "description", type = FieldType.String, length=45)
	private String description;

	@BeanField(title = "最后决定日期", name = "last_determine_date", type = FieldType.Date)
	private String last_determine_date;

	@BeanField(title = "发送回应", name = "return_status", type = FieldType.String)
	private String return_status;
	@BeanField(title = "最后提交消息组编号", name = "last_sent_message_number", type = FieldType.String)
	private String last_sent_message_number;

	@BeanField(title = "报告记录者 ID", name = "operator_id", type = FieldType.String)
	private String operator_id;
	@BeanField(title = "机种类别", name = "kind", type = FieldType.Integer)
	private String kind;
	@BeanField(title = "机种", name = "category_name", type = FieldType.String)
	private String category_name;
	@BeanField(title = "机身号", name = "serial_no", type = FieldType.String)
	private String serial_no;
	@BeanField(title = "发现时间", name = "occur_time", type = FieldType.Date)
	private String occur_time;

	@BeanField(title = "发生工程 ID", name = "line_id", type = FieldType.String)
	private String line_id;
	@BeanField(title = "发生工程", name = "line_name", type = FieldType.String)
	private String line_name;
	@BeanField(title = "报告记录者", name = "operator_name", type = FieldType.String)
	private String operator_name;
	@BeanField(title = "报告发送者", name = "determine_operator_name", type = FieldType.String)
	private String determine_operator_name;
	@BeanField(title = "发现时间起", name = "occur_time_start", type = FieldType.Date)
	private String occur_time_from;
	@BeanField(title = "发现时间止", name = "occur_time_end", type = FieldType.Date)
	private String occur_time_to;
	@BeanField(title = "最后决定日起", name = "last_determine_date_start", type = FieldType.Date)
	private String last_determine_date_from;
	@BeanField(title = "最后决定日止", name = "last_determine_date_end", type = FieldType.Date)
	private String last_determine_date_to;

	private String level_name;
	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}
	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}
	public String getMaterial_id() {
		return material_id;
	}
	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getLocation_group_desc() {
		return location_group_desc;
	}
	public void setLocation_group_desc(String location_group_desc) {
		this.location_group_desc = location_group_desc;
	}
	public String getLocation_desc() {
		return location_desc;
	}
	public void setLocation_desc(String location_desc) {
		this.location_desc = location_desc;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLast_determine_date() {
		return last_determine_date;
	}
	public void setLast_determine_date(String last_determine_date) {
		this.last_determine_date = last_determine_date;
	}
	public String getReturn_status() {
		return return_status;
	}
	public void setReturn_status(String return_status) {
		this.return_status = return_status;
	}
	public String getLast_sent_message_number() {
		return last_sent_message_number;
	}
	public void setLast_sent_message_number(String last_sent_message_number) {
		this.last_sent_message_number = last_sent_message_number;
	}
	public String getOperator_id() {
		return operator_id;
	}
	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	public String getSerial_no() {
		return serial_no;
	}
	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}
	public String getOccur_time() {
		return occur_time;
	}
	public void setOccur_time(String occur_time) {
		this.occur_time = occur_time;
	}
	public String getLine_id() {
		return line_id;
	}
	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}
	public String getLine_name() {
		return line_name;
	}
	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}
	public String getOperator_name() {
		return operator_name;
	}
	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}
	public String getDetermine_operator_name() {
		return determine_operator_name;
	}
	public void setDetermine_operator_name(String determine_operator_name) {
		this.determine_operator_name = determine_operator_name;
	}
	public String getOccur_time_from() {
		return occur_time_from;
	}
	public void setOccur_time_from(String occur_time_from) {
		this.occur_time_from = occur_time_from;
	}
	public String getOccur_time_to() {
		return occur_time_to;
	}
	public void setOccur_time_to(String occur_time_to) {
		this.occur_time_to = occur_time_to;
	}
	public String getLast_determine_date_from() {
		return last_determine_date_from;
	}
	public void setLast_determine_date_from(String last_determine_date_from) {
		this.last_determine_date_from = last_determine_date_from;
	}
	public String getLast_determine_date_to() {
		return last_determine_date_to;
	}
	public void setLast_determine_date_to(String last_determine_date_to) {
		this.last_determine_date_to = last_determine_date_to;
	}
	public String getLevel_name() {
		return level_name;
	}
	public void setLevel_name(String level_name) {
		this.level_name = level_name;
	}
}
