package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
public class ConsumableListEntity implements Serializable{
	/**
	 * 消耗品仓库库存一览
	 */
	private static final long serialVersionUID = -2419721195186907371L;
	
	/*零件 ID*/
	private Integer partial_id;
	
    /*消耗品代码*/
	private String code;
	
	/*说明*/
	private String description;
	
	/*分类*/
	private Integer type;
	
	/*基准库存*/
	private Integer benchmark;
	
	/*安全库存*/
	private Integer safety_lever;
	
	/*当前有效库存*/
	private Integer available_inventory;
	
	/*有效库存差值*/
	private Integer available_inventory_temp;
	
	/*补充在途量*/
	private Integer on_passage;
	
	/*补充在途差值*/
	private Integer on_passage_temp;
	
	/*修正理由*/
	private String adjust_reason;

	/*期间内补充量*/
	private Integer supply_count_quantity;
	
	/*期间内消耗量*/
	private Integer substituted_count_quantity;
	private Integer used_count_quantity;
	
	/*消耗率*/
	private BigDecimal consumed_rate;
	
	/*库位*/
	private String stock_code;
	
	/*图片是否存在*/
	private Integer image_uploaded_flg;
	
	/*消耗率警报*/
	private Integer consumed_rate_alarm;
	
	/*消耗率警报线*/
	private Integer consumed_rate_alarmline;
	
	/*库存不足警报*/
	private Integer safety_lever_alarm;
	
	/*计算日期起*/
	private Date search_count_period_start;
	
	/*计算日期止*/
	private Date search_count_period_end;
	
	/*库存不足*/
	private Integer leak;
	
	/*常用消耗品*/
	private Integer popular_item;
	
	/*补充周期*/
	private Integer supply_cycle;
	
	/*补充日*/
	private Integer supply_day;
	
	/*无效标记*/
	private Integer delete_flg;
	
	/*单位名称*/
	private String unit_name;
	
	/*内容量*/
	private Integer content;
	
	/*operator_id*/
	private String operator_id;

	private BigDecimal consumpt_quota;
	private BigDecimal price;
	
	/* 上架耗时 */
	private Integer in_shelf_cost;
	
	/* 下架耗时 */
	private Integer out_shelf_cost;

	public Integer getPopular_item() {
		return popular_item;
	}

	public void setPopular_item(Integer popular_item) {
		this.popular_item = popular_item;
	}

	public Integer getSupply_cycle() {
		return supply_cycle;
	}

	public void setSupply_cycle(Integer supply_cycle) {
		this.supply_cycle = supply_cycle;
	}

	public Integer getSupply_day() {
		return supply_day;
	}

	public void setSupply_day(Integer supply_day) {
		this.supply_day = supply_day;
	}

	public Integer getDelete_flg() {
		return delete_flg;
	}

	public void setDelete_flg(Integer delete_flg) {
		this.delete_flg = delete_flg;
	}

	public Integer getConsumed_rate_alarmline() {
		return consumed_rate_alarmline;
	}

	public void setConsumed_rate_alarmline(Integer consumed_rate_alarmline) {
		this.consumed_rate_alarmline = consumed_rate_alarmline;
	}

	public Integer getLeak() {
		return leak;
	}

	public void setLeak(Integer leak) {
		this.leak = leak;
	}

	public Integer getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(Integer partial_id) {
		this.partial_id = partial_id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getBenchmark() {
		return benchmark;
	}

	public void setBenchmark(Integer benchmark) {
		this.benchmark = benchmark;
	}

	public Integer getSafety_lever() {
		return safety_lever;
	}

	public void setSafety_lever(Integer safety_lever) {
		this.safety_lever = safety_lever;
	}

	public Integer getAvailable_inventory() {
		return available_inventory;
	}

	public void setAvailable_inventory(Integer available_inventory) {
		this.available_inventory = available_inventory;
	}

	public Integer getOn_passage() {
		return on_passage;
	}

	public void setOn_passage(Integer on_passage) {
		this.on_passage = on_passage;
	}

	public Integer getSupply_count_quantity() {
		return supply_count_quantity;
	}

	public void setSupply_count_quantity(Integer supply_count_quantity) {
		this.supply_count_quantity = supply_count_quantity;
	}

	public Integer getUsed_count_quantity() {
		return used_count_quantity;
	}

	public void setUsed_count_quantity(Integer used_count_quantity) {
		this.used_count_quantity = used_count_quantity;
	}

	public BigDecimal getConsumed_rate() {
		return consumed_rate;
	}

	public void setConsumed_rate(BigDecimal consumed_rate) {
		this.consumed_rate = consumed_rate;
	}

	public String getStock_code() {
		return stock_code;
	}

	public void setStock_code(String stock_code) {
		this.stock_code = stock_code;
	}

	public Integer getImage_uploaded_flg() {
		return image_uploaded_flg;
	}

	public void setImage_uploaded_flg(Integer image_uploaded_flg) {
		this.image_uploaded_flg = image_uploaded_flg;
	}

	public Integer getConsumed_rate_alarm() {
		return consumed_rate_alarm;
	}

	public void setConsumed_rate_alarm(Integer consumed_rate_alarm) {
		this.consumed_rate_alarm = consumed_rate_alarm;
	}

	public Integer getSafety_lever_alarm() {
		return safety_lever_alarm;
	}

	public void setSafety_lever_alarm(Integer safety_lever_alarm) {
		this.safety_lever_alarm = safety_lever_alarm;
	}

	public Date getSearch_count_period_start() {
		return search_count_period_start;
	}

	public void setSearch_count_period_start(Date search_count_period_start) {
		this.search_count_period_start = search_count_period_start;
	}

	public Date getSearch_count_period_end() {
		return search_count_period_end;
	}

	public void setSearch_count_period_end(Date search_count_period_end) {
		this.search_count_period_end = search_count_period_end;
	}

	public String getUnit_name() {
		return unit_name;
	}

	public void setUnit_name(String unit_name) {
		this.unit_name = unit_name;
	}

	public Integer getContent() {
		return content;
	}

	public void setContent(Integer content) {
		this.content = content;
	}
	public Integer getAvailable_inventory_temp() {
		return available_inventory_temp;
	}

	public void setAvailable_inventory_temp(Integer available_inventory_temp) {
		this.available_inventory_temp = available_inventory_temp;
	}

	public Integer getOn_passage_temp() {
		return on_passage_temp;
	}

	public void setOn_passage_temp(Integer on_passage_temp) {
		this.on_passage_temp = on_passage_temp;
	}

	public String getAdjust_reason() {
		return adjust_reason;
	}

	public void setAdjust_reason(String adjust_reason) {
		this.adjust_reason = adjust_reason;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public Integer getSubstituted_count_quantity() {
		return substituted_count_quantity;
	}

	public void setSubstituted_count_quantity(Integer substituted_count_quantity) {
		this.substituted_count_quantity = substituted_count_quantity;
	}

	public BigDecimal getConsumpt_quota() {
		return consumpt_quota;
	}

	public void setConsumpt_quota(BigDecimal consumpt_quota) {
		this.consumpt_quota = consumpt_quota;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getIn_shelf_cost() {
		return in_shelf_cost;
	}

	public void setIn_shelf_cost(Integer in_shelf_cost) {
		this.in_shelf_cost = in_shelf_cost;
	}

	public Integer getOut_shelf_cost() {
		return out_shelf_cost;
	}

	public void setOut_shelf_cost(Integer out_shelf_cost) {
		this.out_shelf_cost = out_shelf_cost;
	}
	
	
}
