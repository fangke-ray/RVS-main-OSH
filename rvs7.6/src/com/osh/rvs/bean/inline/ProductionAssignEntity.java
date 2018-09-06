package com.osh.rvs.bean.inline;

import java.io.Serializable;
import java.util.Date;

public class ProductionAssignEntity implements Serializable {
	private static final long serialVersionUID = 181694260905574250L;

	// '维修对象 ID'
	private String material_id;

	// '工位 ID'
	private String position_id;
	// '就位时间'
	private Date in_place_time;
	// '默认指派人员'
	private String assigned_operator_id;
	// '线长指派标记'
	private Integer assigned_flg;

	private String omr_notifi_no;
	private String process_code;
	private Integer overtime;

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public Date getIn_place_time() {
		return in_place_time;
	}

	public void setIn_place_time(Date in_place_time) {
		this.in_place_time = in_place_time;
	}

	public String getAssigned_operator_id() {
		return assigned_operator_id;
	}

	public void setAssigned_operator_id(String assigned_operator_id) {
		this.assigned_operator_id = assigned_operator_id;
	}

	public Integer getAssigned_flg() {
		return assigned_flg;
	}

	public void setAssigned_flg(Integer assigned_flg) {
		this.assigned_flg = assigned_flg;
	}

	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}

	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public Integer getOvertime() {
		return overtime;
	}

	public void setOvertime(Integer overtime) {
		this.overtime = overtime;
	}
}
