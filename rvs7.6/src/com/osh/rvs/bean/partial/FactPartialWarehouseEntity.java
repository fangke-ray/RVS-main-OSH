package com.osh.rvs.bean.partial;

import java.io.Serializable;

/**
 * 现品入库作业数
 * 
 * @author liuxb
 * 
 */
public class FactPartialWarehouseEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2698374280829717431L;

	/**
	 * KEY
	 */
	private String af_pf_key;

	/**
	 * 零件入库单 KE
	 */
	private String partial_warehouse_key;

	/**
	 * 规格种别
	 */
	private Integer spec_kind;

	/**
	 * 作业数量
	 */
	private Integer quantity;

	/**
	 * 作业内容
	 */
	private Integer production_type;

	public String getAf_pf_key() {
		return af_pf_key;
	}

	public void setAf_pf_key(String af_pf_key) {
		this.af_pf_key = af_pf_key;
	}

	public String getPartial_warehouse_key() {
		return partial_warehouse_key;
	}

	public void setPartial_warehouse_key(String partial_warehouse_key) {
		this.partial_warehouse_key = partial_warehouse_key;
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

	public Integer getProduction_type() {
		return production_type;
	}

	public void setProduction_type(Integer production_type) {
		this.production_type = production_type;
	}

}
