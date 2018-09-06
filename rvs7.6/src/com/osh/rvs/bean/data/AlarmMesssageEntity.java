package com.osh.rvs.bean.data;

import java.io.Serializable;
import java.util.Date;

public class AlarmMesssageEntity implements Serializable {

	private static final long serialVersionUID = 7801613561297795956L;

	private String alarm_messsage_id;
	private Integer level;
	private Date occur_time;
	private String material_id;
	private Integer reason;
	private String section_id;
	private String line_id;
	private String position_id;
	private String operator_id;

	private String sorc_no;
	private String model_name;
	private String serial_no;
	private String line_name;
	private String process_code;
	private String position_name;
	private String operator_name;

	private String model_id;
	private String section_name;
	private Date occur_time_from;
	private Date occur_time_to;
	private String reciever_id;
	private String resolver_id;
	private String resolver_name;
	private Date resolve_time;

	// List<AlarmMesssageSendationEntity> sendation = new ArrayList

	public String getAlarm_messsage_id() {
		return alarm_messsage_id;
	}
	public void setAlarm_messsage_id(String alarm_messsage_id) {
		this.alarm_messsage_id = alarm_messsage_id;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Date getOccur_time() {
		return occur_time;
	}
	public void setOccur_time(Date occur_time) {
		this.occur_time = occur_time;
	}
	public String getMaterial_id() {
		return material_id;
	}
	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}
	public Integer getReason() {
		return reason;
	}
	public void setReason(Integer reason) {
		this.reason = reason;
	}
	public String getSection_id() {
		return section_id;
	}
	public void setSection_id(String section_id) {
		this.section_id = section_id;
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
	public String getOperator_id() {
		return operator_id;
	}
	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
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
	public String getSorc_no() {
		return sorc_no;
	}
	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
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
	public Date getResolve_time() {
		return resolve_time;
	}
	public void setResolve_time(Date resolve_time) {
		this.resolve_time = resolve_time;
	}
	public Date getOccur_time_from() {
		return occur_time_from;
	}
	public void setOccur_time_from(Date occur_time_from) {
		this.occur_time_from = occur_time_from;
	}
	public Date getOccur_time_to() {
		return occur_time_to;
	}
	public void setOccur_time_to(Date occur_time_to) {
		this.occur_time_to = occur_time_to;
	}
	public String getReciever_id() {
		return reciever_id;
	}
	public void setReciever_id(String reciever_id) {
		this.reciever_id = reciever_id;
	}
	public String getPosition_name() {
		return position_name;
	}
	public void setPosition_name(String position_name) {
		this.position_name = position_name;
	}
}
