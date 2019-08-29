package com.osh.rvs.bean.partial;

import java.io.Serializable;

/**
 * 现品消耗品入库作业数
 * 
 * @author liuxb
 * 
 */
public class FactConsumableWarehouseEntity implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -3332051420030593080L;

	/**
	 * KEY
	 */
	private String af_pf_key;

	/**
	 * 货架耗时
	 */
	private Integer shelf_cost;

	/**
	 * 作业品名数
	 */
	private Integer quantity;

	public String getAf_pf_key() {
		return af_pf_key;
	}

	public void setAf_pf_key(String af_pf_key) {
		this.af_pf_key = af_pf_key;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getShelf_cost() {
		return shelf_cost;
	}

	public void setShelf_cost(Integer shelf_cost) {
		this.shelf_cost = shelf_cost;
	}

}
