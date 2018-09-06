package com.osh.rvs.bean.qf;

import java.io.Serializable;

/**
 * 不更换项目
 * 
 * @author lxb
 * 
 */
public class SelectableRetainsEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5527906926498228652L;
	private String material_id;// 维修对象ID
	private Integer seq_no;// 序号
	private String image_uuid;// 图片

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

	public String getImage_uuid() {
		return image_uuid;
	}

	public void setImage_uuid(String image_uuid) {
		this.image_uuid = image_uuid;
	}

}
