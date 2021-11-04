package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.util.Date;

public class MaterialPartInstructEntity implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2767082343795120361L;

	private String material_id;
	private String omr_notifi_no;
	private String model_name;
	private String serial_no;
	private Integer level;
	private Date inline_time;
	private Integer occur_times;
	private Date order_date;
	private Integer procedure;
	private Integer confirm; 
	private Boolean inline_adjust;

	private String section_id;
	private String[] search_procedures;

	private String model_id;
	private Date agreed_date;
	private String operator_id;

	public String getMaterial_id() {
		return material_id;
	}
	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}
	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}
	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}
	public String getModel_name() {
		return model_name;
	}
	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}
	public String getSerial_no() {
		return serial_no;
	}
	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Date getInline_time() {
		return inline_time;
	}
	public void setInline_time(Date inline_time) {
		this.inline_time = inline_time;
	}
	public Integer getOccur_times() {
		return occur_times;
	}
	public void setOccur_times(Integer occur_times) {
		this.occur_times = occur_times;
	}
	public Date getOrder_date() {
		return order_date;
	}
	public void setOrder_date(Date order_date) {
		this.order_date = order_date;
	}
	public Integer getProcedure() {
		return procedure;
	}
	public void setProcedure(Integer procedure) {
		this.procedure = procedure;
	}
	public Integer getConfirm() {
		return confirm;
	}
	public void setConfirm(Integer confirm) {
		this.confirm = confirm;
	}
	public Boolean getInline_adjust() {
		return inline_adjust;
	}
	public void setInline_adjust(Boolean inline_adjust) {
		this.inline_adjust = inline_adjust;
	}
	public String[] getSearch_procedures() {
		return search_procedures;
	}
	public void setSearch_procedures(String[] search_procedures) {
		this.search_procedures = search_procedures;
	}
	public String getSection_id() {
		return section_id;
	}
	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}
	public String getModel_id() {
		return model_id;
	}
	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}
	public Date getAgreed_date() {
		return agreed_date;
	}
	public void setAgreed_date(Date agreed_date) {
		this.agreed_date = agreed_date;
	}
	public String getOperator_id() {
		return operator_id;
	}
	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}
}
