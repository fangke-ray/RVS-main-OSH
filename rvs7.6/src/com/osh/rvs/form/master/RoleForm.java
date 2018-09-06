package com.osh.rvs.form.master;

import java.util.List;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;
import framework.huiqing.common.util.AutofillArrayList;

public class RoleForm extends ActionForm {

	/** serialVersionUID */
	private static final long serialVersionUID = 8461113289381456595L;

	/** 角色 ID */
	@BeanField(title = "角色 ID", name = "role_id", primaryKey = true, length = 11)
	private String id;
	/** 角色名称 */
	@BeanField(title = "角色名称", name = "name", type = FieldType.FullString, length = 50)
	private String name;

	/** 等级类型 */
	@BeanField(title = "等级类型", name = "rank_kind", type = FieldType.Integer, length = 1, notNull=true)
	private String rank_kind;

	/** 最后更新人 */
	@BeanField(title = "更新者", name = "updated_by")
	private String updated_by;
	/** 最后更新时间 */
	@BeanField(title = "更新时间", name = "updated_time", type = FieldType.TimeStamp)
	private String updated_time;

	/** 拥有权限 */
	private List<String> privacies = new AutofillArrayList<String>(String.class);

	/**
	 * 取得角色 ID
	 * @return role_id 角色 ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 角色 ID设定
	 * @param role_id 角色 ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 取得角色名称
	 * @return name 角色名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 角色名称设定
	 * @param name 角色名称
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

	/**
	 * 取得拥有权限
	 * @return privacies 拥有权限
	 */
	public List<String> getPrivacies() {
		return privacies;
	}

	/**
	 * 拥有权限设定
	 * @param privacies 拥有权限
	 */
	public void setPrivacies(List<String> privacies) {
		this.privacies = privacies;
	}

	public String getRank_kind() {
		return rank_kind;
	}

	public void setRank_kind(String rank_kind) {
		this.rank_kind = rank_kind;
	}

}
