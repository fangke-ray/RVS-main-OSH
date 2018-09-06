package com.osh.rvs.form.partial;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;

public class PartialOrderManageForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6153790312601356289L;
	
	//修改之后的订购数量
	@BeanField(title = "修改之后的订购数量", name = "new_quantity", type = FieldType.Integer,length = 2)
	private String  new_quantity;
	//修改之后的定位工位
	@BeanField(title = "修改之后的定位工位", name = "new_position_id", type = FieldType.String)
	private String  new_position_id;
	//修改之后的订购者
	@BeanField(title = "修改之后的订购者'", name = "new_belongs", type = FieldType.Integer, length = 1)
	private String new_belongs;

	@BeanField(title = "定位完毕", name = "positioning", type = FieldType.String)
	private String positioning;
	
	
	@BeanField(title = "维修对象ID", name = "material_id", type = FieldType.String, length = 11, notNull = true)
	private String material_id;
	
	@BeanField(title = "零件BO'", name = "bo_flg", type = FieldType.Integer, length = 1)
	private String bo_flg;
	
	@BeanField(title = "零件缺品备注", name = "bo_contents", type = FieldType.String, length = 200)
	private String bo_contents;
	@BeanField(title = "零件缺品备注2期", name = "bo_contents_new", type = FieldType.String, length = 200)
	private String bo_contents_new;
	
	
	@BeanField(title = "零件订购日期", name = "order_date", type = FieldType.Date, notNull = true)
	private String order_date;
	
	@BeanField(title = "零件到货日", name = "arrival_date", type = FieldType.Date)
	private String arrival_date;
	@BeanField(title = "入库预定日", name = "arrival_plan_date", type = FieldType.Date)
	private String arrival_plan_date;
	
	@BeanField(title = "订购日开始", name = "order_date_start", type = FieldType.Date)
	private String order_date_start;
	@BeanField(title = "订购日终了", name = "order_date_end", type = FieldType.Date)
	private String order_date_end;
	
	@BeanField(title = "签收日开始", name = "arrival_date_start", type = FieldType.Date)
	private String arrival_date_start;
	@BeanField(title = "签收日终了", name = "arrival_date_end", type = FieldType.Date)
	private String arrival_date_end;
	private String arrival_plan_date_start;
	private String arrival_plan_date_end;
	
	@BeanField(title = "出货日开始", name = "outline_date_start", type = FieldType.Date)
	private String outline_date_start;
	@BeanField(title = "出货日终了", name = "outline_date_end", type = FieldType.Date)
	private String outline_date_end;

	private String sorc_no;
	@BeanField(title = "维修对象型号 ID", name = "model_id", type = FieldType.String, length = 11)
	private String model_id;
	@BeanField(title = "等级", name = "level", type = FieldType.Integer)
	private String level;
	
	private String levelName;
	private String model_name;
	
	private String serial_no;
	private String scheduled_expedited;
	
	@BeanField(title = "纳期", name = "scheduled_date", type = FieldType.Date)
	private Date scheduled_date;

	@BeanField(title = "缺品发生次数", name = "occur_times", type = FieldType.Integer, length = 2)
	private String occur_times;
	@BeanField(title = "缺品发生工位", name = "position_id", type = FieldType.String, length = 11)
	private String position_id;
	private String process_code;
	private String process_name;
	
	private String line_name;
	
	 
	//====新增检索条件====
	private String range;//检索范围
	private String bo_occur_line;//发生BO工程
	private String echelon;//所属梯队
	private String bo_partial;//发生BO零件
	
	private String bo_within_3days;
	private String qty;
	private String bo_item;
	private String type_of_bo_item;
	private String remarks;
	private String safety;
	private String isHistory;

	private String section_id;
	
	//添加belongs(零件追加明细)
	@BeanField(title = "订购者", name = "belongs", type = FieldType.Integer, length = 2)
	private String belongs;
	
	/* 零件订购签收明细key */
	@BeanField(title = "零件订购签收明细key", name = "material_partial_detail_key", type = FieldType.String, primaryKey = true, length = 11)
	private String material_partial_detail_key;

	/* 零件 ID */
	@BeanField(title = "零件 ID", name = "partial_id", type = FieldType.String, notNull = true, length = 11)
	private String partial_id;

	/* 订购数量 */
	@BeanField(title = "订购数量", name = "quantity", type = FieldType.Integer, length = 2)
	private String quantity;

	/* 价格 */
	@BeanField(title = "价格", name = "price", type = FieldType.Double, notNull = true)
	private String price;


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

	/* 本次签收数量 */
	@BeanField(title = "本次签收数量", name = "recept_quantity", type = FieldType.Integer, length = 1)
	private String recept_quantity;

	private String line_id;
	private String partial_code;
	/** 
	 * 页面追加标记
	 * 消耗品代替借用为消耗品标记 
	 * 零件签收借用为BOM + 消耗品标记 
	 **/
	private String append;
	
	@BeanField(title = "BOM数量", name = "bom_quantity", type = FieldType.Integer)
	private String bom_quantity;
	
	@BeanField(title="消耗品库存对象",name="consumable_flg",type=FieldType.Integer,length=1)
	private String consumable_flg;

	/*未签收数量*/
	@BeanField(title="未签收数量",name="waiting_receive_quantity",type=FieldType.Integer,length=2)
	private String waiting_receive_quantity;
	
	/*最后签收时间*/
	@BeanField(title="最后签收时间",name="recent_receive_time",type=FieldType.DateTime)
	private String recent_receive_time;

	
	public String getNew_quantity() {
		return new_quantity;
	}
	public void setNew_quantity(String new_quantity) {
		this.new_quantity = new_quantity;
	}
	public String getNew_position_id() {
		return new_position_id;
	}
	public void setNew_position_id(String new_position_id) {
		this.new_position_id = new_position_id;
	}
	public String getNew_belongs() {
		return new_belongs;
	}
	public void setNew_belongs(String new_belongs) {
		this.new_belongs = new_belongs;
	}
	public String getMaterial_partial_detail_key() {
		return material_partial_detail_key;
	}
	public void setMaterial_partial_detail_key(String material_partial_detail_key) {
		this.material_partial_detail_key = material_partial_detail_key;
	}
	public String getPartial_id() {
		return partial_id;
	}
	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
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
	public String getBelongs() {
		return belongs;
	}
	public void setBelongs(String belongs) {
		this.belongs = belongs;
	}
	public String getPositioning() {
		return positioning;
	}
	public void setPositioning(String positioning) {
		this.positioning = positioning;
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
	public String getOccur_times() {
		return occur_times;
	}
	public void setOccur_times(String occur_times) {
		this.occur_times = occur_times;
	}
	public String getPosition_id() {
		return position_id;
	}
	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}
	public String getLevelName() {
		if (level != null) {
			return CodeListUtils.getValue("material_level", level);
		}
		return levelName;
	}
	public void setLevelName(String levelName) {
		this.levelName = levelName;
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
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getMaterial_id() {
		return material_id;
	}
	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}
	public String getBo_flg() {
		return bo_flg;
	}
	public void setBo_flg(String bo_flg) {
		this.bo_flg = bo_flg;
	}
	public String getBo_contents() {
		return bo_contents;
	}
	public void setBo_contents(String bo_contents) {
		this.bo_contents = bo_contents;
	}
	public String getOrder_date() {
		return order_date;
	}
	public void setOrder_date(String order_date) {
		this.order_date = order_date;
	}
	public String getArrival_date() {
		return arrival_date;
	}
	public void setArrival_date(String arrival_date) {
		this.arrival_date = arrival_date;
	}
	public String getArrival_plan_date() {
		return arrival_plan_date;
	}
	public void setArrival_plan_date(String arrival_plan_date) {
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
	
	public String getIsHistory() {
		return isHistory;
	}
	public void setIsHistory(String isHistory) {
		this.isHistory = isHistory;
	}
	public String filterSpecialValue(String value, String preName) {
		if ("bo_flg".equals(preName)) {
			if ("1".equals(value)) {
				return "BO";
			} else if ("2".equals(value)) {
				return "BO解决";
			}
			return "";
		} else if ("bo_contents".equals(preName)) {
			try {
				String rt = "";
				List<Map<String, String>> list = JSON.decode("[" + value + "]");
				for (Map<String, String> map : list) {
					rt += map.get("dec") + map.get("ns") + map.get("com") + " ";
				}

				return rt;
			} catch (Exception e) {
				return value;
			}
		} else if ("scheduled_expedited".equals(preName)) {
			return "1".equals(value) ? "加急" : "";
		} else if ("arrival_plan_date".equals(preName)) {
			return "9999/12/31".equals(value) ? "未定":value;
		} else if ("occur_times".equals(preName)) {
			return CommonStringUtil.isEmpty(value) ? "" : value+"次";
		}
		return value;
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
	public String getOutline_date_start() {
		return outline_date_start;
	}
	public void setOutline_date_start(String outline_date_start) {
		this.outline_date_start = outline_date_start;
	}
	public String getOutline_date_end() {
		return outline_date_end;
	}
	public void setOutline_date_end(String outline_date_end) {
		this.outline_date_end = outline_date_end;
	}
	public String getSection_id() {
		return section_id;
	}
	public void setSection_id(String section_id) {
		this.section_id = section_id;
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
	public Date getScheduled_date() {
		return scheduled_date;
	}
	public void setScheduled_date(Date scheduled_date) {
		this.scheduled_date = scheduled_date;
	}
}
