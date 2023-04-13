package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author fxc PS: String 在Mapper.XML中IF判断条件 !=NULL and !=''; 其他 !=NULL
 */
public class MaterialPartialEntity implements Serializable {

	private static final long serialVersionUID = 216497845327462667L;

	// 总计损金
	private BigDecimal totalPrice;

	private BigDecimal price;

	// 设置一个belongs，在订购时载入文件作为条件传递（1是属于bom，6不良）
	private Integer belongs;

	private String material_id;
	private Integer bo_flg;
	private String bo_contents;
	private String bo_contents_new;
	private Date order_date;
	private Date arrival_date;
	private Date arrival_plan_date;

	private String sorc_no;
	private String model_id;
	private String model_name;
	private Integer level;
	private String serial_no;
	private String scheduled_expedited;
	private Date scheduled_date;

	private Date order_date_start;
	private Date order_date_end;
	private Date arrival_date_start;
	private Date arrival_date_end;
	private String arrival_plan_date_start;
	private String arrival_plan_date_end;

	private Date outline_date_start;
	private Date outline_date_end;

	private Integer occur_times;
	private String position_id;
	private String process_code;
	private String process_name;
	private String line_id;
	private String line_name;

	private String section_id;

	// ====新增检索条件====
	private String range;// 检索范围
	private String bo_occur_line;// 发生BO工程
	private String echelon;// 所属梯队
	private String bo_partial;// 发生BO零件

	private Integer isHistory;

	// ====导出时所需
	private String bo_within_3days;
	private String qty;
	private String bo_item;
	private String type_of_bo_item;
	private String remarks;
	private String safety;

	private Integer over_state;// 订购时间超出状态

	private Integer fix_type;// 流水线分类

	private Integer kind;// 分类
	
	private Date order_time;

	private Date inline_time;

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getBelongs() {
		return belongs;
	}

	public void setBelongs(Integer belongs) {
		this.belongs = belongs;
	}

	public String getProcess_name() {
		return process_name;
	}

	public void setProcess_name(String process_name) {
		this.process_name = process_name;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public Integer getOccur_times() {
		return occur_times;
	}

	public void setOccur_times(Integer occur_times) {
		this.occur_times = occur_times;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getSerial_no() {
		return serial_no;
	}

	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}

	public String getScheduled_expedited() {
		return scheduled_expedited;
	}

	public void setScheduled_expedited(String scheduled_expedited) {
		this.scheduled_expedited = scheduled_expedited;
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

	public String getSorc_no() {
		return sorc_no;
	}

	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
	}

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public Integer getBo_flg() {
		return bo_flg;
	}

	public void setBo_flg(Integer bo_flg) {
		this.bo_flg = bo_flg;
	}

	public String getBo_contents() {
		return bo_contents;
	}

	public void setBo_contents(String bo_contents) {
		this.bo_contents = bo_contents;
	}

	public Date getOrder_date() {
		return order_date;
	}

	public void setOrder_date(Date order_date) {
		this.order_date = order_date;
	}

	public Date getArrival_date() {
		return arrival_date;
	}

	public void setArrival_date(Date arrival_date) {
		this.arrival_date = arrival_date;
	}

	public Date getArrival_plan_date() {
		return arrival_plan_date;
	}

	public void setArrival_plan_date(Date arrival_plan_date) {
		this.arrival_plan_date = arrival_plan_date;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public String getBo_occur_line() {
		return bo_occur_line;
	}

	public void setBo_occur_line(String bo_occur_line) {
		this.bo_occur_line = bo_occur_line;
	}

	public String getEchelon() {
		return echelon;
	}

	public void setEchelon(String echelon) {
		this.echelon = echelon;
	}

	public String getBo_partial() {
		return bo_partial;
	}

	public void setBo_partial(String bo_partial) {
		this.bo_partial = bo_partial;
	}

	public String getBo_contents_new() {
		return bo_contents_new;
	}

	public void setBo_contents_new(String bo_contents_new) {
		this.bo_contents_new = bo_contents_new;
	}

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	public Integer getIsHistory() {
		return isHistory;
	}

	public void setIsHistory(Integer isHistory) {
		this.isHistory = isHistory;
	}

	public String getBo_within_3days() {
		return bo_within_3days;
	}

	public void setBo_within_3days(String bo_within_3days) {
		this.bo_within_3days = bo_within_3days;
	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public String getBo_item() {
		return bo_item;
	}

	public void setBo_item(String bo_item) {
		this.bo_item = bo_item;
	}

	public String getType_of_bo_item() {
		return type_of_bo_item;
	}

	public void setType_of_bo_item(String type_of_bo_item) {
		this.type_of_bo_item = type_of_bo_item;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getSafety() {
		return safety;
	}

	public void setSafety(String safety) {
		this.safety = safety;
	}

	public Date getOutline_date_start() {
		return outline_date_start;
	}

	public void setOutline_date_start(Date outline_date_start) {
		this.outline_date_start = outline_date_start;
	}

	public Date getOutline_date_end() {
		return outline_date_end;
	}

	public void setOutline_date_end(Date outline_date_end) {
		this.outline_date_end = outline_date_end;
	}

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public Date getArrival_date_start() {
		return arrival_date_start;
	}

	public void setArrival_date_start(Date arrival_date_start) {
		this.arrival_date_start = arrival_date_start;
	}

	public Date getArrival_date_end() {
		return arrival_date_end;
	}

	public void setArrival_date_end(Date arrival_date_end) {
		this.arrival_date_end = arrival_date_end;
	}

	public Date getScheduled_date() {
		return scheduled_date;
	}

	public void setScheduled_date(Date scheduled_date) {
		this.scheduled_date = scheduled_date;
	}

	public Integer getOver_state() {
		return over_state;
	}

	public void setOver_state(Integer over_state) {
		this.over_state = over_state;
	}

	public Integer getFix_type() {
		return fix_type;
	}

	public void setFix_type(Integer fix_type) {
		this.fix_type = fix_type;
	}

	public Integer getKind() {
		return kind;
	}

	public void setKind(Integer kind) {
		this.kind = kind;
	}

	public Date getOrder_time() {
		return order_time;
	}

	public void setOrder_time(Date order_time) {
		this.order_time = order_time;
	}

	public Date getInline_time() {
		return inline_time;
	}

	public void setInline_time(Date inline_time) {
		this.inline_time = inline_time;
	}

}
