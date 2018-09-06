package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class SorcLossEntity implements Serializable {

	/**
	 * SORC损金147PA 所有数据
	 */
	private static final long serialVersionUID = -2732062514165683428L;
	
	//零件订购日期
	private Date order_date;
	
	//有出货日期---月损金导入数据
	private String ocm_shipping_time;
	
	// 零件订购签收明细key
	private String material_partial_detail_key;

	// 出货日期(物流配送日)
	private Date ocm_shipping_date;

	// 出货月
	private String ocm_shipping_month;

	// 修理编号
	private String sorc_no;

	// 型号
	private String model_name;

	// 机身号
	private String serial_no;

	// 分室(委托处)
	private Integer ocm;

	// OCM RANK
	private Integer ocm_rank;

	// SORC RANK
	private Integer level;

	// 追加分类
	private Integer belongs;

	// 对象机种
	private Integer kind;

	// 等级变更(OCM RANK和level的值不相等时：是；否则：否；)
	private String change_rank;

	// 发现工程列
	private String line_id;

	// 责任区分
	private Integer liability_flg;

	// 不良简述
	private String nogood_description;

	// 零件型号
	private String code;

	// 数量
	private Integer quantity;

	// 零件单价
	private BigDecimal price;

	// 报价差异损金
	private BigDecimal loss_price;

	// 有偿与否
	private Integer service_free_flg;

	// 备注
	private String comment;

	private String countermeasures;

	public Date getOrder_date() {
		return order_date;
	}

	public void setOrder_date(Date order_date) {
		this.order_date = order_date;
	}

	public String getOcm_shipping_time() {
		return ocm_shipping_time;
	}

	public void setOcm_shipping_time(String ocm_shipping_time) {
		this.ocm_shipping_time = ocm_shipping_time;
	}

	public String getOcm_shipping_month() {
		return ocm_shipping_month;
	}

	public void setOcm_shipping_month(String ocm_shipping_month) {
		this.ocm_shipping_month = ocm_shipping_month;
	}

	public String getMaterial_partial_detail_key() {
		return material_partial_detail_key;
	}

	public void setMaterial_partial_detail_key(String material_partial_detail_key) {
		this.material_partial_detail_key = material_partial_detail_key;
	}

	public Date getOcm_shipping_date() {
		return ocm_shipping_date;
	}

	public void setOcm_shipping_date(Date ocm_shipping_date) {
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

	public Integer getOcm() {
		return ocm;
	}

	public void setOcm(Integer ocm) {
		this.ocm = ocm;
	}

	public Integer getOcm_rank() {
		return ocm_rank;
	}

	public void setOcm_rank(Integer ocm_rank) {
		this.ocm_rank = ocm_rank;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
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

	public Integer getLiability_flg() {
		return liability_flg;
	}

	public void setLiability_flg(Integer liability_flg) {
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

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getLoss_price() {
		return loss_price;
	}

	public void setLoss_price(BigDecimal loss_price) {
		this.loss_price = loss_price;
	}

	public Integer getService_free_flg() {
		return service_free_flg;
	}

	public void setService_free_flg(Integer service_free_flg) {
		this.service_free_flg = service_free_flg;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getBelongs() {
		return belongs;
	}

	public void setBelongs(Integer belongs) {
		this.belongs = belongs;
	}

	public Integer getKind() {
		return kind;
	}

	public void setKind(Integer kind) {
		this.kind = kind;
	}

	public String getCountermeasures() {
		return countermeasures;
	}

	public void setCountermeasures(String countermeasures) {
		this.countermeasures = countermeasures;
	}

}
