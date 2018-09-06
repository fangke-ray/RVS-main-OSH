package com.osh.rvs.bean.infect;

import java.io.Serializable;
import java.util.Date;

public class ToolManagerConfirmEntity implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 34776553561356625L;

	// 当时所属课室	
	private String section_id;
	// 当时所属工位	
	private String position_id;
	// 确认人
	private String upper_confirmer_id;
	private String process_code;
	// 确认时间
	private Date comfirm_time;
	public String getSection_id() {
		return section_id;
	}
	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}
	public String getPosition_id() {
		return position_id;
	}
	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}
	public String getUpper_confirmer_id() {
		return upper_confirmer_id;
	}
	public void setUpper_confirmer_id(String upper_confirmer_id) {
		this.upper_confirmer_id = upper_confirmer_id;
	}
	public String getProcess_code() {
		return process_code;
	}
	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}
	public Date getComfirm_time() {
		return comfirm_time;
	}
	public void setComfirm_time(Date comfirm_time) {
		this.comfirm_time = comfirm_time;
	}
}
