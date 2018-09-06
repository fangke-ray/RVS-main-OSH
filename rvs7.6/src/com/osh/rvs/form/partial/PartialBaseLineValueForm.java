package com.osh.rvs.form.partial;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class PartialBaseLineValueForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4908493820416846263L;

	@BeanField(title = "零件ID", name = "partial_id", length = 11, notNull = true)
	private String partial_id;// 零件ID

	@BeanField(title = "零件编号", name = "partial_code", length = 9)
	private String partial_code;// 零件编号

	@BeanField(title = "零件名称", name = "partial_name", length = 100)
	private String partial_name;// 零件名称

	@BeanField(title = "前半年平均使用量", name = "quantityOfHalfYear", type = FieldType.Double)
	private String quantityOfHalfYear;// 前半年平均使用量

	@BeanField(title = "前三个月平均使用量", name = "quantityOfThreeMonthAge", type = FieldType.Double)
	private String quantityOfThreeMonthAge;// 前三个月平均使用量

	@BeanField(title = "前一个月平均使用量", name = "quantityOfOneMonthAge", type = FieldType.Double)
	private String quantityOfOneMonthAge;// 前一个月平均使用量

	@BeanField(title = "当前基准值设定", name = "total_foreboard_count", type = FieldType.Integer, length = 3)
	private String total_foreboard_count;// 当前基准值设定
	
	@BeanField(title="型号名称",name="modelName",length=50)
	private String modelName;//型号名称
	
	@BeanField(title="等级",name="level",type=FieldType.Integer,length=1,notNull=true)
	private String level;//等级
	
	@BeanField(title="拉动台数",name="forecast_setting",type=FieldType.Integer,length=3)
	private String forecast_setting;//拉动台数
	
	@BeanField(title="标准零件使用数",name="countOfStandardPartial",type=FieldType.Integer,length=2,notNull=true)
	private String countOfStandardPartial;//标准零件使用数
	
	@BeanField(title="梯队",name="echelon",type=FieldType.Integer,length=1,notNull=true)
	private String echelon;//梯队
	
	@BeanField(title="基准值(OSH)",name="osh_foreboard_count",type=FieldType.Integer,length=3)
	private String	 osh_foreboard_count;//基准值(OSH)
	
	@BeanField(title="有效区间终了",name="end_date",type=FieldType.Date,notNull=true)
	private String end_date;//有效区间终了
	
	@BeanField(title="SORCWH基准值" ,name="sorcwh_foreboard_count",type=FieldType.Integer,length=3)
	private String  sorcwh_foreboard_count;//SORCWH基准值
    
	@BeanField(title="wh2pWH基准值" ,name="wh2p_foreboard_count",type=FieldType.Integer,length=3)
    private String  wh2p_foreboard_count;//wh2pWH基准值
    
	@BeanField(title="消耗品库存基准值" ,name="consumable_foreboard_count",type=FieldType.Integer,length=4)
    private String consumable_foreboard_count;//消耗品库存基准值
    
	@BeanField(title="OGZ基准值" ,name="ogz_foreboard_count",type=FieldType.Integer,length=3)
    private String ogz_foreboard_count;//OGZ基准值
	
	@BeanField(title="采样周期开始",name="supply_date_start",type=FieldType.Date)
	private String supply_date_start;//采样周期开始
	
	@BeanField(title="采样周期结束",name="supply_date_end",type=FieldType.Date)
	private String supply_date_end;//采样周期结束
	
	@BeanField(title="第一梯队周期平均使用量",name="echelon1OfAverage",type=FieldType.Double)
	private String echelon1OfAverage;//第一梯队周期平均使用量
	  
	@BeanField(title="第二梯队周期平均使用量",name="echelon2OfAverage",type=FieldType.Double)
	private String echelon2OfAverage;//第二梯队周期平均使用量
	   
	@BeanField(title="第三梯队周期平均使用量",name="echelon3OfAverage",type=FieldType.Double)
    private String echelon3OfAverage;//第三梯队周期平均使用量
	  
	@BeanField(title="第四梯队周期平均使用量",name="echelon4OfAverage",type=FieldType.Double)
    private String echelon4OfAverage;//第四梯队周期平均使用量
	
	@BeanField(title="周期折算使用量合计",name="echelonOfAverage",type=FieldType.Double)
	private String echelonOfAverage;//周期折算使用量合计
	
	@BeanField(title="订购平均数",name="orderCountOfAverage",type=FieldType.Integer)
	private String orderCountOfAverage;//订购平均数
	
	@BeanField(title="SORCWH 基准值设定时间",name="sorcwh_end_date",type=FieldType.Date)
	private String sorcwh_end_date;//SORCWH 基准值设定时间
	
	@BeanField(title="WH2P 基准值设定时间",name="wh2p_end_date",type=FieldType.Date)
	private String wh2p_end_date;//WH2P 基准值设定时间
	
	@BeanField(title="消耗品库存 基准值设定时间",name="consumble_end_date",type=FieldType.Date)
	private String consumble_end_date;//消耗品库存 基准值设定时间
	
	@BeanField(title="ogz 基准值设定时间",name="ogz_end_date",type=FieldType.Date)
	private String  ogz_end_date;//ogz 基准值设定时间
	
	@BeanField(title="设定类型",name="identification",type=FieldType.Integer,length=1,primaryKey=true)
	private String identification;//设定类型
	
	@BeanField(title="最后更新人ID",name="updated_by",type=FieldType.Integer,length=11,notNull=true)
	private String updated_by;//最后更新人ID
	
	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public String getPartial_code() {
		return partial_code;
	}

	public void setPartial_code(String partial_code) {
		this.partial_code = partial_code;
	}

	public String getPartial_name() {
		return partial_name;
	}

	public void setPartial_name(String partial_name) {
		this.partial_name = partial_name;
	}

	public String getQuantityOfHalfYear() {
		return quantityOfHalfYear;
	}

	public void setQuantityOfHalfYear(String quantityOfHalfYear) {
		this.quantityOfHalfYear = quantityOfHalfYear;
	}

	public String getQuantityOfThreeMonthAge() {
		return quantityOfThreeMonthAge;
	}

	public void setQuantityOfThreeMonthAge(String quantityOfThreeMonthAge) {
		this.quantityOfThreeMonthAge = quantityOfThreeMonthAge;
	}

	public String getQuantityOfOneMonthAge() {
		return quantityOfOneMonthAge;
	}

	public void setQuantityOfOneMonthAge(String quantityOfOneMonthAge) {
		this.quantityOfOneMonthAge = quantityOfOneMonthAge;
	}

	public String getTotal_foreboard_count() {
		return total_foreboard_count;
	}

	public void setTotal_foreboard_count(String total_foreboard_count) {
		this.total_foreboard_count = total_foreboard_count;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getForecast_setting() {
		return forecast_setting;
	}

	public void setForecast_setting(String forecast_setting) {
		this.forecast_setting = forecast_setting;
	}

	public String getCountOfStandardPartial() {
		return countOfStandardPartial;
	}

	public void setCountOfStandardPartial(String countOfStandardPartial) {
		this.countOfStandardPartial = countOfStandardPartial;
	}

	public String getEchelon() {
		return echelon;
	}

	public void setEchelon(String echelon) {
		this.echelon = echelon;
	}

	public String getOsh_foreboard_count() {
		return osh_foreboard_count;
	}

	public void setOsh_foreboard_count(String osh_foreboard_count) {
		this.osh_foreboard_count = osh_foreboard_count;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getSorcwh_foreboard_count() {
		return sorcwh_foreboard_count;
	}

	public void setSorcwh_foreboard_count(String sorcwh_foreboard_count) {
		this.sorcwh_foreboard_count = sorcwh_foreboard_count;
	}

	public String getWh2p_foreboard_count() {
		return wh2p_foreboard_count;
	}

	public void setWh2p_foreboard_count(String wh2p_foreboard_count) {
		this.wh2p_foreboard_count = wh2p_foreboard_count;
	}

	public String getConsumable_foreboard_count() {
		return consumable_foreboard_count;
	}

	public void setConsumable_foreboard_count(String consumable_foreboard_count) {
		this.consumable_foreboard_count = consumable_foreboard_count;
	}

	public String getOgz_foreboard_count() {
		return ogz_foreboard_count;
	}

	public void setOgz_foreboard_count(String ogz_foreboard_count) {
		this.ogz_foreboard_count = ogz_foreboard_count;
	}

	public String getSupply_date_start() {
		return supply_date_start;
	}

	public void setSupply_date_start(String supply_date_start) {
		this.supply_date_start = supply_date_start;
	}

	public String getSupply_date_end() {
		return supply_date_end;
	}

	public void setSupply_date_end(String supply_date_end) {
		this.supply_date_end = supply_date_end;
	}

	public String getEchelon1OfAverage() {
		return echelon1OfAverage;
	}

	public void setEchelon1OfAverage(String echelon1OfAverage) {
		this.echelon1OfAverage = echelon1OfAverage;
	}

	public String getEchelon2OfAverage() {
		return echelon2OfAverage;
	}

	public void setEchelon2OfAverage(String echelon2OfAverage) {
		this.echelon2OfAverage = echelon2OfAverage;
	}

	public String getEchelon3OfAverage() {
		return echelon3OfAverage;
	}

	public void setEchelon3OfAverage(String echelon3OfAverage) {
		this.echelon3OfAverage = echelon3OfAverage;
	}

	public String getEchelon4OfAverage() {
		return echelon4OfAverage;
	}

	public void setEchelon4OfAverage(String echelon4OfAverage) {
		this.echelon4OfAverage = echelon4OfAverage;
	}

	public String getEchelonOfAverage() {
		return echelonOfAverage;
	}

	public void setEchelonOfAverage(String echelonOfAverage) {
		this.echelonOfAverage = echelonOfAverage;
	}

	public String getOrderCountOfAverage() {
		return orderCountOfAverage;
	}

	public void setOrderCountOfAverage(String orderCountOfAverage) {
		this.orderCountOfAverage = orderCountOfAverage;
	}

	public String getSorcwh_end_date() {
		return sorcwh_end_date;
	}

	public void setSorcwh_end_date(String sorcwh_end_date) {
		this.sorcwh_end_date = sorcwh_end_date;
	}

	public String getWh2p_end_date() {
		return wh2p_end_date;
	}

	public void setWh2p_end_date(String wh2p_end_date) {
		this.wh2p_end_date = wh2p_end_date;
	}

	public String getConsumble_end_date() {
		return consumble_end_date;
	}

	public void setConsumble_end_date(String consumble_end_date) {
		this.consumble_end_date = consumble_end_date;
	}

	public String getOgz_end_date() {
		return ogz_end_date;
	}

	public void setOgz_end_date(String ogz_end_date) {
		this.ogz_end_date = ogz_end_date;
	}

	public String getIdentification() {
		return identification;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}

	public String getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}
	
}
