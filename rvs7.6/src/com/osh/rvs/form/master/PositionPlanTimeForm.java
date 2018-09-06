package com.osh.rvs.form.master;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class PositionPlanTimeForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2668103122253634496L;

	@BeanField(title = "维修对象ID", name = "material_id", type = FieldType.String, length = 11)
	private String material_id;

	@BeanField(title = "工程ID", name = "line_id", type = FieldType.String, length = 11)
	private String line_id;

	@BeanField(title = "工位ID", name = "position_id", type = FieldType.String, length = 11)
	private String position_id;

	@BeanField(title = "计划开始时间", name = "plan_start_time", type = FieldType.DateTime)
	private String plan_start_time;

	@BeanField(title = "计划结束时间", name = "plan_end_time", type = FieldType.DateTime)
	private String plan_end_time;

	@BeanField(title = "顺序", name = "seq", type = FieldType.Integer, length = 2)
	private String seq;

	@BeanField(title = "进度代码", name = "process_code", type = FieldType.String, length = 3)
	private String process_code;

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
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

	public String getPlan_start_time() {
		return plan_start_time;
	}

	public void setPlan_start_time(String plan_start_time) {
		this.plan_start_time = plan_start_time;
	}

	public String getPlan_end_time() {
		return plan_end_time;
	}

	public void setPlan_end_time(String plan_end_time) {
		this.plan_end_time = plan_end_time;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

}
