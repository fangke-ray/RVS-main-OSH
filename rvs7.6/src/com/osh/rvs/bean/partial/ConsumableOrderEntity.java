package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.util.Date;

public class ConsumableOrderEntity implements Serializable {

	private static final long serialVersionUID = -447093140545075127L;

	/* DB区分 */
	private String db_flg;

	/* 确认并导出 */
	private String report_flg;

	/**
	 * 检索用
	 */
	/* 包含品名编号 */
	private String code;
	/*说明*/
	private String description;


	/* 订购单编号 */
	private String order_no;

	/* 订购日期.起 */
	private Date order_date_start;

	/* 订购日期.止 */
	private Date order_date_end;

	/**
	 * 一览用
	 */
	/* 消耗品订购单Key */
	private Integer consumable_order_key;

	/* 订购品数 */
	private Integer sum_order_quantity;

	/* 生成日时 */
	private Date create_time;

	/* 发布状态 */
	private Integer sent;

	/* 零件 ID*/
	private Integer partial_id;

	/* 零件 ID*/
	private Integer cm_partial_id;

	/* 订购数量*/
	private Integer order_quantity;

	/* 基准库存 */
	private Integer benchmark;

	/* 安全库存 */
	private Integer safety_lever;

	/* 当前有效库存 */
	private Integer available_inventory;

	/* 补充在途量 */
	private Integer on_passage;
	
	
	/* 消耗品名称 */
	private String partial_name;
	/* 危化品标记 */
	private Integer hazardous_flg;

	public String getDb_flg() {
		return db_flg;
	}

	public void setDb_flg(String db_flg) {
		this.db_flg = db_flg;
	}

	public String getReport_flg() {
		return report_flg;
	}

	public void setReport_flg(String report_flg) {
		this.report_flg = report_flg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
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

	public Integer getConsumable_order_key() {
		return consumable_order_key;
	}

	public void setConsumable_order_key(Integer consumable_order_key) {
		this.consumable_order_key = consumable_order_key;
	}

	public Integer getSum_order_quantity() {
		return sum_order_quantity;
	}

	public void setSum_order_quantity(Integer sum_order_quantity) {
		this.sum_order_quantity = sum_order_quantity;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Integer getSent() {
		return sent;
	}

	public void setSent(Integer sent) {
		this.sent = sent;
	}

	public Integer getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(Integer partial_id) {
		this.partial_id = partial_id;
	}

	public Integer getCm_partial_id() {
		return cm_partial_id;
	}

	public void setCm_partial_id(Integer cm_partial_id) {
		this.cm_partial_id = cm_partial_id;
	}

	public Integer getOrder_quantity() {
		return order_quantity;
	}

	public void setOrder_quantity(Integer order_quantity) {
		this.order_quantity = order_quantity;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPartial_name() {
		return partial_name;
	}

	public void setPartial_name(String partial_name) {
		this.partial_name = partial_name;
	}

	public Integer getHazardous_flg() {
		return hazardous_flg;
	}

	public void setHazardous_flg(Integer hazardous_flg) {
		this.hazardous_flg = hazardous_flg;
	}

}
