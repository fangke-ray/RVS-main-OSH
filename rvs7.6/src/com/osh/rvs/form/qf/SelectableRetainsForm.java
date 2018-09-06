package com.osh.rvs.form.qf;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.form.qf
 * @ClassName: SelectableRetainsForm
 * @Description: 不更换项目Form
 * @author lxb
 * @date 2014-6-11 上午11:39:27
 * 
 */

public class SelectableRetainsForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1473650948202248588L;

	@BeanField(title = "维修对象ID", name = "material_id", type = FieldType.String, length = 11, primaryKey = true, notNull = true)
	private String material_id;// 维修对象ID

	@BeanField(title = "序号", name = "seq_no", type = FieldType.Integer, length = 1, primaryKey = true, notNull = true)
	private String seq_no;// 序号

	@BeanField(title = "图片", name = "image_uuid", type = FieldType.String, length = 32)
	private String image_uuid;// 图片

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getSeq_no() {
		return seq_no;
	}

	public void setSeq_no(String seq_no) {
		this.seq_no = seq_no;
	}

	public String getImage_uuid() {
		return image_uuid;
	}

	public void setImage_uuid(String image_uuid) {
		this.image_uuid = image_uuid;
	}

}
