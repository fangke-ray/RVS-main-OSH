package com.osh.rvs.bean.partial;

import java.math.BigDecimal;
import java.util.Date;

public class PartialOrderRecordEntity {

	private String category_id;
	private String model_id;
	private String level;
	private String agreed_date_start;
	private String agreed_date_end;
	private String finish_date_start;
	private String finish_date_end;
	private String echelon;
	private Date order_date_start;
	private Date order_date_end;
	private String arrival_plan_date_start;
	private String arrival_plan_date_end;
	private String arrival_date_start;
	private String arrival_date_end;
	private String partial_id;
	private String partial_code;
	private String partial_name;
	private Integer consumable_flg;
	
	private String echelon_name;
	private String level_name;
	private String model_name;

	private Integer order_num;

	private Integer order_num1;
	private Integer order_num2;
	private Integer order_num3;
	private Integer order_num4;
	private Integer order_num5;
	private Integer order_num6;
	private Integer order_num7;

	private Integer bo_num;
	private Integer bo3days_num;
	private Integer ld_num;
	private Integer recept_num;
	private Integer agreed_num;
	private Integer base_num;
	
	private Integer base_setting;

	private Date maxd;
	private Date mind;
	private Integer workday;
	
	private Float bo_rate;
	private Float bo3days_rate;
	
	private BigDecimal turnround_rate;
	private BigDecimal osh_turnround_rate;
	private BigDecimal wh2p_turnround_rate;
	private BigDecimal ogz_turnround_rate;

	private Integer sorcwh_foreboard_count;
	private Integer wh2p_foreboard_count;
	private Integer consumable_foreboard_count;
	private Integer ogz_foreboard_count;

	private Integer l_high;
	private Integer l_low;

	private Boolean top_stick_high;
	private Boolean top_stick_low;
	private Boolean top_stick_bo_lt;

	private BigDecimal price;

