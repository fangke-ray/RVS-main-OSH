package com.osh.rvs.form.support;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 
 * @Description 物品申购明细
 * @author liuxb
 * @date 2021-12-2 下午1:35:37
 */
public class SuppliesDetailForm extends ActionForm implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -277625488068007054L;

	/**
	 * 物品申购明细 KEY
	 */
	@BeanField(name = "supplies_key", title = "物品申购明细 KEY", type = FieldType.String, length = 11, primaryKey = true)
	private String supplies_key;

	/**
	 * 品名
	 */
	@BeanField(name = "product_name", title = "品名", type = FieldType.String, length = 64, notNull = true)
	private String product_name;

	/**
	 * 规格
	 */
	@BeanField(name = "model_name", title = "规格", type = FieldType.String, length = 32)
	private String model_name;

	/**
	 * 预定单价
	 */
	@BeanField(name = "unit_price", title = "预定单价", type = FieldType.UDouble, length = 6, scale = 2)
	private String unit_price;

	/**
	 * 单位
	 */
	@BeanField(name = "unit_text", title = "单位", type = FieldType.String, length = 3)
	private String unit_text;

	/**
	 * 数量
	 */
	@BeanField(name = "quantity", title = "数量", type = FieldType.Integer, length = 2, notNull = true)
	private String quantity;

	/**
	 * 申请课室ID
	 */
	@BeanField(name = "section_id", title = "申请课室ID", type = FieldType.String, length = 11, notNull = true)
	private String section_id;

	/**
	 * 申购人员ID
	 */
	@BeanField(name = "applicator_id", title = "申购人员ID", type = FieldType.String, length = 11, notNull = true)
	private String applicator_id;

	/**
	 * 用途
	 */
	@BeanField(name = "nesssary_reason", title = "用途", type = FieldType.String, length = 256)
	private String nesssary_reason;

	/**
	 * 申请日期
	 */
	@BeanField(name = "applicate_date", title = "申请日期", type = FieldType.Date)
	private String applicate_date;

	/**
	 * 上级确认者ID
	 */
	@BeanField(name = "confirmer_id", title = "上级确认者ID", type = FieldType.String, length = 11)
	private String confirmer_id;

	/**
	 * 物品申购单 Key
	 */
	@BeanField(name = "order_key", title = "物品申购单 Key", type = FieldType.String, length = 11)
	private String order_key;

	/**
	 * 供应商/采购渠道
	 */
	@BeanField(name = "supplier", title = "供应商/采购渠道", type = FieldType.String, length = 64)
	private String supplier;

	/**
	 * 预计到货日期
	 */
	@BeanField(name = "scheduled_date", title = "预计到货日期", type = FieldType.Date)
	private String scheduled_date;

	/**
	 * 收货日期
	 */
	@BeanField(name = "recept_date", title = "收货日期", type = FieldType.Date)
	private String recept_date;

	/**
	 * 预算月
	 */
	@BeanField(name = "budget_month", title = "预算月", type = FieldType.String, length = 6)
	private String budget_month;

	/**
	 * 验收日期
	 */
	@BeanField(name = "inline_recept_date", title = "验收日期", type = FieldType.Date)
	private String inline_recept_date;

	/**
	 * 备注
	 */
	@BeanField(name = "comments", title = "备注", type = FieldType.String, length = 256)
	private String comments;

	/**
	 * 发票号
	 */
	@BeanField(name = "invoice_no", title = "发票号", type = FieldType.String, length = 8)
	private String invoice_no;

	/**
	 * 申请课室
	 */
	@BeanField(name = "section_name", title = "申请课室", type = FieldType.String, length = 11)
	private String section_name;

	/**
	 * 申购人员
	 */
	@BeanField(name = "applicator_name", title = "申购人员", type = FieldType.String, length = 8)
	private String applicator_name;

	/**
	 * 上级确认者
	 */
	@BeanField(name = "confirmer_name", title = "上级确认者", type = FieldType.String, length = 8)
	private String confirmer_name;

	/**
	 * 申购单号
	 */
	@BeanField(name = "order_no", title = "申购单号", type = FieldType.String, length = 13)
	private String order_no;

	/**
	 * 申请开始日期
	 */
	@BeanField(name = "applicate_date_start", title = "申请开始日期", type = FieldType.Date)
	private String applicate_date_start;

	/**
	 * 申请结束日期
	 */
	@BeanField(name = "applicate_date_end", title = "申请结束日期", type = FieldType.Date)
	private String applicate_date_end;

	/**
	 * 预计到货开始日期
	 */
	@BeanField(name = "scheduled_date_start", title = "预计到货开始日期", type = FieldType.Date)
	private String scheduled_date_start;

	/**
	 * 预计到货结束日期
	 */
	@BeanField(name = "scheduled_date_end", title = "预计到货结束日期", type = FieldType.Date)
	private String scheduled_date_end;

	/**
	 * 收货开始日期
	 */
	@BeanField(name = "recept_date_start", title = "收货开始日期", type = FieldType.Date)
	private String recept_date_start;

	/**
	 * 收货结束日期
	 */
	@BeanField(name = "recept_date_end", title = "收货结束日期", type = FieldType.Date)
	private String recept_date_end;

	/**
	 * 验收开始日期
	 */
	@BeanField(name = "inline_recept_date_start", title = "验收开始日期", type = FieldType.Date)
	private String inline_recept_date_start;

	/**
	 * 验收结束日期
	 */
	@BeanField(name = "inline_recept_date_end", title = "验收结束日期", type = FieldType.Date)
	private String inline_recept_date_end;
	
	/**
	 * 课室全称
	 */
	@BeanField(name = "section_full_name", title = "课室全称", type = FieldType.String, length = 45)
	private String section_full_name;

	// 确认标记
	private String confirm_flg;
	
	private String isLiner;

	public String getSupplies_key() {
		return supplies_key;
	}

	public void setSupplies_key(String supplies_key) {
		this.supplies_key = supplies_key;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getUnit_price() {
		return unit_price;
	}

	public void setUnit_price(String unit_price) {
		this.unit_price = unit_price;
	}

	public String getUnit_text() {
		return unit_text;
	}

	public void setUnit_text(String unit_text) {
		this.unit_text = unit_text;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getApplicator_id() {
		return applicator_id;
	}

	public void setApplicator_id(String applicator_id) {
		this.applicator_id = applicator_id;
	}

	public String getNesssary_reason() {
		return nesssary_reason;
	}

	public void setNesssary_reason(String nesssary_reason) {
		this.nesssary_reason = nesssary_reason;
	}

	public String getApplicate_date() {
		return applicate_date;
	}

	public void setApplicate_date(String applicate_date) {
		this.applicate_date = applicate_date;
	}

	public String getConfirmer_id() {
		return confirmer_id;
	}

	public void setConfirmer_id(String confirmer_id) {
		this.confirmer_id = confirmer_id;
	}

	public String getOrder_key() {
		return order_key;
	}

	public void setOrder_key(String order_key) {
		this.order_key = order_key;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getScheduled_date() {
		return scheduled_date;
	}

	public void setScheduled_date(String scheduled_date) {
		this.scheduled_date = scheduled_date;
	}

	public String getRecept_date() {
		return recept_date;
	}

	public void setRecept_date(String recept_date) {
		this.recept_date = recept_date;
	}

	public String getBudget_month() {
		return budget_month;
	}

	public void setBudget_month(String budget_month) {
		this.budget_month = budget_month;
	}

	public String getInline_recept_date() {
		return inline_recept_date;
	}

	public void setInline_recept_date(String inline_recept_date) {
		this.inline_recept_date = inline_recept_date;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getInvoice_no() {
		return invoice_no;
	}

	public void setInvoice_no(String invoice_no) {
		this.invoice_no = invoice_no;
	}

	public String getSection_name() {
		return section_name;
	}

	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}

	public String getApplicator_name() {
		return applicator_name;
	}

	public void setApplicator_name(String applicator_name) {
		this.applicator_name = applicator_name;
	}

	public String getConfirmer_name() {
		return confirmer_name;
	}

	public void setConfirmer_name(String confirmer_name) {
		this.confirmer_name = confirmer_name;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public String getApplicate_date_start() {
		return applicate_date_start;
	}

	public void setApplicate_date_start(String applicate_date_start) {
		this.applicate_date_start = applicate_date_start;
	}

	public String getApplicate_date_end() {
		return applicate_date_end;
	}

	public void setApplicate_date_end(String applicate_date_end) {
		this.applicate_date_end = applicate_date_end;
	}

	public String getScheduled_date_start() {
		return scheduled_date_start;
	}

	public void setScheduled_date_start(String scheduled_date_start) {
		this.scheduled_date_start = scheduled_date_start;
	}

	public String getScheduled_date_end() {
		return scheduled_date_end;
	}

	public void setScheduled_date_end(String scheduled_date_end) {
		this.scheduled_date_end = scheduled_date_end;
	}

	public String getRecept_date_start() {
		return recept_date_start;
	}

	public void setRecept_date_start(String recept_date_start) {
		this.recept_date_start = recept_date_start;
	}

	public String getRecept_date_end() {
		return recept_date_end;
	}

	public void setRecept_date_end(String recept_date_end) {
		this.recept_date_end = recept_date_end;
	}

	public String getInline_recept_date_start() {
		return inline_recept_date_start;
	}

	public void setInline_recept_date_start(String inline_recept_date_start) {
		this.inline_recept_date_start = inline_recept_date_start;
	}

	public String getInline_recept_date_end() {
		return inline_recept_date_end;
	}

	public void setInline_recept_date_end(String inline_recept_date_end) {
		this.inline_recept_date_end = inline_recept_date_end;
	}

	public String getConfirm_flg() {
		return confirm_flg;
	}

	public void setConfirm_flg(String confirm_flg) {
		this.confirm_flg = confirm_flg;
	}

	public String getIsLiner() {
		return isLiner;
	}

	public void setIsLiner(String isLiner) {
		this.isLiner = isLiner;
	}

	public String getSection_full_name() {
		return section_full_name;
	}

	public void setSection_full_name(String section_full_name) {
		this.section_full_name = section_full_name;
	}

}