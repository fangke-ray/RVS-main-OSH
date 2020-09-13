package com.osh.rvs.bean.data;

import java.io.Serializable;

/**
 * 
 * @Description 维修对象属性标签
 * @author dell
 * @date 2020-9-2 上午10:52:54
 */
public class MaterialTagEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8769095850257617841L;

	/** 维修对象ID **/
	private String material_id;

	/** 属性标签 **/
	private Integer tag_type;

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public Integer getTag_type() {
		return tag_type;
	}

	public void setTag_type(Integer tag_type) {
		this.tag_type = tag_type;
	}

}
