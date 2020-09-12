package com.osh.rvs.bean.qf;

import java.io.Serializable;
import java.util.Date;

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

	private Integer production_type;
	
	private String operator_id;
	
	private Date action_time_start;
	
	private Date action_time_end;

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

	public Integer getProduction_type() {
		return production_type;
	}

	public void setProduction_type(Integer production_type) {
		this.production_type = production_type;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public Date getAction_time_start() {
		return action_time_start;
	}

	public void setAction_time_start(Date action_time_start) {
		this.action_time_start = action_time_start;
	}

	public Date getAction_time_end() {
		return action_time_end;
	}

	public void setAction_time_end(Date action_time_end) {
		this.action_time_end = action_time_end;
	}

}
