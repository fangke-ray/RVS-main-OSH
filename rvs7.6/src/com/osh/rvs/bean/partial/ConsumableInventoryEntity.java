package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.util.Date;
public class ConsumableInventoryEntity implements Serializable{

	private static final long serialVersionUID = -447093140545075127L;

	/**
	 * 检索用
	 */
	/* 包含品名编号 */
	private String code;

	/* 申请日期.起 */
	private Date adjust_date_start;
	
	/* 申请日期.止*/
	private Date adjust_date_end;

	/* 理由 */
	private String reason;


	/**
	 * 一览用
	 */
	/* 零件 ID*/
	private Integer partial_id;

	/* 说明 */
	private String name;

	/* 消耗品分类 */
	private Integer type;
	
	/* 调整量*/
	private Integer adjust_inventory;

	/* 调整日时 */
	private Date adjust_time;

	/* 担当人 ID */
	private String operator_id;

	/* 调整负责人 */
	private String operator_name;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getAdjust_date_start() {
		return adjust_date_start;
	}

	public void setAdjust_date_start(Date adjust_date_start) {
		this.adjust_date_start = adjust_date_start;
	}

	public Date getAdjust_date_end() {
		return adjust_date_end;  
	}

	public void setAdjust_date_end(Date adjust_date_end) {
		this.adjust_date_end = adjust_date_end;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Integer getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(Integer partial_id) {
		this.partial_id = partial_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getAdjust_inventory() {
		return adjust_inventory;
	}

	public void setAdjust_inventory(Integer adjust_inventory) {
		this.adjust_inventory = adjust_inventory;
	}

	public Date getAdjust_time() {
		return adjust_time;
	}

	public void setAdjust_time(Date adjust_time) {
		this.adjust_time = adjust_time;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}
}
