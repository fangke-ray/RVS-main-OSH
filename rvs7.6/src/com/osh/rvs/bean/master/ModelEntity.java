package com.osh.rvs.bean.master;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class ModelEntity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -5960636192988879037L;

	/** 维修对象型号 ID */
	private String model_id;
	/** 维修对象型号名称 */
	private String name;
	/** 维修对象机种ID */
	private String category_id;
	private String category_name;
	/** 备注1 */
	private String feature1;
	/** 备注2 */
	private String feature2;
	/** 系列 */
	private String series;
	/** 种别 */
	private String kind;

	/** 删除标记 */
	private boolean delete_flg = false;
	/** 最后更新人 */
	private String updated_by;
	/** 最后更新时间 */
	private Timestamp updated_time;

	private Integer level;// 等级

	private Integer echelon;// 梯队

	private Date avaliable_end_date;// 停止受理时间

	private Integer forecast_setting;// 拉动台数

	private Integer agree_count;// 维修同意总数

	private Integer status;// 追加记录状态

	private Integer selectable;// 是否选择性报价

	private String el_base_type;// EL 座类别

	private String s_connector_base_type;// S 连接座类别

	private String operate_part_type;// 操作部类别

	private String ocular_type;// 接眼类别

	private String item_code;// 维修对象型号代码

	private String description;// 维修对象型号描述

	private Integer imbalance;// 不平衡机型

	private String imbalance_line_id;

	/** 默认流程 ID */
	private String default_pat_id;

	/**
	 * 取得维修对象型号 ID
	 * 
	 * @return model_id 维修对象型号 ID
	 */
	public String getModel_id() {
		return model_id;
	}

	/**
	 * 维修对象型号 ID设定
	 * 
	 * @param category_id
	 *            维修对象型号 ID
	 */
	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	/**
	 * 取得维修对象机种 ID
	 * 
	 * @return category_id 维修对象机种 ID
	 */
	public String getCategory_id() {
		return category_id;
	}

	/**
	 * 维修对象机种 ID设定
	 * 
	 * @param category_id
	 *            维修对象机种 ID
	 */
	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            セットする name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return delete_flg
	 */
	public boolean isDelete_flg() {
		return delete_flg;
	}

	public String getFeature1() {
		return feature1;
	}

	public void setFeature1(String feature1) {
		this.feature1 = feature1;
	}

	public String getFeature2() {
		return feature2;
	}

	public void setFeature2(String feature2) {
		this.feature2 = feature2;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	/**
	 * @param delete_flg
	 *            セットする delete_flg
	 */
	public void setDelete_flg(boolean delete_flg) {
		this.delete_flg = delete_flg;
	}

	/**
	 * @return updated_by
	 */
	public String getUpdated_by() {
		return updated_by;
	}

	/**
	 * @param updated_by
	 *            セットする updated_by
	 */
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}

	/**
	 * @return updated_time
	 */
	public Timestamp getUpdated_time() {
		return updated_time;
	}

	/**
	 * @param updated_time
	 *            セットする updated_time
	 */
	public void setUpdated_time(Timestamp updated_time) {
		this.updated_time = updated_time;
	}

	/**
	 * 文字列化
	 * 
	 * @return 文字列
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.model_id).append(", "); //$NON-NLS-1$
		buffer.append(this.name).append(", "); //$NON-NLS-1$
		buffer.append(this.category_id).append(". "); //$NON-NLS-1$
		return buffer.toString();
	}

	/**
	 * @return category_name
	 */
	public String getCategory_name() {
		return category_name;
	}

	/**
	 * @param category_name
	 *            セットする category_name
	 */
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getEchelon() {
		return echelon;
	}

	public void setEchelon(Integer echelon) {
		this.echelon = echelon;
	}

	public Date getAvaliable_end_date() {
		return avaliable_end_date;
	}

	public void setAvaliable_end_date(Date avaliable_end_date) {
		this.avaliable_end_date = avaliable_end_date;
	}

	public Integer getForecast_setting() {
		return forecast_setting;
	}

	public void setForecast_setting(Integer forecast_setting) {
		this.forecast_setting = forecast_setting;
	}

	public Integer getAgree_count() {
		return agree_count;
	}

	public void setAgree_count(Integer agree_count) {
		this.agree_count = agree_count;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getSelectable() {
		return selectable;
	}

	public void setSelectable(Integer selectable) {
		this.selectable = selectable;
	}

	public String getEl_base_type() {
		return el_base_type;
	}

	public void setEl_base_type(String el_base_type) {
		this.el_base_type = el_base_type;
	}

	public String getS_connector_base_type() {
		return s_connector_base_type;
	}

	public void setS_connector_base_type(String s_connector_base_type) {
		this.s_connector_base_type = s_connector_base_type;
	}

	public String getOperate_part_type() {
		return operate_part_type;
	}

	public void setOperate_part_type(String operate_part_type) {
		this.operate_part_type = operate_part_type;
	}

	public String getOcular_type() {
		return ocular_type;
	}

	public void setOcular_type(String ocular_type) {
		this.ocular_type = ocular_type;
	}

	public String getItem_code() {
		return item_code;
	}

	public void setItem_code(String item_code) {
		this.item_code = item_code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getImbalance() {
		return imbalance;
	}

	public void setImbalance(Integer imbalance) {
		this.imbalance = imbalance;
	}

	public String getImbalance_line_id() {
		return imbalance_line_id;
	}

	public void setImbalance_line_id(String imbalance_line_id) {
		this.imbalance_line_id = imbalance_line_id;
	}

	public String getDefault_pat_id() {
		return default_pat_id;
	}

	public void setDefault_pat_id(String default_pat_id) {
		this.default_pat_id = default_pat_id;
	}

}
