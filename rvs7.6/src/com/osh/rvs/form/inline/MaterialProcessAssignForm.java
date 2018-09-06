package com.osh.rvs.form.inline;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;

/**
 * 
 * @Title MaterialProcessAssignForm.java
 * @Project rvs
 * @Package com.osh.rvs.form.inline
 * @ClassName: MaterialProcessAssignForm
 * @Description:维修对象独有修理流程
 * @author lxb
 * @date 2015-8-19 下午3:32:26
 */
public class MaterialProcessAssignForm extends ActionForm implements	Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5047655885281548998L;

	@BeanField(title = "维修对象 ID", name = "material_id", length = 11, notNull = true, primaryKey = true)
	private String material_id;// 维修对象 ID

	@BeanField(title = "分枝", name = "line_id", length = 11, notNull = true)
	private String line_id;// 分枝

	@BeanField(title = "工位 ID", name = "position_id", length = 11, notNull = true, primaryKey = true)
	private String position_id;// 工位 ID

	@BeanField(title = "顺位标记", name = "sign_position_id", length = 11, notNull = true)
	private String sign_position_id;// 顺位标记

	@BeanField(title = "先决标记", name = "prev_position_id", length = 11, notNull = true)
	private String prev_position_id;// 先决标记

	@BeanField(title = "后位标记", name = "next_position_id", length = 11)
	private String next_position_id;// 后位标记
	
	@BeanField(title = "小修理标准编制 ID", name = "light_fix_id", length = 11)
	private String light_fix_id;//
	
	@BeanField(title = "维修流程模板ID", name = "pad_id", length = 11)
	private String pad_id;//维修流程模板ID
	
	private String level;
	
	private String fix_type;

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public String getSign_position_id() {
		return sign_position_id;
	}

	public void setSign_position_id(String sign_position_id) {
		this.sign_position_id = sign_position_id;
	}

	public String getPrev_position_id() {
		return prev_position_id;
	}

	public void setPrev_position_id(String prev_position_id) {
		this.prev_position_id = prev_position_id;
	}

	public String getNext_position_id() {
		return next_position_id;
	}

	public void setNext_position_id(String next_position_id) {
		this.next_position_id = next_position_id;
	}

	public String getLight_fix_id() {
		return light_fix_id;
	}

	public void setLight_fix_id(String light_fix_id) {
		this.light_fix_id = light_fix_id;
	}

	public String getPad_id() {
		return pad_id;
	}

	public void setPad_id(String pad_id) {
		this.pad_id = pad_id;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getFix_type() {
		return fix_type;
	}

	public void setFix_type(String fix_type) {
		this.fix_type = fix_type;
	}

}
