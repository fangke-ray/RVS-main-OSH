package com.osh.rvs.bean.master;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @Description 常用采购清单
 * @author liuxb
 * @date 2021-11-29 下午4:12:54
 */
public class SuppliesReferListEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3803155440447551733L;

	/**
	 * 常用采购清单KEY
	 */
	private String refer_key;

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
	 * 供应商
	 */
	private String supplier;

	/**
	 * 照片 UUID
	 */
	private String photo_uuid;

	public String getRefer_key() {
		return refer_key;
	}

	public void setRefer_key(String refer_key) {
		this.refer_key = refer_key;
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

	public String getUnit_text() {
		return unit_text;
	}

	public void setUnit_text(String unit_text) {
		this.unit_text = unit_text;
	}

	public void setUnit_price(BigDecimal unit_price) {
		this.unit_price = unit_price;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getPhoto_uuid() {
		return photo_uuid;
	}

	public void setPhoto_uuid(String photo_uuid) {
		this.photo_uuid = photo_uuid;
	}
}