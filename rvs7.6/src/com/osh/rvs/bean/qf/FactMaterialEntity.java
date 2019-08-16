package com.osh.rvs.bean.qf;

import java.io.Serializable;

/**
 * 现品维修对象作业
 * 
 * @author liuxb
 * 
 */
public class FactMaterialEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7819430867999303844L;

	/**
	 * KEY
	 */
	private String af_pf_key;

	/**
	 * 维修对象 ID
	 */
	private String material_id;

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

}
