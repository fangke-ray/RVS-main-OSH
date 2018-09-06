package com.osh.rvs.form.report;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 作业时间分析
 * 
 * @author gonglm
 * 
 */
public class WorkDurationForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2289690371853916042L;

	@BeanField(title = "课室", name = "section_id", type = FieldType.String, length = 11, notNull = true)
	private String section_id;

	@BeanField(title = "课室名称", name = "section_name", type = FieldType.String, length = 11)
	private String section_name;

	@BeanField(title = "工程", name = "line_id", type = FieldType.String, length = 11, notNull = true)
	private String line_id;

	@BeanField(title = "工程名称", name = "line_name", type = FieldType.String, length = 15)
	private String line_name;

	@BeanField(title = "作业日期", name = "action_time", type = FieldType.Date, notNull = true)
	private String action_date;

	@BeanField(title = "图表", name = "svg", type = FieldType.String, length = 100000)
	private String svg;

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

	public String getSvg() {
		return svg;
	}

	public void setSvg(String svg) {
		this.svg = svg;
	}

	public String getAction_date() {
		return action_date;
	}

	public void setAction_date(String action_date) {
		this.action_date = action_date;
	}
}
