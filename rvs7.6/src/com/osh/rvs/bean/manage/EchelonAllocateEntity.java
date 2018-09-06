package com.osh.rvs.bean.manage;

import java.io.Serializable;
import java.util.Date;

public class EchelonAllocateEntity implements Serializable {
	/**
	 * 梯队设定
	 */
	private static final long serialVersionUID = -3090468978269032152L;
	/*上一次同意维修数*/
	private String new_ageed_count;
	/*同意维修数*/
	private String agreed_count;	
	/*前次比较*/
	private String fluctuate;
	
	
	/*型号*/
	private String model_name;
	
	//型号 ID   
	private String echelon_history_key;
	//区间开始日  
	private Date start_date;
	//区间终了日  
	private Date end_date;
	//最后更新人
	private String updated_by;
	//最后更新时间 
	private String updated_time;
	//型号 ID    
	private String model_id;
	//等级     
	private String level;
	// 梯队   
	private String echelon;
	
	
	public String getNew_ageed_count() {
		return new_ageed_count;
	}
	public void setNew_ageed_count(String new_ageed_count) {
		this.new_ageed_count = new_ageed_count;
	}
	public String getAgreed_count() {
		return agreed_count;
	}
	public void setAgreed_count(String agreed_count) {
		this.agreed_count = agreed_count;
	}
	public String getFluctuate() {
		return fluctuate;
	}
	public void setFluctuate(String fluctuate) {
		this.fluctuate = fluctuate;
	}
	public String getModel_name() {
		return model_name;
	}
	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}
	public String getEchelon_history_key() {
		return echelon_history_key;
	}
	public void setEchelon_history_key(String echelon_history_key) {
		this.echelon_history_key = echelon_history_key;
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
	public String getModel_id() {
		return model_id;
	}
	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getEchelon() {
		return echelon;
	}
	public void setEchelon(String echelon) {
		this.echelon = echelon;
	}
	
}
