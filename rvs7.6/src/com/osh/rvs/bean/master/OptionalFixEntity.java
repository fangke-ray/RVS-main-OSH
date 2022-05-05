package com.osh.rvs.bean.master;

import java.io.Serializable;
import java.sql.Timestamp;

public class OptionalFixEntity implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 309153699669513228L;
	/** 选择修理 ID */
	private String optional_fix_id;
	/** 检查标准号 */
	private String standard_code ;
	/** 检查项目 */
	private String infection_item;
	/** 修理等级 */
	private String rank;
	/** 最后更新人 */
	private String updated_by;
	/** 最后更新时间 */
	private Timestamp updated_time;

	private String material_id;

	/**
	 * @return the rank
	 */
	public String getRank() {
		return rank;
	}

	/**
	 * @param rank
	 *            the rank to set
	 */
	public void setRank(String rank) {
		this.rank = rank;
	}

	/**
	 * @return the updated_by
	 */
	public String getUpdated_by() {
		return updated_by;
	}

	/**
	 * @param updated_by
	 *            the updated_by to set
	 */
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}

	/**
	 * @return the updated_time
	 */
	public Timestamp getUpdated_time() {
		return updated_time;
	}

	/**
	 * @param updated_time
	 *            the updated_time to set
	 */
	public void setUpdated_time(Timestamp updated_time) {
		this.updated_time = updated_time;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

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

}
