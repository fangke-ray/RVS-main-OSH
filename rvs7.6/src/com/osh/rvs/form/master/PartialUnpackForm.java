package com.osh.rvs.form.master;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 零件分装
 *
 * @author liuxb
 *
 */
public class PartialUnpackForm extends ActionForm {

	/**
	 *
	 */
	private static final long serialVersionUID = -8824983339663734194L;

	/**
	 * 零件 ID
	 */
	@BeanField(title = "零件 ID", name = "partial_id", length = 11, primaryKey = true, notNull = true)
	private String partial_id;

	/**
	 * 分装数量
	 */
	@BeanField(title = "分装数量", name = "split_quantity", type = FieldType.Integer, length = 3, notNull = true)
	private String split_quantity;

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public String getSplit_quantity() {
		return split_quantity;
	}

	public void setSplit_quantity(String split_quantity) {
		this.split_quantity = split_quantity;
	}

}
