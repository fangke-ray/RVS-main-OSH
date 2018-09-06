package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class MaterialPartialDetailEntity implements Serializable {

	/**
	 * 零件订购签收明细material_partial_detail
	 */
	private static final long serialVersionUID = 4045413846090489147L;
	/*维修对象BOM数量*/
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
	/* 修改之后的订购数量 */
	private Integer new_quantity;
	/* 价格 */
	private BigDecimal price;
	/* 订购者 */
	private Integer belongs;
	/* 指定工位 */
	private String position_id;
	/* 输入后的订购工位*/
	private String new_position_id;
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
	
	/*未签收数量*/
	private Integer waiting_receive_quantity;
	/*最后签收时间*/
	private Date recent_receive_time;
	
	/*入库预定日*/
	private Date arrival_plan_date;

	/** 查询者课室 **/
	private String section_id;

	/* 线上签收者 ID */
	private String r_operator_id;

	///***零件定位表***/////	
	/*判断零件是否已经超过有效期*/
	private String isOverdue;	
    /*判断零件是否有新零件ID*/
	private String isEqual;
	
	private String old_partial_id;
	/*型号ID*/
	private String model_id;
	/*有效截止日期*/
	private Date history_limit_date;
	/*更名对应零件 ID*/
	private String new_partial_id;
	/*UNIT零件 ID*/
	private String parent_partial_id;
	
	/*型号*/
	private String model_name;
	/*等级*/
	private String level;
	
	private String  name;
	
	/*UNIT零件 代码*/
	private String parent_partial_code;

	/*使用率*/
	private String userate;
	/*最后更新人*/
	private String updated_by;
	/*最后更新时间*/
	private Timestamp updated_time;
	/*bom*/
	private String bom;
	
	private String active_date;
	
	private Integer available_inventory;//当前有效库存
	
	private Integer fix_type;//流水线分类
	
	private String smo_item_no;
	
	public String getNew_position_id() {
		return new_position_id;
	}

	public void setNew_position_id(String new_position_id) {
		this.new_position_id = new_position_id;
	}

	public Integer getNew_quantity() {
		return new_quantity;
	}

	public void setNew_quantity(Integer new_quantity) {
		this.new_quantity = new_quantity;
	}

	public String getIsOverdue() {
		return isOverdue;
	}

	public void setIsOverdue(String isOverdue) {
		this.isOverdue = isOverdue;
	}

	public String getIsEqual() {
		return isEqual;
	}

	public void setIsEqual(String isEqual) {
		this.isEqual = isEqual;
	}

	public String getOld_partial_id() {
		return old_partial_id;
	}

	public void setOld_partial_id(String old_partial_id) {
		this.old_partial_id = old_partial_id;
	}

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public Date getHistory_limit_date() {
		return history_limit_date;
	}

	public void setHistory_limit_date(Date history_limit_date) {
		this.history_limit_date = history_limit_date;
	}

	public String getNew_partial_id() {
		return new_partial_id;
	}

	public void setNew_partial_id(String new_partial_id) {
		this.new_partial_id = new_partial_id;
	}

	public String getParent_partial_id() {
		return parent_partial_id;
	}

	public void setParent_partial_id(String parent_partial_id) {
		this.parent_partial_id = parent_partial_id;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParent_partial_code() {
		return parent_partial_code;
	}

	public void setParent_partial_code(String parent_partial_code) {
		this.parent_partial_code = parent_partial_code;
	}

	public String getUserate() {
		return userate;
	}

	public void setUserate(String userate) {
		this.userate = userate;
	}

	public String getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}

	public Timestamp getUpdated_time() {
		return updated_time;
	}

	public void setUpdated_time(Timestamp updated_time) {
		this.updated_time = updated_time;
	}

	public String getBom() {
		return bom;
	}

	public void setBom(String bom) {
		this.bom = bom;
	}

	public String getActive_date() {
		return active_date;
	}

	public void setActive_date(String active_date) {
		this.active_date = active_date;
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

	public Integer getAvailable_inventory() {
		return available_inventory;
	}

	public void setAvailable_inventory(Integer available_inventory) {
		this.available_inventory = available_inventory;
	}

	public Integer getFix_type() {
		return fix_type;
	}

	public void setFix_type(Integer fix_type) {
		this.fix_type = fix_type;
	}

	public String getSmo_item_no() {
		return smo_item_no;
	}

	public void setSmo_item_no(String smo_item_no) {
		this.smo_item_no = smo_item_no;
	}

}
