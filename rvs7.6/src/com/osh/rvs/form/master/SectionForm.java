package com.osh.rvs.form.master;

import java.util.List;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.copy.BooleanConverter;

public class SectionForm extends ActionForm {

	/** serialVersionUID */
	private static final long serialVersionUID = -4508926102117132327L;
	/** 课室 ID */
	@BeanField(title = "课室 ID", name = "section_id", primaryKey = true, length = 11)
	private String id;
	/** 课室名称 */
	@BeanField(title = "课室名称", name = "name", type = FieldType.String, length = 11)
	private String name;
	/** 是否计划管理对象 */
	@BeanField(title = "是否在线维修课室", name = "inline_flg", type = FieldType.Bool, scale=BooleanConverter.PATTERN_NUM)
	private String inline_flg;
	/** 最后更新人 */
	@BeanField(title = "更新者", name = "updated_by")
	private String updated_by;
	/** 最后更新时间 */
	@BeanField(title = "更新时间", name = "updated_time", type = FieldType.TimeStamp)
	private String updated_time;

	/** 拥有工位 */
	private List<String> positions = new AutofillArrayList<String>(String.class);

	/**
	 * 取得课室 ID
	 * @return section_id 课室 ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 课室 ID设定
	 * @param section_id 课室 ID
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
	 * 取得课室名称
	 * @return name 课室名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 课室名称设定
	 * @param name 课室名称
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
	 * 取得拥有工位
	 * @return positions 拥有工位
	 */
	public List<String> getPositions() {
		return positions;
	}

	/**
	 * 拥有工位设定
	 * @param positions 拥有工位
	 */
	public void setPositions(List<String> positions) {
		this.positions = positions;
	}

}
