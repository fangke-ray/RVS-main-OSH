package com.osh.rvs.form.qf;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 故障内容
 * 
 * @author lxb
 * 
 */
public class DamagePointForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5216511233410125480L;

	@BeanField(title = "维修对象ID", name = "material_id", length = 11, primaryKey = true, notNull = true)
	private String material_id;// 维修对象ID

	@BeanField(title = "故障点序号", name = "seq_no", type = FieldType.Integer, length = 2, primaryKey = true, notNull = true)
	private String seq_no;// 故障点序号

	@BeanField(title = "故障点代码", name = "damage_points", type = FieldType.String, length = 4, notNull = true)
	private String damage_points;// 故障点代码

	@BeanField(title = "故障点内容", name = "content")
	private String content;// 故障点内容

	@BeanField(title = "ＯＬＹＭＰＵＳ确认结果", name = "check_result", type = FieldType.Integer, length = 1, notNull = true)
	private String check_result;// ＯＬＹＭＰＵＳ确认结果

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

	public String getCheck_result() {
		return check_result;
	}

	public void setCheck_result(String check_result) {
		this.check_result = check_result;
	}

}
