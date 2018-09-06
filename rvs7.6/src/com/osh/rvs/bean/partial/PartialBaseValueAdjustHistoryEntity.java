package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.util.Date;

public class PartialBaseValueAdjustHistoryEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7851131717861422630L;

	private String partial_code;// 零件编号

	private String partial_name;// 零件名称

	private Integer identification;// 设定类型

	private Integer foreboard_count;// 当前基准值设定

	private Integer updated_by;// 最后更新人ID
	
	private Date start_date;//起效日期
	
	private Date end_date;//终止日期

	private Date start_date_start;// 起效日期开始

	private Date start_date_end;// 起效日期结束

	private Date updated_time;// 修改日期

	private Date update_time_start;// 修改日期开始

	private Date update_time_end;// 修改日期结束

	private String update_name;// 最后更新人

	private Integer order_num;// 订购数

	private Integer bo_num;// BO数

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

	public Integer getIdentification() {
		return identification;
	}

	public void setIdentification(Integer identification) {
		this.identification = identification;
	}

	public Integer getForeboard_count() {
		return foreboard_count;
	}

	public void setForeboard_count(Integer total_foreboard_count) {
		this.foreboard_count = total_foreboard_count;
	}

	public Integer getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(Integer updated_by) {
		this.updated_by = updated_by;
	}

	public Date getStart_date_start() {
		return start_date_start;
	}

	public void setStart_date_start(Date start_date_start) {
		this.start_date_start = start_date_start;
	}

	public Date getStart_date_end() {
		return start_date_end;
	}

	public void setStart_date_end(Date start_date_end) {
		this.start_date_end = start_date_end;
	}

	public Date getUpdated_time() {
		return updated_time;
	}

	public void setUpdated_time(Date updated_time) {
		this.updated_time = updated_time;
	}

	public Date getUpdate_time_start() {
		return update_time_start;
	}

	public void setUpdate_time_start(Date update_time_start) {
		this.update_time_start = update_time_start;
	}

	public Date getUpdate_time_end() {
		return update_time_end;
	}

	public void setUpdate_time_end(Date update_time_end) {
		this.update_time_end = update_time_end;
	}

	public String getUpdate_name() {
		return update_name;
	}

	public void setUpdate_name(String update_name) {
		this.update_name = update_name;
	}

	public Integer getOrder_num() {
		return order_num;
	}

	public void setOrder_num(Integer order_num) {
		this.order_num = order_num;
	}

	public Integer getBo_num() {
		return bo_num;
	}

	public void setBo_num(Integer bo_num) {
		this.bo_num = bo_num;
	}

	public Date getStart_date() {
		return start_date;
	}

	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}

	public Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}
	
}
