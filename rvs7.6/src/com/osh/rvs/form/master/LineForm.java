package com.osh.rvs.form.master;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;
import framework.huiqing.common.util.copy.BooleanConverter;

public class LineForm extends ActionForm {

	/** serialVersionUID */
	private static final long serialVersionUID = -8849009246377096812L;

	/** 工程 ID */
	@BeanField(title = "工程 ID", name = "line_id", primaryKey = true, length = 11)
	private String id;
	/** 工程名称 */
	@BeanField(title = "工程名称", name = "name", type = FieldType.String, length = 11)
	private String name;
	/** 是否计划管理对象 */
	@BeanField(title = "是否计划管理对象", name = "inline_flg", type = FieldType.Bool, scale=BooleanConverter.PATTERN_NUM)
	private String inline_flg;
	/** 最后更新人 */
	@BeanField(title = "更新者", name = "updated_by")
	private String updated_by;
	/** 最后更新时间 */
	@BeanField(title = "更新时间", name = "updated_time", type = FieldType.TimeStamp)
	private String updated_time;

	/**
	 * 取得工程 ID
	 * @return section_id 工程 ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 工程 ID设定
	 * @param section_id 工程 ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String getInline_flg() {
		return inline_flg;
	}

	public void setInline_flg(String inline_flg) {
		this.inline_flg = inline_flg;
	}

	/**
	 * 取得工程名称
	 * @return name 工程名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 工程名称设定
	 * @param name 工程名称
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
}
