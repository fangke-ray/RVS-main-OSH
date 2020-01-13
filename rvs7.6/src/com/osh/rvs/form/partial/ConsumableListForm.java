package com.osh.rvs.form.partial;
import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class ConsumableListForm extends ActionForm{
	
	private static final long serialVersionUID = 8931917335686576264L;
	
	/*零件 ID*/
	@BeanField(title = "零件 ID", name = "partial_id", type = FieldType.Integer, primaryKey = true, length = 11)
	private String partial_id;
    /*消耗品代码*/
	@BeanField(title = "消耗品代码", name = "code", length = 8,notNull = true)
	private String code;
	/*说明*/
	@BeanField(title = "说明", name = "description", length = 120)
	private String description;
	/*分类*/
	@BeanField(title = "分类", name = "type", type = FieldType.Integer, length = 2, notNull = true)
	private String type;
	/*基准库存*/
	@BeanField(title = "基准库存", name = "benchmark", type = FieldType.Integer,length = 5)
	private String benchmark;
	/*安全库存*/
	@BeanField(title = "安全库存", name = "safety_lever", type = FieldType.Integer,length = 5)
	private String safety_lever;
	/*当前有效库存*/
	@BeanField(title = "当前有效库存", name = "available_inventory", type = FieldType.Integer,length = 5,notNull = true)
	private String available_inventory;
	/*有效库存差值*/
	@BeanField(title = "有效库存差值", name = "available_inventory_temp", type = FieldType.Integer,length = 5,notNull = true)
	private String available_inventory_temp;
	/*补充在途量*/
	@BeanField(title = "补充在途量", name = "on_passage", type = FieldType.Integer,length = 5,notNull = true)
	private String on_passage;
	/*补充在途差值*/
	@BeanField(title = "补充在途差值", name = "on_passage_temp", type = FieldType.Integer,length = 5,notNull = true)
	private String on_passage_temp;
	/*修正理由*/
	@BeanField(title = "修正理由", name = "adjust_reason",length = 256)
	private String adjust_reason;
	/*期间内补充量*/
	@BeanField(title = "期间内补充量", name = "supply_count_quantity", type = FieldType.Integer,length = 5,notNull = true)
	private String supply_count_quantity;
	/*期间内消耗量*/
	@BeanField(title = "期间内替代发放量", name = "substituted_count_quantity", type = FieldType.Integer,length = 5,notNull = true)
	private String released_count_quantity;
	@BeanField(title = "期间内在线补充量", name = "used_count_quantity", type = FieldType.Integer,length = 5,notNull = true)
	private String used_count_quantity;
	/*消耗率*/
	@BeanField(title = "消耗率", name = "consumed_rate", type = FieldType.Double)
	private String consumed_rate;
	/*库位*/
	@BeanField(title = "库位", name = "stock_code",length = 20)
	private String stock_code;
	/*图片是否存在*/
	@BeanField(title = "图片是否存在",  name = "image_uploaded_flg", type = FieldType.Integer,length = 1,notNull = true)
	private String image_uploaded_flg;
	/*消耗率警报线*/
	@BeanField(title = "消耗率警报线",  name = "consumed_rate_alarmline", type = FieldType.Integer,length = 5,notNull = true)
	private String consumed_rate_alarmline;
	/*消耗率警报*/
	@BeanField(title = "消耗率警报",  name = "consumed_rate_alarm", type = FieldType.Integer,length = 1,notNull = true)
	private String consumed_rate_alarm;
	/*库存不足警报*/
	@BeanField(title = "库存不足警报",  name = "safety_lever_alarm", type = FieldType.Integer,length = 1,notNull = true)
	private String safety_lever_alarm;
	/*计算日期始*/
	@BeanField(title = "起效日期始", name = "search_count_period_start", type = FieldType.Date, notNull = true)
	private String search_count_period_start;
	/*计算日期止*/
	@BeanField(title = "起效日期止", name = "search_count_period_end", type = FieldType.Date, notNull = true)
	private String search_count_period_end;
	/*库存不足*/
	@BeanField(title = "库存不足", name = "leak", type = FieldType.Integer,length = 1, notNull = true)
	private String leak;
	/*常用消耗品*/
	@BeanField(title = "常用消耗品", name = "popular_item", type = FieldType.Integer,length = 1, notNull = true)
	private String popular_item;
	/*补充周期*/
	@BeanField(title = "补充周期", name = "supply_cycle", type = FieldType.Integer)
	private String supply_cycle;
	/*补充日*/
	@BeanField(title = "补充日", name = "supply_day", type = FieldType.Integer)
	private String supply_day;
	/*无效标记*/
	@BeanField(title = "无效标记", name = "delete_flg", type = FieldType.Integer,length = 1, notNull = true)
	private String delete_flg;
	/*单位名称*/
	@BeanField(title = "单位名称", name = "unit_name", length = 8)
	private String unit_name;
	/*内容量*/
	@BeanField(title = "内容量", name = "content", type = FieldType.Integer,length = 4)
	private String content;
	/*目标值*/
	@BeanField(title = "目标值", name = "consumpt_quota", type = FieldType.UDouble, length = 5, scale = 2)
	private String consumpt_quota;
	/*单价*/
	@BeanField(title = "单价", name = "price", type = FieldType.UDouble)
	private String price;
	/* 上架耗时 */
	@BeanField(title = "上架耗时", name = "in_shelf_cost", type = FieldType.Integer,length = 1,notNull = true)
	private String in_shelf_cost;
	/* 下架耗时 */
	@BeanField(title = "下架耗时", name = "out_shelf_cost", type = FieldType.Integer,length = 1,notNull = true)
	private String out_shelf_cost;
	/* 危化品标记 */
	@BeanField(title = "危化品标记", name = "hazardous_flg", type = FieldType.Integer,length = 1,notNull = true)
	private String hazardous_flg;

	/** 按照产量取消耗平均 */
	private String cost_avg_by_outline;
	
	public String getSupply_cycle() {
		return supply_cycle;
	}
	public void setSupply_cycle(String supply_cycle) {
		this.supply_cycle = supply_cycle;
	}
	public String getSupply_day() {
		return supply_day;
	}
	public void setSupply_day(String supply_day) {
		this.supply_day = supply_day;
	}
	public String getDelete_flg() {
		return delete_flg;
	}
	public void setDelete_flg(String delete_flg) {
		this.delete_flg = delete_flg;
	}
	public String getPopular_item() {
		return popular_item;
	}
	public void setPopular_item(String popular_item) {
		this.popular_item = popular_item;
	}
	public String getLeak() {
		return leak;
	}
	public void setLeak(String leak) {
		this.leak = leak;
	}
	public String getPartial_id() {
		return partial_id;
	}
	public void setPartial_id(String partial_id) {
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBenchmark() {
		return benchmark;
	}
	public void setBenchmark(String benchmark) {
		this.benchmark = benchmark;
	}
	public String getSafety_lever() {
		return safety_lever;
	}
	public void setSafety_lever(String safety_lever) {
		this.safety_lever = safety_lever;
	}
	public String getAvailable_inventory() {
		return available_inventory;
	}
	public void setAvailable_inventory(String available_inventory) {
		this.available_inventory = available_inventory;
	}
	public String getOn_passage() {
		return on_passage;
	}
	public void setOn_passage(String on_passage) {
		this.on_passage = on_passage;
	}
	public String getSupply_count_quantity() {
		return supply_count_quantity;
	}
	public void setSupply_count_quantity(String supply_count_quantity) {
		this.supply_count_quantity = supply_count_quantity;
	}
	public String getUsed_count_quantity() {
		return used_count_quantity;
	}
	public void setUsed_count_quantity(String used_count_quantity) {
		this.used_count_quantity = used_count_quantity;
	}
	public String getConsumed_rate() {
		return consumed_rate;
	}
	public void setConsumed_rate(String consumed_rate) {
		this.consumed_rate = consumed_rate;
	}
	public String getStock_code() {
		return stock_code;
	}
	public void setStock_code(String stock_code) {
		this.stock_code = stock_code;
	}
	public String getImage_uploaded_flg() {
		return image_uploaded_flg;
	}
	public void setImage_uploaded_flg(String image_uploaded_flg) {
		this.image_uploaded_flg = image_uploaded_flg;
	}
	public String getConsumed_rate_alarm() {
		return consumed_rate_alarm;
	}
	public void setConsumed_rate_alarm(String consumed_rate_alarm) {
		this.consumed_rate_alarm = consumed_rate_alarm;
	}
	public String getSafety_lever_alarm() {
		return safety_lever_alarm;
	}
	public void setSafety_lever_alarm(String safety_lever_alarm) {
		this.safety_lever_alarm = safety_lever_alarm;
	}
	public String getSearch_count_period_start() {
		return search_count_period_start;
	}
	public void setSearch_count_period_start(String search_count_period_start) {
		this.search_count_period_start = search_count_period_start;
	}
	public String getSearch_count_period_end() {
		return search_count_period_end;
	}
	public void setSearch_count_period_end(String search_count_period_end) {
		this.search_count_period_end = search_count_period_end;
	}
	public String getConsumed_rate_alarmline() {
		return consumed_rate_alarmline;
	}
	public void setConsumed_rate_alarmline(String consumed_rate_alarmline) {
		this.consumed_rate_alarmline = consumed_rate_alarmline;
	}
	public String getAvailable_inventory_temp() {
		return available_inventory_temp;
	}
	public void setAvailable_inventory_temp(String available_inventory_temp) {
		this.available_inventory_temp = available_inventory_temp;
	}
	public String getOn_passage_temp() {
		return on_passage_temp;
	}
	public void setOn_passage_temp(String on_passage_temp) {
		this.on_passage_temp = on_passage_temp;
	}
	public String getAdjust_reason() {
		return adjust_reason;
	}
	public void setAdjust_reason(String adjust_reason) {
		this.adjust_reason = adjust_reason;
	}
	public String getUnit_name() {
		return unit_name;
	}
	public void setUnit_name(String unit_name) {
		this.unit_name = unit_name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getReleased_count_quantity() {
		return released_count_quantity;
	}
	public void setReleased_count_quantity(String released_count_quantity) {
		this.released_count_quantity = released_count_quantity;
	}
	public String getConsumpt_quota() {
		return consumpt_quota;
	}
	public void setConsumpt_quota(String consumpt_quota) {
		this.consumpt_quota = consumpt_quota;
	}
	public String getCost_avg_by_outline() {
		return cost_avg_by_outline;
	}
	public void setCost_avg_by_outline(String cost_avg_by_outline) {
		this.cost_avg_by_outline = cost_avg_by_outline;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getIn_shelf_cost() {
		return in_shelf_cost;
	}
	public void setIn_shelf_cost(String in_shelf_cost) {
		this.in_shelf_cost = in_shelf_cost;
	}
	public String getOut_shelf_cost() {
		return out_shelf_cost;
	}
	public void setOut_shelf_cost(String out_shelf_cost) {
		this.out_shelf_cost = out_shelf_cost;
	}
	public String getHazardous_flg() {
		return hazardous_flg;
	}
	public void setHazardous_flg(String hazardous_flg) {
		this.hazardous_flg = hazardous_flg;
	}
	
}
