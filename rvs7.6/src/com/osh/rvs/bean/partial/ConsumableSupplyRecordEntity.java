package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 消耗品发放记录
 * @author liuxb
 * @date 2018-5-17 下午5:44:04
 */
public class ConsumableSupplyRecordEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7766758195525132984L;

	// 消耗品申请单
	/** 消耗品申请单编号 **/
	private String application_no;

	/** 申请日期 **/
	private Date apply_time;

	/** 申请理由 **/
	private String apply_reason;

	/** 发放者 **/
	private String supplier_name;

	/** 发放时间 **/
	private Date supply_time;

	// 消耗品申请单明细
	/** 申请方式 **/
	private Integer apply_method;

	/** 申请者 **/
	private String petitioner_name;

	/** 申请数量 **/
	private Integer apply_quantity;

	/** 发放数量 **/
	private Integer supply_quantity;

	/** 总价(price * supply_quantity) **/
	private BigDecimal total_price;

	// 消耗品管理
	/** 消耗品分类 **/
	private Integer type;

	/** 基准库存 **/
	private Integer benchmark;

	/** 安全库存 **/
	private Integer safety_lever;

	/** 库位编号 **/
	private String stock_code;

	/** 课室 **/
	private String section_name;

	/** 工程 **/
	private String line_name;

	/** 工位代码 **/
	private String process_code;

	// 维修对象
	/** 修理单号 **/
	private String omr_notifi_no;

	// 零件
	/** 零件编码 **/
	private String partial_code;

	/** 零件名称 **/
	private String partial_name;

	/** 参考价格 **/
	private BigDecimal price;

	/** 发放开始时间 **/
	private Date supply_time_start;

	/** 发放结束时间 **/
	private Date supply_time_end;

	private String types;

	private String order_by;

	/** 零件 ID **/
	private String partial_id;

	/** 消耗目标 **/
	private BigDecimal consumpt_quota;

	/** 平均消耗量 **/
	private BigDecimal average_supply_quantity;

	public String getApplication_no() {
		return application_no;
	}

	public void setApplication_no(String application_no) {
		this.application_no = application_no;
	}

	public Date getApply_time() {
		return apply_time;
	}

	public void setApply_time(Date apply_time) {
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

	public Date getSupply_time() {
		return supply_time;
	}

	public void setSupply_time(Date supply_time) {
		this.supply_time = supply_time;
	}

	public Integer getApply_method() {
		return apply_method;
	}

	public void setApply_method(Integer apply_method) {
		this.apply_method = apply_method;
	}

	public String getPetitioner_name() {
		return petitioner_name;
	}

	public void setPetitioner_name(String petitioner_name) {
		this.petitioner_name = petitioner_name;
	}

	public Integer getApply_quantity() {
		return apply_quantity;
	}

	public void setApply_quantity(Integer apply_quantity) {
		this.apply_quantity = apply_quantity;
	}

	public Integer getSupply_quantity() {
		return supply_quantity;
	}

	public void setSupply_quantity(Integer supply_quantity) {
		this.supply_quantity = supply_quantity;
	}

	public BigDecimal getTotal_price() {
		return total_price;
	}

	public void setTotal_price(BigDecimal total_price) {
		this.total_price = total_price;
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

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Date getSupply_time_start() {
		return supply_time_start;
	}

	public void setSupply_time_start(Date supply_time_start) {
		this.supply_time_start = supply_time_start;
	}

	public Date getSupply_time_end() {
		return supply_time_end;
	}

	public void setSupply_time_end(Date supply_time_end) {
		this.supply_time_end = supply_time_end;
	}

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public String getOrder_by() {
		return order_by;
	}

	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public BigDecimal getConsumpt_quota() {
		return consumpt_quota;
	}

	public void setConsumpt_quota(BigDecimal consumpt_quota) {
		this.consumpt_quota = consumpt_quota;
	}

	public BigDecimal getAverage_supply_quantity() {
		return average_supply_quantity;
	}

	public void setAverage_supply_quantity(BigDecimal average_supply_quantity) {
		this.average_supply_quantity = average_supply_quantity;
	}

}
