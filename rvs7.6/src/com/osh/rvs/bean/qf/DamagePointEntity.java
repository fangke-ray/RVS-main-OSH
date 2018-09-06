package com.osh.rvs.bean.qf;

import java.io.Serializable;

/**
 * 故障内容
 * 
 * @author lxb
 * 
 */
public class DamagePointEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 480059957108648284L;
	private String material_id;// 维修对象ID
	private Integer seq_no;// 故障点序号
	private String damage_points;// 故障点代码
	private String content;// 故障点内容
	private Integer check_result;// ＯＬＹＭＰＵＳ确认结果

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getCheck_result() {
		return check_result;
	}

	public void setCheck_result(Integer check_result) {
		this.check_result = check_result;
	}

}
