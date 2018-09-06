package com.osh.rvs.form.report;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 代工时间统计
 * 
 * @author liuxb
 * 
 */
public class FoundryForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5730069909822808863L;

	@BeanField(title = "课室", name = "section_id", type = FieldType.String, length = 11, notNull = true)
	private String section_id;

	@BeanField(title = "课室名称", name = "section_name", type = FieldType.String, length = 11)
	private String section_name;

	@BeanField(title = "工程", name = "line_id", type = FieldType.String, length = 11, notNull = true)
	private String line_id;

	@BeanField(title = "工程名称", name = "line_name", type = FieldType.String, length = 15)
	private String line_name;

	@BeanField(title = "作业开始时间", name = "finish_time_start", type = FieldType.Date, notNull = true)
	private String finish_time_start;

	@BeanField(title = "作业结束时间", name = "finish_time_end", type = FieldType.Date, notNull = true)
	private String finish_time_end;

	@BeanField(title = "工位代码", name = "finish_time_end", type = FieldType.String, length = 3)
	private String process_code;

	@BeanField(title = "图表", name = "svg", type = FieldType.String, length = 100000)
	private String svg;

	@BeanField(title = "图表", name = "svg2", type = FieldType.String, length = 100000)
	private String svg2;

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

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getSvg() {
		return svg;
	}

	public void setSvg(String svg) {
		this.svg = svg;
	}

	public String getSvg2() {
		return svg2;
	}

	public void setSvg2(String svg2) {
		this.svg2 = svg2;
	}

}
