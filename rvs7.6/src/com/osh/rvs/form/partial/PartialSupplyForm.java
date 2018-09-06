package com.osh.rvs.form.partial;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class PartialSupplyForm extends ActionForm {

	private static final long serialVersionUID = -8332389940488800454L;

	@BeanField(title = "零件ID", name = "partial_id", type = FieldType.String)
	private String partial_id;
	@BeanField(title = "日期", name = "supply_date", type = FieldType.Date)
	private String supply_date;
	@BeanField(title = "识别", name = "identification", type = FieldType.Integer)
	private String identification;
	@BeanField(title = "数量", name = "quantity", type = FieldType.Integer)
	private String quantity;
	@BeanField(title = "零件编码", name = "code", type = FieldType.String)
	private String code;
	private String partial_name;
	public String getPartial_id() {
		return partial_id;
	}
	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}
	public String getSupply_date() {
		return supply_date;
	}
	public void setSupply_date(String supply_date) {
		this.supply_date = supply_date;
	}
	public String getIdentification() {
		return identification;
	}
	public void setIdentification(String identification) {
		this.identification = identification;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getPartial_name() {
		return partial_name;
	}
	public void setPartial_name(String partial_name) {
		this.partial_name = partial_name;
	}

}
