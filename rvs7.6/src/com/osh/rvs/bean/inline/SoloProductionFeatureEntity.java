package com.osh.rvs.bean.inline;

import java.io.Serializable;
import java.util.Date;

public class SoloProductionFeatureEntity implements Serializable{

	private static final long serialVersionUID = 7204433595748489202L;

	private String position_id;
	private String serial_no;
	private String model_id;
	private String model_name;
	private Date judge_date;
	private Integer pace;
	private String operator_id;
	private String operator_name;
	private String job_no;
	private Integer operate_result;
	private Date action_time;
	private Integer action_time_null;
	private Date finish_time;
	private Integer finish_time_null;
	private String pcs_inputs;
	private String pcs_comments;
	private Integer used;
	private Integer use_seconds;
	private String section_id;

	public String getPosition_id() {
		return position_id;
	}
	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}
	public String getSerial_no() {
		return serial_no;
	}
	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}
	public String getModel_id() {
		return model_id;
	}
	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}
	public String getModel_name() {
		return model_name;
	}
	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}
	public Integer getPace() {
		return pace;
	}
	public void setPace(Integer pace) {
		this.pace = pace;
	}
	public String getOperator_id() {
		return operator_id;
	}
	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}
	public Integer getOperate_result() {
		return operate_result;
	}
	public void setOperate_result(Integer operate_result) {
		this.operate_result = operate_result;
	}
	public Date getAction_time() {
		return action_time;
	}
	public void setAction_time(Date action_time) {
		this.action_time = action_time;
	}
	public Date getFinish_time() {
		return finish_time;
	}
	public void setFinish_time(Date finish_time) {
		this.finish_time = finish_time;
	}
	public String getPcs_inputs() {
		return pcs_inputs;
	}
	public void setPcs_inputs(String pcs_inputs) {
		this.pcs_inputs = pcs_inputs;
	}
	public String getPcs_comments() {
		return pcs_comments;
	}
	public void setPcs_comments(String pcs_comments) {
		this.pcs_comments = pcs_comments;
	}
	public Integer getUsed() {
		return used;
	}
	public void setUsed(Integer used) {
		this.used = used;
	}
	public String getOperator_name() {
		return operator_name;
	}
	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}
	public String getJob_no() {
		return job_no;
	}
	public void setJob_no(String job_no) {
		this.job_no = job_no;
	}
	public Integer getAction_time_null() {
		return action_time_null;
	}
	public void setAction_time_null(Integer action_time_null) {
		this.action_time_null = action_time_null;
	}
	public Integer getFinish_time_null() {
		return finish_time_null;
	}
	public void setFinish_time_null(Integer finish_time_null) {
		this.finish_time_null = finish_time_null;
	}
	/**
	 * @return the use_seconds
	 */
	public Integer getUse_seconds() {
		return use_seconds;
	}
	/**
	 * @param use_seconds the use_seconds to set
	 */
	public void setUse_seconds(Integer use_seconds) {
		this.use_seconds = use_seconds;
	}
	public Date getJudge_date() {
		return judge_date;
	}
	public void setJudge_date(Date judge_date) {
		this.judge_date = judge_date;
	}
	public String getSection_id() {
		return section_id;
	}
	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

}
