package com.osh.rvs.bean.support;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @Description 物品申购明细
 * @author liuxb
 * @date 2021-12-2 下午1:19:09
 */
public class SuppliesDetailEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -4739183061032596613L;

	/**
	 * 物品申购明细 KEY
	 */
	private String supplies_key;

	/**
	 * 品名
	 */
	private String product_name;

	/**
	 * 规格
	 */
	private String model_name;

	/**
	 * 预定单价
	 */
	private BigDecimal unit_price;

	/**
	 * 单位
	 */
	private String unit_text;

	/**
	 * 数量
	 */
	private Integer quantity;

	/**
	 * 申请课室ID
	 */
	private String section_id;

	/**
	 * 申购人员ID
	 */
	private String applicator_id;

	/**
	 * 用途
	 */
	private String nesssary_reason;

	/**
	 * 申请日期
	 */
	private Date applicate_date;

	/**
	 * 上级确认者ID
	 */
	private String confirmer_id;

	/**
	 * 物品申购单 Key
	 */
	private String order_key;

	/**
	 * 供应商/采购渠道
	 */
	private String supplier;

	/**
	 * 预计到货日期
	 */
	private Date scheduled_date;

	/**
	 * 收货日期
	 */
	private Date recept_date;

	/**
	 * 预算月
	 */
	private String budget_month;

	/**
	 * 验收日期
	 */
	private Date inline_recept_date;

	/**
	 * 备注
	 */
	private String comments;

	/**
	 * 发票号
	 */
	private String invoice_no;

	/**
	 * 申请课室
	 */
	private String section_name;

	/**
	 * 申购人员
	 */
	private String applicator_name;

	/**
	 * 上级确认者
	 */
	private String confirmer_name;

	/**
	 * 申购单号
	 */
	private String order_no;

	/**
	 * 申请开始日期
	 */
	private Date applicate_date_start;

	/**
	 * 申请结束日期
	 */
	private Date applicate_date_end;

	/**
	 * 预计到货开始日期
	 */
	private Date scheduled_date_start;

	/**
	 * 预计到货结束日期
	 */
	private Date scheduled_date_end;

	/**
	 * 收货开始日期
	 */
	private Date recept_date_start;

	/**
	 * 收货结束日期
	 */
	private Date recept_date_end;

	/**
	 * 验收开始日期
	 */
	private Date inline_recept_date_start;

	/**
	 * 验收结束日期
	 */
	private Date inline_recept_date_end;

	/**
	 * 课室全称
	 */
	private String section_full_name;

	/**
	 * 执行步骤
	 */
	private Integer step;

	/**
	 * 加急申请
	 */
	private Integer urgent_flg;

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

	public BigDecimal getUnit_price() {
		return unit_price;
	}

	public void setUnit_price(BigDecimal unit_price) {
		this.unit_price = unit_price;
	}

	public String getUnit_text() {
		return unit_text;
	}

	public void setUnit_text(String unit_text) {
		this.unit_text = unit_text;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
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

	public Date getApplicate_date() {
		return applicate_date;
	}

	public void setApplicate_date(Date applicate_date) {
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

	public Date getScheduled_date() {
		return scheduled_date;
	}

	public void setScheduled_date(Date scheduled_date) {
		this.scheduled_date = scheduled_date;
	}

	public Date getRecept_date() {
		return recept_date;
	}

	public void setRecept_date(Date recept_date) {
		this.recept_date = recept_date;
	}

	public String getBudget_month() {
		return budget_month;
	}

	public void setBudget_month(String budget_month) {
		this.budget_month = budget_month;
	}

	public Date getInline_recept_date() {
		return inline_recept_date;
	}

	public void setInline_recept_date(Date inline_recept_date) {
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

	public Date getApplicate_date_start() {
		return applicate_date_start;
	}

	public void setApplicate_date_start(Date applicate_date_start) {
		this.applicate_date_start = applicate_date_start;
	}

	public Date getApplicate_date_end() {
		return applicate_date_end;
	}

	public void setApplicate_date_end(Date applicate_date_end) {
		this.applicate_date_end = applicate_date_end;
	}

	public Date getScheduled_date_start() {
		return scheduled_date_start;
	}

	public void setScheduled_date_start(Date scheduled_date_start) {
		this.scheduled_date_start = scheduled_date_start;
	}

	public Date getScheduled_date_end() {
		return scheduled_date_end;
	}

	public void setScheduled_date_end(Date scheduled_date_end) {
		this.scheduled_date_end = scheduled_date_end;
	}

	public Date getRecept_date_start() {
		return recept_date_start;
	}

	public void setRecept_date_start(Date recept_date_start) {
		this.recept_date_start = recept_date_start;
	}

	public Date getRecept_date_end() {
		return recept_date_end;
	}

	public void setRecept_date_end(Date recept_date_end) {
		this.recept_date_end = recept_date_end;
	}

	public Date getInline_recept_date_start() {
		return inline_recept_date_start;
	}

	public void setInline_recept_date_start(Date inline_recept_date_start) {
		this.inline_recept_date_start = inline_recept_date_start;
	}

	public Date getInline_recept_date_end() {
		return inline_recept_date_end;
	}

	public void setInline_recept_date_end(Date inline_recept_date_end) {
		this.inline_recept_date_end = inline_recept_date_end;
	}

	public String getSection_full_name() {
		return section_full_name;
	}

	public void setSection_full_name(String section_full_name) {
		this.section_full_name = section_full_name;
	}

	public Integer getStep() {
		return step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}

	/**
	 * @return the urgent_flg
	 */
	public Integer getUrgent_flg() {
		return urgent_flg;
	}

	/**
	 * @param urgent_flg the urgent_flg to set
	 */
	public void setUrgent_flg(Integer urgent_flg) {
		this.urgent_flg = urgent_flg;
	}

}