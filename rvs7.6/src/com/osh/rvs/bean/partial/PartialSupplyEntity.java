package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.util.Date;

public class PartialSupplyEntity implements Serializable{

	private static final long serialVersionUID = 5290575289129634728L;

	private String partial_id;
	private Date supply_date;
	private Integer identification;
	private Integer quantity;
	private String code;
	private String partial_name;
	public String getPartial_id() {
		return partial_id;
	}
	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}
	public Date getSupply_date() {
		return supply_date;
	}
	public void setSupply_date(Date supply_date) {
		this.supply_date = supply_date;
	}
	public Integer getIdentification() {
		return identification;
	}
	public void setIdentification(Integer identification) {
		this.identification = identification;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String toString() {
		return supply_date + ">>" + code + "(" + partial_id + ") = " + quantity;
	}
	public String getPartial_name() {
		return partial_name;
	}
	public void setPartial_name(String partial_name) {
		this.partial_name = partial_name;
	}
}
