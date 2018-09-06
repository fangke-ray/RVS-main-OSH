package com.osh.rvs.form.partial;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class ConsumableManageForm extends ActionForm implements Serializable{
	
	/**
	 * 消耗品仓库管理记录
	 */
	private static final long serialVersionUID = -116552365716715639L;

	/* 检索区分 */
	private String search_page;

	/* DB区分 */
	private String db_flg;

	/* 确认并导出 */
	private String report_flg;

	/* 包含品名编号 */
	@BeanField(title = "包含品名编号", name = "code", type = FieldType.String, length=9)
	private String code;
	/*说明*/
	@BeanField(title = "说明", name = "description", length = 120)
	private String description;

	/* 订购单编号 */
	@BeanField(title = "订购单编号", name = "order_no", type = FieldType.String, length=15)
	private String order_no;
	
	/* 订购日期.起 */
	@BeanField(title = "订购日期.起", name = "order_date_start", type = FieldType.Date)
	private String order_date_start;
	
	/* 订购日期.止 */
	@BeanField(title = "订购日期.止", name = "order_date_end", type = FieldType.Date)
	private String order_date_end;
	
	/* 入库日期 .起*/
	@BeanField(title = "入库日期.起", name = "supply_date_start", type = FieldType.Date)
	private String supply_date_start;
	
	/* 入库日期.止 */
	@BeanField(title = "入库日期.止", name = "supply_date_end", type = FieldType.Date)
	private String supply_date_end;
	
	/* 申请单编号 */
	@BeanField(title = "申请单编号", name = "application_no", type = FieldType.String, length=15)
	private String application_no;
	
	/* 申请课室 */
	@BeanField(title = "申请课室", name = "section_id", type = FieldType.Integer, length=11)
	private String section_id;
	
	/* 申请工程 */
	@BeanField(title = "申请工程", name = "line_id", type = FieldType.Integer, length=11)
	private String line_id;
	
	/* 发放完成 */
	private String order_sent;
	
	/* 申请日期.起 */
	@BeanField(title = "申请日期.起", name = "apply_date_start", type = FieldType.Date)
	private String apply_date_start;
	
	/* 申请日期.止 */
	@BeanField(title = "申请日期.止", name = "apply_date_end", type = FieldType.Date)
	private String apply_date_end;
	
	/* 即时领用  */
	@BeanField(title = "即时领用 ", name = "apply_method", type = FieldType.Integer, length=1)
	private String apply_method;
	
	/* 申请日期.起 */
	@BeanField(title = "申请日期.起", name = "adjust_date_start", type = FieldType.Date)
	private String adjust_date_start;
	
	/* 申请日期.止 */
	@BeanField(title = "申请日期.止 ", name = "adjust_date_end", type = FieldType.Date)
	private String adjust_date_end;
	
	/* 理由 */
	@BeanField(title = "理由", name = "reason", type = FieldType.String, length=256)
	private String reason;

	/* 消耗品订购单Key*/
	@BeanField(title = "消耗品订购单Key", name = "consumable_order_key", primaryKey = true, type = FieldType.Integer, length=11)
	private String consumable_order_key;

	/* 订购品数 */
	private String sum_order_quantity;

	/* 生成日时 */
	@BeanField(title = "生成日时", name = "create_time", type = FieldType.DateTime)
	private String create_time;

	/* 发布状态 */
	@BeanField(title = "发布状态", name = "sent", type = FieldType.Integer, length=1)
	private String sent;

	/* 零件 ID*/
	@BeanField(title = "零件 ID", name = "partial_id", type = FieldType.Integer, length=11)
	private String partial_id;

	/* 零件 ID*/
	@BeanField(title = "零件 ID", name = "cm_partial_id", type = FieldType.Integer, length=11)
	private String cm_partial_id;

	/* 订购数量*/
	@BeanField(title = "订购数量", name = "order_quantity", type = FieldType.Integer, length=4)
	private String order_quantity;

	/* 基准库存 */
	@BeanField(title = "基准库存", name = "benchmark", type = FieldType.Integer, length=5)
	private String benchmark;

	/* 安全库存 */
	@BeanField(title = "安全库存", name = "safety_lever", type = FieldType.Integer, length=5)
	private String safety_lever;

	/* 当前有效库存 */
	@BeanField(title = "当前有效库存", name = "available_inventory", type = FieldType.Integer, length=5)
	private String available_inventory;

	/* 补充在途量 */
	@BeanField(title = "补充在途量 ", name = "on_passage", type = FieldType.Integer, length=5)
	private String on_passage;

	/* 零件名称 */
	@BeanField(title = "零件名称", name = "name", type = FieldType.String)
	private String name;

	/* 消耗品分类 */
	@BeanField(title = "消耗品分类", name = "type", type = FieldType.Integer, length=1)
	private String type;
	
	/* 入库日期*/
	@BeanField(title = "入库日期", name = "supply_date", type = FieldType.Date)
	private String supply_date;
	
	/* 入库数量*/
	@BeanField(title = "入库数量", name = "quantity", type = FieldType.Integer, length=5)
	private String quantity;

	/* 消耗品申请单Key*/
	@BeanField(title = "消耗品申请单Key", name = "consumable_application_key", primaryKey = true, type = FieldType.Integer, length=11)
	private String consumable_application_key;

	/* 申请工程 */
	private String line_name;

	/* 申请日时 */
	@BeanField(title = "申请日时", name = "apply_time", type = FieldType.DateTime)
	private String apply_time;

	/* 即时领用*/
	private String fix_exchange;

	/* 申请理由 */
	@BeanField(title = "申请理由", name = "apply_reason", type = FieldType.String)
	private String apply_reason;
	
	/* 发放日时*/
	@BeanField(title = "发放日时", name = "supply_time", type = FieldType.DateTime)
	private String supply_time;
	
	/* 调整量*/
	@BeanField(title = "调整量", name = "adjust_inventory", type = FieldType.Integer, length=5)
	private String adjust_inventory;

	/* 调整日时 */
	@BeanField(title = "调整日时", name = "adjust_time", type = FieldType.DateTime)
	private String adjust_time;

	/* 担当人 ID */
	@BeanField(title = "担当人 ID", name = "operator_id", type = FieldType.String)
	private String operator_id;

	/* 调整负责人 */
	@BeanField(title = "调整负责人", name = "operator_name", type = FieldType.String)
	private String operator_name;

	public String getSearch_page() {
		return search_page;
	}

	public void setSearch_page(String search_page) {
		this.search_page = search_page;
	}

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

	public String getApplication_no() {
		return application_no;
	}

	public void setApplication_no(String application_no) {
		this.application_no = application_no;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public String getOrder_sent() {
		return order_sent;
	}

	public void setOrder_sent(String order_sent) {
		this.order_sent = order_sent;
	}

	public String getApply_date_start() {
		return apply_date_start;
	}

	public void setApply_date_start(String apply_date_start) {
		this.apply_date_start = apply_date_start;
	}

	public String getApply_date_end() {
		return apply_date_end;
	}

	public void setApply_date_end(String apply_date_end) {
		this.apply_date_end = apply_date_end;
	}

	public String getApply_method() {
		return apply_method;
	}

	public void setApply_method(String apply_method) {
		this.apply_method = apply_method;
	}

	public String getAdjust_date_start() {
		return adjust_date_start;
	}

	public void setAdjust_date_start(String adjust_date_start) {
		this.adjust_date_start = adjust_date_start;
	}

	public String getAdjust_date_end() {
		return adjust_date_end;
	}

	public void setAdjust_date_end(String adjust_date_end) {
		this.adjust_date_end = adjust_date_end;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getConsumable_order_key() {
		return consumable_order_key;
	}

	public void setConsumable_order_key(String consumable_order_key) {
		this.consumable_order_key = consumable_order_key;
	}

	public String getSum_order_quantity() {
		return sum_order_quantity;
	}

	public void setSum_order_quantity(String sum_order_quantity) {
		this.sum_order_quantity = sum_order_quantity;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getSent() {
		return sent;
	}

	public void setSent(String sent) {
		this.sent = sent;
	}

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public String getCm_partial_id() {
		return cm_partial_id;
	}

	public void setCm_partial_id(String cm_partial_id) {
		this.cm_partial_id = cm_partial_id;
	}

	public String getOrder_quantity() {
		return order_quantity;
	}

	public void setOrder_quantity(String order_quantity) {
		this.order_quantity = order_quantity;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSupply_date() {
		return supply_date;
	}

	public void setSupply_date(String supply_date) {
		this.supply_date = supply_date;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getConsumable_application_key() {
		return consumable_application_key;
	}

	public void setConsumable_application_key(String consumable_application_key) {
		this.consumable_application_key = consumable_application_key;
	}

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	public String getApply_time() {
		return apply_time;
	}

	public void setApply_time(String apply_time) {
		this.apply_time = apply_time;
	}

	public String getFix_exchange() {
		return fix_exchange;
	}

	public void setFix_exchange(String fix_exchange) {
		this.fix_exchange = fix_exchange;
	}

	public String getApply_reason() {
		return apply_reason;
	}

	public void setApply_reason(String apply_reason) {
		this.apply_reason = apply_reason;
	}

	public String getSupply_time() {
		return supply_time;
	}

	public void setSupply_time(String supply_time) {
		this.supply_time = supply_time;
	}

	public String getAdjust_inventory() {
		return adjust_inventory;
	}

	public void setAdjust_inventory(String adjust_inventory) {
		this.adjust_inventory = adjust_inventory;
	}

	public String getAdjust_time() {
		return adjust_time;
	}

	public void setAdjust_time(String adjust_time) {
		this.adjust_time = adjust_time;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
