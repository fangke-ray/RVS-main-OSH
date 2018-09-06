package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.util.Date;

/**
 * 消耗品替代记录
 */
public class ConsumableSubstituteEntity implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1985154301417141252L;

	/** Key */
	private String consumable_substitute_key;

	/** 维修对象 ID */
	private String material_id;

	/** 订购次数 */
	private Integer occur_times;

	/** 消耗品 ID */
	private String partial_id;

	/** 操作者 ID */
	private String operator_id;

	/** 替代时间 */
	private Date substitute_time;

	/** 替代场合 */
	private Integer occasion_flg;

	/** 替代数量 */
	private Integer quantity;

	/** 使用工位 ID */
	private String position_id;

	public String getConsumable_substitute_key() {
		return consumable_substitute_key;
	}

	public void setConsumable_substitute_key(String consumable_substitute_key) {
		this.consumable_substitute_key = consumable_substitute_key;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public Integer getOccur_times() {
		return occur_times;
	}

	public void setOccur_times(Integer occur_times) {
		this.occur_times = occur_times;
	}

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public Date getSubstitute_time() {
		return substitute_time;
	}

	public void setSubstitute_time(Date substitute_time) {
		this.substitute_time = substitute_time;
	}

	public Integer getOccasion_flg() {
		return occasion_flg;
	}

	public void setOccasion_flg(Integer occasion_flg) {
		this.occasion_flg = occasion_flg;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}
}
