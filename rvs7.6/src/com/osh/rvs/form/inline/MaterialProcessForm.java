package com.osh.rvs.form.inline;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class MaterialProcessForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6203036408131580503L;

	@BeanField(title = "维修对象ID", name = "material_id", type = FieldType.String, length = 11)
	private String material_id;
	
	@BeanField(title="", name="dec_plan_date", type = FieldType.Date)
	private String dec_plan_date;
	@BeanField(title="", name="dec_finish_date", type = FieldType.Date)
	private String dec_finish_date;
	@BeanField(title="", name="ns_plan_date", type = FieldType.Date)
	private String ns_plan_date;
	@BeanField(title="", name="ns_finish_date", type = FieldType.Date)
	private String ns_finish_date;
	@BeanField(title="", name="com_plan_date", type = FieldType.Date)
	private String com_plan_date;
	@BeanField(title="", name="com_finish_date", type = FieldType.Date)
	private String com_finish_date;
	
	@BeanField(title="产出安排", name="scheduled_date", type = FieldType.Date)
	private String schedule_date;
	
	public String getSchedule_date() {
		return schedule_date;
	}
	public void setSchedule_date(String schedule_date) {
		this.schedule_date = schedule_date;
	}
	public String getMaterial_id() {
		return material_id;
	}
	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}
	public String getDec_plan_date() {
		return dec_plan_date;
	}
	public void setDec_plan_date(String dec_plan_date) {
		this.dec_plan_date = dec_plan_date;
	}
	public String getDec_finish_date() {
		return dec_finish_date;
	}
	public void setDec_finish_date(String dec_finish_date) {
		this.dec_finish_date = dec_finish_date;
	}
	public String getNs_plan_date() {
		return ns_plan_date;
	}
	public void setNs_plan_date(String ns_plan_date) {
		this.ns_plan_date = ns_plan_date;
	}
	public String getNs_finish_date() {
		return ns_finish_date;
	}
	public void setNs_finish_date(String ns_finish_date) {
		this.ns_finish_date = ns_finish_date;
	}
	public String getCom_plan_date() {
		return com_plan_date;
	}
	public void setCom_plan_date(String com_plan_date) {
		this.com_plan_date = com_plan_date;
	}
	public String getCom_finish_date() {
		return com_finish_date;
	}
	public void setCom_finish_date(String com_finish_date) {
		this.com_finish_date = com_finish_date;
	}
	
	
}
