package com.osh.rvs.form.partial;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class ComponentManageForm extends ActionForm {

	private static final long serialVersionUID = 2413471756814818982L;

	/** 维修对象型号 ID */
	@BeanField(title = "组件库存管理KEY", name = "component_key", primaryKey = true, length = 11)
	private String component_key;
	
	/** 型号 ID */
	@BeanField(title = "型号 ID", name = "model_id", length = 11, notNull = true)
	private String model_id;
	private String model_name;
	
	/** 来源维修品 ID */
	@BeanField(title = "来源维修品 ID", name = "origin_material_id", length = 11, notNull = true)
	private String origin_material_id;
	
	/** 来源维修品SFDC No. */
	@BeanField(title = "来源维修品 SFDC No.", name = "origin_omr_notifi_no")
	private String origin_omr_notifi_no;
	
	/** 进度(0:原料签收;1:原料入库;2:组装中;3:组装完成;4:已使用) */
	@BeanField(title = "进度", name = "step", length = 1, notNull = true)
	private String step;
	private String step_name;
	
	/** 库位编号 */
	@BeanField(title = "库位编号", name = "stock_code", length = 8)
	private String stock_code;
	
	/** 投入日期 */
	@BeanField(title = "投入日期", name = "inline_date", type = FieldType.Date)
	private String inline_date;
	
	/** 组件序列号 */
	@BeanField(title = "组件序列号", name = "serial_no", length = 13)
	private String serial_no;
	
	/** 组装完成时间  */
	@BeanField(title = "组装完成时间", name = "finish_time", type = FieldType.TimeStamp)
	private String finish_time;
	
	/** 采用维修品 ID */
	@BeanField(title = "采用维修品 ID", name = "target_material_id", length = 11)
	private String target_material_id;
	
	/** 采用维修品SFDC No. */
	@BeanField(title = "采用维修品 SFDC No.", name = "target_omr_notifi_no")
	private String target_omr_notifi_no;
	
	/** 零件ID */
	@BeanField(title = "零件ID", name = "partial_id")
	private String partial_id;
	
	/** 零件名称 */
	@BeanField(title = "零件名称", name = "partial_name")
	private String partial_name;

	/** 制作者 */
	@BeanField(title = "制作者", name = "operator_name")
	private String operator_name;
	
	/** NS组件设置用定义 */

	/** 组件代码 */
	@BeanField(title = "组件代码", name = "component_code")
	private String component_code;
	
	/** 零件ID */
	@BeanField(title = "零件ID", name = "component_partial_id")
	private String component_partial_id;
	
	/** 识别代号 */
	@BeanField(title = "识别代号", name = "identify_code", length = 2, notNull = true)
	private String identify_code;

	/** 安全库存*/
	@BeanField(title = "安全库存", name = "safety_lever", length = 3)
	private String safety_lever;
	
	/** 子零件代码 */
	@BeanField(title = "子零件代码", name = "partial_code")
	private String partial_code;
	
	
	/** 检索用部分定义 */
	/** 型号 */
	@BeanField(title = "型号", name = "search_model_id",length = 11)
	private String search_model_id;
	
	/** 组件代码 */
	@BeanField(title = "组件代码", name = "search_component_code")
	private String search_component_code;
	
	/** 子零件代码 */
	@BeanField(title = "子零件代码", name = "search_partial_code")
	private String search_partial_code;
	
	/** NS 组件序列号 */
	@BeanField(title = "NS 组件序列号", name = "search_serial_no")
	private String search_serial_no;
	
	/** 库位编号 */
	@BeanField(title = "库位编号", name = "search_stock_code")
	private String search_stock_code;
	
	/** 状态 */
	@BeanField(title = "状态", name = "search_step")
	private String search_step;
	
	/** 投入日期始 */
	@BeanField(title = "投入日期始", name = "search_inline_date_start", type = FieldType.Date)
	private String search_inline_date_start;
	
	/** 投入日期终 */
	@BeanField(title = "投入日期终", name = "search_inline_date_end", type = FieldType.Date)
	private String search_inline_date_end;
	
	/** 组装完成日期始 */
	@BeanField(title = "组装完成日期始", name = "search_finish_time_start", type = FieldType.Date)
	private String search_finish_time_start;
	
	/** 组装完成日期终 */
	@BeanField(title = "组装完成日期终", name = "search_finish_time_end", type = FieldType.Date)
	private String search_finish_time_end;
	
	public String getPartial_name() {
		return partial_name;
	}

	public void setPartial_name(String partial_name) {
		this.partial_name = partial_name;
	}

	public String getIdentify_code() {
		return identify_code;
	}

	public void setIdentify_code(String identify_code) {
		this.identify_code = identify_code;
	}

	public String getSafety_lever() {
		return safety_lever;
	}

	public void setSafety_lever(String safety_lever) {
		this.safety_lever = safety_lever;
	}

	public String getComponent_partial_id() {
		return component_partial_id;
	}

	public void setComponent_partial_id(String component_partial_id) {
		this.component_partial_id = component_partial_id;
	}

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public String getComponent_key() {
		return component_key;
	}

	public void setComponent_key(String component_key) {
		this.component_key = component_key;
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

	public String getOrigin_material_id() {
		return origin_material_id;
	}

	public void setOrigin_material_id(String origin_material_id) {
		this.origin_material_id = origin_material_id;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getStep_name() {
		return step_name;
	}

	public void setStep_name(String step_name) {
		this.step_name = step_name;
	}

	public String getStock_code() {
		return stock_code;
	}

	public void setStock_code(String stock_code) {
		this.stock_code = stock_code;
	}

	public String getInline_date() {
		return inline_date;
	}

	public void setInline_date(String inline_date) {
		this.inline_date = inline_date;
	}

	public String getSerial_no() {
		return serial_no;
	}

	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}

	public String getFinish_time() {
		return finish_time;
	}

	public void setFinish_time(String finish_time) {
		this.finish_time = finish_time;
	}

	public String getTarget_material_id() {
		return target_material_id;
	}

	public void setTarget_material_id(String target_material_id) {
		this.target_material_id = target_material_id;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getSearch_model_id() {
		return search_model_id;
	}

	public void setSearch_model_id(String search_model_id) {
		this.search_model_id = search_model_id;
	}

	public String getSearch_component_code() {
		return search_component_code;
	}

	public void setSearch_component_code(String search_component_code) {
		this.search_component_code = search_component_code;
	}

	public String getSearch_partial_code() {
		return search_partial_code;
	}

	public void setSearch_partial_code(String search_partial_code) {
		this.search_partial_code = search_partial_code;
	}

	public String getSearch_serial_no() {
		return search_serial_no;
	}

	public void setSearch_serial_no(String search_serial_no) {
		this.search_serial_no = search_serial_no;
	}

	public String getSearch_stock_code() {
		return search_stock_code;
	}

	public void setSearch_stock_code(String search_stock_code) {
		this.search_stock_code = search_stock_code;
	}

	public String getSearch_step() {
		return search_step;
	}

	public void setSearch_step(String search_step) {
		this.search_step = search_step;
	}

	public String getSearch_inline_date_start() {
		return search_inline_date_start;
	}

	public void setSearch_inline_date_start(String search_inline_date_start) {
		this.search_inline_date_start = search_inline_date_start;
	}

	public String getSearch_inline_date_end() {
		return search_inline_date_end;
	}

	public void setSearch_inline_date_end(String search_inline_date_end) {
		this.search_inline_date_end = search_inline_date_end;
	}

	public String getSearch_finish_time_start() {
		return search_finish_time_start;
	}

	public void setSearch_finish_time_start(String search_finish_time_start) {
		this.search_finish_time_start = search_finish_time_start;
	}

	public String getSearch_finish_time_end() {
		return search_finish_time_end;
	}

	public void setSearch_finish_time_end(String search_finish_time_end) {
		this.search_finish_time_end = search_finish_time_end;
	}

	public String getOrigin_omr_notifi_no() {
		return origin_omr_notifi_no;
	}

	public void setOrigin_omr_notifi_no(String origin_omr_notifi_no) {
		this.origin_omr_notifi_no = origin_omr_notifi_no;
	}

	public String getTarget_omr_notifi_no() {
		return target_omr_notifi_no;
	}

	public void setTarget_omr_notifi_no(String target_omr_notifi_no) {
		this.target_omr_notifi_no = target_omr_notifi_no;
	}

	public String getComponent_code() {
		return component_code;
	}

	public void setComponent_code(String component_code) {
		this.component_code = component_code;
	}

	public String getPartial_code() {
		return partial_code;
	}

	public void setPartial_code(String partial_code) {
		this.partial_code = partial_code;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

}
