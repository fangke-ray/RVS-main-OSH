package com.osh.rvs.form.manage;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

public class UserDefineCodesForm extends ActionForm implements Serializable {
	/**
	 * 用户定义数值
	 */
	private static final long serialVersionUID = 278646368244736625L;
	/* 代码 */
	private String code;
	/* 说明 */
	private String description;
	/* 值 */
	private String value;
	/* 使用说明 */
	private String manual;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getManual() {
		return manual;
	}

	public void setManual(String manual) {
		this.manual = manual;
	}

}
