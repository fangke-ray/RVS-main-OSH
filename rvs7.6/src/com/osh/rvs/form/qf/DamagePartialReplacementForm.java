package com.osh.rvs.form.qf;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 零件·组件更换说明
 * 
 * @author lxb
 * 
 */
public class DamagePartialReplacementForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6786739917087543039L;

	@BeanField(title = "维修对象ID", name = "material_id", length = 11, type = FieldType.String, primaryKey = true, notNull = true)
	private String material_id;// 维修对象ID

	@BeanField(title = "故障描述序号", name = "seq_no", type = FieldType.Integer, length = 2, notNull = true)
	private String seq_no;// 故障描述序号

	@BeanField(title = "关联故障点", name = "damage_points", type = FieldType.String, length = 20, notNull = true)
	private String damage_points;// 关联故障点

	@BeanField(title = "更换理由", name = "replace_reason", type = FieldType.Integer, length = 1, notNull = true)
	private String replace_reason;// 更换理由

	@BeanField(title = "更换零件·组件名", name = "partial_names", type = FieldType.String, length = 60, notNull = true)
	private String partial_names;// 更换零件·组件名

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

	public String getReplace_reason() {
		return replace_reason;
	}

	public void setReplace_reason(String replace_reason) {
		this.replace_reason = replace_reason;
	}

	public String getPartial_names() {
		return partial_names;
	}

	public void setPartial_names(String partial_names) {
		this.partial_names = partial_names;
	}

}
