package com.osh.rvs.form.master;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class PartialPositionForm extends ActionForm implements Serializable{
	
	/**
	 * 零件定位管理
	 */
	private static final long serialVersionUID = -116552365716715639L;
	
	/*active_date*/
	@BeanField(title = "", name = "active_date")
	private String active_date;
	/*型号*/
	@BeanField(title = "型号", name = "model_name",length=30)
	private String model_name;
	/*零件编码*/
	@BeanField(title = "零件编码", name = "code",length=9)
	private String code;
	/*零件名称*/
	@BeanField(title = "零件名称", name = "name",length = 120)
	private String name;
	/*工位*/
	@BeanField(title = "工位", name = "process_code")
	private String process_code;
	/*使用率*/
	@BeanField(title = "使用率", name = "userate")
	private String userate;
	/*最后更新人*/
	@BeanField(title = "最后更新人", name = "updated_by")
	private String updated_by;
	/*最后更新时间*/
	@BeanField(title = "最后更新时间", name = "updated_time",length=100,type=FieldType.TimeStamp)
	private String updated_time;
	/*BOM*/
	@BeanField(title = "BOM", name = "bom")
	private String bom;
	/*等级*/
	@BeanField(title = "等级", name = "level")
	private String level;
	/*型号ID*/
	@BeanField(title = "型号ID", name = "model_id",type = FieldType.String, primaryKey = true, length = 11)
	private String model_id;
	/*零件ID*/
	@BeanField(title = "零件ID", name = "partial_id",type = FieldType.String, primaryKey = true, length = 11)
	private String partial_id;
	
	/*零件oldID*/
	@BeanField(title = "零件oldID", name = "old_partial_id",type = FieldType.String, length = 11)
	private String old_partial_id;
	/*工位ID*/
	@BeanField(title = "工位ID", name = "position_id",type = FieldType.String, primaryKey = true, length = 11)
	private String position_id;
	/*有效截止日期*/
	@BeanField(title = "有效截止日期", name = "history_limit_date", type = FieldType.Date)
	private String history_limit_date;
	/*更名对应零件 ID*/
	@BeanField(title = "更名对应零件 ID", name = "new_partial_id",length=11)
	private String new_partial_id;
	/*UNIT零件 ID*/
	@BeanField(title = "UNIT零件 ID", name = "parent_partial_id",length=11)
	private String parent_partial_id;
	private String parent_partial_code;
	
	
	public String getOld_partial_id() {
		return old_partial_id;
	}
	public void setOld_partial_id(String old_partial_id) {
		this.old_partial_id = old_partial_id;
	}
	public String getActive_date() {
		return active_date;
	}
	public void setActive_date(String active_date) {
		this.active_date = active_date;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getBom() {
		return bom;
	}
	public void setBom(String bom) {
		this.bom = bom;
	}
	public String getModel_id() {
		return model_id;
	}
	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}
	public String getPartial_id() {
		return partial_id;
	}
	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}
	public String getPosition_id() {
		return position_id;
	}
	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}
	public String getHistory_limit_date() {
		return history_limit_date;
	}
	public void setHistory_limit_date(String history_limit_date) {
		this.history_limit_date = history_limit_date;
	}
	public String getNew_partial_id() {
		return new_partial_id;
	}
	public void setNew_partial_id(String new_partial_id) {
		this.new_partial_id = new_partial_id;
	}
	public String getParent_partial_id() {
		return parent_partial_id;
	}
	public void setParent_partial_id(String parent_partial_id) {
		this.parent_partial_id = parent_partial_id;
	}
	public String getModel_name() {
		return model_name;
	}
	public void setModel_name(String model_name) {
		this.model_name = model_name;
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
	public String getProcess_code() {
		return process_code;
	}
	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}
	public String getUserate() {
		return userate;
	}
	public void setUserate(String userate) {
		this.userate = userate;
	}
	public String getUpdated_by() {
		return updated_by;
	}
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}
	public String getUpdated_time() {
		return updated_time;
	}
	public void setUpdated_time(String updated_time) {
		this.updated_time = updated_time;
	}
	public String getParent_partial_code() {
		return parent_partial_code;
	}
	public void setParent_partial_code(String parent_partial_code) {
		this.parent_partial_code = parent_partial_code;
	}
	
}
