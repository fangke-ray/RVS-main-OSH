package com.osh.rvs.form.partial;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;
import framework.huiqing.common.util.CodeListUtils;

public class PartialOrderRecordForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 893604687243074451L;
	
	private String category_id;
	private String model_id;
	private String level;
	private String agreed_date_start;
	private String agreed_date_end;
	private String finish_date_start;
	private String finish_date_end;
	private String echelon;
	@BeanField(title = "零件订购日Start", name = "order_date_start", type = FieldType.Date)
	private String order_date_start;
	@BeanField(title = "零件订购日End", name = "order_date_end", type = FieldType.Date)
	private String order_date_end;
	private String arrival_plan_date_start;
	private String arrival_plan_date_end;
	private String arrival_date_start;
	private String arrival_date_end;
	
	@BeanField(title = "零件 ID", name = "partial_id", type = FieldType.String)
	private String partial_id;
	private String partial_code;
	@BeanField(title = "零件名称", name = "partial_name", type = FieldType.String)
	private String partial_name;
	@BeanField(title = "第1梯队订购", name = "consumable_flg", type = FieldType.Integer)
	private String consumable_flg;

	private String echelon_name;
	private String level_name;
	private String model_name;
	
	private String order_num;
	@BeanField(title = "第1梯队订购", name = "order_num1", type = FieldType.Integer)
	private String order_num1;
	@BeanField(title = "第2梯队订购", name = "order_num2", type = FieldType.Integer)
	private String order_num2;
	@BeanField(title = "第3梯队订购", name = "order_num3", type = FieldType.Integer)
	private String order_num3;
	@BeanField(title = "第4梯队订购", name = "order_num4", type = FieldType.Integer)
	private String order_num4;
	@BeanField(type = FieldType.Integer, name = "order_num5", title = "")
	private String order_num5;
	@BeanField(type = FieldType.Integer, name = "order_num6", title = "")
	private String order_num6;
	@BeanField(type = FieldType.Integer, name = "order_num7", title = "")
	private String order_num7;

	private String bo_num;
	private String bo3days_num;
	private String ld_num;
	private String recept_num;
	private String agreed_num;
	private String base_num;

	@BeanField(title = "设定期间内基准", name = "base_setting", type = FieldType.Integer)
	private String base_setting;

	private String maxd;
	private String mind;
	private String workday;
	
	private String bo_rate;
	private String bo3days_rate;
	
	@BeanField(title = "消耗速率", name = "turnround_rate", type = FieldType.Double)
	private String turnround_rate;
	private String osh_turnround_rate;
	private String wh2p_turnround_rate;
	private String ogz_turnround_rate;

	private String sorcwh_foreboard_count;
	private String wh2p_foreboard_count;
	private String consumable_foreboard_count;
	private String ogz_foreboard_count;

	@BeanField(title = "高消耗率警报线", name = "l_high", type = FieldType.Integer)
	private String l_high;
	@BeanField(title = "低消耗率警报线", name = "l_low", type = FieldType.Integer)
	private String l_low;

	@BeanField(title = "高消耗率警报置顶", name = "top_stick_high", type = FieldType.Bool)
	private String top_stick_high;
	@BeanField(title = "高消耗率警报置顶", name = "top_stick_low", type = FieldType.Bool)
	private String top_stick_low;
	@BeanField(title = "BOLT置顶", name = "top_stick_bo_lt", type = FieldType.Bool)
	private String top_stick_bo_lt;

	@BeanField(title = "价格", name = "price", type = FieldType.Double)
	private String price;

	public String getOrder_num() {
		return order_num;
	}

	public void setOrder_num(String order_num) {
		this.order_num = order_num;
	}

	public String getBo_num() {
		return bo_num;
	}

	public void setBo_num(String bo_num) {
		this.bo_num = bo_num;
	}

	public String getBo3days_num() {
		return bo3days_num;
	}

	public void setBo3days_num(String bo3days_num) {
		this.bo3days_num = bo3days_num;
	}

	public String getLd_num() {
		return ld_num;
	}

	public void setLd_num(String ld_num) {
		this.ld_num = ld_num;
	}

	public String getRecept_num() {
		return recept_num;
	}

	public void setRecept_num(String recept_num) {
		this.recept_num = recept_num;
	}

	public String getAgreed_num() {
		return agreed_num;
	}

	public void setAgreed_num(String agreed_num) {
		this.agreed_num = agreed_num;
	}

	public String getBase_num() {
		return base_num;
	}

	public void setBase_num(String base_num) {
		this.base_num = base_num;
	}

	public String getMaxd() {
		return maxd;
	}

	public void setMaxd(String maxd) {
		this.maxd = maxd;
	}

	public String getMind() {
		return mind;
	}

	public void setMind(String mind) {
		this.mind = mind;
	}

	public String getWorkday() {
		return workday;
	}

	public void setWorkday(String workday) {
		this.workday = workday;
	}

	public String getBo_rate() {
		return bo_rate;
	}

	public void setBo_rate(String bo_rate) {
		this.bo_rate = bo_rate;
	}

	public String getBo3days_rate() {
		return bo3days_rate;
	}

	public void setBo3days_rate(String bo3days_rate) {
		this.bo3days_rate = bo3days_rate;
	}

	public String getTurnround_rate() {
		return turnround_rate;
	}

	public void setTurnround_rate(String turnround_rate) {
		this.turnround_rate = turnround_rate;
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

	public String getOrder_date_start() {
		return order_date_start;
	}

	public void setOrder_date_start(String order_date_start) {
		this.order_date_start = order_date_start;
	}

	public String getOrder_date_end() {
		return order_date_end;
	}

	public void setOrder_date_end(String order_date_end) {
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

	public String getEchelon_name() {
		if (echelon != null && echelon != "") {
			return CodeListUtils.getValue("echelon_code", echelon);
		}
		return echelon_name;
	}

	public void setEchelon_name(String echelon_name) {
		this.echelon_name = echelon_name;
	}

	public String getLevel_name() {
		if (level != null && level !="") {
			return CodeListUtils.getValue("material_level", level);
			
		}
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

	public String getBase_setting() {
		return base_setting;
	}

	public void setBase_setting(String base_setting) {
		this.base_setting = base_setting;
	}

	public String getPartial_name() {
		return partial_name;
	}

	public void setPartial_name(String partial_name) {
		this.partial_name = partial_name;
	}

	public String getOrder_num1() {
		return order_num1;
	}

	public void setOrder_num1(String order_num1) {
		this.order_num1 = order_num1;
	}

	public String getOrder_num2() {
		return order_num2;
	}

	public void setOrder_num2(String order_num2) {
		this.order_num2 = order_num2;
	}

	public String getOrder_num3() {
		return order_num3;
	}

	public void setOrder_num3(String order_num3) {
		this.order_num3 = order_num3;
	}

	public String getOrder_num4() {
		return order_num4;
	}

	public void setOrder_num4(String order_num4) {
		this.order_num4 = order_num4;
	}

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public String getL_high() {
		return l_high;
	}

	public void setL_high(String l_high) {
		this.l_high = l_high;
	}

	public String getL_low() {
		return l_low;
	}

	public void setL_low(String l_low) {
		this.l_low = l_low;
	}

	public String getTop_stick_high() {
		return top_stick_high;
	}

	public void setTop_stick_high(String top_stick_high) {
		this.top_stick_high = top_stick_high;
	}

	public String getTop_stick_low() {
		return top_stick_low;
	}

	public void setTop_stick_low(String top_stick_low) {
		this.top_stick_low = top_stick_low;
	}

	public String getTop_stick_bo_lt() {
		return top_stick_bo_lt;
	}

	public void setTop_stick_bo_lt(String top_stick_bo_lt) {
		this.top_stick_bo_lt = top_stick_bo_lt;
	}

	public String getOsh_turnround_rate() {
		return osh_turnround_rate;
	}

	public void setOsh_turnround_rate(String osh_turnround_rate) {
		this.osh_turnround_rate = osh_turnround_rate;
	}

	public String getOgz_turnround_rate() {
		return ogz_turnround_rate;
	}

	public void setOgz_turnround_rate(String ogz_turnround_rate) {
		this.ogz_turnround_rate = ogz_turnround_rate;
	}

	public String getConsumable_flg() {
		return consumable_flg;
	}

	public void setConsumable_flg(String consumable_flg) {
		this.consumable_flg = consumable_flg;
	}

	public String getOrder_num5() {
		return order_num5;
	}

	public void setOrder_num5(String order_num5) {
		this.order_num5 = order_num5;
	}

	public String getOrder_num6() {
		return order_num6;
	}

	public void setOrder_num6(String order_num6) {
		this.order_num6 = order_num6;
	}

	public String getOrder_num7() {
		return order_num7;
	}

	public void setOrder_num7(String order_num7) {
		this.order_num7 = order_num7;
	}

	public String getWh2p_turnround_rate() {
		return wh2p_turnround_rate;
	}

	public void setWh2p_turnround_rate(String wh2p_turnround_rate) {
		this.wh2p_turnround_rate = wh2p_turnround_rate;
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

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	
}
