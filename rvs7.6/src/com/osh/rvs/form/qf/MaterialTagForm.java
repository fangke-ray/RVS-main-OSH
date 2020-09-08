package com.osh.rvs.form.qf;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 
 * @Description 维修对象属性标签
 * @author dell
 * @date 2020-9-2 上午10:56:06
 */
public class MaterialTagForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8923548152491012142L;

	@BeanField(title = "维修对象ID", name = "material_id", type = FieldType.String, length = 11, notNull = true)
	private String material_id;

	@BeanField(title = "属性标签", name = "tag_type", type = FieldType.Integer, length = 1, notNull = true)
	private String tag_type;

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getTag_type() {
		return tag_type;
	}

	public void setTag_type(String tag_type) {
		this.tag_type = tag_type;
	}

}
