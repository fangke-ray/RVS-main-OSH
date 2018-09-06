package com.osh.rvs.form.master;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class PrivacyForm extends ActionForm {

	private static final long serialVersionUID = -6772953557829159713L;

	 
	/** 权限 ID */
	@BeanField(title = "权限 ID", name = "privacy_id", primaryKey = true, length = 11)
	private String id;
	/** 权限名 */
	@BeanField(title = "权限名", name = "name", type = FieldType.FullString, length = 16)
	private String name;
	/** 权限说明 */
	@BeanField(title = "权限说明", name = "comments", type = FieldType.String, length = 100)
	private String comments;

	/**
	 * 取得权限 ID
	 * @return id 权限 ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 权限 ID设定
	 * @param id 权限 ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 取得权限名
	 * @return name 权限名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 权限名设定
	 * @param name 权限名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 取得权限说明
	 * @return type 权限说明
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * 权限说明设定
	 * @param type 权限说明
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

}
