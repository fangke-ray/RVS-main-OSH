package com.osh.rvs.form.partial;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 零件出库作业数
 * 
 * @author liux
 * 
 */
public class FactPartialReleaseForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5987771604355755673L;

	@BeanField(title = "KEY", name = "af_pf_key", type = FieldType.String, length = 11, notNull = true)
	private String af_pf_key;

	@BeanField(title = "维修对象 ID", name = "material_id", type = FieldType.String, length = 11, notNull = true)
	private String material_id;

	@BeanField(title = "规格种别", name = "spec_kind", type = FieldType.Integer, length = 1, notNull = true)
	private String spec_kind;

	@BeanField(title = "作业数量", name = "quantity", type = FieldType.Integer, length = 5, notNull = true)
	private String quantity;

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

}
