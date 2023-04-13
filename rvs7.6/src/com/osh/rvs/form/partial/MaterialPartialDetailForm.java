package com.osh.rvs.form.partial;


import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class MaterialPartialDetailForm extends ActionForm {

	/**
	 * 零件订购签收明细
	 */
	private static final long serialVersionUID = -2846091470319922807L;
	/* 零件订购签收明细key */
	@BeanField(title = "零件订购签收明细key", name = "material_partial_detail_key", type = FieldType.String, primaryKey = true, length = 11)
	private String material_partial_detail_key;

	/* 维修对象 ID */
	@BeanField(title = "维修对象 ID", name = "material_id", type = FieldType.String, notNull = true, length = 11)
	private String material_id;

	/* 零件 ID */
	@BeanField(title = "零件 ID", name = "partial_id", type = FieldType.String, notNull = true, length = 11)
	private String partial_id;

	/* 订购次数 */
	@BeanField(title = "订购次数", name = "occur_times", type = FieldType.Integer, notNull = true, length = 2)
	private String occur_times;

	/* 订购数量 */
	@BeanField(title = "订购数量", name = "quantity", type = FieldType.Integer, notNull = true, length = 2)
	private String quantity;
	
	/* 修改之后的订购数量 */
	@BeanField(title = "修改之后的订购数量", name = "new_quantity", type = FieldType.Integer, notNull = true, length = 2)
	private String new_quantity;

	/* 价格 */
	@BeanField(title = "价格", name = "price", type = FieldType.Double, notNull = true)
	private String price;

	/* 订购者 */
	@BeanField(title = "订购者", name = "belongs", type = FieldType.Integer, notNull = true, length=2)
	private String belongs;

	/* 指定工位 */
	@BeanField(title = "指定工位", name = "position_id", type = FieldType.String, length = 11)
	private String position_id;
	
	/* 修改后的定位工位*/
	@BeanField(title = "修改后的定位工位", name = "new_position_id", type = FieldType.String, length = 11)
	private String new_position_id;

	/* 未发放数量 */
	@BeanField(title = "未发放数量", name = "waiting_quantity", type = FieldType.Integer, notNull = true, length = 2)
	private String waiting_quantity;

	/* 最后发放时间 */
	@BeanField(title = "最后发放时间", name = "recent_signin_time", type = FieldType.DateTime)
	private String recent_signin_time;

	/* 签收状态 */
	@BeanField(title = "签收状态", name = "status", type = FieldType.Integer, length = 1)
	private String status;

	/* 已签收数量 */
	@BeanField(title = "已签收数量 ", name = "cur_quantity", type = FieldType.Integer, length = 1)
	private String cur_quantity;

	@BeanField(title = "零件编码", name = "code")
	private String code;// 零件编码

	@BeanField(title = "零件名称", name = "partial_name")
	private String partial_name;// 零件名称

	@BeanField(title = "使用工位", name = "process_code", length = 3)
	private String process_code;//

	/* 本次签收数量 */
	@BeanField(title = "本次签收数量", name = "recept_quantity", type = FieldType.Integer, length = 1)
	private String recept_quantity;

	private String line_id;
	private String partial_code;
	private String order_flg;

	/** 
	 * 页面追加标记
	 * 消耗品代替借用为消耗品标记 
	 * 零件签收借用为BOM + 消耗品标记 
	 **/
	private String append;
	
	@BeanField(title = "BOM数量", name = "bom_quantity", type = FieldType.Integer)
	private String bom_quantity;

	/*Sorc NO.*/
	@BeanField(title="修理单号",name="sorc_no",length=15)
	private String sorc_no;
	
	/*零件订购开始时间*/
	@BeanField(title="零件订购开始时间",name="order_date_start",type=FieldType.Date)
	private String order_date_start;
	
	/*零件订购结束时间*/
	@BeanField(title="零件订购结束时间",name="order_date_end",type=FieldType.Date)
	private String order_date_end;
	
	/*有无BO*/
	@BeanField(title="有无BO",name="bo_flg",type=FieldType.Integer,length=1)
	private String bo_flg;
	
	@BeanField(title="消耗品库存对象",name="consumable_flg",type=FieldType.Integer,length=1)
	private String consumable_flg;

	/*未签收数量*/
	@BeanField(title="未签收数量",name="waiting_receive_quantity",type=FieldType.Integer,length=2)
	private String waiting_receive_quantity;
	
	/*最后签收时间*/
	@BeanField(title="最后签收时间",name="recent_receive_time",type=FieldType.DateTime)
	private String recent_receive_time;
	
	/*入库预定日*/
	@BeanField(title="入库预定日",name="arrival_plan_date",type=FieldType.Date)
	private String arrival_plan_date;
	
	@BeanField(title="消耗品当前有效库存",name="available_inventory",type=FieldType.Integer,length=5)
	private String available_inventory;
	
	@BeanField(title = "流水线分类", name = "fix_type", type = FieldType.Integer, length = 1)
	private String fix_type;//流水线分类
	
	@BeanField(title = "SMO项目号", name = "smo_item_no", type = FieldType.String, length = 4)
	private String smo_item_no;

	@BeanField(title = "工程名称", name = "line_name", type = FieldType.String)
	private String line_name;
	
	@BeanField(title = "规格种别", name = "spec_kind", type = FieldType.Integer)
	private String spec_kind;

	@BeanField(title = "等级", name = "level", type = FieldType.Integer, length = 2)
	private String level;

	@BeanField(title = "分配课室", name = "section_id", type = FieldType.String, length = 11)
	private String section_id;

	public String getNew_position_id() {
		return new_position_id;
	}

	public void setNew_position_id(String new_position_id) {
		this.new_position_id = new_position_id;
	}

	public String getNew_quantity() {
		return new_quantity;
	}

	public void setNew_quantity(String new_quantity) {
		this.new_quantity = new_quantity;
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

	public String getOccur_times() {
		return occur_times;
	}

	public void setOccur_times(String occur_times) {
		this.occur_times = occur_times;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getBelongs() {
		return belongs;
	}

	public void setBelongs(String belongs) {
		this.belongs = belongs;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public String getWaiting_quantity() {
		return waiting_quantity;
	}

	public void setWaiting_quantity(String waiting_quantity) {
		this.waiting_quantity = waiting_quantity;
	}

	public String getRecent_signin_time() {
		return recent_signin_time;
	}

	public void setRecent_signin_time(String recent_signin_time) {
		this.recent_signin_time = recent_signin_time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCur_quantity() {
		return cur_quantity;
	}

	public void setCur_quantity(String cur_quantity) {
		this.cur_quantity = cur_quantity;
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

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getRecept_quantity() {
		return recept_quantity;
	}

	public void setRecept_quantity(String recept_quantity) {
		this.recept_quantity = recept_quantity;
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

	public String getSorc_no() {
		return sorc_no;
	}

	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
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

	public String getBo_flg() {
		return bo_flg;
	}

	public void setBo_flg(String bo_flg) {
		this.bo_flg = bo_flg;
	}

	public String getWaiting_receive_quantity() {
		return waiting_receive_quantity;
	}

	public void setWaiting_receive_quantity(String waiting_receive_quantity) {
		this.waiting_receive_quantity = waiting_receive_quantity;
	}

	public String getRecent_receive_time() {
		return recent_receive_time;
	}

	public void setRecent_receive_time(String recent_receive_time) {
		this.recent_receive_time = recent_receive_time;
	}

	public String getArrival_plan_date() {
		return arrival_plan_date;
	}

	public void setArrival_plan_date(String arrival_plan_date) {
		this.arrival_plan_date = arrival_plan_date;
	}

	public String getBom_quantity() {
		return bom_quantity;
	}

	public void setBom_quantity(String bom_quantity) {
		this.bom_quantity = bom_quantity;
	}

	public String getConsumable_flg() {
		return consumable_flg;
	}

	public void setConsumable_flg(String consumable_flg) {
		this.consumable_flg = consumable_flg;
	}

	public String getAvailable_inventory() {
		return available_inventory;
	}

	public void setAvailable_inventory(String available_inventory) {
		this.available_inventory = available_inventory;
	}

	public String getFix_type() {
		return fix_type;
	}

	public void setFix_type(String fix_type) {
		this.fix_type = fix_type;
	}

	public String getSmo_item_no() {
		return smo_item_no;
	}

	public void setSmo_item_no(String smo_item_no) {
		this.smo_item_no = smo_item_no;
	}

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	public String getSpec_kind() {
		return spec_kind;
	}

	public void setSpec_kind(String spec_kind) {
		this.spec_kind = spec_kind;
	}

	public String getOrder_flg() {
		return order_flg;
	}

	public void setOrder_flg(String order_flg) {
		this.order_flg = order_flg;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

}
