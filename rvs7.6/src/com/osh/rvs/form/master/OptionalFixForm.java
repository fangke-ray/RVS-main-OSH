package com.osh.rvs.form.master;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class OptionalFixForm extends ActionForm implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 5566867601244563691L;
	/** 选择修理 ID */
	@BeanField(title = "选择修理 ID", name = "optional_fix_id", type = FieldType.String, length = 11, primaryKey = true)
	private String optional_fix_id;
	/** 检查标准号 */
	@BeanField(title = "检查标准号", name = "standard_code", type = FieldType.String, length = 8, notNull = true)
	private String standard_code ;
	/** 检查项目 */
	@BeanField(title = "检查项目", name = "infection_item", type = FieldType.String, length = 64, notNull = true)
	private String infection_item;
	@BeanField(title = "修理等级", name = "rank", type = FieldType.String, length = 10)
	private String rank;
	@BeanField(title = "最后更新人", name = "updated_by", type = FieldType.String, length = 11)
	private String updated_by;

	@BeanField(title = "最后更新时间", name = "updated_time", type = FieldType.TimeStamp)
	private String updated_time;

	@BeanField(title = "维修对象 ID", name = "material_id", type = FieldType.String, length = 11)
	private String material_id;

	public String getOptional_fix_id() {
		return optional_fix_id;
	}

	public void setOptional_fix_id(String optional_fix_id) {
		this.optional_fix_id = optional_fix_id;
	}

	public String getStandard_code() {
		return standard_code;
	}

	public void setStandard_code(String standard_code) {
		this.standard_code = standard_code;
	}

	public String getInfection_item() {
		return infection_item;
	}

	public void setInfection_item(String infection_item) {
		this.infection_item = infection_item;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
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

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}
}