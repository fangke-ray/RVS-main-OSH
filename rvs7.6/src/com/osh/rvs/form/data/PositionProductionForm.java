package com.osh.rvs.form.data;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;
import framework.huiqing.common.util.CodeListUtils;

public class PositionProductionForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4617219233528485984L;

	
	private String section_id;
	private String section_name;
	private String position_id;
	private String process_code;
	private String position_name;
	private String line_id;
	private String line_name;
	private String operator_id;
	private String operator_name;
	private String processing_count;
	private String waiting_count;
	private String processed_count;
	
	@BeanField(title="开始时间", name="action_time", type=FieldType.DateTime)
	private String action_time;
	@BeanField(title="开始时间起", name="action_time_start", type=FieldType.Date)
	private String action_time_start;
	@BeanField(title="开始时间止", name="action_time_end", type=FieldType.Date)
	private String action_time_end;

	private String isToday;
	
	/*Detail*/
	@BeanField(title="结束时间", name="finish_time", type=FieldType.DateTime)
	private String finish_time;
	
	private String oem_count;
	private String stop_count;
	private String sorc_no;
	private String model_name;
	private String operate_result;
	
	public String getIsToday() {
		return isToday;
	}

	public void setIsToday(String isToday) {
		this.isToday = isToday;
	}

	
	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getSorc_no() {
		return sorc_no;
	}

	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getOperate_result() {
		if (operate_result != null) {
			return CodeListUtils.getValue("material_operate_result", operate_result);
		}
		return operate_result;
	}

	public void setOperate_result(String operate_result) {
		this.operate_result = operate_result;
	}

	public String getFinish_time() {
		return finish_time;
	}

	public void setFinish_time(String finish_time) {
		this.finish_time = finish_time;
	}

	public String getOem_count() {
		return oem_count;
	}

	public void setOem_count(String oem_count) {
		this.oem_count = oem_count;
	}

	public String getStop_count() {
		return stop_count;
	}

	public void setStop_count(String stop_count) {
		this.stop_count = stop_count;
	}

	public String getAction_time_start() {
		return action_time_start;
	}

	public void setAction_time_start(String action_time_start) {
		this.action_time_start = action_time_start;
	}

	public String getAction_time_end() {
		return action_time_end;
	}

	public void setAction_time_end(String action_time_end) {
		this.action_time_end = action_time_end;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getSection_name() {
		return section_name;
	}

	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getPosition_name() {
		return position_name;
	}

	public void setPosition_name(String position_name) {
		this.position_name = position_name;
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

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

	public String getAction_time() {
		return action_time;
	}

	public void setAction_time(String action_time) {
		this.action_time = action_time;
	}

	public String getProcessing_count() {
		return processing_count;
	}

	public void setProcessing_count(String processing_count) {
		this.processing_count = processing_count;
	}

	public String getWaiting_count() {
		return waiting_count;
	}

	public void setWaiting_count(String waiting_count) {
		this.waiting_count = waiting_count;
	}

	public String getProcessed_count() {
		return processed_count;
	}

	public void setProcessed_count(String processed_count) {
		this.processed_count = processed_count;
	}

}
