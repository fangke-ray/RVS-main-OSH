package com.osh.rvs.bean.partial;

import java.io.Serializable;

/**
 * 零件入库明细
 * 
 * @author liuxb
 * 
 */
public class PartialWarehouseDetailEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1046986262646618507L;

	/**
	 * KEY
	 */
	private String key;

	/**
	 * 零件ID
	 */
	private String partial_id;

	/**
	 * 数量
	 */
	private Integer quantity;

	/**
	 * 零件名称
	 */
	private String partial_name;

	/**
	 * 零件编码
	 */
	private String code;

	/**
	 * 规格种别
	 */
	private Integer spec_kind;

	/**
	 * 是否分裝
	 */
	private Integer unpack_flg;

	/**
	 * 分装数量
	 */
	private Integer split_quantity;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getPartial_name() {
		return partial_name;
	}

	public void setPartial_name(String partial_name) {
		this.partial_name = partial_name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getSpec_kind() {
		return spec_kind;
	}

	public void setSpec_kind(Integer spec_kind) {
		this.spec_kind = spec_kind;
	}

	public Integer getUnpack_flg() {
		return unpack_flg;
	}

	public void setUnpack_flg(Integer unpack_flg) {
		this.unpack_flg = unpack_flg;
	}

	public Integer getSplit_quantity() {
		return split_quantity;
	}

	public void setSplit_quantity(Integer split_quantity) {
		this.split_quantity = split_quantity;
	}

}
