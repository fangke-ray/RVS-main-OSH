package com.osh.rvs.form.report;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class WorkTimeAnalysisForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8949534032102148L;

	@BeanField(title = "机种ID", name = "category_id", type = FieldType.String, length = 11)
	private String category_id;

	@BeanField(title = "机种名称", name = "category_name", type = FieldType.String, length = 50)
	private String category_name;

	@BeanField(title = "型号ID", name = "model_id", type = FieldType.String, length = 11)
	private String model_id;

	@BeanField(title = "型号名称", name = "model_name", type = FieldType.String, length = 50)
	private String model_name;

	@BeanField(title = "等级", name = "level", type = FieldType.Integer, length = 1)
	private String level;

	@BeanField(title = "课室", name = "section_id", type = FieldType.String, length = 11)
	private String section_id;

	@BeanField(title = "工位ID", name = "position_id", type = FieldType.String, length = 11)
	private String position_id;

	@BeanField(title = "工位代码", name = "process_code", type = FieldType.String, length = 3)
	private String process_code;

	@BeanField(title = "加急", name = "scheduled_expedited", type = FieldType.Integer, length = 1)
	private String scheduled_expedited;

	@BeanField(title = "是否包含返工", name = "rework", type = FieldType.Integer, length = 1)
	private String rework;

	@BeanField(title = "人员ID", name = "operator_id", type = FieldType.String, length = 11)
	private String operator_id;

	@BeanField(title = "作业开始时间", name = "finish_time_start", type = FieldType.Date)
	private String finish_time_start;

	@BeanField(title = "作业结束时间", name = "finish_time_end", type = FieldType.Date)
	private String finish_time_end;

	@BeanField(title = "图表", name = "svg", type = FieldType.String, length = 10000)
	private String svg;

	@BeanField(title = "操作人员", name = "operator_name", type = FieldType.String, length = 8)
	private String operator_name;

	@BeanField(title = "科室名称", name = "section_name", type = FieldType.String, length = 11)
	private String section_name;

	@BeanField(title = "是否包含异常", name = "abnormal", type = FieldType.Integer, length = 1)
	private String abnormal;

	@BeanField(title = "工程", name = "line_id", type = FieldType.String, length = 11)
	private String line_id;

	@BeanField(title = "工程名称", name = "line_name", type = FieldType.String, length = 15)
	private String line_name;

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
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

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getScheduled_expedited() {
		return scheduled_expedited;
	}

	public void setScheduled_expedited(String scheduled_expedited) {
		this.scheduled_expedited = scheduled_expedited;
	}

	public String getRework() {
		return rework;
	}

	public void setRework(String rework) {
		this.rework = rework;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getFinish_time_start() {
		return finish_time_start;
	}

	public void setFinish_time_start(String finish_time_start) {
		this.finish_time_start = finish_time_start;
	}

	public String getFinish_time_end() {
		return finish_time_end;
	}

	public void setFinish_time_end(String finish_time_end) {
		this.finish_time_end = finish_time_end;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getSvg() {
		return svg;
	}

	public void setSvg(String svg) {
		this.svg = svg;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

	public String getSection_name() {
		return section_name;
	}

	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}

	public String getAbnormal() {
		return abnormal;
	}

	public void setAbnormal(String abnormal) {
		this.abnormal = abnormal;
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

}
