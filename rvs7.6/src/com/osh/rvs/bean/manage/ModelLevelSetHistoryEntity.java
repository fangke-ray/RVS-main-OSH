package com.osh.rvs.bean.manage;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 型号等级设定历史
 * @author lxb
 *
 */
public class ModelLevelSetHistoryEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8411408031594986953L;
	
	private Integer model_id;//型号ID
	
	private String model_name;//型号名称
	
	private Integer level;//等级
	
	private Date avaliable_end_date;//停止修理日期
	
	private Date avaliable_end_date_start;//停止修理日期开始
	
	private Date avaliable_end_date_end;//停止修理日期结束
	
	private Integer updated_by;//更新人ID
	
	private String updated_name;//更新人名称
	
	private Timestamp updated_time;//最后更新时间
	
	private Date updated_time_start;//最后更新时间开始
	
	private Date updated_time_end;//最后更新时间结束
	
	private Integer echelon;//梯队

	public Integer getModel_id() {
		return model_id;
	}

	public void setModel_id(Integer model_id) {
		this.model_id = model_id;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Date getAvaliable_end_date() {
		return avaliable_end_date;
	}

	public void setAvaliable_end_date(Date avaliable_end_date) {
		this.avaliable_end_date = avaliable_end_date;
	}

	public Date getAvaliable_end_date_start() {
		return avaliable_end_date_start;
	}

	public void setAvaliable_end_date_start(Date avaliable_end_date_start) {
		this.avaliable_end_date_start = avaliable_end_date_start;
	}

	public Date getAvaliable_end_date_end() {
		return avaliable_end_date_end;
	}

	public void setAvaliable_end_date_end(Date avaliable_end_date_end) {
		this.avaliable_end_date_end = avaliable_end_date_end;
	}

	public Integer getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(Integer updated_by) {
		this.updated_by = updated_by;
	}

	public String getUpdated_name() {
		return updated_name;
	}

	public void setUpdated_name(String updated_name) {
		this.updated_name = updated_name;
	}

	public Timestamp getUpdated_time() {
		return updated_time;
	}

	public void setUpdated_time(Timestamp updated_time) {
		this.updated_time = updated_time;
	}

	public Date getUpdated_time_start() {
		return updated_time_start;
	}

	public void setUpdated_time_start(Date updated_time_start) {
		this.updated_time_start = updated_time_start;
	}

	public Date getUpdated_time_end() {
		return updated_time_end;
	}

	public void setUpdated_time_end(Date updated_time_end) {
		this.updated_time_end = updated_time_end;
	}

	public Integer getEchelon() {
		return echelon;
	}

	public void setEchelon(Integer echelon) {
		this.echelon = echelon;
	}
	
}
