package com.osh.rvs.bean.qf;

import java.io.Serializable;

public class WipStorageEntity implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6127935500671539392L;

	// 位置
	private String wip_storage_code;

	// 货架
	private String shelf;

	// 层数
	private Integer layer;

	// 表示位置
	private String simple_code;

	// 已同意用
	private Integer for_agreed;

	// 机种
	private Integer kind;

	// 动物实验用库位
	private Integer anml_exp;

	// 占用
	private Integer occupied;

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
	public Integer getLayer() {
		return layer;
	}

	/**
	 * @param layer the 层数 to set
	 */
	public void setLayer(Integer layer) {
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
	public Integer getFor_agreed() {
		return for_agreed;
	}

	/**
	 * @param for_agreed the 已同意用 to set
	 */
	public void setFor_agreed(Integer for_agreed) {
		this.for_agreed = for_agreed;
	}

	/**
	 * @return the 机种
	 */
	public Integer getKind() {
		return kind;
	}

	/**
	 * @param kind the 机种 to set
	 */
	public void setKind(Integer kind) {
		this.kind = kind;
	}

	/**
	 * @return the 动物实验用库位
	 */
	public Integer getAnml_exp() {
		return anml_exp;
	}

	/**
	 * @param anml_exp the 动物实验用库位 to set
	 */
	public void setAnml_exp(Integer anml_exp) {
		this.anml_exp = anml_exp;
	}

	public Integer getOccupied() {
		return occupied;
	}

	public void setOccupied(Integer occupied) {
		this.occupied = occupied;
	}
}
