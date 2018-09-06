package com.osh.rvs.form.master;

import java.util.List;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;
import framework.huiqing.common.util.AutofillArrayList;

public class ProcessAssignTemplateForm extends ActionForm {

	private static final long serialVersionUID = -1610392915550683338L;
	/** 修理流程模板 ID */
	@BeanField(title = "修理流程模板 ID", name = "process_assign_template_id", primaryKey = true, length = 11)
	private String id;
	/** 修理流程模板名 */
	@BeanField(title = "修理流程模板名", name = "name", length = 30)
	private String name;
	/** 最后更新人 */
	@BeanField(title = "最后更新人", name = "updated_by")
	private String updated_by;
	/** 最后更新时间 */
	@BeanField(title = "最后更新时间", type = FieldType.TimeStamp, name = "updated_time")
	private String updated_time;

	private List<ProcessAssignForm> processAssigns = new AutofillArrayList<ProcessAssignForm>(ProcessAssignForm.class);

	/** 派生类型 */
	@BeanField(title = "派生类型", name = "derive_kind", type = FieldType.Integer)
	private String derive_kind;
	/** 派生来源 */
	@BeanField(title = "派生来源", name = "derive_from_id")
	private String derive_from_id;

	@BeanField(title = "使用方式", name = "fix_type", type = FieldType.Integer, notNull = true)
	private String fix_type;

	/**
	 * 取得修理流程模板 ID
	 * @return process_assign_template_id 修理流程模板 ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 修理流程模板 ID设定
	 * @param process_assign_template_id 修理流程模板 ID
	 */
	public void setId(String process_assign_template_id) {
		this.id = process_assign_template_id;
	}

	/**
	 * 取得修理流程模板名
	 * @return name 修理流程模板名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 修理流程模板名设定
	 * @param name 修理流程模板名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 取得最后更新人
	 * @return updated_by 最后更新人
	 */
	public String getUpdated_by() {
		return updated_by;
	}

	/**
	 * 最后更新人设定
	 * @param updated_by 最后更新人
	 */
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}

	/**
	 * 取得最后更新时间
	 * @return updated_time 最后更新时间
	 */
	public String getUpdated_time() {
		return updated_time;
	}

	/**
	 * 最后更新时间设定
	 * @param updated_time 最后更新时间
	 */
	public void setUpdated_time(String updated_time) {
		this.updated_time = updated_time;
	}

	public List<ProcessAssignForm> getProcessAssigns() {
		return processAssigns;
	}

	public void setProcessAssigns(List<ProcessAssignForm> processAssigns) {
		this.processAssigns = processAssigns;
	}

	public String getDerive_kind() {
		return derive_kind;
	}

	public void setDerive_kind(String derive_kind) {
		this.derive_kind = derive_kind;
	}

	public String getDerive_from_id() {
		return derive_from_id;
	}

	public void setDerive_from_id(String derive_from_id) {
		this.derive_from_id = derive_from_id;
	}

	public String getFix_type() {
		return fix_type;
	}

	public void setFix_type(String fix_type) {
		this.fix_type = fix_type;
	}
}
