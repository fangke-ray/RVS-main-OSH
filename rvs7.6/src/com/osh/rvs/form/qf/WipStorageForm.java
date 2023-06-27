package com.osh.rvs.form.qf;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class WipStorageForm extends ActionForm implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6127935500671539392L;

	@BeanField(title = "位置", name = "wip_storage_code", length = 6, primaryKey = true)
	private String wip_storage_code;

	@BeanField(title = "货架", name = "shelf", length = 2, notNull = true)
	private String shelf;

	@BeanField(title = "层数", name = "layer", length = 2, notNull = true, type=FieldType.Integer)
	private String layer;

	@BeanField(title = "表示位置", name = "simple_code", length = 6, notNull = true)
	private String simple_code;

	@BeanField(title = "已同意用", name = "for_agreed", length = 2, notNull = true, type=FieldType.Integer)
	private String for_agreed;

	@BeanField(title = "机种", name = "kind", length = 1, notNull = true, type=FieldType.Integer)
	private String kind;

	@BeanField(title = "动物实验用库位", name = "anml_exp", length = 1, notNull = true, type=FieldType.Integer)
	private String anml_exp;

	@BeanField(title = "占用", name = "occupied", length = 1, type=FieldType.Integer)
	private String occupied;

	private String material_id;

	private String origin_wip_storage_code;

	/**
	 * @return the 位置
	 */
	public String getWip_storage_code() {
		return wip_storage_code;
	}

	/**
	 * @param wip_storage_code the 位置 to set
	 */
	public void setWip_storage_code(String wip_storage_code) {
		this.wip_storage_code = wip_storage_code;
	}

	/**
	 * @return the 货架
	 */
	public String getShelf() {
		return shelf;
	}

	/**
	 * @param shelf the 货架 to set
	 */
	public void setShelf(String shelf) {
		this.shelf = shelf;
	}

	/**
	 * @return the 层数
	 */
	public String getLayer() {
		return layer;
	}

	/**
	 * @param layer the 层数 to set
	 */
	public void setLayer(String layer) {
		this.layer = layer;
	}

	/**
	 * @return the 表示位置
	 */
	public String getSimple_code() {
		return simple_code;
	}

	/**
	 * @param simple_code the 表示位置 to set
	 */
	public void setSimple_code(String simple_code) {
		this.simple_code = simple_code;
	}

	/**
	 * @return the 已同意用
	 */
	public String getFor_agreed() {
		return for_agreed;
	}

	/**
	 * @param for_agreed the 已同意用 to set
	 */
	public void setFor_agreed(String for_agreed) {
		this.for_agreed = for_agreed;
	}

	/**
	 * @return the 机种
	 */
	public String getKind() {
		return kind;
	}

	/**
	 * @param kind the 机种 to set
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}

	/**
	 * @return the 动物实验用库位
	 */
	public String getAnml_exp() {
		return anml_exp;
	}

	/**
	 * @param anml_exp the 动物实验用库位 to set
	 */
	public void setAnml_exp(String anml_exp) {
		this.anml_exp = anml_exp;
	}

	/**
	 * @return the 占用
	 */
	public String getOccupied() {
		return occupied;
	}

	/**
	 * @param occupied the 占用 to set
	 */
	public void setOccupied(String occupied) {
		this.occupied = occupied;
	}

	/**
	 * @return the material_id
	 */
	public String getMaterial_id() {
		return material_id;
	}

	/**
	 * @param material_id the material_id to set
	 */
	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getOrigin_wip_storage_code() {
		return origin_wip_storage_code;
	}

	public void setOrigin_wip_storage_code(String origin_wip_storage_code) {
		this.origin_wip_storage_code = origin_wip_storage_code;
	}

}
