package com.osh.rvs.form.qf;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 现品维修对象作业
 * 
 * @author liuxb
 * 
 */
public class FactMaterialForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8406210111024685941L;

	@BeanField(title = "KEY", name = "af_pf_key", type = FieldType.String, length = 11, notNull = true)
	private String af_pf_key;

	@BeanField(title = "维修对象 ID", name = "material_id", type = FieldType.String, length = 11, notNull = true)
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
