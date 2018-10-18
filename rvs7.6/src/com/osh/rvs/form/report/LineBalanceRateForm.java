package com.osh.rvs.form.report;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class LineBalanceRateForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1059123994464981009L;

	@BeanField(title = "机种ID", name = "category_id", type = FieldType.String, length = 11)
	private String category_id;

	private String category_name;

	@BeanField(title = "型号", name = "model_id", type = FieldType.String, length = 11)
	private String model_id;

	private String model_name;

	@BeanField(title = "等级", name = "level", type = FieldType.Integer, length = 1)
	private String level;

	@BeanField(title = "课室", name = "section_id", type = FieldType.String, length = 11)
	private String section_id;

	private String section_name;

	@BeanField(title = "工程ID", name = "line_id", type = FieldType.String, length = 11)
	private String line_id;

	private String line_name;

	@BeanField(title = "是否包含返工", name = "rework", type = FieldType.Integer, length = 1)
	private String rework;

	@BeanField(title = "包含单元化工位", name = "cell", type = FieldType.Integer, length = 1)
	private String cell;

	@BeanField(title = "作业开始时间", name = "finish_time_start", type = FieldType.Date)
	private String finish_time_start;

	@BeanField(title = "作业结束时间", name = "finish_time_end", type = FieldType.Date)
	private String finish_time_end;

	private String balance_rate;

	private String svg;
	
	private String process_codes;

	@BeanField(title = "分线", name = "px", type = FieldType.Integer, length = 1)
	private String px;

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

	public String getRework() {
		return rework;
	}

	public void setRework(String rework) {
		this.rework = rework;
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

	public String getBalance_rate() {
		return balance_rate;
	}

	public void setBalance_rate(String balance_rate) {
		this.balance_rate = balance_rate;
	}

	public String getSvg() {
		return svg;
	}

	public void setSvg(String svg) {
		this.svg = svg;
	}

	public String getPx() {
		return px;
	}

	public void setPx(String px) {
		this.px = px;
	}

	public String getProcess_codes() {
		return process_codes;
	}

	public void setProcess_codes(String process_codes) {
		this.process_codes = process_codes;
	}

	public String getCell() {
		return cell;
	}

	public void setCell(String cell) {
		this.cell = cell;
	}

}
