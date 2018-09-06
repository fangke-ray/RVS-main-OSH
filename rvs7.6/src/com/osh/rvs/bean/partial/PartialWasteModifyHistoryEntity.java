package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class PartialWasteModifyHistoryEntity implements Serializable {

	/**
	 * 零件废改订历史管理
	 */
	private static final long serialVersionUID = 7741823305595634918L;
	
	private String operator_id;
	
	private String operator_type;
	
	private String code;
	
	private String name;

	private String operator;

	private String old_code;

	private String old_name;

	private String new_code;

	private String new_name;

	private String model_name;

	private String model_id;

	private String old_partial_id;

	private String new_partial_id;

	private Date active_date_start;

	private Date active_date;

	private Date active_date_end;

	private String updated_by;

	private Timestamp updated_time;

	
	public String getOperator_type() {
		return operator_type;
	}

	public void setOperator_type(String operator_type) {
		this.operator_type = operator_type;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getModel_name() {
		return model_name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public String getOld_partial_id() {
		return old_partial_id;
	}

	public void setOld_partial_id(String old_partial_id) {
		this.old_partial_id = old_partial_id;
	}

	public String getNew_partial_id() {
		return new_partial_id;
	}

	public void setNew_partial_id(String new_partial_id) {
		this.new_partial_id = new_partial_id;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOld_code() {
		return old_code;
	}

	public void setOld_code(String old_code) {
		this.old_code = old_code;
	}

	public String getOld_name() {
		return old_name;
	}

	public void setOld_name(String old_name) {
		this.old_name = old_name;
	}

	public String getNew_code() {
		return new_code;
	}

	public void setNew_code(String new_code) {
		this.new_code = new_code;
	}

	public String getNew_name() {
		return new_name;
	}

	public void setNew_name(String new_name) {
		this.new_name = new_name;
	}

	public Date getActive_date_start() {
		return active_date_start;
	}

	public void setActive_date_start(Date active_date_start) {
		this.active_date_start = active_date_start;
	}

	public Date getActive_date_end() {
		return active_date_end;
	}

	public void setActive_date_end(Date active_date_end) {
		this.active_date_end = active_date_end;
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

	public Date getActive_date() {
		return active_date;
	}

	public void setActive_date(Date active_date) {
		this.active_date = active_date;
	}

}
