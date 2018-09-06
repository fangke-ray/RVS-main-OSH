package com.osh.rvs.form.master;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class CategoryForm extends ActionForm {

	private static final long serialVersionUID = -6772953557829159713L;

	/** 维修对象机种 ID */
	@BeanField(title = "维修对象机种 ID", name = "category_id", primaryKey = true, length = 11)
	private String id;
	/** 维修对象机种名称 */
	@BeanField(title = "维修对象机种名称", name = "name", type = FieldType.String, length = 50)
	private String name;
	/** 维修对象种类 */
	@BeanField(title = "维修对象种类", name = "kind", type = FieldType.Integer, length = 2)
	private String kind;
	/** 最后更新人 */
	@BeanField(title = "更新者", name = "updated_by")
	private String updated_by;
	/** 最后更新时间 */
	@BeanField(title = "更新时间", name = "updated_time", type = FieldType.TimeStamp)
	private String updated_time;
	/** 默认流程 ID */
	@BeanField(title = "默认流程 ID", name = "default_pat_id", notNull=true, length = 11)
	private String default_pat_id;

	/**
	 * 取得维修对象机种 ID
	 * @return category_id 维修对象机种 ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 维修对象机种 ID设定
	 * @param category_id 维修对象机种 ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 取得维修对象机种名称
	 * @return name 维修对象机种名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 维修对象机种名称设定
	 * @param name 维修对象机种名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 取得维修对象种类
	 * @return type 维修对象种类
	 */
	public String getKind() {
		return kind;
	}

	/**
	 * 维修对象种类设定
	 * @param type 维修对象种类
	 */
	public void setKind(String kind) {
		this.kind = kind;
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

	public String getDefault_pat_id() {
		return default_pat_id;
	}

	public void setDefault_pat_id(String default_pat_id) {
		this.default_pat_id = default_pat_id;
	}

}
