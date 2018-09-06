package com.osh.rvs.bean.pda;

import java.io.Serializable;
import java.math.BigDecimal;

public class PdaApplyElementEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1911372083710977562L;

	private String consumable_application_key;

	private String partial_id;

	private String code;// 零件Code

	private Integer type;// 类别

	private String type_name;// 类别名称

	private Integer available_inventory;// 有效数量

	private Integer on_passage;// 在途量

	private String stock_code;// 库位

	private Integer apply_quantity;//申请数量
	
	private Integer pack_method;//包装
	
	private Integer content;//内容量
	
	private String unit_name;//单位名称
	
	private String application_no;//消耗品申请单编号
	
	private String open_flg;

	private BigDecimal total_price;

	public String getConsumable_application_key() {
		return consumable_application_key;
	}

	public void setConsumable_application_key(String consumable_application_key) {
		this.consumable_application_key = consumable_application_key;
	}

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

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

	public Integer getApply_quantity() {
		return apply_quantity;
	}

	public void setApply_quantity(Integer apply_quantity) {
		this.apply_quantity = apply_quantity;
	}

	public Integer getPack_method() {
		return pack_method;
	}

	public void setPack_method(Integer pack_method) {
		this.pack_method = pack_method;
	}

	public Integer getContent() {
		return content;
	}

	public void setContent(Integer content) {
		this.content = content;
	}

	public String getUnit_name() {
		return unit_name;
	}

	public void setUnit_name(String unit_name) {
		this.unit_name = unit_name;
	}

	public String getApplication_no() {
		return application_no;
	}

	public void setApplication_no(String application_no) {
		this.application_no = application_no;
	}

	public String getOpen_flg() {
		return open_flg;
	}

	public void setOpen_flg(String open_flg) {
		this.open_flg = open_flg;
	}

	public BigDecimal getTotal_price() {
		return total_price;
	}

	public void setTotal_price(BigDecimal total_price) {
		this.total_price = total_price;
	}

}
