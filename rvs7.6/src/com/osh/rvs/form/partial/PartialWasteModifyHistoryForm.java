package com.osh.rvs.form.partial;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class PartialWasteModifyHistoryForm extends ActionForm implements Serializable {

	/**
	 * 零件废改订历史管理
	 */
	private static final long serialVersionUID = 3999650271664883384L;
	
	private String operator_id;
    private String operator_type;
	
	@BeanField(title = "零件编码", name = "code", type = FieldType.String, notNull = true)
	private String code;

	@BeanField(title = "零件名称", name = "name", type = FieldType.String, notNull = true)
	private String name;

	@BeanField(title = "操作者", name = "operator", type = FieldType.String)
	private String operator;

	@BeanField(title = "旧零件编码", name = "old_code", type = FieldType.String, notNull = true)
	private String old_code;

	@BeanField(title = "旧零件名称", name = "old_name", type = FieldType.String, notNull = true)
	private String old_name;

	@BeanField(title = "新零件编码", name = "new_code", type = FieldType.String, notNull = true)
	private String new_code;

	@BeanField(title = "新零件名称", name = "new_name", type = FieldType.String, notNull = true)
	private String new_name;

	@BeanField(title = "型号名称", name = "model_name", type = FieldType.String, notNull = true)
	private String model_name;

	@BeanField(title = "型号ID", name = "model_id", type = FieldType.String, notNull = true)
	private String model_id;

	@BeanField(title = "旧零件ID", name = "old_partial_id", type = FieldType.String, notNull = true)
	private String old_partial_id;

	@BeanField(title = "新零件ID", name = "new_partial_id", type = FieldType.String, notNull = true)
	private String new_partial_id;

	@BeanField(title = "起效日期始", name = "active_date_start", type = FieldType.Date, notNull = true)
	private String active_date_start;

	@BeanField(title = "起效日期", name = "active_date", type = FieldType.Date, notNull = true)
	private String active_date;

	@BeanField(title = "起效日期止", name = "active_date_end", type = FieldType.Date, notNull = true)
	private String active_date_end;

	@BeanField(title = "更新人", name = "updated_by", type = FieldType.String)
	private String updated_by;

	@BeanField(title = "更新日期", name = "updated_time", type = FieldType.Date, notNull = true)
	private String updated_time;

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getOperator_type() {
		return operator_type;
	}

	public void setOperator_type(String operator_type) {
		this.operator_type = operator_type;
	}

	public String getModel_name() {
		return model_name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getModel_id() {
		return model_id;
	}

	public String getActive_date() {
		return active_date;
	}

	public void setActive_date(String active_date) {
		this.active_date = active_date;
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

	public String getActive_date_start() {
		return active_date_start;
	}

	public void setActive_date_start(String active_date_start) {
		this.active_date_start = active_date_start;
	}

	public String getActive_date_end() {
		return active_date_end;
	}

	public void setActive_date_end(String active_date_end) {
		this.active_date_end = active_date_end;
	}

}
