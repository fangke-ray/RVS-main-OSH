package com.osh.rvs.form.manage;

import java.io.Serializable;
import org.apache.struts.action.ActionForm;
import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 等级型号设定
 * 
 * @author lxb
 * 
 */
public class ModelLevelSetForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5078594380022039945L;

	@BeanField(title = "型号 ID", name = "model_id", length = 11, notNull = true, primaryKey = true)
	private String model_id;// 型号 ID

	private String model_name;// 型号名称

	@BeanField(title = "等级", name = "level", length = 2, type = FieldType.Integer, notNull = true, primaryKey = true)
	private String level;// 等级

	@BeanField(title = "梯队", name = "echelon", length = 1, type = FieldType.Integer, notNull = true)
	private String echelon;// 梯队

	@BeanField(title = "计算结果拉动台数", name = "forecast_result", type = FieldType.Integer, length = 3)
	private String forecast_result;// 计算结果拉动台数

	@BeanField(title = "设置拉动台数", name = "forecast_setting", type = FieldType.Integer, length = 3)
	private String forecast_setting;// 设置拉动台数

	@BeanField(title = "拉动台数预警", name = "warn", type = FieldType.Integer)
	private String warn;// 拉动台数预警

	@BeanField(title = "采样区间开始", name = "start_date", type = FieldType.Date, notNull = true)
	private String start_date;// 采样区间开始

	@BeanField(title = "采样区间终了", name = "end_date", type = FieldType.Date, notNull = true)
	private String end_date;// 采样区间终了

	@BeanField(title = "采样受理数", name = "sampling_recept_count", type = FieldType.Integer, length = 4)
	private String sampling_recept_count;// 采样受理数

	@BeanField(title = "采样同意数", name = "sampling_agree_count", type = FieldType.Integer, length = 4)
	private String sampling_agree_count;// 采样同意数

	@BeanField(title = "潜在修理同意量", name = "agreed_count_of_potential", type = FieldType.Double)
	private String agreed_count_of_potential;// 潜在修理同意量

	@BeanField(title = "周期修理订购量", name = "order_count_of_period", type = FieldType.Double)
	private String order_count_of_period;// 周期修理订购量

	@BeanField(title = "波动系数", name = "coefficient_of_variation", type = FieldType.Double)
	private String coefficient_of_variation;// 波动系数

	@BeanField(title = "开始日期", name = "reception_time_start", type = FieldType.Date)
	private String reception_time_start01;// 开始日期

	@BeanField(title = "截止日期", name = "reception_time_end", type = FieldType.Date)
	private String reception_time_ened01;// 截止日期

	@BeanField(title = "开始日期", name = "reception_time_start", type = FieldType.Date)
	private String reception_time_start02;// 开始日期

	@BeanField(title = "截止日期", name = "reception_time_end", type = FieldType.Date)
	private String reception_time_ened02;// 截止日期

	@BeanField(title = "开始日期", name = "reception_time_start", type = FieldType.Date)
	private String reception_time_start03;// 开始日期

	@BeanField(title = "截止日期", name = "reception_time_end", type = FieldType.Date)
	private String reception_time_ened03;// 截止日期

	@BeanField(title = "开始日期", name = "reception_time_start", type = FieldType.Date)
	private String reception_time_start04;// 开始日期

	@BeanField(title = "截止日期", name = "reception_time_end", type = FieldType.Date)
	private String reception_time_ened04;// 截止日期

	@BeanField(title = "开始日期", name = "reception_time_start", type = FieldType.Date)
	private String reception_time_start05;// 开始日期

	@BeanField(title = "截止日期", name = "reception_time_end", type = FieldType.Date)
	private String reception_time_ened05;// 截止日期

	@BeanField(title = "开始日期", name = "reception_time_start", type = FieldType.Date)
	private String reception_time_start06;// 开始日期

	@BeanField(title = "截止日期", name = "reception_time_end", type = FieldType.Date)
	private String reception_time_ened06;// 截止日期

	@BeanField(title = "覆盖率", name = "coverage", type = FieldType.Integer, length = 3)
	private String coverage;

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

	public String getForecast_result() {
		return forecast_result;
	}

	public void setForecast_result(String forecast_result) {
		this.forecast_result = forecast_result;
	}

	public String getForecast_setting() {
		return forecast_setting;
	}

	public void setForecast_setting(String forecast_setting) {
		this.forecast_setting = forecast_setting;
	}

	public String getWarn() {
		return warn;
	}

	public void setWarn(String warn) {
		this.warn = warn;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getSampling_recept_count() {
		return sampling_recept_count;
	}

	public void setSampling_recept_count(String sampling_recept_count) {
		this.sampling_recept_count = sampling_recept_count;
	}

	public String getSampling_agree_count() {
		return sampling_agree_count;
	}

	public void setSampling_agree_count(String sampling_agree_count) {
		this.sampling_agree_count = sampling_agree_count;
	}

	public String getAgreed_count_of_potential() {
		return agreed_count_of_potential;
	}

	public void setAgreed_count_of_potential(String agreed_count_of_potential) {
		this.agreed_count_of_potential = agreed_count_of_potential;
	}

	public String getOrder_count_of_period() {
		return order_count_of_period;
	}

	public void setOrder_count_of_period(String order_count_of_period) {
		this.order_count_of_period = order_count_of_period;
	}

	public String getCoefficient_of_variation() {
		return coefficient_of_variation;
	}

	public void setCoefficient_of_variation(String coefficient_of_variation) {
		this.coefficient_of_variation = coefficient_of_variation;
	}

	public String getReception_time_start01() {
		return reception_time_start01;
	}

	public void setReception_time_start01(String reception_time_start01) {
		this.reception_time_start01 = reception_time_start01;
	}

	public String getReception_time_ened01() {
		return reception_time_ened01;
	}

	public void setReception_time_ened01(String reception_time_ened01) {
		this.reception_time_ened01 = reception_time_ened01;
	}

	public String getReception_time_start02() {
		return reception_time_start02;
	}

	public void setReception_time_start02(String reception_time_start02) {
		this.reception_time_start02 = reception_time_start02;
	}

	public String getReception_time_ened02() {
		return reception_time_ened02;
	}

	public void setReception_time_ened02(String reception_time_ened02) {
		this.reception_time_ened02 = reception_time_ened02;
	}

	public String getReception_time_start03() {
		return reception_time_start03;
	}

	public void setReception_time_start03(String reception_time_start03) {
		this.reception_time_start03 = reception_time_start03;
	}

	public String getReception_time_ened03() {
		return reception_time_ened03;
	}

	public void setReception_time_ened03(String reception_time_ened03) {
		this.reception_time_ened03 = reception_time_ened03;
	}

	public String getReception_time_start04() {
		return reception_time_start04;
	}

	public void setReception_time_start04(String reception_time_start04) {
		this.reception_time_start04 = reception_time_start04;
	}

	public String getReception_time_ened04() {
		return reception_time_ened04;
	}

	public void setReception_time_ened04(String reception_time_ened04) {
		this.reception_time_ened04 = reception_time_ened04;
	}

	public String getReception_time_start05() {
		return reception_time_start05;
	}

	public void setReception_time_start05(String reception_time_start05) {
		this.reception_time_start05 = reception_time_start05;
	}

	public String getReception_time_ened05() {
		return reception_time_ened05;
	}

	public void setReception_time_ened05(String reception_time_ened05) {
		this.reception_time_ened05 = reception_time_ened05;
	}

	public String getReception_time_start06() {
		return reception_time_start06;
	}

	public void setReception_time_start06(String reception_time_start06) {
		this.reception_time_start06 = reception_time_start06;
	}

	public String getReception_time_ened06() {
		return reception_time_ened06;
	}

	public void setReception_time_ened06(String reception_time_ened06) {
		this.reception_time_ened06 = reception_time_ened06;
	}

	public String getCoverage() {
		return coverage;
	}

	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}

}
