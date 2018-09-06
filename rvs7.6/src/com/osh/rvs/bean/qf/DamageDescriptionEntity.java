package com.osh.rvs.bean.qf;

import java.io.Serializable;

/**
 * 故障描述
 * 
 * @author lxb
 * 
 */
public class DamageDescriptionEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4671139730908095088L;
	private String material_id;// 维修对象ID
	private Integer seq_no;// 故障描述序号
	private String content;// 故障描述内容
	private String image_uuid;// 图片
	private String image_intro;// 图片说明

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public Integer getSeq_no() {
		return seq_no;
	}

	public void setSeq_no(Integer seq_no) {
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
