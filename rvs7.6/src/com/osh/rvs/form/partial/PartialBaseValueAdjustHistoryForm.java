package com.osh.rvs.form.partial;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class PartialBaseValueAdjustHistoryForm extends ActionForm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3207322773399696859L;

	@BeanField(title = "零件编号", name = "partial_code", length = 9)
	private String partial_code;// 零件编号

	@BeanField(title = "零件名称", name = "partial_name", length = 100)
	private String partial_name;// 零件名称

	@BeanField(title = "当前基准值设定", name = "foreboard_count", type = FieldType.Integer, length = 3)
	private String total_foreboard_count;// 当前基准值设定

	@BeanField(title = "设定类型", name = "identification", type = FieldType.Integer, length = 1, primaryKey = true)
	private String identification;// 设定类型

	@BeanField(title = "最后更新人ID", name = "updated_by", type = FieldType.Integer, length = 11, notNull = true)
	private String updated_by;// 最后更新人ID

	@BeanField(title = "起效日期", name = "start_date", type = FieldType.Date)
	private String start_date;// 起效日期

	@BeanField(title = "终止日期", name = "end_date", type = FieldType.Date)
	private String end_date;// 终止日期

	@BeanField(title = "起效日期开始", name = "start_date_start", type = FieldType.Date)
	private String start_date_start;// 起效日期开始

	@BeanField(title = "起效日期结束", name = "start_date_end", type = FieldType.Date)
	private String start_date_end;// 起效日期结束

	@BeanField(title = "修改日期", name = "updated_time", type = FieldType.Date)
	private String updated_time;// 修改日期

	@BeanField(title = "修改日期开始", name = "update_time_start", type = FieldType.Date)
	private String update_time_start;// 修改日期开始

	@BeanField(title = "修改日期结束", name = "update_time_end", type = FieldType.Date)
	private String update_time_end;// 修改日期结束

	@BeanField(title = "最后更新人", name = "update_name")
	private String update_name;// 最后更新人

	@BeanField(title = "订购数", name = "order_num", type = FieldType.Integer)
	private String order_num;// 订购数

	@BeanField(title = "BO数", name = "bo_num", type = FieldType.Integer)
	private String bo_num;// BO数

	public String getPartial_code() {
		return partial_code;
	}

	public void setPartial_code(String partial_code) {
		this.partial_code = partial_code;
	}

	public String getPartial_name() {
		return partial_name;
	}

	public void setPartial_name(String partial_name) {
		this.partial_name = partial_name;
	}

	public String getTotal_foreboard_count() {
		return total_foreboard_count;
	}

	public void setTotal_foreboard_count(String total_foreboard_count) {
		this.total_foreboard_count = total_foreboard_count;
	}

	public String getIdentification() {
		return identification;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}

	public String getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getStart_date_start() {
		return start_date_start;
	}

	public void setStart_date_start(String start_date_start) {
		this.start_date_start = start_date_start;
	}

	public String getStart_date_end() {
		return start_date_end;
	}

	public void setStart_date_end(String start_date_end) {
		this.start_date_end = start_date_end;
	}

	public String getUpdated_time() {
		return updated_time;
	}

	public void setUpdated_time(String updated_time) {
		this.updated_time = updated_time;
	}

	public String getUpdate_time_start() {
		return update_time_start;
	}

	public void setUpdate_time_start(String update_time_start) {
		this.update_time_start = update_time_start;
	}

	public String getUpdate_time_end() {
		return update_time_end;
	}

	public void setUpdate_time_end(String update_time_end) {
		this.update_time_end = update_time_end;
	}

	public String getUpdate_name() {
		return update_name;
	}

	public void setUpdate_name(String update_name) {
		this.update_name = update_name;
	}

	public String getOrder_num() {
		return order_num;
	}

	public void setOrder_num(String order_num) {
		this.order_num = order_num;
	}

	public String getBo_num() {
		return bo_num;
	}

	public void setBo_num(String bo_num) {
		this.bo_num = bo_num;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

}
