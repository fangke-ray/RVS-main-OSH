package com.osh.rvs.bean.master;

import java.io.Serializable;

public class PartialBomEntity implements Serializable {

	/**
	 * 零件BOM管理
	 */
	private static final long serialVersionUID = 8890673720525630453L;

	/* 零件 ID */
	private String partial_id;
	/* 型号 */
	private String model_id;
	/* 等級 */
	private Integer level;
	/* 型号名称 */
	private String model_name;
	/* 零件编码 */
	private String code;
	/* 零件名称 */
	private String partial_name;
	/* 使用数量 */
	private Integer quantity;
	/* 梯队 ID */
	private Integer echelon;
	/* 更名对应零件 ID */
	private String new_partial_id;

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPartial_name() {
		return partial_name;
	}

	public void setPartial_name(String partial_name) {
		this.partial_name = partial_name;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getEchelon() {
		return echelon;
	}

	public void setEchelon(Integer echelon) {
		this.echelon = echelon;
	}

	public String getNew_partial_id() {
		return new_partial_id;
	}

	public void setNew_partial_id(String new_partial_id) {
		this.new_partial_id = new_partial_id;
	}

}
