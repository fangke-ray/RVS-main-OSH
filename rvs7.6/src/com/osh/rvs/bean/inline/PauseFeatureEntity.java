package com.osh.rvs.bean.inline;

import java.io.Serializable;
import java.util.Date;


public class PauseFeatureEntity implements Serializable {

	private static final long serialVersionUID = -5763719762909130972L;

	/** 担当人 ID */
	private String operator_id;
	private String operator_name;
	/** 暂停终止者 ID */
	private String finisher_id;
	private String finisher_name;
	/** 暂停原因 */
	private Integer reason;
	/** 产生警报记录ID */
	private String alarm_messsage_id;
	/** 维修对象ID */
	private String material_id;
	/** 课室ID */
	private String section_id;
	private String section_name;
	/** 工位ID */
	private String position_id;
	private String process_code;
	/** 备注信息	 */
	private String comments;
	/** 暂停开始时间 */
	private Date pause_start_time;
	/** 暂停结束时间 */
	private Date pause_finish_time;
	/** 先端预制头序号 */
	private String snout_serial_no;

	public String getOperator_id() {
		return operator_id;
	}
	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}
	public String getOperator_name() {
		return operator_name;
	}
	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}
	public String getFinisher_id() {
		return finisher_id;
	}
	public void setFinisher_id(String finisher_id) {
		this.finisher_id = finisher_id;
	}
	public String getFinisher_name() {
		return finisher_name;
	}
	public void setFinisher_name(String finisher_name) {
		this.finisher_name = finisher_name;
	}
	public Integer getReason() {
		return reason;
	}
	public void setReason(Integer reason) {
		this.reason = reason;
	}
	public String getAlarm_messsage_id() {
		return alarm_messsage_id;
	}
	public void setAlarm_messsage_id(String alarm_messsage_id) {
		this.alarm_messsage_id = alarm_messsage_id;
	}
	public String getMaterial_id() {
		return material_id;
	}
	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}
	public String getSection_id() {
		return section_id;
	}
	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}
	public String getSection_name() {
		return section_name;
	}
	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}
	public String getPosition_id() {
		return position_id;
	}
	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public Date getPause_start_time() {
		return pause_start_time;
	}
	public void setPause_start_time(Date pause_start_time) {
		this.pause_start_time = pause_start_time;
	}
	public Date getPause_finish_time() {
		return pause_finish_time;
	}
	public void setPause_finish_time(Date pause_finish_time) {
		this.pause_finish_time = pause_finish_time;
	}
	public String getProcess_code() {
		return process_code;
	}
	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}
	/**
	 * @return the snout_serial_no
	 */
	public String getSnout_serial_no() {
		return snout_serial_no;
	}
	/**
	 * @param snout_serial_no the snout_serial_no to set
	 */
	public void setSnout_serial_no(String snout_serial_no) {
		this.snout_serial_no = snout_serial_no;
	}
}
