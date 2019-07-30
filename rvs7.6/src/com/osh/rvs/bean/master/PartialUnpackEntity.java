package com.osh.rvs.bean.master;

import java.io.Serializable;

/**
 * 分装零件
 *
 * @author liuxb
 *
 */
public class PartialUnpackEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -717971643123266253L;

	/**
	 * 零件 ID
	 */
	private String partial_id;

	/**
	 * 分装数量
	 */
	private Integer split_quantity;

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public Integer getSplit_quantity() {
		return split_quantity;
	}

	public void setSplit_quantity(Integer split_quantity) {
		this.split_quantity = split_quantity;
	}

}
