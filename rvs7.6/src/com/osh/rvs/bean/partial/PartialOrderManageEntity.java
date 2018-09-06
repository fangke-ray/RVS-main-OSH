package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PartialOrderManageEntity implements Serializable {

	/**
	 * 零件订购签收明细material_partial_detail
	 */
	private static final long serialVersionUID = 4045413846090489147L;
    //修改之后的订购数量
	private Integer  new_quantity;
	//修改之后的定位工位
	private String  new_position_id;
	//修改之后的订购者
	private Integer new_belongs;
	
	// 设置一个belongs，在订购时载入文件作为条件传递（1是属于bom，6不良）
	private Integer bo_flg;
	private String bo_contents;
	private String bo_contents_new;
	private Date order_date;
	private Date arrival_date;

	private String model_id;
	private String model_name;
	private Integer level;
	private String serial_no;
	private String scheduled_expedited;
	private Date scheduled_date;

	private Date order_date_start;
	private Date order_date_end;
	private String arrival_date_start;
	private String arrival_date_end;
	private String arrival_plan_date_start;
	private String arrival_plan_date_end;

	private Date outline_date_start;
	private Date outline_date_end;

	private String process_name;
	private String line_name;

	// ====新增检索条件====
	private String range;// 检索范围
	private String bo_occur_line;// 发生BO工程
	private String echelon;// 所属梯队
	private String bo_partial;// 发生BO零件

	private Integer isHistory;

	private String sorc_no;

	/* 维修对象BOM数量 */
	private Integer bom_quantity;

	/* 零件订购签收明细key */
	private String material_partial_detail_key;
	/* 维修对象 ID */
	private String material_id;
	/* 零件 ID */
	private String partial_id;
	/* 订购次数 */
	private Integer occur_times;
	/* 订购数量 */
	private Integer quantity;
	/* 价格 */
	private BigDecimal price;
	/* 订购者 */
	private Integer belongs;
	/* 指定工位 */
	private String position_id;
	/* 未发放数量 */
	private Integer waiting_quantity;
	/* 最后发放时间 */
	private Date recent_signin_time;
	/* 签收状态 */
	private Integer status;
	/* 已签收数量 */
	private Integer cur_quantity;
	/* 本次签收数量 */
	private Integer recept_quantity;

	private String job_no;
	private String process_code;// 使用工位
	private String code;// 零件编码
	private String partial_name;// 零件名称
	private Integer consumable_flg;// 零件消耗品库存

	private String line_id;
	private String partial_code;
	private String append;

	/* 未签收数量 */
	private Integer waiting_receive_quantity;
	/* 最后签收时间 */
	private Date recent_receive_time;

	/* 入库预定日 */
	private Date arrival_plan_date;

	/** 查询者课室 **/
	private String section_id;

	/* 线上签收者 ID */
	private String r_operator_id;


	public Integer getNew_quantity() {
		return new_quantity;
	}

	public void setNew_quantity(Integer new_quantity) {
		this.new_quantity = new_quantity;
	}

	public String getNew_position_id() {
		return new_position_id;
	}

	public void setNew_position_id(String new_position_id) {
		this.new_position_id = new_position_id;
	}

	public Integer getNew_belongs() {
		return new_belongs;
	}

	public void setNew_belongs(Integer new_belongs) {
		this.new_belongs = new_belongs;
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

	public String getBo_contents_new() {
		return bo_contents_new;
	}

	public void setBo_contents_new(String bo_contents_new) {
		this.bo_contents_new = bo_contents_new;
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

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
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

	public Date getScheduled_date() {
		return scheduled_date;
	}

	public void setScheduled_date(Date scheduled_date) {
		this.scheduled_date = scheduled_date;
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

	public String getProcess_name() {
		return process_name;
	}

	public void setProcess_name(String process_name) {
		this.process_name = process_name;
	}

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
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

	public Integer getIsHistory() {
		return isHistory;
	}

	public void setIsHistory(Integer isHistory) {
		this.isHistory = isHistory;
	}

	public String getSorc_no() {
		return sorc_no;
	}

	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
	}

	public Integer getBom_quantity() {
		return bom_quantity;
	}

	public void setBom_quantity(Integer bom_quantity) {
		this.bom_quantity = bom_quantity;
	}

	public String getMaterial_partial_detail_key() {
		return material_partial_detail_key;
	}

	public void setMaterial_partial_detail_key(String material_partial_detail_key) {
		this.material_partial_detail_key = material_partial_detail_key;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public Integer getOccur_times() {
		return occur_times;
	}

	public void setOccur_times(Integer occur_times) {
		this.occur_times = occur_times;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
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

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public Integer getWaiting_quantity() {
		return waiting_quantity;
	}

	public void setWaiting_quantity(Integer waiting_quantity) {
		this.waiting_quantity = waiting_quantity;
	}

	public Date getRecent_signin_time() {
		return recent_signin_time;
	}

	public void setRecent_signin_time(Date recent_signin_time) {
		this.recent_signin_time = recent_signin_time;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getCur_quantity() {
		return cur_quantity;
	}

	public void setCur_quantity(Integer cur_quantity) {
		this.cur_quantity = cur_quantity;
	}

	public Integer getRecept_quantity() {
		return recept_quantity;
	}

	public void setRecept_quantity(Integer recept_quantity) {
		this.recept_quantity = recept_quantity;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPartial_name() {
		return partial_name;
	}

	public void setPartial_name(String partial_name) {
		this.partial_name = partial_name;
	}

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public String getPartial_code() {
		return partial_code;
	}

	public void setPartial_code(String partial_code) {
		this.partial_code = partial_code;
	}

	public String getAppend() {
		return append;
	}

	public void setAppend(String append) {
		this.append = append;
	}

	public Integer getWaiting_receive_quantity() {
		return waiting_receive_quantity;
	}

	public void setWaiting_receive_quantity(Integer waiting_receive_quantity) {
		this.waiting_receive_quantity = waiting_receive_quantity;
	}

	public Date getRecent_receive_time() {
		return recent_receive_time;
	}

	public void setRecent_receive_time(Date recent_receive_time) {
		this.recent_receive_time = recent_receive_time;
	}

	public Date getArrival_plan_date() {
		return arrival_plan_date;
	}

	public void setArrival_plan_date(Date arrival_plan_date) {
		this.arrival_plan_date = arrival_plan_date;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getR_operator_id() {
		return r_operator_id;
	}

	public void setR_operator_id(String r_operator_id) {
		this.r_operator_id = r_operator_id;
	}

	public String getJob_no() {
		return job_no;
	}

	public void setJob_no(String job_no) {
		this.job_no = job_no;
	}

	public Integer getConsumable_flg() {
		return consumable_flg;
	}

	public void setConsumable_flg(Integer consumable_flg) {
		this.consumable_flg = consumable_flg;
	}

}
