package com.osh.rvs.form.report;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class WaittingTimeReportForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3533210486949429729L;

	@BeanField(title = "机种ID", name = "category_id", type = FieldType.String, length = 11)
	private String category_id;

	@BeanField(title = "机种名称", name = "category_name", type = FieldType.String, length = 50)
	private String category_name;

	@BeanField(title = "修理单号", name = "omr_notifi_no", type = FieldType.String, length = 14)
	private String omr_notifi_no;

	@BeanField(title = "机身号", name = "serial_no", type = FieldType.String, length = 12)
	private String serial_no;

	@BeanField(title = "型号ID", name = "model_id", type = FieldType.String, length = 11)
	private String model_id;

	@BeanField(title = "型号名称", name = "model_name", type = FieldType.String, length = 50)
	private String model_name;

	@BeanField(title = "等级", name = "level", type = FieldType.Integer, length = 1)
	private String level;

	@BeanField(title = "课室ID", name = "section_id", type = FieldType.String, length = 11)
	private String section_id;

	@BeanField(title = "课室名称", name = "section_name", type = FieldType.String, length = 11)
	private String section_name;

	@BeanField(title = "零件BO", name = "bo_flg", type = FieldType.Integer, length = 1)
	private String bo_flg;

	@BeanField(title = "加急", name = "scheduled_expedited", type = FieldType.Integer, length = 1)
	private String scheduled_expedited;

	@BeanField(title = "返工", name = "rework", type = FieldType.Integer, length = 1)
	private String rework;

	@BeanField(title = "直送", name = "direct_flg", type = FieldType.Integer, length = 1)
	private String direct_flg;

	@BeanField(title = "完成开始时间", name = "outline_time_start", type = FieldType.Date)
	private String outline_time_start;

	@BeanField(title = "完成结束时间", name = "outline_time_end", type = FieldType.Date)
	private String outline_time_end;

	@BeanField(title = "零件订购开始时间", name = "order_date_start", type = FieldType.Date)
	private String order_date_start;

	@BeanField(title = "零件订购结束时间", name = "order_date_end", type = FieldType.Date)
	private String order_date_end;

	@BeanField(title = "零件发放开始时间", name = "arrival_date_start", type = FieldType.Date)
	private String arrival_date_start;

	@BeanField(title = "零件发放结束时间", name = "arrival_date_end", type = FieldType.Date)
	private String arrival_date_end;

	@BeanField(title = "图表", name = "svg1", type = FieldType.String, length = 10000)
	private String svg1;

	@BeanField(title = "图表", name = "svg2", type = FieldType.String, length = 10000)
	private String svg2;

	@BeanField(title = "图表", name = "svg3", type = FieldType.String, length = 10000)
	private String svg3;

	@BeanField(title = "图表", name = "svg4", type = FieldType.String, length = 10000)
	private String svg4;

	@BeanField(title = "分解分线", name = "dec_px", type = FieldType.Integer, length = 1)
	private String dec_px;

	@BeanField(title = "NS分线", name = "ns_px", type = FieldType.Integer, length = 1)
	private String ns_px;

	@BeanField(title = "总组分线", name = "com_px", type = FieldType.Integer, length = 1)
	private String com_px;

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

	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}

	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
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

	public String getBo_flg() {
		return bo_flg;
	}

	public void setBo_flg(String bo_flg) {
		this.bo_flg = bo_flg;
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

	public String getDirect_flg() {
		return direct_flg;
	}

	public void setDirect_flg(String direct_flg) {
		this.direct_flg = direct_flg;
	}

	public String getOutline_time_start() {
		return outline_time_start;
	}

	public void setOutline_time_start(String outline_time_start) {
		this.outline_time_start = outline_time_start;
	}

	public String getOutline_time_end() {
		return outline_time_end;
	}

	public void setOutline_time_end(String outline_time_end) {
		this.outline_time_end = outline_time_end;
	}

	public String getOrder_date_start() {
		return order_date_start;
	}

	public void setOrder_date_start(String order_date_start) {
		this.order_date_start = order_date_start;
	}

	public String getOrder_date_end() {
		return order_date_end;
	}

	public void setOrder_date_end(String order_date_end) {
		this.order_date_end = order_date_end;
	}

	public String getArrival_date_start() {
		return arrival_date_start;
	}

	public void setArrival_date_start(String arrival_date_start) {
		this.arrival_date_start = arrival_date_start;
	}

	public String getArrival_date_end() {
		return arrival_date_end;
	}

	public void setArrival_date_end(String arrival_date_end) {
		this.arrival_date_end = arrival_date_end;
	}

	public String getSvg1() {
		return svg1;
	}

	public void setSvg1(String svg1) {
		this.svg1 = svg1;
	}

	public String getSvg2() {
		return svg2;
	}

	public void setSvg2(String svg2) {
		this.svg2 = svg2;
	}

	public String getSvg3() {
		return svg3;
	}

	public void setSvg3(String svg3) {
		this.svg3 = svg3;
	}

	public String getSvg4() {
		return svg4;
	}

	public void setSvg4(String svg4) {
		this.svg4 = svg4;
	}

	public String getDec_px() {
		return dec_px;
	}

	public void setDec_px(String dec_px) {
		this.dec_px = dec_px;
	}

	public String getNs_px() {
		return ns_px;
	}

	public void setNs_px(String ns_px) {
		this.ns_px = ns_px;
	}

	public String getCom_px() {
		return com_px;
	}

	public void setCom_px(String com_px) {
		this.com_px = com_px;
	}

}
