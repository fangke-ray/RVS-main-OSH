package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ComponentManageEntity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -5960636192988879037L;

	/** 维修对象型号 ID */
	private String component_key;
	/** 型号 ID */
	private String model_id;
	private String model_name;
	/** 来源维修品 ID */
	private String origin_material_id;	
	/** 来源维修品SFDC No. */
	private String origin_omr_notifi_no;
	/** 进度(0:原料签收;1:原料入库;2:组装中;3:组装完成;4:已使用) */
	private String step;
	private String step_name;	
	/** 库位编号 */
	private String stock_code;	
	/** 投入日期 */
	private Date inline_date;	
	/** 组件序列号 */
	private String serial_no;	
	/** 组装完成时间  */
	private Date finish_time;	
	/** 采用维修品 ID */
	private String target_material_id;
	/** 采用维修品SFDC No. */
	private String target_omr_notifi_no;
	/** 子零件ID */
	private String partial_id;
	/** 子零件名称 */
	private String partial_name;
	/** 组件代码 */
	private String component_code;
	/** 子零件代码 */
	private String partial_code;
	/** 制作者 */
	private String operator_name;
	/** 组装进展工位 */
	private String process_code;
	
	/** 型号 */
	private String search_model_id;
	
	/** 组件代码 */
	private String search_component_code;
	
	/** 子零件代码 */
	private String search_partial_code;
	
	/** NS 组件序列号 */
	private String search_serial_no;
	
	/** 库位编号 */
	private String search_stock_code;
	
	/** 状态 */
	private String search_step;
	
	/** 状态 */
	private List<String> search_step_list;
	
	/** 投入日期始 */
	private Date search_inline_date_start;
	
	/** 投入日期终 */
	private Date search_inline_date_end;
	
	/** 组装完成日期始 */
	private Date search_finish_time_start;
	
	/** 组装完成日期终 */
	private Date search_finish_time_end;
	
	public String getPartial_name() {
		return partial_name;
	}
	public void setPartial_name(String partial_name) {
		this.partial_name = partial_name;
	}
	public String getPartial_id() {
		return partial_id;
	}
	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}
	public List<String> getSearch_step_list() {
		return search_step_list;
	}
	public void setSearch_step_list(List<String> search_step_list) {
		this.search_step_list = search_step_list;
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
	public Date getInline_date() {
		return inline_date;
	}
	public void setInline_date(Date inline_date) {
		this.inline_date = inline_date;
	}
	public String getSerial_no() {
		return serial_no;
	}
	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}
	public Date getFinish_time() {
		return finish_time;
	}
	public void setFinish_time(Date finish_time) {
		this.finish_time = finish_time;
	}
	public String getTarget_material_id() {
		return target_material_id;
	}
	public void setTarget_material_id(String target_material_id) {
		this.target_material_id = target_material_id;
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
	public Date getSearch_inline_date_start() {
		return search_inline_date_start;
	}
	public void setSearch_inline_date_start(Date search_inline_date_start) {
		this.search_inline_date_start = search_inline_date_start;
	}
	public Date getSearch_inline_date_end() {
		return search_inline_date_end;
	}
	public void setSearch_inline_date_end(Date search_inline_date_end) {
		this.search_inline_date_end = search_inline_date_end;
	}
	public Date getSearch_finish_time_start() {
		return search_finish_time_start;
	}
	public void setSearch_finish_time_start(Date search_finish_time_start) {
		this.search_finish_time_start = search_finish_time_start;
	}
	public Date getSearch_finish_time_end() {
		return search_finish_time_end;
	}
	public void setSearch_finish_time_end(Date search_finish_time_end) {
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
	public String getProcess_code() {
		return process_code;
	}
	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

}