	public Date getMaxd() {
		return maxd;
	}
	public void setMaxd(Date maxd) {
		this.maxd = maxd;
	}
	public Date getMind() {
		return mind;
	}
	public void setMind(Date mind) {
		this.mind = mind;
	}
	public Integer getWorkday() {
		return workday;
	}
	public void setWorkday(Integer workday) {
		this.workday = workday;
	}
	public Integer getBase_num() {
		return base_num;
	}
	public void setBase_num(Integer base_num) {
		this.base_num = base_num;
	}
	public Integer getOrder_num() {
		return order_num;
	}
	public void setOrder_num(Integer order_num) {
		this.order_num = order_num;
	}
	public Integer getBo_num() {
		return bo_num;
	}
	public void setBo_num(Integer bo_num) {
		this.bo_num = bo_num;
	}
	public Integer getBo3days_num() {
		return bo3days_num;
	}
	public void setBo3days_num(Integer bo3days_num) {
		this.bo3days_num = bo3days_num;
	}
	public Integer getLd_num() {
		return ld_num;
	}
	public void setLd_num(Integer ld_num) {
		this.ld_num = ld_num;
	}
	public Integer getRecept_num() {
		return recept_num;
	}
	public void setRecept_num(Integer recept_num) {
		this.recept_num = recept_num;
	}
	public Integer getAgreed_num() {
		return agreed_num;
	}
	public void setAgreed_num(Integer agreed_num) {
		this.agreed_num = agreed_num;
	}
	public String getCategory_id() {
		return category_id;
	}
	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}
	public String getModel_id() {
		return model_id;
	}
	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getAgreed_date_start() {
		return agreed_date_start;
	}
	public void setAgreed_date_start(String agreed_date_start) {
		this.agreed_date_start = agreed_date_start;
	}
	public String getAgreed_date_end() {
		return agreed_date_end;
	}
	public void setAgreed_date_end(String agreed_date_end) {
		this.agreed_date_end = agreed_date_end;
	}
	public String getFinish_date_start() {
		return finish_date_start;
	}
	public void setFinish_date_start(String finish_date_start) {
		this.finish_date_start = finish_date_start;
	}
	public String getFinish_date_end() {
		return finish_date_end;
	}
	public void setFinish_date_end(String finish_date_end) {
		this.finish_date_end = finish_date_end;
	}
	public String getEchelon() {
		return echelon;
	}
	public void setEchelon(String echelon) {
		this.echelon = echelon;
	}
	public Date getOrder_date_start() {
		return order_date_start;
	}
	public void setOrder_date_start(Date order_date_start) {
		this.order_date_start = order_date_start;
	}
	public Date getOrder_date_end() {
		return order_date_end;
	}
	public void setOrder_date_end(Date order_date_end) {
		this.order_date_end = order_date_end;
	}
	public String getArrival_plan_date_start() {
		return arrival_plan_date_start;
	}
	public void setArrival_plan_date_start(String arrival_plan_date_start) {
		this.arrival_plan_date_start = arrival_plan_date_start;
	}
	public String getArrival_plan_date_end() {
		return arrival_plan_date_end;
	}
	public void setArrival_plan_date_end(String arrival_plan_date_end) {
		this.arrival_plan_date_end = arrival_plan_date_end;
	}
	public String getArrival_date_start() {
		return arrival_date_start;
	}
	public void setArrival_date_start(String arrival_date_start) {
		this.arrival_date_start = arrival_date_start;
	}
	public String getArrival_date_end() {
		return arrival_date_end;
	}
	public void setArrival_date_end(String arrival_date_end) {
		this.arrival_date_end = arrival_date_end;
	}
	public String getPartial_code() {
		return partial_code;
	}
	public void setPartial_code(String partial_code) {
		this.partial_code = partial_code;
	}
	public Float getBo_rate() {
		return bo_rate;
	}
	public void setBo_rate(Float bo_rate) {
		this.bo_rate = bo_rate;
	}
	public Float getBo3days_rate() {
		return bo3days_rate;
	}
	public void setBo3days_rate(Float bo3days_rate) {
		this.bo3days_rate = bo3days_rate;
	}
	public BigDecimal getTurnround_rate() {
		return turnround_rate;
	}
	public void setTurnround_rate(BigDecimal turnround_rate) {
		this.turnround_rate = turnround_rate;
	}
	public String getEchelon_name() {
		return echelon_name;
	}
	public void setEchelon_name(String echelon_name) {
		this.echelon_name = echelon_name;
	}
	public String getLevel_name() {
		return level_name;
	}
	public void setLevel_name(String level_name) {
		this.level_name = level_name;
	}
	public String getModel_name() {
		return model_name;
	}
	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}
	public Integer getBase_setting() {
		return base_setting;
	}
	public void setBase_setting(Integer base_setting) {
		this.base_setting = base_setting;
	}
	public String getPartial_name() {
		return partial_name;
	}
	public void setPartial_name(String partial_name) {
		this.partial_name = partial_name;
	}
	public Integer getOrder_num1() {
		return order_num1;
	}
	public void setOrder_num1(Integer order_num1) {
		this.order_num1 = order_num1;
	}
	public Integer getOrder_num2() {
		return order_num2;
	}
	public void setOrder_num2(Integer order_num2) {
		this.order_num2 = order_num2;
	}
	public Integer getOrder_num3() {
		return order_num3;
	}
	public void setOrder_num3(Integer order_num3) {
		this.order_num3 = order_num3;
	}
	public Integer getOrder_num4() {
		return order_num4;
	}
	public void setOrder_num4(Integer order_num4) {
		this.order_num4 = order_num4;
	}
	public String getPartial_id() {
		return partial_id;
	}
	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}
	public Integer getL_high() {
		return l_high;
	}
	public void setL_high(Integer l_high) {
		this.l_high = l_high;
	}
	public Integer getL_low() {
		return l_low;
	}
	public void setL_low(Integer l_low) {
		this.l_low = l_low;
	}
	public Boolean getTop_stick_high() {
		return top_stick_high;
	}
	public void setTop_stick_high(Boolean top_stick_high) {
		this.top_stick_high = top_stick_high;
	}
	public Boolean getTop_stick_low() {
		return top_stick_low;
	}
	public void setTop_stick_low(Boolean top_stick_low) {
		this.top_stick_low = top_stick_low;
	}
	public Boolean getTop_stick_bo_lt() {
		return top_stick_bo_lt;
	}
	public void setTop_stick_bo_lt(Boolean top_stick_bo_lt) {
		this.top_stick_bo_lt = top_stick_bo_lt;
	}
	public BigDecimal getOsh_turnround_rate() {
		return osh_turnround_rate;
	}
	public void setOsh_turnround_rate(BigDecimal osh_turnround_rate) {
		this.osh_turnround_rate = osh_turnround_rate;
	}
	public BigDecimal getOgz_turnround_rate() {
		return ogz_turnround_rate;
	}
	public void setOgz_turnround_rate(BigDecimal ogz_turnround_rate) {
		this.ogz_turnround_rate = ogz_turnround_rate;
	}
	public Integer getConsumable_flg() {
		return consumable_flg;
	}
	public void setConsumable_flg(Integer consumable_flg) {
		this.consumable_flg = consumable_flg;
	}
	public Integer getOrder_num5() {
		return order_num5;
	}
	public void setOrder_num5(Integer order_num5) {
		this.order_num5 = order_num5;
	}
	public Integer getOrder_num6() {
		return order_num6;
	}
	public void setOrder_num6(Integer order_num6) {
		this.order_num6 = order_num6;
	}
	public Integer getOrder_num7() {
		return order_num7;
	}
	public void setOrder_num7(Integer order_num7) {
		this.order_num7 = order_num7;
	}
	public BigDecimal getWh2p_turnround_rate() {
		return wh2p_turnround_rate;
	}
	public void setWh2p_turnround_rate(BigDecimal wh2p_turnround_rate) {
		this.wh2p_turnround_rate = wh2p_turnround_rate;
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
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	
}
