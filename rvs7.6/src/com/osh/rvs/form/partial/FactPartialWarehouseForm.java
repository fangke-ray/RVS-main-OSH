package com.osh.rvs.form.partial;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 现品入库作业数
 * 
 * @author liuxb
 * 
 */
public class FactPartialWarehouseForm extends ActionForm {

	/**
	 *
	 */
	private static final long serialVersionUID = 3734706389406616400L;

	@BeanField(title = "KEY", name = "af_pf_key", length = 11, notNull = true)
	private String af_pf_key;

	@BeanField(title = "零件入库单 KEY", name = "partial_warehouse_key", length = 11, notNull = true)
	private String partial_warehouse_key;

	@BeanField(title = "规格种别", name = "spec_kind", type = FieldType.Integer, length = 1, notNull = true)
	private String spec_kind;

	@BeanField(title = "作业数量", name = "quantity", type = FieldType.Integer, length = 5, notNull = true)
	private String quantity;

	@BeanField(title = "作业内容", name = "production_type", type = FieldType.Integer)
	private String production_type;

	public String getAf_pf_key() {
		return af_pf_key;
	}

	public void setAf_pf_key(String af_pf_key) {
		this.af_pf_key = af_pf_key;
	}

	public String getPartial_warehouse_key() {
		return partial_warehouse_key;
	}

	public void setPartial_warehouse_key(String partial_warehouse_key) {
		this.partial_warehouse_key = partial_warehouse_key;
	}

	public String getSpec_kind() {
		return spec_kind;
	}

	public void setSpec_kind(String spec_kind) {
		this.spec_kind = spec_kind;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getProduction_type() {
		return production_type;
	}

	public void setProduction_type(String production_type) {
		this.production_type = production_type;
	}

}
