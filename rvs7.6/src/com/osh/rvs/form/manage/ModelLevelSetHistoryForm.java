package com.osh.rvs.form.manage;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 型号等级设定历史
 * 
 * @author lxb
 * 
 */
public class ModelLevelSetHistoryForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -444015976801618520L;

	@BeanField(title = "型号ID", name = "model_id", type = FieldType.Integer, length = 11, notNull = true, primaryKey = true)
	private String model_id;// 型号ID
	
	@BeanField(title="型号名称",name="model_name")
	private String model_name;// 型号名称
	
	@BeanField(title="等级",name="level",type=FieldType.Integer,length=1,notNull=true,primaryKey=true)
	private String level;// 等级

	@BeanField(title="止修理日期",name="avaliable_end_date",type=FieldType.Date,notNull=true,primaryKey=true)
	private String avaliable_end_date;// 停止修理日期
	
	@BeanField(title="停止修理日期开始",name="avaliable_end_date_start",type=FieldType.Date)
	private String avaliable_end_date_start;// 停止修理日期开始

	@BeanField(title="停止修理日期结束",name="avaliable_end_date_end",type=FieldType.Date)
	private String avaliable_end_date_end;// 停止修理日期结束
	
	@BeanField(title="更新人ID",name="updated_by",type=FieldType.Integer,length=11,notNull=true)
	private String updated_by;// 更新人ID

	@BeanField(title="更新人名称",name="updated_name")
	private String updated_name;// 更新人名称
	
	@BeanField(title="最后更新时间",name="updated_time",type=FieldType.TimeStamp,notNull=true)
	private String updated_time;// 最后更新时间

	@BeanField(title="最后更新时间开始",name="updated_time_start",type=FieldType.Date)
	private String updated_time_start;// 最后更新时间开始

	@BeanField(title="最后更新时间结束",name="updated_time_end",type=FieldType.Date)
	private String updated_time_end;// 最后更新时间结束
	
	@BeanField(title="梯队",name="echelon",type=FieldType.Integer)
	private String echelon;//梯队

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getAvaliable_end_date() {
		return avaliable_end_date;
	}

	public void setAvaliable_end_date(String avaliable_end_date) {
		this.avaliable_end_date = avaliable_end_date;
	}

	public String getAvaliable_end_date_start() {
		return avaliable_end_date_start;
	}

	public void setAvaliable_end_date_start(String avaliable_end_date_start) {
		this.avaliable_end_date_start = avaliable_end_date_start;
	}

	public String getAvaliable_end_date_end() {
		return avaliable_end_date_end;
	}

	public void setAvaliable_end_date_end(String avaliable_end_date_end) {
		this.avaliable_end_date_end = avaliable_end_date_end;
	}

	public String getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}

	public String getUpdated_name() {
		return updated_name;
	}

	public void setUpdated_name(String updated_name) {
		this.updated_name = updated_name;
	}

	public String getUpdated_time() {
		return updated_time;
	}

	public void setUpdated_time(String updated_time) {
		this.updated_time = updated_time;
	}

	public String getUpdated_time_start() {
		return updated_time_start;
	}

	public void setUpdated_time_start(String updated_time_start) {
		this.updated_time_start = updated_time_start;
	}

	public String getUpdated_time_end() {
		return updated_time_end;
	}

	public void setUpdated_time_end(String updated_time_end) {
		this.updated_time_end = updated_time_end;
	}

	public String getEchelon() {
		return echelon;
	}

	public void setEchelon(String echelon) {
		this.echelon = echelon;
	}

	
}
