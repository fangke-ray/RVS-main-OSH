package com.osh.rvs.form.partial;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * @Description: 消耗品发放记录
 * @author liuxb
 * @date 2018-5-18 上午8:10:12
 */
public class ConsumableSupplyRecordForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1973682353317753824L;
	/** 消耗品申请单编号 **/
	@BeanField(title = "消耗品申请单编号", name = "application_no", type = FieldType.String, length = 15)
	private String application_no;

	/** 申请日期 **/
	@BeanField(title = "申请日期", name = "apply_time", type = FieldType.DateTime)
	private String apply_time;

	/** 申请理由 **/
	@BeanField(title = "申请理由", name = "apply_reason", type = FieldType.String, length = 120)
	private String apply_reason;

	/** 发放者 **/
	@BeanField(title = "发放者", name = "supplier_name", type = FieldType.String, length = 8)
	private String supplier_name;

	/** 发放时间 **/
	@BeanField(title = "发放时间", name = "supply_time", type = FieldType.DateTime)
	private String supply_time;

	/** 申请方式 **/
	@BeanField(title = "申请方式", name = "apply_method", type = FieldType.Integer, length = 1)
	private String apply_method;

	/** 申请者 **/
	@BeanField(title = "申请者", name = "petitioner_name", type = FieldType.String, length = 8)
	private String petitioner_name;

	/** 申请数量 **/
	@BeanField(title = "申请数量", name = "apply_quantity", type = FieldType.Integer, length = 4)
	private String apply_quantity;

	/** 发放数量 **/
	@BeanField(title = "发放数量", name = "supply_quantity", type = FieldType.Integer, length = 4)
	private String supply_quantity;

	/** 总价(price * supply_quantity) **/
	@BeanField(title = "总价", name = "total_price", type = FieldType.Double)
	private String total_price;

	/** 消耗品分类 **/
	@BeanField(title = "消耗品分类", name = "type", type = FieldType.Integer, length = 1)
	private String type;

	/** 基准库存 **/
	@BeanField(title = "基准库存", name = "benchmark", type = FieldType.Integer, length = 5)
	private String benchmark;

	/** 安全库存 **/
	@BeanField(title = "安全库存", name = "safety_lever", type = FieldType.Integer, length = 5)
	private String safety_lever;

	/** 库位编号 **/
	@BeanField(title = "库位编号", name = "stock_code", type = FieldType.String, length = 20)
	private String stock_code;

	/** 课室 **/
	@BeanField(title = "课室", name = "section_name", type = FieldType.String, length = 11)
	private String section_name;

	/** 工程 **/
	@BeanField(title = "工程", name = "line_name", type = FieldType.String, length = 15)
	private String line_name;

	/** 工位代码 **/
	@BeanField(title = "工位代码", name = "process_code", type = FieldType.String, length = 3)
	private String process_code;

	/** 修理单号 **/
	@BeanField(title = "修理单号", name = "omr_notifi_no", type = FieldType.String, length = 14)
	private String omr_notifi_no;

	/** 零件编码 **/
	@BeanField(title = "零件编码 ", name = "partial_code", type = FieldType.String, length = 9)
	private String partial_code;

	/** 零件名称 **/
	@BeanField(title = "零件名称 ", name = "partial_name", type = FieldType.String, length = 100)
	private String partial_name;

	/** 参考价格 **/
	@BeanField(title = "参考价格 ", name = "price", type = FieldType.Double)
	private String price;

	/** 发放开始时间 **/
	@BeanField(title = "发放开始时间", name = "supply_time_start", type = FieldType.Date)
	private String supply_time_start;

	/** 发放结束时间 **/
	@BeanField(title = "发放结束时间", name = "supply_time_end", type = FieldType.Date)
	private String supply_time_end;

	@BeanField(title = "消耗品分类", name = "types", type = FieldType.String)
	private String types;

	@BeanField(title = "消耗目标", name = "consumpt_quota", type = FieldType.UDouble, length = 8, scale = 3)
	private String consumpt_quota;

	@BeanField(title = "零件 ID ", name = "partial_id", type = FieldType.String, length = 11)
	private String partial_id;

	public String getApplication_no() {
		return application_no;
	}

	public void setApplication_no(String application_no) {
		this.application_no = application_no;
	}

	public String getApply_time() {
		return apply_time;
	}

	public void setApply_time(String apply_time) {
		this.apply_time = apply_time;
	}

	public String getApply_reason() {
		return apply_reason;
	}

	public void setApply_reason(String apply_reason) {
		this.apply_reason = apply_reason;
	}

	public String getSupplier_name() {
		return supplier_name;
	}

	public void setSupplier_name(String supplier_name) {
		this.supplier_name = supplier_name;
	}

	public String getSupply_time() {
		return supply_time;
	}

	public void setSupply_time(String supply_time) {
		this.supply_time = supply_time;
	}

	public String getApply_method() {
		return apply_method;
	}

	public void setApply_method(String apply_method) {
		this.apply_method = apply_method;
	}

	public String getPetitioner_name() {
		return petitioner_name;
	}

	public void setPetitioner_name(String petitioner_name) {
		this.petitioner_name = petitioner_name;
	}

	public String getApply_quantity() {
		return apply_quantity;
	}

	public void setApply_quantity(String apply_quantity) {
		this.apply_quantity = apply_quantity;
	}

	public String getSupply_quantity() {
		return supply_quantity;
	}

	public void setSupply_quantity(String supply_quantity) {
		this.supply_quantity = supply_quantity;
	}

	public String getTotal_price() {
		return total_price;
	}

	public void setTotal_price(String total_price) {
		this.total_price = total_price;
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

	public String getStock_code() {
		return stock_code;
	}

	public void setStock_code(String stock_code) {
		this.stock_code = stock_code;
	}

	public String getSection_name() {
		return section_name;
	}

	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}

	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
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

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getSupply_time_start() {
		return supply_time_start;
	}

	public void setSupply_time_start(String supply_time_start) {
		this.supply_time_start = supply_time_start;
	}

	public String getSupply_time_end() {
		return supply_time_end;
	}

	public void setSupply_time_end(String supply_time_end) {
		this.supply_time_end = supply_time_end;
	}

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public String getConsumpt_quota() {
		return consumpt_quota;
	}

	public void setConsumpt_quota(String consumpt_quota) {
		this.consumpt_quota = consumpt_quota;
	}

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

}
