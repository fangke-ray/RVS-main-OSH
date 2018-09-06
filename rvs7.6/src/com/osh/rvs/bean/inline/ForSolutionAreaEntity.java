package com.osh.rvs.bean.inline;

import java.io.Serializable;
import java.util.Date;

public class ForSolutionAreaEntity implements Serializable {

	private static final long serialVersionUID = -5822192971434374094L;

	private String for_solution_area_key;
	private String material_id;
	private String sorc_no;
	private String model_id;
	private String model_name;
	private String category_id;
	private String category_name;
	private String serial_no;
	private Date happen_time;
	private Date solved_time;
	private Integer reason;
	private String process_code;
	private String position_id;
	private String line_id;
	private String line_name;
	private String comment;
	private Integer check_flg1;
	private Integer check_flg2;
	private Integer bo_flg;
	private Date arrival_plan_date;
	private Date scheduled_date_start;
	private Date scheduled_date_end;
	private String section_id;
	private Integer scheduled_expedited;
	private Integer resolved;
	private Date arrival_plan_date_start;
	private Date arrival_plan_date_end;
	private Integer level;
	private Integer direct_flg;
	private Date order_date;
	private String section_name;
	private Date scheduled_date;
	private Date scheduled_assign_date;
	private String scheduled_manager_comment;
	private Date inline_time;
	private Date arrival_date;

	private String resolver_id;
	private String resolver_name;

	private String am_pm;
	private Integer break_back_flg;
	private String break_message;
	private Integer expedition_diff;

	public String getFor_solution_area_key() {
		return for_solution_area_key;
	}

	public void setFor_solution_area_key(String for_solution_area_key) {
		this.for_solution_area_key = for_solution_area_key;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
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

	public Date getHappen_time() {
		return happen_time;
	}

	public void setHappen_time(Date happen_time) {
		this.happen_time = happen_time;
	}

	public Date getSolved_time() {
		return solved_time;
	}

	public void setSolved_time(Date solved_time) {
		this.solved_time = solved_time;
	}

	public Integer getReason() {
		return reason;
	}

	public void setReason(Integer reason) {
		this.reason = reason;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getCheck_flg1() {
		return check_flg1;
	}

	public void setCheck_flg1(Integer check_flg1) {
		this.check_flg1 = check_flg1;
	}

	public Integer getCheck_flg2() {
		return check_flg2;
	}

	public void setCheck_flg2(Integer check_flg2) {
		this.check_flg2 = check_flg2;
	}

	public Integer getBo_flg() {
		return bo_flg;
	}

	public void setBo_flg(Integer bo_flg) {
		this.bo_flg = bo_flg;
	}

	public Date getArrival_plan_date() {
		return arrival_plan_date;
	}

	public void setArrival_plan_date(Date arrival_plan_date) {
		this.arrival_plan_date = arrival_plan_date;
	}

	public Date getScheduled_date_start() {
		return scheduled_date_start;
	}

	public void setScheduled_date_start(Date scheduled_date_start) {
		this.scheduled_date_start = scheduled_date_start;
	}

	public Date getScheduled_date_end() {
		return scheduled_date_end;
	}

	public void setScheduled_date_end(Date scheduled_date_end) {
		this.scheduled_date_end = scheduled_date_end;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public Integer getScheduled_expedited() {
		return scheduled_expedited;
	}

	public void setScheduled_expedited(Integer scheduled_expedited) {
		this.scheduled_expedited = scheduled_expedited;
	}

	public Integer getResolved() {
		return resolved;
	}

	public void setResolved(Integer resolved) {
		this.resolved = resolved;
	}

	public Date getArrival_plan_date_start() {
		return arrival_plan_date_start;
	}

	public void setArrival_plan_date_start(Date arrival_plan_date_start) {
		this.arrival_plan_date_start = arrival_plan_date_start;
	}

	public Date getArrival_plan_date_end() {
		return arrival_plan_date_end;
	}

	public void setArrival_plan_date_end(Date arrival_plan_date_end) {
		this.arrival_plan_date_end = arrival_plan_date_end;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getDirect_flg() {
		return direct_flg;
	}

	public void setDirect_flg(Integer direct_flg) {
		this.direct_flg = direct_flg;
	}

	public Date getOrder_date() {
		return order_date;
	}

	public void setOrder_date(Date order_date) {
		this.order_date = order_date;
	}

	public String getSection_name() {
		return section_name;
	}

	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}

	public Date getScheduled_date() {
		return scheduled_date;
	}

	public void setScheduled_date(Date scheduled_date) {
		this.scheduled_date = scheduled_date;
	}

	public Date getScheduled_assign_date() {
		return scheduled_assign_date;
	}

	public void setScheduled_assign_date(Date scheduled_assign_date) {
		this.scheduled_assign_date = scheduled_assign_date;
	}

	public String getScheduled_manager_comment() {
		return scheduled_manager_comment;
	}

	public void setScheduled_manager_comment(String scheduled_manager_comment) {
		this.scheduled_manager_comment = scheduled_manager_comment;
	}

	public Date getInline_time() {
		return inline_time;
	}

	public void setInline_time(Date inline_time) {
		this.inline_time = inline_time;
	}

	public Date getArrival_date() {
		return arrival_date;
	}

	public void setArrival_date(Date arrival_date) {
		this.arrival_date = arrival_date;
	}

	public String getResolver_id() {
		return resolver_id;
	}

	public void setResolver_id(String resolver_id) {
		this.resolver_id = resolver_id;
	}

	public String getResolver_name() {
		return resolver_name;
	}

	public void setResolver_name(String resolver_name) {
		this.resolver_name = resolver_name;
	}

	public String getAm_pm() {
		return this.am_pm;
	}

	public void setAm_pm(String am_pm) {
		this.am_pm = am_pm;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public Integer getBreak_back_flg() {
		return break_back_flg;
	}

	public void setBreak_back_flg(Integer break_back_flg) {
		this.break_back_flg = break_back_flg;
	}

	public String getBreak_message() {
		return break_message;
	}

	public void setBreak_message(String break_message) {
		this.break_message = break_message;
	}

	public Integer getExpedition_diff() {
		return expedition_diff;
	}

	public void setExpedition_diff(Integer expedition_diff) {
		this.expedition_diff = expedition_diff;
	}

}
