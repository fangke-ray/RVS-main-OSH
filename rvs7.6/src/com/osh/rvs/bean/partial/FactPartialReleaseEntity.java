package com.osh.rvs.bean.partial;

import java.io.Serializable;

/**
 * 零件出库作业数
 * 
 * @author liuxb
 * 
 */
public class FactPartialReleaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3542930796766820510L;

	/**
	 * KEY
	 */
	private String af_pf_key;

	/**
	 * 维修对象 ID
	 */
	private String material_id;

	/**
	 * 规格种别
	 */
	private Integer spec_kind;

	/**
	 * 作业数量
	 */
	private Integer quantity;

	public String getAf_pf_key() {
		return af_pf_key;
	}

	public void setAf_pf_key(String af_pf_key) {
		this.af_pf_key = af_pf_key;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public Integer getSpec_kind() {
		return spec_kind;
	}

	public void setSpec_kind(Integer spec_kind) {
		this.spec_kind = spec_kind;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
