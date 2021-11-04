package com.osh.rvs.form.partial;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class MaterialPartInstructForm extends ActionForm {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -3428271266471270869L;

	@BeanField(title = "价格", name = "price", type = FieldType.UDouble)
	private String price;

	@BeanField(title = "维修对象ID", name = "material_id", type = FieldType.String, length = 11, notNull = true)
	private String material_id;

	@BeanField(title = "零件订购日期", name = "order_date", type = FieldType.Date)
	private String order_date;

	@BeanField(title = "等级", name = "level", type = FieldType.Integer)
	private String level;

	@BeanField(title = "修理单号", name = "omr_notifi_no", type = FieldType.String)
	private String omr_notifi_no;

	@BeanField(title = "维修对象型号", name = "model_name", type = FieldType.String)
	private String model_name;

	private String model_id;

	@BeanField(title = "机身号", name = "serial_no", type = FieldType.String)
	private String serial_no;

	// inline_adjust
	private String inline_adjust;
	// 不良追加
	private String nongood_append;

	private String range;// 检索范围

	@BeanField(title = "订购时间超出状态", name = "over_state", type = FieldType.Integer, length = 1)
	private String over_state;// 订购时间超出状态

	@BeanField(title = "流水线分类", name = "fix_type", type = FieldType.Integer, length = 1)
	private String fix_type;// 流水线分类

	@BeanField(title = "订购时间", name = "order_time", type = FieldType.DateTime)
	private String order_time;

	@BeanField(title = "进展", name = "procedure")
	private String procedure;

	@BeanField(title = "维修课室ID", name = "section_id", type = FieldType.String, length = 11)
	private String section_id;

	private String quote_job_no;
	private String inline_job_no;

	@BeanField(title = "同意日", name = "agreed_date", type = FieldType.Date)
	private String agreed_date;

	private String upd_action;

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getOrder_date() {
		return order_date;
	}

	public void setOrder_date(String order_date) {
		this.order_date = order_date;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getSerial_no() {
		return serial_no;
	}

	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public String getOver_state() {
		return over_state;
	}

	public void setOver_state(String over_state) {
		this.over_state = over_state;
	}

	public String getFix_type() {
		return fix_type;
	}

	public void setFix_type(String fix_type) {
		this.fix_type = fix_type;
	}

	public String getOrder_time() {
		return order_time;
	}

	public void setOrder_time(String order_time) {
		this.order_time = order_time;
	}

	public String getProcedure() {
		return procedure;
	}

	public void setProcedure(String procedure) {
		this.procedure = procedure;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getNongood_append() {
		return nongood_append;
	}

	public void setNongood_append(String nongood_append) {
		this.nongood_append = nongood_append;
	}

	public String getInline_adjust() {
		return inline_adjust;
	}

	public void setInline_adjust(String inline_adjust) {
		this.inline_adjust = inline_adjust;
	}

	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}

	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public String getQuote_job_no() {
		return quote_job_no;
	}

	public void setQuote_job_no(String quote_job_no) {
		this.quote_job_no = quote_job_no;
	}

	public String getInline_job_no() {
		return inline_job_no;
	}

	public void setInline_job_no(String inline_job_no) {
		this.inline_job_no = inline_job_no;
	}

	public String getAgreed_date() {
		return agreed_date;
	}

	public void setAgreed_date(String agreed_date) {
		this.agreed_date = agreed_date;
	}

	public String getUpd_action() {
		return upd_action;
	}
	public void setUpd_action(String upd_action) {
		this.upd_action = upd_action;
	}
}
