package com.osh.rvs.form.inline;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class SoloProductionFeatureForm extends ActionForm {

	private static final long serialVersionUID = -2729276757795884055L;

	private String position_id;
	private String serial_no;
	private String model_id;
	private String model_name;
	@BeanField(title="判定日期", name="judge_date", type=FieldType.Date)
	private String judge_date;
	private String operator_id;
	private String operator_name;
	private String job_no;
	private String finish_time;
	@BeanField(title="完成日", name="finish_time_from", type=FieldType.Date)
	private String finish_time_from;
	@BeanField(title="完成日", name="finish_time_to", type=FieldType.Date)
	private String finish_time_to;
	private String used;

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
	public String getOperator_id() {
		return operator_id;
	}
	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}
	public String getFinish_time() {
		return finish_time;
	}
	public void setFinish_time(String finish_time) {
		this.finish_time = finish_time;
	}
	public String getUsed() {
		return used;
	}
	public void setUsed(String used) {
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
	public String getFinish_time_from() {
		return finish_time_from;
	}
	public void setFinish_time_from(String finish_time_from) {
		this.finish_time_from = finish_time_from;
	}
	public String getFinish_time_to() {
		return finish_time_to;
	}
	public void setFinish_time_to(String finish_time_to) {
		this.finish_time_to = finish_time_to;
	}
	public String getJudge_date() {
		return judge_date;
	}
	public void setJudge_date(String judge_date) {
		this.judge_date = judge_date;
	}
}
