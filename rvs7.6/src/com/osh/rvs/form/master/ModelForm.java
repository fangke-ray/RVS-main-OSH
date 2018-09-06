package com.osh.rvs.form.master;

import java.util.List;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;
import framework.huiqing.common.util.AutofillArrayList;

public class ModelForm extends ActionForm {

	private static final long serialVersionUID = 2413471756814818982L;

	/** 维修对象型号 ID */
	@BeanField(title = "维修对象型号 ID", name = "model_id", primaryKey = true, length = 11)
	private String id;
	/** 维修对象型号名称 */
	@BeanField(title = "维修对象型号名称", name = "name", length = 50, notNull = true)
	private String name;
	/** 维修对象类别ID */
	@BeanField(title = "维修对象类别", name = "category_id", length = 11, notNull = true)
	private String category_id;
	private String category_name;
	private String kind;
	/** 备注1 */
	@BeanField(title = "备注1", name = "feature1", length = 16)
	private String feature1;
	/** 备注2 */
	@BeanField(title = "备注2", name = "feature2", length = 16)
	private String feature2;
	/** 系列 */
	@BeanField(title = "系列", name = "series", length = 16)
	private String series;
	/** 最后更新人 */
	@BeanField(title = "更新者", name = "updated_by")
	private String updated_by;
	/** 最后更新时间 */
	@BeanField(title = "更新时间", name = "updated_time", type = FieldType.TimeStamp)
	private String updated_time;

	@BeanField(title = "等级", name = "level", type = FieldType.Integer, length = 1)
	private String level;// 等级

	@BeanField(title = "梯队", name = "echelon", type = FieldType.Integer, length = 1)
	private String echelon;// 梯队

	@BeanField(title = "停止受理时间", name = "avaliable_end_date", type = FieldType.Date)
	private String avaliable_end_date;// 停止受理时间

	@BeanField(title = "拉动台数", name = "forecast_setting", type = FieldType.Integer, length = 3)
	private String forecast_setting;// 拉动台数

	@BeanField(title = "维修同意总数", name = "agree_count", type = FieldType.Integer)
	private String agree_count;// 维修同意总数

	@BeanField(title = "追加记录状态", name = "status", type = FieldType.Integer)
	private String status;// 追加记录状态

	@BeanField(title = "是否选择性报价", name = "selectable", type = FieldType.Integer, length = 1, notNull = true)
	private String selectable;// 是否选择性报价

	@BeanField(title = "EL 座类别", name = "el_base_type", length = 16)
	private String el_base_type;// EL 座类别

	@BeanField(title = "S 连接座类别", name = "s_connector_base_type", length = 16)
	private String s_connector_base_type;// S 连接座类别

	@BeanField(title = "操作部类别", name = "operate_part_type", length = 16)
	private String operate_part_type;// 操作部类别

	@BeanField(title = "接眼类别", name = "ocular_type", length = 16)
	private String ocular_type;// 接眼类别

	@BeanField(title = "维修对象型号代码", name = "item_code", length = 50)
	private String item_code;// 维修对象型号代码

	@BeanField(title = "维修对象型号描述", name = "description", length = 120)
	private String description;// 维修对象型号描述

	/** 默认流程 ID */
	@BeanField(title = "默认流程 ID", name = "default_pat_id", length = 11)
	private String default_pat_id;

//	@BeanField(title = "不平衡机型", name = "imbalance", type = FieldType.Integer, length = 1)
//	private String imbalance;// 不平衡机型

	@BeanField(title = "不平衡机型", name = "imbalance_line_id")
	private String imbalance_line_id;// 不平衡机型

	private List<String> imbalance_line_ids = new AutofillArrayList<String>(String.class);

	/**
	 * 取得维修对象型号名称
	 * 
	 * @return name 维修对象型号名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 维修对象型号名称设定
	 * 
	 * @param name
	 *            维修对象型号名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 取得维修对象型号 ID
	 * 
	 * @return category_id 维修对象型号 ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 维修对象型号 ID设定
	 * 
	 * @param id
	 *            维修对象型号 ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 取得最后更新人
	 * 
	 * @return updated_by 最后更新人
	 */
	public String getUpdated_by() {
		return updated_by;
	}

	/**
	 * 最后更新人设定
	 * 
	 * @param updated_by
	 *            最后更新人
	 */
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}

	/**
	 * 取得最后更新时间
	 * 
	 * @return updated_time 最后更新时间
	 */
	public String getUpdated_time() {
		return updated_time;
	}

	/**
	 * 最后更新时间设定
	 * 
	 * @param updated_time
	 *            最后更新时间
	 */
	public void setUpdated_time(String updated_time) {
		this.updated_time = updated_time;
	}

	/**
	 * 取得维修对象类别
	 * 
	 * @return category 维修对象类别
	 */
	public String getCategory_id() {
		return category_id;
	}

	/**
	 * 维修对象类别设定
	 * 
	 * @param category
	 *            维修对象类别
	 */
	public void setCategory_id(String category_id) {
		this.category_id = category_id;
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

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getEchelon() {
		return echelon;
	}

	public void setEchelon(String echelon) {
		this.echelon = echelon;
	}

	public String getAvaliable_end_date() {
		return avaliable_end_date;
	}

	public void setAvaliable_end_date(String avaliable_end_date) {
		this.avaliable_end_date = avaliable_end_date;
	}

	public String getForecast_setting() {
		return forecast_setting;
	}

	public void setForecast_setting(String forecast_setting) {
		this.forecast_setting = forecast_setting;
	}

	public String getAgree_count() {
		return agree_count;
	}

	public void setAgree_count(String agree_count) {
		this.agree_count = agree_count;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSelectable() {
		return selectable;
	}

	public void setSelectable(String selectable) {
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

	public String getDefault_pat_id() {
		return default_pat_id;
	}

	public void setDefault_pat_id(String default_pat_id) {
		this.default_pat_id = default_pat_id;
	}

//	public String getImbalance() {
//		return imbalance;
//	}
//
//	public void setImbalance(String imbalance) {
//		this.imbalance = imbalance;
//	}

	public String getImbalance_line_id() {
		return imbalance_line_id;
	}

	public void setImbalance_line_id(String imbalance_line_id) {
		this.imbalance_line_id = imbalance_line_id;
	}

	public List<String> getImbalance_line_ids() {
		return imbalance_line_ids;
	}

	public void setImbalance_line_ids(List<String> imbalance_line_ids) {
		this.imbalance_line_ids = imbalance_line_ids;
	}
}
