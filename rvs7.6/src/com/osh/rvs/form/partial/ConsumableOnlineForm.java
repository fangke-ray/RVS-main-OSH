/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：消耗品在线一览Form<br>
 * @author 龚镭敏
 * @version 0.01
 */
package com.osh.rvs.form.partial;
import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class ConsumableOnlineForm extends ActionForm implements Serializable{

	/**
	 * 消耗品在线一览
	 */
	private static final long serialVersionUID = -116552365716715639L;

	/**
	 * 检索用
	 */
	/* 消耗品 */
	@BeanField(title = "消耗品", name = "consumable", type = FieldType.String, length = 9)
	private String consumable;

	/* 课室 */
	@BeanField(title = "课室", name = "course", type = FieldType.Integer, length = 11, notNull = true)
	private String course;

	/* 工程 */
	@BeanField(title = "工程", name = "project", type = FieldType.Integer, length = 11, notNull = true)
	private String project;
	
	/**
	 * 一览用
	 */
	/* 零件 ID */
	@BeanField(title = "零件 ID", name = "partial_id", type = FieldType.Integer, length = 11, notNull = true)
	private String partial_id;

	/* 零件编码 */
	@BeanField(title = "零件编码", name = "code", type = FieldType.String, length = 9, notNull = true)
	private String code;

	/* 零件说明 */
	@BeanField(title = "零件说明", name = "name", type = FieldType.String, length = 100)
	private String name;

	/* 消耗品分类 */
	@BeanField(title = "消耗品分类", name = "type", type = FieldType.Integer, length = 1, notNull = true)
	private String type;
	
	/* 当前仓库有效库存 */
	@BeanField(title = "当前仓库有效库存", name = "available_inventory", type = FieldType.Integer, length = 5, notNull = true)
	private String available_inventory;
	
	/* 当前线上有效库存 */
	@BeanField(title = "当前线上有效库存", name = "quantity", type = FieldType.Integer, length = 5, notNull = true)
	private String quantity;
	
	/* 当前线上有效库存(修改用) */
	@BeanField(title = "当前线上有效库存(修改用)", name = "quantity_modify", type = FieldType.Integer, length = 5, notNull = true)
	private String quantity_modify;
	
	/* 最后清点日期 */
	@BeanField(title = "最后清点日期", name = "adjust_time", type = FieldType.Date)
	private String adjust_time;

	public String getConsumable() {
		return consumable;
	}

	public void setConsumable(String consumable) {
		this.consumable = consumable;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAvailable_inventory() {
		return available_inventory;
	}

	public void setAvailable_inventory(String available_inventory) {
		this.available_inventory = available_inventory;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getQuantity_modify() {
		return quantity_modify;
	}

	public void setQuantity_modify(String quantity_modify) {
		this.quantity_modify = quantity_modify;
	}

	public String getAdjust_time() {
		return adjust_time;
	}

	public void setAdjust_time(String adjust_time) {
		this.adjust_time = adjust_time;
	}

}
