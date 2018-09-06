package com.osh.rvs.bean.qf;

import java.io.Serializable;

/**
 * 零件·组件更换说明
 * 
 * @author lxb
 * 
 */
public class DamagePartialReplacementEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5597420416076879681L;
	private String material_id;// 维修对象ID
	private Integer seq_no;// 故障描述序
	private String damage_points;// 关联故障点
	private Integer replace_reason;// 更换理由
	private String partial_names;// 更换零件·组件名

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

	public String getDamage_points() {
		return damage_points;
	}

	public void setDamage_points(String damage_points) {
		this.damage_points = damage_points;
	}

	public Integer getReplace_reason() {
		return replace_reason;
	}

	public void setReplace_reason(Integer replace_reason) {
		this.replace_reason = replace_reason;
	}

	public String getPartial_names() {
		return partial_names;
	}

	public void setPartial_names(String partial_names) {
		this.partial_names = partial_names;
	}

}
