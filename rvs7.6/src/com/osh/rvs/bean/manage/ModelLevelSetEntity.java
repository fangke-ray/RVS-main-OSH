package com.osh.rvs.bean.manage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ModelLevelSetEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4289347785216353709L;

	private String model_id;// 型号 ID

	private String model_name;// 型号名称

	private Integer level;// 等级

	private Integer echelon;// 梯队

	private Integer forecast_result;// 计算结果拉动台数

	private Integer forecast_setting;// 设置拉动台数

	private Integer warn;// 拉动台数预警

	private Date start_date;// 采样区间开始

	private Date end_date;// 采样区间终了

	private Integer sampling_recept_count;// 采样受理数

	private Integer sampling_agree_count;// 采样同意数

	private BigDecimal agreed_count_of_potential;// 潜在修理同意量

	private BigDecimal order_count_of_period;// 周期修理订购量

	private BigDecimal coefficient_of_variation;// 波动系数

	private Integer coverage;// 覆盖率

	private Integer kind;// 机种

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
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

	public Integer getForecast_result() {
		return forecast_result;
	}

	public void setForecast_result(Integer forecast_result) {
		this.forecast_result = forecast_result;
	}

	public Integer getForecast_setting() {
		return forecast_setting;
	}

	public void setForecast_setting(Integer forecast_setting) {
		this.forecast_setting = forecast_setting;
	}

	public Integer getWarn() {
		return warn;
	}

	public void setWarn(Integer warn) {
		this.warn = warn;
	}

	public Date getStart_date() {
		return start_date;
	}

	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}

	public Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}

	public Integer getSampling_recept_count() {
		return sampling_recept_count;
	}

	public void setSampling_recept_count(Integer sampling_recept_count) {
		this.sampling_recept_count = sampling_recept_count;
	}

	public Integer getSampling_agree_count() {
		return sampling_agree_count;
	}

	public void setSampling_agree_count(Integer sampling_agree_count) {
		this.sampling_agree_count = sampling_agree_count;
	}

	public BigDecimal getAgreed_count_of_potential() {
		return agreed_count_of_potential;
	}

	public void setAgreed_count_of_potential(
			BigDecimal agreed_count_of_potential) {
		this.agreed_count_of_potential = agreed_count_of_potential;
	}

	public BigDecimal getOrder_count_of_period() {
		return order_count_of_period;
	}

	public void setOrder_count_of_period(BigDecimal order_count_of_period) {
		this.order_count_of_period = order_count_of_period;
	}

	public BigDecimal getCoefficient_of_variation() {
		return coefficient_of_variation;
	}

	public void setCoefficient_of_variation(BigDecimal coefficient_of_variation) {
		this.coefficient_of_variation = coefficient_of_variation;
	}

	public Integer getCoverage() {
		return coverage;
	}

	public void setCoverage(Integer coverage) {
		this.coverage = coverage;
	}

	public Integer getKind() {
		return kind;
	}

	public void setKind(Integer kind) {
		this.kind = kind;
	}

}
