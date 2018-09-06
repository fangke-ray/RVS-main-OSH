package com.osh.rvs.bean.data;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class PositionProductionEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7225697734772940647L;
	
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
	private String oem_count;
	private String stop_count;
	
	private Date action_time;
	private Date action_time_start;
	private Date action_time_end;
	private Date finish_time;
	private String sorc_no;
	private String model_name;
	private String operate_result;
	private String isToday;
	
	public String getIsToday() {
		if (action_time != null) {
			Calendar today = Calendar.getInstance();
			Calendar date = Calendar.getInstance();
			date.setTime(action_time);
			
			if (today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
				today.get(Calendar.MONTH) == date.get(Calendar.MONTH) &&
				today.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)) {
				return "1";
			}
		}
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
		return operate_result;
	}

	public void setOperate_result(String operate_result) {
		this.operate_result = operate_result;
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

	public Date getFinish_time() {
		return finish_time;
	}

	public void setFinish_time(Date finish_time) {
		this.finish_time = finish_time;
	}

	public Date getAction_time_start() {
		return action_time_start;
	}

	public void setAction_time_start(Date action_time_start) {
		this.action_time_start = action_time_start;
	}

	public Date getAction_time_end() {
		return action_time_end;
	}

	public void setAction_time_end(Date action_time_end) {
		this.action_time_end = action_time_end;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
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

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
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


	public Date getAction_time() {
		return action_time;
	}

	public void setAction_time(Date action_time) {
		this.action_time = action_time;
	}

	public String getPosition_name() {
		return position_name;
	}

	public void setPosition_name(String position_name) {
		this.position_name = position_name;
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
