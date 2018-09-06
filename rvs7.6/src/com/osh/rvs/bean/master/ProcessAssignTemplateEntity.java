package com.osh.rvs.bean.master;

import java.io.Serializable;
import java.sql.Timestamp;

public class ProcessAssignTemplateEntity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 2154642609790916792L;

	/** 修理流程模板 ID */
	private String process_assign_template_id;
	/** 修理流程模板名 */
	private String name;
	/** 删除类别 */
	private boolean delete_flg = false;
	/** 最后更新人 */
	private String updated_by;
	/** 最后更新时间 */
	private Timestamp updated_time;

	/** 派生类型 */
	private Integer derive_kind;
	/** 派生来源 */
	private String derive_from_id;
	/** 维修方式 */
	private Integer fix_type;

	/**
	 * 取得修理流程模板 ID
	 * @return process_assign_template_id 修理流程模板 ID
	 */
	public String getProcess_assign_template_id() {
		return process_assign_template_id;
	}

	/**
	 * 修理流程模板 ID设定
	 * @param process_assign_template_id 修理流程模板 ID
	 */
	public void setProcess_assign_template_id(String process_assign_template_id) {
		this.process_assign_template_id = process_assign_template_id;
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
	 * 取得删除标记
	 * @return delete_flg 删除标记
	 */
	public boolean isDelete_flg() {
		return delete_flg;
	}

	/**
	 * 删除标记设定
	 * @param delete_flg 删除标记
	 */
	public void setDelete_flg(boolean delete_flg) {
		this.delete_flg = delete_flg;
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
	public Timestamp getUpdated_time() {
		return updated_time;
	}

	/**
	 * 最后更新时间设定
	 * @param updated_time 最后更新时间
	 */
	public void setUpdated_time(Timestamp updated_time) {
		this.updated_time = updated_time;
	}

	/**
	 * 文字列化
	 * 
	 * @return 文字列
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.process_assign_template_id).append(", ");
		buffer.append(this.name).append(". ");
		return buffer.toString();
	}

	public Integer getDerive_kind() {
		return derive_kind;
	}

	public void setDerive_kind(Integer derive_kind) {
		this.derive_kind = derive_kind;
	}

	public String getDerive_from_id() {
		return derive_from_id;
	}

	public void setDerive_from_id(String derive_from_id) {
		this.derive_from_id = derive_from_id;
	}

	public Integer getFix_type() {
		return fix_type;
	}

	public void setFix_type(Integer fix_type) {
		this.fix_type = fix_type;
	}
}
