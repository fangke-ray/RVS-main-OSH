package com.osh.rvs.form.inline;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class MaterialOgzForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3603300290861095806L;

	@BeanField(title = "维修对象ID", name = "material_id", type = FieldType.String, length = 11, primaryKey = true, notNull = true)
	private String material_id;// 维修对象ID

	@BeanField(title = "修理单号", name = "sorc_no", type = FieldType.String, length = 15)
	private String sorc_no;// SORC No.

	@BeanField(title = "SFDC No.", name = "sfdc_no", type = FieldType.String, length = 14)
	private String sfdc_no;// SFDC No.

	@BeanField(title = "ESAS No.", name = "esas_no", type = FieldType.String, length = 7)
	private String esas_no;// ESAS No.

	@BeanField(title = "维修对象型号 ID", name = "model_id", type = FieldType.String, length = 11, notNull = true)
	private String model_id;// 维修对象型号 ID

	@BeanField(title = "维修对象型号名称", name = "model_name", type = FieldType.String)
	private String model_name;// 维修对象型号名称

	@BeanField(title = "机身号", name = "serial_no", type = FieldType.String, length = 8, notNull = true)
	private String serial_no;// 机身号

	@BeanField(title = "委托处", name = "ocm", type = FieldType.Integer, length = 2, notNull = true)
	private String ocm;// 委托处

	@BeanField(title = "OCM等级", name = "ocm_rank", type = FieldType.Integer, length = 1, notNull = true)
	private String ocm_rank;// OCM等级

	@BeanField(title = "等级", name = "level", type = FieldType.Integer, length = 1)
	private String level;// 等级

	@BeanField(title = "客户 ID", name = "customer_id", type = FieldType.String, length = 11, notNull = true)
	private String customer_id;// 客户 ID

	@BeanField(title = "客户名称", name = "customer_name", type = FieldType.String)
	private String customer_name;// 客户名称

	@BeanField(title = "OCM出库日期", name = "ocm_deliver_date", type = FieldType.Date)
	private String ocm_deliver_date;// OCM出库日期

	@BeanField(title = "受理时间", name = "reception_time", type = FieldType.DateTime)
	private String reception_time;// 受理时间

	@BeanField(title = "受理时间开始", name = "reception_time_start", type = FieldType.Date)
	private String reception_time_start;// 受理时间开始

	@BeanField(title = "受理时间结束", name = "reception_time_end", type = FieldType.Date)
	private String reception_time_end;// 受理时间结束

	@BeanField(title = "消毒灭菌时间", name = "sterilization_time", type = FieldType.DateTime)
	private String sterilization_time;// 消毒灭菌时间

	@BeanField(title = "报价完成时间", name = "quotation_complete_time", type = FieldType.DateTime)
	private String quotation_complete_time;// 报价完成时间

	@BeanField(title = "客户同意日", name = "agreed_date", type = FieldType.Date)
	private String agreed_date;// 客户同意日

	@BeanField(title = "客户同意日开始", name = "agreed_date_start", type = FieldType.Date)
	private String agreed_date_start;// 客户同意日开始

	@BeanField(title = "客户同意日结束", name = "agreed_date_end", type = FieldType.Date)
	private String agreed_date_end;// 客户同意日结束

	@BeanField(title = "投线时间", name = "inline_time", type = FieldType.DateTime)
	private String inline_time;// 投线时间

	@BeanField(title = "分解完成时间", name = "dec_complete_time", type = FieldType.DateTime)
	private String dec_complete_time;// 分解完成时间

	@BeanField(title = "NS完成时间", name = "ns_complete_time", type = FieldType.DateTime)
	private String ns_complete_time;// NS完成时间

	@BeanField(title = "总组完成时间", name = "com_complete_time", type = FieldType.DateTime)
	private String com_complete_time;// 总组完成时间

	@BeanField(title = "(品保)完成时间", name = "outline_time", type = FieldType.DateTime)
	private String outline_time;// (品保)完成时间

	@BeanField(title = "(品保)完成时间开始", name = "outline_time_start", type = FieldType.Date)
	private String outline_time_start;// (品保)完成时间开始

	@BeanField(title = "(品保)完成时间结束", name = "outline_time_end", type = FieldType.Date)
	private String outline_time_end;// (品保)完成时间结束

	@BeanField(title = "包装出货时间", name = "shipping_time", type = FieldType.DateTime)
	private String shipping_time;// 包装出货时间

	@BeanField(title = "物流配送日", name = "ocm_shipping_date", type = FieldType.Date)
	private String ocm_shipping_date;// 物流配送日

	@BeanField(title = "返修标记", name = "service_repair_flg", type = FieldType.Integer,notNull=true)
	private String service_repair_flg;// 返修标记

	@BeanField(title = "直送", name = "direct_flg", type = FieldType.Integer,notNull=true)
	private String direct_flg;// 直送

	@BeanField(title = "返还", name = "break_back_flg", type = FieldType.Integer,notNull=true)
	private String break_back_flg;// 返还

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getSorc_no() {
		return sorc_no;
	}

	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
	}

	public String getSfdc_no() {
		return sfdc_no;
	}

	public void setSfdc_no(String sfdc_no) {
		this.sfdc_no = sfdc_no;
	}

	public String getEsas_no() {
		return esas_no;
	}

	public void setEsas_no(String esas_no) {
		this.esas_no = esas_no;
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

	public String getSerial_no() {
		return serial_no;
	}

	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}

	public String getOcm() {
		return ocm;
	}

	public void setOcm(String ocm) {
		this.ocm = ocm;
	}

	public String getOcm_rank() {
		return ocm_rank;
	}

	public void setOcm_rank(String ocm_rank) {
		this.ocm_rank = ocm_rank;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public String getOcm_deliver_date() {
		return ocm_deliver_date;
	}

	public void setOcm_deliver_date(String ocm_deliver_date) {
		this.ocm_deliver_date = ocm_deliver_date;
	}

	public String getReception_time() {
		return reception_time;
	}

	public void setReception_time(String reception_time) {
		this.reception_time = reception_time;
	}

	public String getSterilization_time() {
		return sterilization_time;
	}

	public void setSterilization_time(String sterilization_time) {
		this.sterilization_time = sterilization_time;
	}

	public String getQuotation_complete_time() {
		return quotation_complete_time;
	}

	public void setQuotation_complete_time(String quotation_complete_time) {
		this.quotation_complete_time = quotation_complete_time;
	}

	public String getAgreed_date() {
		return agreed_date;
	}

	public void setAgreed_date(String agreed_date) {
		this.agreed_date = agreed_date;
	}

	public String getInline_time() {
		return inline_time;
	}

	public void setInline_time(String inline_time) {
		this.inline_time = inline_time;
	}

	public String getDec_complete_time() {
		return dec_complete_time;
	}

	public void setDec_complete_time(String dec_complete_time) {
		this.dec_complete_time = dec_complete_time;
	}

	public String getNs_complete_time() {
		return ns_complete_time;
	}

	public void setNs_complete_time(String ns_complete_time) {
		this.ns_complete_time = ns_complete_time;
	}

	public String getCom_complete_time() {
		return com_complete_time;
	}

	public void setCom_complete_time(String com_complete_time) {
		this.com_complete_time = com_complete_time;
	}

	public String getOutline_time() {
		return outline_time;
	}

	public void setOutline_time(String outline_time) {
		this.outline_time = outline_time;
	}

	public String getShipping_time() {
		return shipping_time;
	}

	public void setShipping_time(String shipping_time) {
		this.shipping_time = shipping_time;
	}

	public String getOcm_shipping_date() {
		return ocm_shipping_date;
	}

	public void setOcm_shipping_date(String ocm_shipping_date) {
		this.ocm_shipping_date = ocm_shipping_date;
	}

	public String getService_repair_flg() {
		return service_repair_flg;
	}

	public void setService_repair_flg(String service_repair_flg) {
		this.service_repair_flg = service_repair_flg;
	}

	public String getDirect_flg() {
		return direct_flg;
	}

	public void setDirect_flg(String direct_flg) {
		this.direct_flg = direct_flg;
	}

	public String getBreak_back_flg() {
		return break_back_flg;
	}

	public void setBreak_back_flg(String break_back_flg) {
		this.break_back_flg = break_back_flg;
	}

	public String getReception_time_start() {
		return reception_time_start;
	}

	public void setReception_time_start(String reception_time_start) {
		this.reception_time_start = reception_time_start;
	}

	public String getReception_time_end() {
		return reception_time_end;
	}

	public void setReception_time_end(String reception_time_end) {
		this.reception_time_end = reception_time_end;
	}

	public String getAgreed_date_start() {
		return agreed_date_start;
	}

	public void setAgreed_date_start(String agreed_date_start) {
		this.agreed_date_start = agreed_date_start;
	}

	public String getAgreed_date_end() {
		return agreed_date_end;
	}

	public void setAgreed_date_end(String agreed_date_end) {
		this.agreed_date_end = agreed_date_end;
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

}
