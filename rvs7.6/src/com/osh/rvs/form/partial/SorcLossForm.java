package com.osh.rvs.form.partial;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class SorcLossForm extends ActionForm implements Serializable {

	/**
	 * SORC损金147PA 所有数据
	 */
	private static final long serialVersionUID = 9072750577248656529L;
	// 零件订购日期
	@BeanField(title = "订购日期", name = "order_date", type = FieldType.Date)
	private String order_date;

	// 出货日期---月损金导出数据
	@BeanField(title = "出货日期", name = "ocm_shipping_time", type = FieldType.String)
	private String ocm_shipping_time;

	// 零件订购签收明细key
	@BeanField(title = "零件订购签收明细key", name = "material_partial_detail_key", primaryKey = true, type = FieldType.String, length = 11)
	private String material_partial_detail_key;

	// 出货日期(物流配送日)
	@BeanField(title = "出货日期", name = "ocm_shipping_date", type = FieldType.Date)
	private String ocm_shipping_date;

	// 出货月
	@BeanField(title = "出货月", name = "ocm_shipping_month", type = FieldType.String)
	private String ocm_shipping_month;

	// 修理编号
	@BeanField(title = "修理编号", name = "sorc_no")
	private String sorc_no;

	// 型号
	@BeanField(title = "型号", name = "model_name", length = 30, notNull = true, primaryKey = true)
	private String model_name;

	// 机身号
	@BeanField(title = "机身号", name = "serial_no", length = 20, notNull = true, primaryKey = true)
	private String serial_no;

	// 分室(委托处)
	@BeanField(title = "分室", name = "ocm", type = FieldType.Integer, length = 2)
	private String ocm;

	// OCM RANK(OCM等级)
	@BeanField(title = "OCM RANK", name = "ocm_rank", type = FieldType.Integer, length = 2)
	private String ocm_rank;

	// SORC RANK(SORC等级)
	@BeanField(title = "SORC RANK", name = "level", type = FieldType.Integer, length = 1)
	private String level;

	// 等级变更(当OCM RANK 和SORC RANK不一样的时候，等级变更:是；相反时，等级变更:否)
	private String change_rank;

	// 发现工程列
	@BeanField(title = "发现工程列", name = "line_id", type = FieldType.String, length = 11)
	private String line_id;

	// 追加分类
	@BeanField(title = "追加分类", name = "belongs", type = FieldType.Integer, length = 1)
	private String belongs;

	// 追加分类
	@BeanField(title = "对象机种", name = "kind", type = FieldType.Integer, length = 2)
	private String kind;

	// 责任区分
	@BeanField(title = "责任区分", name = "liability_flg", type = FieldType.Integer, length = 1)
	private String liability_flg;

	// 不良简述
	@BeanField(title = "不良简述", name = "nogood_description", length = 60)
	private String nogood_description;

	// 零件型号
	@BeanField(title = "零件编码", name = "code", length = 8, notNull = true)
	private String code;

	// 数量
	@BeanField(title = "数量", name = "quantity", type = FieldType.Integer, length = 2)
	private String quantity;

	// 零件单价
	@BeanField(title = "零件单价", name = "price", type = FieldType.UDouble)
	private String price;

	// 报价差异损金(数量和零件单价的乘积)
	@BeanField(title = "报价差异损金", name = "loss_price", type = FieldType.UDouble)
	private String loss_price;

	// 有偿与否
	@BeanField(title = "有无偿", name = "service_free_flg", type = FieldType.Integer, length = 1)
	private String service_free_flg;

	// 备注
	@BeanField(title = "备注", name = "comment", length = 100)
	private String comment;

	private String countermeasures;

	public String getOcm_shipping_time() {
		return ocm_shipping_time;
	}

	public void setOcm_shipping_time(String ocm_shipping_time) {
		this.ocm_shipping_time = ocm_shipping_time;
	}

	public String getOrder_date() {
		return order_date;
	}

	public void setOrder_date(String order_date) {
		this.order_date = order_date;
	}

	public String getMaterial_partial_detail_key() {
		return material_partial_detail_key;
	}

	public void setMaterial_partial_detail_key(String material_partial_detail_key) {
		this.material_partial_detail_key = material_partial_detail_key;
	}

	public String getOcm_shipping_month() {
		return ocm_shipping_month;
	}

	public void setOcm_shipping_month(String ocm_shipping_month) {
		this.ocm_shipping_month = ocm_shipping_month;
	}

	public String getService_free_flg() {
		return service_free_flg;
	}

	public void setService_free_flg(String service_free_flg) {
		this.service_free_flg = service_free_flg;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getOcm_shipping_date() {
		return ocm_shipping_date;
	}

	public void setOcm_shipping_date(String ocm_shipping_date) {
		this.ocm_shipping_date = ocm_shipping_date;
	}

	public String getSorc_no() {
		return sorc_no;
	}

	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
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

	public String getChange_rank() {
		return change_rank;
	}

	public void setChange_rank(String change_rank) {
		this.change_rank = change_rank;
	}

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public String getLiability_flg() {
		return liability_flg;
	}

	public void setLiability_flg(String liability_flg) {
		this.liability_flg = liability_flg;
	}

	public String getNogood_description() {
		return nogood_description;
	}

	public void setNogood_description(String nogood_description) {
		this.nogood_description = nogood_description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getLoss_price() {
		return loss_price;
	}

	public void setLoss_price(String loss_price) {
		this.loss_price = loss_price;
	}

	public String getBelongs() {
		return belongs;
	}

	public void setBelongs(String belongs) {
		this.belongs = belongs;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getCountermeasures() {
		return countermeasures;
	}

	public void setCountermeasures(String countermeasures) {
		this.countermeasures = countermeasures;
	}

}
