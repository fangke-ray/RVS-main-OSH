package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.math.BigDecimal;

public class MaterialPartPrelistEntity implements Serializable{
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -2419721195186907371L;

	/*维修品ID*/
	private String material_id;

	/*零件ID*/
	private String partial_id;
	
	/*零件编码*/
	private String code;
	private String name;
	/* 价格 */
	private BigDecimal price;
	
	/*bom_code*/
	private String bom_code;

	private Integer quantity;

	private Integer rank;
	private Integer ship;

	private String quote_operator_id;
	private String quote_job_no;
	private String inline_operator_id;
	private String inline_job_no;
	private String inline_comment;

	private Integer quote_adjust = 0;

	private Integer no_priv;

	private Integer order_flg;
	private String process_code; 
	private String line_id;

	public String getMaterial_id() {
		return material_id;
	}
	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getBom_code() {
		return bom_code;
	}
	public void setBom_code(String bom_code) {
		this.bom_code = bom_code;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Integer getRank() {
		return rank;
	}
	public void setRank(Integer rank) {
		this.rank = rank;
	}
	public Integer getShip() {
		return ship;
	}
	public void setShip(Integer ship) {
		this.ship = ship;
	}
	public String getQuote_operator_id() {
		return quote_operator_id;
	}
	public void setQuote_operator_id(String quote_operator_id) {
		this.quote_operator_id = quote_operator_id;
	}
	public String getQuote_job_no() {
		return quote_job_no;
	}
	public void setQuote_job_no(String quote_job_no) {
		this.quote_job_no = quote_job_no;
	}
	public String getInline_operator_id() {
		return inline_operator_id;
	}
	public void setInline_operator_id(String inline_operator_id) {
		this.inline_operator_id = inline_operator_id;
	}
	public String getInline_job_no() {
		return inline_job_no;
	}
	public void setInline_job_no(String inline_job_no) {
		this.inline_job_no = inline_job_no;
	}
	public String getInline_comment() {
		return inline_comment;
	}
	public void setInline_comment(String inline_comment) {
		this.inline_comment = inline_comment;
	}
	public Integer getNo_priv() {
		return no_priv;
	}
	public void setNo_priv(Integer no_priv) {
		this.no_priv = no_priv;
	}
	public Integer getQuote_adjust() {
		return quote_adjust;
	}
	public void setQuote_adjust(Integer quote_adjust) {
		this.quote_adjust = quote_adjust;
	}
	public Integer getOrder_flg() {
		return order_flg;
	}
	public void setOrder_flg(Integer order_flg) {
		this.order_flg = order_flg;
	}
	public String getProcess_code() {
		return process_code;
	}
	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}
	public String getLine_id() {
		return line_id;
	}
	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}
}
