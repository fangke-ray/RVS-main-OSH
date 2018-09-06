package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.util.Date;
public class ConsumableSupplyEntity implements Serializable{

	private static final long serialVersionUID = -447093140545075127L;

	/**
	 * 检索用
	 */
	/* 包含品名编号 */
	private String code;
	
	/* 入库日期.起 */
	private Date supply_date_start;
	
	/* 入库日期.止*/
	private Date supply_date_end;

	/**
	 * 一览用
	 */
	/* 零件 ID*/
	private Integer partial_id;

	/* 说明 */
	private String name;

	/* 消耗品分类 */
	private Integer type;
	
	/* 入库日期*/
	private Date supply_date;

	/* 入库数量*/
	private Integer quantity;

	/* 入库数量*/
	private Integer available_inventory;

	/* 入库数量*/
	private Integer on_passage;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getSupply_date_start() {
		return supply_date_start;
	}

	public void setSupply_date_start(Date supply_date_start) {
		this.supply_date_start = supply_date_start;
	}

	public Date getSupply_date_end() {
		return supply_date_end;
	}

	public void setSupply_date_end(Date supply_date_end) {
		this.supply_date_end = supply_date_end;
	}

	public Integer getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(Integer partial_id) {
		this.partial_id = partial_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date getSupply_date() {
		return supply_date;
	}

	public void setSupply_date(Date supply_date) {
		this.supply_date = supply_date;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
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
}
