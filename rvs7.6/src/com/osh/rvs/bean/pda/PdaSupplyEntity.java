package com.osh.rvs.bean.pda;

import java.io.Serializable;

public class PdaSupplyEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5289425227932111400L;

	private String partial_id;//零件ID
	
	private String code;// 零件Code

	private Integer type;// 类型

	private String type_name;// 类型名称

	private Integer available_inventory;// 有效数量

	private Integer on_passage;// 在途数量

	private String stock_code;// 库位
	
	private Integer quantity;//入库数量

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getType_name() {
		return type_name;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}

	public Integer getAvailable_inventory() {
		return available_inventory;
	}

	public void setAvailable_inventory(Integer available_inventory) {
		this.available_inventory = available_inventory;
	}

	public Integer getOn_passage() {
		return on_passage;
	}

	public void setOn_passage(Integer on_passage) {
		this.on_passage = on_passage;
	}

	public String getStock_code() {
		return stock_code;
	}

	public void setStock_code(String stock_code) {
		this.stock_code = stock_code;
	}

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
