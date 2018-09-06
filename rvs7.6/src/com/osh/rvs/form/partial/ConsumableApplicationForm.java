package com.osh.rvs.form.partial;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class ConsumableApplicationForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1154274888044788539L;

	@BeanField(title = "消耗品申请单Key", name = "consumable_application_key", type = FieldType.String, length = 11, notNull = true, primaryKey = true)
	private String consumable_application_key;// 消耗品申请单Key

	@BeanField(title = "消耗品申请单编号", name = "application_no", type = FieldType.String, length = 15, notNull = true)
	private String application_no;// 消耗品申请单编号

	@BeanField(title = "课室ID", name = "section_id", type = FieldType.String, length = 11, notNull = true)
	private String section_id;// 课室ID

	@BeanField(title = "工程ID", name = "line_id", type = FieldType.String, length = 11, notNull = true)
	private String line_id;// 工程ID

	@BeanField(title = "申请日时", name = "apply_time", type = FieldType.DateTime)
	private String apply_time;// 申请日时

	@BeanField(title = "维修对象ID", name = "material_id", type = FieldType.String, length = 11)
	private String material_id;// 维修对象ID

	@BeanField(title = "申请理由", name = "apply_reason", type = FieldType.String, length = 120)
	private String apply_reason;// 申请理由

	@BeanField(title = "申请确认人ID", name = "comfirmer_id", type = FieldType.String, length = 11)
	private String comfirmer_id;// 申请确认人ID

	@BeanField(title = "发放提供人ID", name = "supplier_id", type = FieldType.String, length = 11)
	private String supplier_id;// 发放提供人ID

	@BeanField(title = "发放提供时间", name = "supply_time", type = FieldType.DateTime)
	private String supply_time;// 发放提供时间

	@BeanField(title = "发放完成标记", name = "all_supplied", type = FieldType.Integer, notNull = true)
	private String all_supplied;// 发放完成标记

	@BeanField(title = "申请日时开始", name = "apply_time_start", type = FieldType.Date)
	private String apply_time_start;// 申请日时开始

	@BeanField(title = "申请日时结束", name = "apply_time_end", type = FieldType.Date)
	private String apply_time_end;// 申请日时结束

	@BeanField(title = "即时领用", name = "flg", type = FieldType.Integer)
	private String flg;// 即时领用

	@BeanField(title = "工程名称", name = "line_name")
	private String line_name;// 工程名称

	@BeanField(title = "零件 ID", name = "partial_id", type = FieldType.String, length = 11, notNull = true, primaryKey = true)
	private String partial_id;// 零件 ID

	@BeanField(title = "补充方式", name = "apply_method", type = FieldType.Integer, notNull = true)
	private String apply_method;// 补充方式

	@BeanField(title = "申请人ID", name = "petitioner_id", type = FieldType.String, length = 11, notNull = true)
	private String petitioner_id;// 申请人ID

	@BeanField(title = "计量单位", name = "unit_code", type = FieldType.Integer, length = 2)
	private String unit_code;// 计量单位

	@BeanField(title = "申请数量", name = "apply_quantity", type = FieldType.Integer, length = 4, notNull = true)
	private String apply_quantity;// 申请数量

	@BeanField(title = "提供数量", name = "supply_quantity", type = FieldType.Integer, length = 4, notNull = true)
	private String supply_quantity;// 提供数量

	@BeanField(title = "包装", name = "pack_method", type = FieldType.Integer, length = 1, notNull = true)
	private String pack_method;// 包装

	@BeanField(title = "消耗品代码", name = "code", type = FieldType.String)
	private String code;// 消耗品代码

	@BeanField(title = "零件名称", name = "partial_name", type = FieldType.String)
	private String partial_name;// 零件名称

	@BeanField(title = "当前有效库存", name = "available_inventory", type = FieldType.Integer)
	private String available_inventory;// 当前有效库存

	@BeanField(title = "待发放数量", name = "waitting_quantity", type = FieldType.Integer)
	private String waitting_quantity;// 待发放数量

	@BeanField(title = "分类", name = "type", type = FieldType.Integer)
	private String type;// 分类

	@BeanField(title = "使用单号", name = "use_no", type = FieldType.String)
	private String use_no;// 使用单号

	@BeanField(title = "内容量", name = "content", type = FieldType.Integer, length = 4)
	private String content;// 内容量
	
	@BeanField(title = "开封标记", name = "openflg", type = FieldType.String)
	private String openflg;//开封标记
	
	private String part_supply;//部分发放
	
	private String part_supply_comfrim;//确认部分发放
	
	@BeanField(title = "发放数量DB", name = "db_supply_quantity", type = FieldType.Integer)
	private String db_supply_quantity;//发放数量DB
	
	@BeanField(title = "单位名称", name = "unit_name", type = FieldType.String)
	private String unit_name;

	/*库位*/
	@BeanField(title = "库位", name = "stock_code",length = 20)
	private String stock_code;

	@BeanField(title = "价格", name = "price", type = FieldType.Double)
	private String price;

	@BeanField(title = "工位代码", name = "process_code")
	private String process_code;

	@BeanField(title = "申请人", name = "operator_name")
	private String operator_name;

	public String getConsumable_application_key() {
		return consumable_application_key;
	}

	public void setConsumable_application_key(String consumable_application_key) {
		this.consumable_application_key = consumable_application_key;
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

	public String getApply_time() {
		return apply_time;
	}

	public void setApply_time(String apply_time) {
		this.apply_time = apply_time;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getApply_reason() {
		return apply_reason;
	}

	public void setApply_reason(String apply_reason) {
		this.apply_reason = apply_reason;
	}

	public String getComfirmer_id() {
		return comfirmer_id;
	}

	public void setComfirmer_id(String comfirmer_id) {
		this.comfirmer_id = comfirmer_id;
	}

	public String getSupplier_id() {
		return supplier_id;
	}

	public void setSupplier_id(String supplier_id) {
		this.supplier_id = supplier_id;
	}

	public String getSupply_time() {
		return supply_time;
	}

	public void setSupply_time(String supply_time) {
		this.supply_time = supply_time;
	}

	public String getAll_supplied() {
		return all_supplied;
	}

	public void setAll_supplied(String all_supplied) {
		this.all_supplied = all_supplied;
	}

	public String getApply_time_start() {
		return apply_time_start;
	}

	public void setApply_time_start(String apply_time_start) {
		this.apply_time_start = apply_time_start;
	}

	public String getApply_time_end() {
		return apply_time_end;
	}

	public void setApply_time_end(String apply_time_end) {
		this.apply_time_end = apply_time_end;
	}

	public String getFlg() {
		return flg;
	}

	public void setFlg(String flg) {
		this.flg = flg;
	}

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public String getApply_method() {
		return apply_method;
	}

	public void setApply_method(String apply_method) {
		this.apply_method = apply_method;
	}

	public String getPetitioner_id() {
		return petitioner_id;
	}

	public void setPetitioner_id(String petitioner_id) {
		this.petitioner_id = petitioner_id;
	}

	public String getUnit_code() {
		return unit_code;
	}

	public void setUnit_code(String unit_code) {
		this.unit_code = unit_code;
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

	public String getPack_method() {
		return pack_method;
	}

	public void setPack_method(String pack_method) {
		this.pack_method = pack_method;
	}

	public String getPartial_name() {
		return partial_name;
	}

	public void setPartial_name(String partial_name) {
		this.partial_name = partial_name;
	}

	public String getAvailable_inventory() {
		return available_inventory;
	}

	public void setAvailable_inventory(String available_inventory) {
		this.available_inventory = available_inventory;
	}

	public String getWaitting_quantity() {
		return waitting_quantity;
	}

	public void setWaitting_quantity(String waitting_quantity) {
		this.waitting_quantity = waitting_quantity;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUse_no() {
		return use_no;
	}

	public void setUse_no(String use_no) {
		this.use_no = use_no;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getOpenflg() {
		return openflg;
	}

	public void setOpenflg(String openflg) {
		this.openflg = openflg;
	}

	public String getPart_supply() {
		return part_supply;
	}

	public void setPart_supply(String part_supply) {
		this.part_supply = part_supply;
	}

	public String getPart_supply_comfrim() {
		return part_supply_comfrim;
	}

	public void setPart_supply_comfrim(String part_supply_comfrim) {
		this.part_supply_comfrim = part_supply_comfrim;
	}

	public String getDb_supply_quantity() {
		return db_supply_quantity;
	}

	public void setDb_supply_quantity(String db_supply_quantity) {
		this.db_supply_quantity = db_supply_quantity;
	}

	public String getUnit_name() {
		return unit_name;
	}

	public void setUnit_name(String unit_name) {
		this.unit_name = unit_name;
	}

	public String getStock_code() {
		return stock_code;
	}

	public void setStock_code(String stock_code) {
		this.stock_code = stock_code;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

	

}
