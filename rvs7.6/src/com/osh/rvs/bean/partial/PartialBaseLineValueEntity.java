package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PartialBaseLineValueEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5438220032007384642L;

	private String partial_id;// 零件ID

	private String partial_code;// 零件编号

	private String partial_name;// 零件名称

	private BigDecimal quantityOfHalfYear;// 前半年平均使用量

	private BigDecimal quantityOfThreeMonthAge;// 前三个月平均使用量

	private BigDecimal quantityOfOneMonthAge;// 当月平均使用量

	private Integer total_foreboard_count;// 当前基准值设定

	private Integer flg;//
	
	private String modelName;//型号名称
	
	private Integer level;//等级
	
	private Integer forecast_setting;//拉动台数
	
	private Integer countOfStandardPartial;//标准零件使用数
	
	private Date start_date;//开始日期
	
	private Date end_date;//结束日期
	
	private Integer echelon;//梯队
	
    private Integer	 osh_foreboard_count;//基准值(OSH)
    
    private Integer  sorcwh_foreboard_count;//SORCWH基准值
    
    private Integer  wh2p_foreboard_count;//wh2pWH基准值
    
    private Integer consumable_foreboard_count;//消耗品库存基准值
    
    private Integer ogz_foreboard_count;//OGZ基准值
    
    private Date supply_date_start;//采样周期开始
    
    private Date supply_date_end;//采样周期结束
    
    private BigDecimal echelon1OfAverage;//第一梯队周期平均使用量
    
    private BigDecimal echelon2OfAverage;//第二梯队周期平均使用量
    
    private BigDecimal echelon3OfAverage;//第三梯队周期平均使用量
  
    private BigDecimal echelon4OfAverage;//第四梯队周期平均使用量
    
    private BigDecimal echelonOfAverage;//周期折算使用量合计
    
    private Integer orderCountOfAverage;//订购平均数
    
    private Date sorcwh_end_date;//SORCWH 基准值设定时间
    
    private Date wh2p_end_date;//WH2P 基准值设定时间
    
    private Date consumble_end_date;//消耗品库存 基准值设定时间
    
    private Date  ogz_end_date;//ogz 基准值设定时间
    
    private Integer identification;//设定类型
    
    private Integer updated_by;//最后更新人ID
    
    private BigDecimal quantityOfBeforeOneMonthAge;//一个月前平均使用量
    private BigDecimal countOfNotStandardOfHalfYear;//非标半年平均使用量
    private BigDecimal countOfNotStandardOfThreeMonth;//非标三个月平均使用量
    private BigDecimal countOfNotStandardOfOneMonth;//非标一个月平均使用量
    private BigDecimal countOfNotStandardOfCurMonth;//非标当月平均使用量
    private Integer  totalOFForecastSetting;//拉动台数合计使用量
    private Integer non_bom_safty_count;//非标安全库存
    
    
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

	public BigDecimal getQuantityOfHalfYear() {
		return quantityOfHalfYear;
	}

	public void setQuantityOfHalfYear(BigDecimal quantityOfHalfYear) {
		this.quantityOfHalfYear = quantityOfHalfYear;
	}

	public BigDecimal getQuantityOfThreeMonthAge() {
		return quantityOfThreeMonthAge;
	}

	public void setQuantityOfThreeMonthAge(BigDecimal quantityOfThreeMonthAge) {
		this.quantityOfThreeMonthAge = quantityOfThreeMonthAge;
	}

	public BigDecimal getQuantityOfOneMonthAge() {
		return quantityOfOneMonthAge;
	}

	public void setQuantityOfOneMonthAge(BigDecimal quantityOfOneMonthAge) {
		this.quantityOfOneMonthAge = quantityOfOneMonthAge;
	}

	public Integer getTotal_foreboard_count() {
		return total_foreboard_count;
	}

	public void setTotal_foreboard_count(Integer total_foreboard_count) {
		this.total_foreboard_count = total_foreboard_count;
	}

	public Integer getFlg() {
		return flg;
	}

	public void setFlg(Integer flg) {
		this.flg = flg;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getForecast_setting() {
		return forecast_setting;
	}

	public void setForecast_setting(Integer forecast_setting) {
		this.forecast_setting = forecast_setting;
	}

	public Integer getCountOfStandardPartial() {
		return countOfStandardPartial;
	}

	public void setCountOfStandardPartial(Integer countOfStandardPartial) {
		this.countOfStandardPartial = countOfStandardPartial;
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

	public Integer getEchelon() {
		return echelon;
	}

	public void setEchelon(Integer echelon) {
		this.echelon = echelon;
	}

	public Integer getOsh_foreboard_count() {
		return osh_foreboard_count;
	}

	public void setOsh_foreboard_count(Integer osh_foreboard_count) {
		this.osh_foreboard_count = osh_foreboard_count;
	}

	public Integer getSorcwh_foreboard_count() {
		return sorcwh_foreboard_count;
	}

	public void setSorcwh_foreboard_count(Integer sorcwh_foreboard_count) {
		this.sorcwh_foreboard_count = sorcwh_foreboard_count;
	}

	public Integer getWh2p_foreboard_count() {
		return wh2p_foreboard_count;
	}

	public void setWh2p_foreboard_count(Integer wh2p_foreboard_count) {
		this.wh2p_foreboard_count = wh2p_foreboard_count;
	}

	public Integer getConsumable_foreboard_count() {
		return consumable_foreboard_count;
	}

	public void setConsumable_foreboard_count(Integer consumable_foreboard_count) {
		this.consumable_foreboard_count = consumable_foreboard_count;
	}

	public Integer getOgz_foreboard_count() {
		return ogz_foreboard_count;
	}

	public void setOgz_foreboard_count(Integer ogz_foreboard_count) {
		this.ogz_foreboard_count = ogz_foreboard_count;
	}

	public Date getSupply_date_start() {
		return supply_date_start;
	}

	public void setSupply_date_start(Date supply_date_start) {
		this.supply_date_start = supply_date_start;
	}

	public Date getSupply_date_end() {
		return supply_date_end;
	}

	public void setSupply_date_end(Date supply_date_end) {
		this.supply_date_end = supply_date_end;
	}

	public BigDecimal getEchelon1OfAverage() {
		return echelon1OfAverage;
	}

	public void setEchelon1OfAverage(BigDecimal echelon1OfAverage) {
		this.echelon1OfAverage = echelon1OfAverage;
	}

	public BigDecimal getEchelon2OfAverage() {
		return echelon2OfAverage;
	}

	public void setEchelon2OfAverage(BigDecimal echelon2OfAverage) {
		this.echelon2OfAverage = echelon2OfAverage;
	}

	public BigDecimal getEchelon3OfAverage() {
		return echelon3OfAverage;
	}

	public void setEchelon3OfAverage(BigDecimal echelon3OfAverage) {
		this.echelon3OfAverage = echelon3OfAverage;
	}

	public BigDecimal getEchelon4OfAverage() {
		return echelon4OfAverage;
	}

	public void setEchelon4OfAverage(BigDecimal echelon4OfAverage) {
		this.echelon4OfAverage = echelon4OfAverage;
	}

	public BigDecimal getEchelonOfAverage() {
		return echelonOfAverage;
	}

	public void setEchelonOfAverage(BigDecimal echelonOfAverage) {
		this.echelonOfAverage = echelonOfAverage;
	}

	public Integer getOrderCountOfAverage() {
		return orderCountOfAverage;
	}

	public void setOrderCountOfAverage(Integer orderCountOfAverage) {
		this.orderCountOfAverage = orderCountOfAverage;
	}

	public Date getSorcwh_end_date() {
		return sorcwh_end_date;
	}

	public void setSorcwh_end_date(Date sorcwh_end_date) {
		this.sorcwh_end_date = sorcwh_end_date;
	}

	public Date getWh2p_end_date() {
		return wh2p_end_date;
	}

	public void setWh2p_end_date(Date wh2p_end_date) {
		this.wh2p_end_date = wh2p_end_date;
	}

	public Date getConsumble_end_date() {
		return consumble_end_date;
	}

	public void setConsumble_end_date(Date consumble_end_date) {
		this.consumble_end_date = consumble_end_date;
	}

	public Date getOgz_end_date() {
		return ogz_end_date;
	}

	public void setOgz_end_date(Date ogz_end_date) {
		this.ogz_end_date = ogz_end_date;
	}

	public Integer getIdentification() {
		return identification;
	}

	public void setIdentification(Integer identification) {
		this.identification = identification;
	}

	public Integer getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(Integer updated_by) {
		this.updated_by = updated_by;
	}

	public BigDecimal getQuantityOfBeforeOneMonthAge() {
		return quantityOfBeforeOneMonthAge;
	}

	public void setQuantityOfBeforeOneMonthAge(BigDecimal quantityOfBeforeOneMonthAge) {
		this.quantityOfBeforeOneMonthAge = quantityOfBeforeOneMonthAge;
	}

	public BigDecimal getCountOfNotStandardOfHalfYear() {
		return countOfNotStandardOfHalfYear;
	}

	public void setCountOfNotStandardOfHalfYear(BigDecimal countOfNotStandardOfHalfYear) {
		this.countOfNotStandardOfHalfYear = countOfNotStandardOfHalfYear;
	}

	public BigDecimal getCountOfNotStandardOfThreeMonth() {
		return countOfNotStandardOfThreeMonth;
	}

	public void setCountOfNotStandardOfThreeMonth(BigDecimal countOfNotStandardOfThreeMonth) {
		this.countOfNotStandardOfThreeMonth = countOfNotStandardOfThreeMonth;
	}

	public BigDecimal getCountOfNotStandardOfOneMonth() {
		return countOfNotStandardOfOneMonth;
	}

	public void setCountOfNotStandardOfOneMonth(BigDecimal countOfNotStandardOfOneMonth) {
		this.countOfNotStandardOfOneMonth = countOfNotStandardOfOneMonth;
	}

	public BigDecimal getCountOfNotStandardOfCurMonth() {
		return countOfNotStandardOfCurMonth;
	}

	public void setCountOfNotStandardOfCurMonth(BigDecimal countOfNotStandardOfCurMonth) {
		this.countOfNotStandardOfCurMonth = countOfNotStandardOfCurMonth;
	}

	public Integer getTotalOFForecastSetting() {
		return totalOFForecastSetting;
	}

	public void setTotalOFForecastSetting(Integer totalOFForecastSetting) {
		this.totalOFForecastSetting = totalOFForecastSetting;
	}

	public Integer getNon_bom_safty_count() {
		return non_bom_safty_count;
	}

	public void setNon_bom_safty_count(Integer non_bom_safty_count) {
		this.non_bom_safty_count = non_bom_safty_count;
	}
	
}
