package com.osh.rvs.form.pda;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 
 * @Title PdaApplyElementForm.java
 * @Project rvs
 * @Package com.osh.rvs.form.pda
 * @ClassName: PdaApplyElementForm
 * @Description: 消耗品入库
 * @author lxb
 * @date 2015-5-29 上午10:38:46
 */
public class PdaApplyElementForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5699255038848703034L;
	
	private String header_flg;

	/** 消耗品申请单Key **/
	@BeanField(title = "消耗品申请单Key", name = "consumable_application_key", type = FieldType.String)
	private String consumable_application_key;

	/** 申请单编号 **/
	@BeanField(title = "申请单编号", name = "application_no", type = FieldType.String)
	private String application_no;

	/** 课室ID **/
	@BeanField(title = "课室ID", name = "section_id", type = FieldType.String)
	private String section_id;

	/** 工程ID **/
	@BeanField(title = "工程ID", name = "line_id", type = FieldType.String)
	private String line_id;

	/** 申请工程 **/
	@BeanField(title = "申请工程", name = "line_name", type = FieldType.String)
	private String line_name;

	@BeanField(title = "即时", name = "flg", type = FieldType.String)
	private String flg;

	/** SAP修理通知单No**/
	@BeanField(title = "SAP修理通知单No", name = "omr_notifi_no", type = FieldType.String)
	private String omr_notifi_no;

	/** 理由 **/
	@BeanField(title = "理由", name = "apply_reason", type = FieldType.String)
	private String apply_reason;

	/** 补充方式 **/
	@BeanField(title = "补充方式", name = "apply_method", type = FieldType.Integer)
	private String apply_method;

	/** 类型 **/
	@BeanField(title = "类型", name = "type", type = FieldType.Integer)
	private String type;

	/** 代码**/
	@BeanField(title = "代码", name = "partial_id", type = FieldType.String)
	private String partial_id;

	/** 代码 **/
	@BeanField(title = "代码", name = "code", type = FieldType.String)
	private String code;

	/** 零件名称 **/
	@BeanField(title = "零件名称", name = "name", type = FieldType.String)
	private String name;

	/** 库位编号**/
	@BeanField(title = "库位编号", name = "stock_code", type = FieldType.String)
	private String stock_code;

	/** 申请数量**/
	@BeanField(title = "申请数量", name = "apply_quantity", type = FieldType.Integer)
	private String apply_quantity;

	/** 申请单发放画面用发放数量 **/
	@BeanField(title = "申请单发放画面用发放数量", name = "supply_quantity", type = FieldType.Integer)
	private String supply_quantity;

	/** DB已发放数量**/
	@BeanField(title = "DB已发放数量", name = "db_supply_quantity", type = FieldType.Integer)
	private String db_supply_quantity;

	/** 申请单详细画面用已发放数量 **/
	@BeanField(title = "申请单详细画面用已发放数量", name = "disp_supply_quantity", type = FieldType.Integer)
	private String disp_supply_quantity;

	private String disp_flg;

	@BeanField(title = "类型名称", name = "type_name", type = FieldType.String)
	private String type_name;// 类型名称

	@BeanField(title = "有效数量", name = "available_inventory", type = FieldType.Integer)
	private String available_inventory;// 有效数量

	/** 待发放数量**/
	@BeanField(title = "待发放数量", name = "waitting_quantity", type = FieldType.Integer)
	private String waitting_quantity;

	@BeanField(title = "在途量", name = "on_passage", type = FieldType.Integer)
	private String on_passage;// 在途量

	@BeanField(title = "库位", name = "pack_method", type = FieldType.Integer)
	private String pack_method;// 包装

	@BeanField(title = "内容量", name = "content", type = FieldType.Integer)
	private String content;// 内容量

	@BeanField(title = "单位名称", name = "unit_name", type = FieldType.String)
	private String unit_name;// 单位名称

	@BeanField(title = "标记", name = "open_flg", type = FieldType.String)
	private String open_flg;

	@BeanField(title = "申请人", name = "petitioner_id", type = FieldType.String)
	private String petitioner_id;

	private String count;

	@BeanField(title = "申请总价", name = "total_price", type = FieldType.Double)
	private String total_price;

	public String getHeader_flg() {
		return header_flg;
	}

	public void setHeader_flg(String header_flg) {
		this.header_flg = header_flg;
	}

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

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	public String getFlg() {
		return flg;
	}

	public void setFlg(String flg) {
		this.flg = flg;
	}

	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}

	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}

	public String getApply_reason() {
		return apply_reason;
	}

	public void setApply_reason(String apply_reason) {
		this.apply_reason = apply_reason;
	}

	public String getApply_method() {
		return apply_method;
	}

	public void setApply_method(String apply_method) {
		this.apply_method = apply_method;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStock_code() {
		return stock_code;
	}

	public void setStock_code(String stock_code) {
		this.stock_code = stock_code;
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

	public String getDisp_flg() {
		return disp_flg;
	}

	public void setDisp_flg(String disp_flg) {
		this.disp_flg = disp_flg;
	}

	public String getType_name() {
		return type_name;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}

	public String getAvailable_inventory() {
		return available_inventory;
	}

	public void setAvailable_inventory(String available_inventory) {
		this.available_inventory = available_inventory;
	}

	public String getDb_supply_quantity() {
		return db_supply_quantity;
	}

	public void setDb_supply_quantity(String db_supply_quantity) {
		this.db_supply_quantity = db_supply_quantity;
	}

	public String getDisp_supply_quantity() {
		return disp_supply_quantity;
	}

	public void setDisp_supply_quantity(String disp_supply_quantity) {
		this.disp_supply_quantity = disp_supply_quantity;
	}

	public String getWaitting_quantity() {
		return waitting_quantity;
	}

	public void setWaitting_quantity(String waitting_quantity) {
		this.waitting_quantity = waitting_quantity;
	}

	public String getOn_passage() {
		return on_passage;
	}

	public void setOn_passage(String on_passage) {
		this.on_passage = on_passage;
	}

	public String getPack_method() {
		return pack_method;
	}

	public void setPack_method(String pack_method) {
		this.pack_method = pack_method;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUnit_name() {
		return unit_name;
	}

	public void setUnit_name(String unit_name) {
		this.unit_name = unit_name;
	}

	public String getOpen_flg() {
		return open_flg;
	}

	public void setOpen_flg(String open_flg) {
		this.open_flg = open_flg;
	}

	public String getPetitioner_id() {
		return petitioner_id;
	}

	public void setPetitioner_id(String petitioner_id) {
		this.petitioner_id = petitioner_id;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getTotal_price() {
		return total_price;
	}

	public void setTotal_price(String total_price) {
		this.total_price = total_price;
	}
}
