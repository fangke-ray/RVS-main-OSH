/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：消耗品在线一览Entity<br>
 * @author 龚镭敏
 * @version 0.01
 */
package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.util.Date;
public class ConsumableOnlineEntity implements Serializable{
	
	/**
	 * 消耗品在线一览
	 */
	private static final long serialVersionUID = -2419721195186907371L;

	/**
	 * 检索用
	 */
	/* 消耗品 */
	private String consumable;
	
	/* 课室 */
	private Integer course;
	
	/* 工程 */
	private Integer project;

	/**
	 * 一览用
	 */
	/* 零件 ID */
	private Integer partial_id;
	
	/* 零件编码 */
	private String code;
	
	/* 零件说明 */
	private String name;
	
	/* 消耗品分类 */
	private Integer type;
	
	/* 当前仓库有效库存 */
	private Integer available_inventory;
	
	/* 当前线上有效库存 */
	private Integer quantity;
	
	/* 当前线上有效库存(修改用) */
	private Integer quantity_modify;
	
	/* 最后清点日期 */
	private Date adjust_time;

	public String getConsumable() {
		return consumable;
	}

	public void setConsumable(String consumable) {
		this.consumable = consumable;
	}

	public Integer getCourse() {
		return course;
	}

	public void setCourse(Integer course) {
		this.course = course;
	}

	public Integer getProject() {
		return project;
	}

	public void setProject(Integer project) {
		this.project = project;
	}

	public Integer getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(Integer partial_id) {
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getAvailable_inventory() {
		return available_inventory;
	}

	public void setAvailable_inventory(Integer available_inventory) {
		this.available_inventory = available_inventory;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getQuantity_modify() {
		return quantity_modify;
	}

	public void setQuantity_modify(Integer quantity_modify) {
		this.quantity_modify = quantity_modify;
	}

	public Date getAdjust_time() {
		return adjust_time;
	}

	public void setAdjust_time(Date adjust_time) {
		this.adjust_time = adjust_time;
	}
}
