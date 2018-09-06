package com.osh.rvs.form.report;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class ProductivityForm extends ActionForm implements Serializable {

	private static final long serialVersionUID = -2068108313908645382L;

	@BeanField(title = "作业开始时间", name = "start_date", type = FieldType.String)
	private String start_date;

	@BeanField(title = "作业结束时间", name = "end_date", type = FieldType.String)
	private String end_date;

	@BeanField(title = "完成日期", name = "outline_date", type = FieldType.String)
	private String outline_date;

	@BeanField(title = "有效出勤数", name = "avalible_productive", type = FieldType.Double)
	private String avalible_productive;

	private String svg;

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getOutline_date() {
		return outline_date;
	}

	public void setOutline_date(String outline_date) {
		this.outline_date = outline_date;
	}

	public String getAvalible_productive() {
		return avalible_productive;
	}

	public void setAvalible_productive(String avalible_productive) {
		this.avalible_productive = avalible_productive;
	}

	public String getSvg() {
		return svg;
	}

	public void setSvg(String svg) {
		this.svg = svg;
	}
}
