package com.osh.rvs.bean.master;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class PartialPositionEntity implements Serializable{
	
	/**
	 * 零件定位管理
	 */
	private static final long serialVersionUID = -2419721195186907371L;

	private String old_partial_id;
	/*型号ID*/
	private String model_id;
	/*零件ID*/
	private String  partial_id;
	/*工位ID*/
	private String position_id;
	/*有效截止日期*/
	private Date history_limit_date;
	/*更名对应零件 ID*/
	private String new_partial_id;
	/*UNIT零件 ID*/
	private String parent_partial_id;
	
	/*型号*/
	private String model_name;
	/*零件编码*/
	private String code;
	/*等级*/
	private String level;
	
	private String  name;
	
	/*UNIT零件 代码*/
	private String parent_partial_code;

	private String process_code;
	/*使用率*/
	private String userate;
	/*最后更新人*/
	private String updated_by;
	/*最后更新时间*/
	private Timestamp updated_time;
	/*bom*/
	private String bom;
	
	private String active_date;
	
	
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
	public Timestamp getUpdated_time() {
		return updated_time;
	}
	public void setUpdated_time(Timestamp updated_time) {
		this.updated_time = updated_time;
	}
	public String getBom() {
		return bom;
	}
	public void setBom(String bom) {
		this.bom = bom;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
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
	public Date getHistory_limit_date() {
		return history_limit_date;
	}
	public void setHistory_limit_date(Date history_limit_date) {
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
	public String getParent_partial_code() {
		return parent_partial_code;
	}
	public void setParent_partial_code(String parent_partial_code) {
		this.parent_partial_code = parent_partial_code;
	}
	

	
	
}
