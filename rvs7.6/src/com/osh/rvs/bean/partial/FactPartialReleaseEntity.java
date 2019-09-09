package com.osh.rvs.bean.partial;

import java.io.Serializable;

/**
 * 零件出库作业数
 * 
 * @author liuxb
 * 
 */
public class FactPartialReleaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3542930796766820510L;

	/**
	 * KEY
	 */
	private String af_pf_key;

	/**
	 * 维修对象 ID
	 */
	private String material_id;
	private String omr_notifi_no;
	private String model_name;
	private Integer level;
	private String level_name;

	/**
	 * 规格种别
	 */
	private Integer spec_kind;

	/**
	 * 作业数量
	 */
	private Integer quantity;

	/**
	 * 用户 ID
	 */
	private String operator_id;

	public String getAf_pf_key() {
		return af_pf_key;
	}

	public void setAf_pf_key(String af_pf_key) {
		this.af_pf_key = af_pf_key;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public Integer getSpec_kind() {
		return spec_kind;
	}

	public void setSpec_kind(Integer spec_kind) {
		this.spec_kind = spec_kind;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}

	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getLevel_name() {
		return level_name;
	}

	public void setLevel_name(String level_name) {
		this.level_name = level_name;
	}

}
