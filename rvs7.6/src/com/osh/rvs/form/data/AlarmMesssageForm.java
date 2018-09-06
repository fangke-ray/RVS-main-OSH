package com.osh.rvs.form.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class AlarmMesssageForm extends ActionForm implements Serializable {

	private static final long serialVersionUID = -6347780780457817033L;

	@BeanField(title = "警报 ID", name = "alarm_messsage_id", primaryKey = true, length = 11)
	private String id;
	@BeanField(title = "警报等级", name = "level", type = FieldType.Integer)
	private String level;
	@BeanField(title = "发生时间", name = "occur_time", type = FieldType.DateTime)
	private String occur_time;
	@BeanField(title = "原因", name = "reason", type = FieldType.Integer)
	private String reason;
	@BeanField(title = "修理单号", name = "sorc_no")
	private String sorc_no;
	@BeanField(title = "型号", name = "model_id")
	private String model_id;
	private String model_name;
	@BeanField(title = "机身号", name = "serial_no")
	private String serial_no;
	private String material_id;
	private String line_id;
	private String line_name;
	private String process_code;
	private String position_name;
	private String operator_name;
	private String section_id;
	private String section_name;
	private String comment;
	private String myComment;
	private String position_id;
	private String resolver_id;
	private String resolver_name;
	@BeanField(title = "resolve_time", name = "resolve_time", type = FieldType.DateTime)
	private String resolve_time;

	@BeanField(title = "发生时间", name = "occur_time_from", type = FieldType.Date)
	private String occur_time_from;
	@BeanField(title = "发生时间", name = "occur_time_to", type = FieldType.Date)
	private String occur_time_to;

	private String reciever_id;

	private List<Map<String, String>> sendations;

	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getSorc_no() {
		return sorc_no;
	}
	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
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
	public String getOperator_name() {
		return operator_name;
	}
	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}
	public String getOccur_time() {
		return occur_time;
	}
	public void setOccur_time(String occur_time) {
		this.occur_time = occur_time;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the position_id
	 */
	public String getPosition_id() {
		return position_id;
	}
	/**
	 * @param position_id the position_id to set
	 */
	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}
	public String getModel_id() {
		return model_id;
	}
	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}
	public String getSection_name() {
		return section_name;
	}
	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}
	public String getResolver_id() {
		return resolver_id;
	}
	public void setResolver_id(String resolver_id) {
		this.resolver_id = resolver_id;
	}
	public String getResolver_name() {
		return resolver_name;
	}
	public void setResolver_name(String resolver_name) {
		this.resolver_name = resolver_name;
	}
	public String getResolve_time() {
		return resolve_time;
	}
	public void setResolve_time(String resolve_time) {
		this.resolve_time = resolve_time;
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
	public String getLine_id() {
		return line_id;
	}
	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}
	public String getSection_id() {
		return section_id;
	}
	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}
	/**
	 * @return the sendationMap
	 */
	public List<Map<String, String>> getSendations() {
		return sendations;
	}
	/**
	 * @param sendationMap the sendationMap to set
	 */
	public void setSendations(List<Map<String, String>> sendations) {
		this.sendations = sendations;
	}
	/**
	 * @return the material_id
	 */
	public String getMaterial_id() {
		return material_id;
	}
	/**
	 * @param material_id the material_id to set
	 */
	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}
	/**
	 * @return the reciever_id
	 */
	public String getReciever_id() {
		return reciever_id;
	}
	/**
	 * @param reciever_id the reciever_id to set
	 */
	public void setReciever_id(String reciever_id) {
		this.reciever_id = reciever_id;
	}
	public String getPosition_name() {
		return position_name;
	}
	public void setPosition_name(String position_name) {
		this.position_name = position_name;
	}
	public String getMyComment() {
		return myComment;
	}
	public void setMyComment(String myComment) {
		this.myComment = myComment;
	}

}
