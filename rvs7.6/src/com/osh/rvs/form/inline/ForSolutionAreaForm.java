package com.osh.rvs.form.inline;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class ForSolutionAreaForm extends ActionForm {

	private static final long serialVersionUID = -9057838136196000775L;

	@BeanField(title = "维修对象ID", name = "material_id", type = FieldType.String, length = 11)
	private String material_id;

	@BeanField(title = "修理单号", name = "sorc_no", type = FieldType.String)
	private String sorc_no;
	@BeanField(title = "维修对象机种ID", name = "category_id", type = FieldType.String, length = 11)
	private String category_id;
	private String category_name;
	@BeanField(title = "维修对象型号 ID", name = "model_id", type = FieldType.String, length = 11)
	private String model_id;
	private String model_name;
	@BeanField(title = "机身号", name = "serial_no", type = FieldType.String)
	private String serial_no;
	@BeanField(title = "等级", name = "level", type = FieldType.Integer, length = 1)
	private String level;
	@BeanField(title = "投线时间", name = "inline_time", type = FieldType.Date)
	private String inline_time;
	@BeanField(title = "入库预定日", name = "arrival_plan_date", type = FieldType.Date)
	private String arrival_plan_date;
	@BeanField(title = "零件到货日", name = "arrival_date", type = FieldType.Date)
	private String arrival_date;
	@BeanField(title = "零件BO", name = "bo_flg", type = FieldType.Integer, length = 1)
	private String bo_flg;
	@BeanField(title = "维修课", name = "section_id", type = FieldType.String, length = 11)
	private String section_id;
	private String section_name;
	@BeanField(title = "零件订购日期", name = "order_date", type = FieldType.Date)
	private String order_date;
	@BeanField(title = "加急", name = "scheduled_expedited", type = FieldType.Integer, length = 1)
	private String scheduled_expedited;

	@BeanField(title = "计划管理备注", name = "scheduled_manager_comment", type = FieldType.String, length = 200)
	private String scheduled_manager_comment;

	@BeanField(title = "入库预定日起", name = "arrival_plan_date_start", type = FieldType.Date)
	private String arrival_plan_date_start;
	@BeanField(title = "入库预定日止", name = "arrival_plan_date_end", type = FieldType.Date)
	private String arrival_plan_date_end;
	@BeanField(title = "计划纳入优先日", name = "scheduled_assign_date", type = FieldType.Date)
	private String scheduled_assign_date;
	private String line_id; // 用于过滤分组
	private String line_name;

	@BeanField(title = "6天纳期起", name = "scheduled_date_start", type = FieldType.Date)
	private String scheduled_date_start;
	@BeanField(title = "6天纳期止", name = "scheduled_date_end", type = FieldType.Date)
	private String scheduled_date_end;

	private String remain_days;

	@BeanField(title = "直送标记", name = "direct_flg", type = FieldType.Integer, length = 1)
	private String direct_flg;

	@BeanField(title = "KEY", name = "for_solution_area_key", type = FieldType.String, primaryKey = true)
	private String for_solution_area_key;

	@BeanField(title = "发生时点", name = "happen_time", type = FieldType.DateTime)
	private String happen_time;
	@BeanField(title = "解决时点", name = "solved_time", type = FieldType.DateTime)
	private String solved_time;
	@BeanField(title = "中断原因", name = "reason", type = FieldType.Integer, length = 1)
	private String reason;
	private String process_code;

	private String comment;
	@BeanField(title = "处理标记1", name = "check_flg1", type = FieldType.Integer, length = 1)
	private String check_flg1;
	@BeanField(title = "处理标记2", name = "check_flg2", type = FieldType.Integer, length = 1)
	private String check_flg2;

	private String resolved;

	@BeanField(title = "延迟", name = "expedition_diff", type = FieldType.Integer, length = 1)
	private String expedition_diff;
	
	@BeanField(title = "6天纳期", name = "scheduled_date", type = FieldType.Date)
	private String scheduled_date;

	private String resolver_id;
	private String resolver_name;

	@BeanField(title = "返还标记", name = "break_back_flg", type = FieldType.Integer, length = 1)
	private String break_back_flg;

	private String break_message;
	private String break_level;

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

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getInline_time() {
		return inline_time;
	}

	public void setInline_time(String inline_time) {
		this.inline_time = inline_time;
	}

	public String getArrival_plan_date() {
		return arrival_plan_date;
	}

	public void setArrival_plan_date(String arrival_plan_date) {
		this.arrival_plan_date = arrival_plan_date;
	}

	public String getArrival_date() {
		return arrival_date;
	}

	public void setArrival_date(String arrival_date) {
		this.arrival_date = arrival_date;
	}

	public String getBo_flg() {
		return bo_flg;
	}

	public void setBo_flg(String bo_flg) {
		this.bo_flg = bo_flg;
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

	public String getOrder_date() {
		return order_date;
	}

	public void setOrder_date(String order_date) {
		this.order_date = order_date;
	}

	public String getScheduled_expedited() {
		return scheduled_expedited;
	}

	public void setScheduled_expedited(String scheduled_expedited) {
		this.scheduled_expedited = scheduled_expedited;
	}

	public String getScheduled_manager_comment() {
		return scheduled_manager_comment;
	}

	public void setScheduled_manager_comment(String scheduled_manager_comment) {
		this.scheduled_manager_comment = scheduled_manager_comment;
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

	public String getScheduled_assign_date() {
		return scheduled_assign_date;
	}

	public void setScheduled_assign_date(String scheduled_assign_date) {
		this.scheduled_assign_date = scheduled_assign_date;
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

	public String getScheduled_date_start() {
		return scheduled_date_start;
	}

	public void setScheduled_date_start(String scheduled_date_start) {
		this.scheduled_date_start = scheduled_date_start;
	}

	public String getScheduled_date_end() {
		return scheduled_date_end;
	}

	public void setScheduled_date_end(String scheduled_date_end) {
		this.scheduled_date_end = scheduled_date_end;
	}

	public String getRemain_days() {
		return remain_days;
	}

	public void setRemain_days(String remain_days) {
		this.remain_days = remain_days;
	}

	public String getDirect_flg() {
		return direct_flg;
	}

	public void setDirect_flg(String direct_flg) {
		this.direct_flg = direct_flg;
	}

	public String getFor_solution_area_key() {
		return for_solution_area_key;
	}

	public void setFor_solution_area_key(String for_solution_area_key) {
		this.for_solution_area_key = for_solution_area_key;
	}

	public String getHappen_time() {
		return happen_time;
	}

	public void setHappen_time(String happen_time) {
		this.happen_time = happen_time;
	}

	public String getSolved_time() {
		return solved_time;
	}

	public void setSolved_time(String solved_time) {
		this.solved_time = solved_time;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCheck_flg1() {
		return check_flg1;
	}

	public void setCheck_flg1(String check_flg1) {
		this.check_flg1 = check_flg1;
	}

	public String getCheck_flg2() {
		return check_flg2;
	}

	public void setCheck_flg2(String check_flg2) {
		this.check_flg2 = check_flg2;
	}

	public String getResolved() {
		return resolved;
	}

	public void setResolved(String resolved) {
		this.resolved = resolved;
	}

	public String getScheduled_date() {
		return scheduled_date;
	}

	public void setScheduled_date(String scheduled_date) {
		this.scheduled_date = scheduled_date;
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

	public String getBreak_back_flg() {
		return break_back_flg;
	}

	public void setBreak_back_flg(String break_back_flg) {
		this.break_back_flg = break_back_flg;
	}

	public String getBreak_level() {
		return break_level;
	}

	public void setBreak_level(String break_level) {
		this.break_level = break_level;
	}

	public String getBreak_message() {
		return break_message;
	}

	public void setBreak_message(String break_message) {
		this.break_message = break_message;
	}

	public String getExpedition_diff() {
		return expedition_diff;
	}

	public void setExpedition_diff(String expedition_diff) {
		this.expedition_diff = expedition_diff;
	}

}
