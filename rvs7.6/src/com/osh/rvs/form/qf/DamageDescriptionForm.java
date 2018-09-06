package com.osh.rvs.form.qf;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 故障描述
 * 
 * @author lxb
 * 
 */
public class DamageDescriptionForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3815269951984200923L;
	@BeanField(title = "维修对象ID", name = "material_id", type = FieldType.String, length = 11, primaryKey = true, notNull = true)
	private String material_id;// 维修对象ID

	@BeanField(title = "故障描述序号", name = "seq_no", type = FieldType.Integer, length = 2, primaryKey = true, notNull = true)
	private String seq_no;// 故障描述序号

	@BeanField(title = "故障描述内容", name = "content", type = FieldType.String, length = 160,notNull=true)
	private String content;// 故障描述内容

	@BeanField(title = "图片", name = "image_uuid", type = FieldType.String, length = 32)
	private String image_uuid;// 图片

	@BeanField(title = "图片说明", name = "image_intro", type = FieldType.String, length = 30)
	private String image_intro;// 图片说明

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImage_uuid() {
		return image_uuid;
	}

	public void setImage_uuid(String image_uuid) {
		this.image_uuid = image_uuid;
	}

	public String getImage_intro() {
		return image_intro;
	}

	public void setImage_intro(String image_intro) {
		this.image_intro = image_intro;
	}

}
