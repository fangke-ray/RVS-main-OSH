package com.osh.rvs.bean.inline;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author fxc
 * PS: String 在Mapper.XML中IF判断条件 !=NULL and !='';
 * 		其他  !=NULL
 */
public class ScheduleEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2057674173412246056L;

	//最大产能--capacity表
	private String upper_limit;
	
	private String material_id;
	private String sorc_no;
	private String esas_no;
	private String model_id;
	private String serial_no;
	private Integer level;
	
	private Date agreed_date_start;
	private Date agreed_date_end;
	
	private Date complete_date_start;
	private Date complete_date_end;
	
	private Integer scheduled_expedited;
	private Integer bo_flg;
	
	private Date arrival_plan_date_start;
	private Date arrival_plan_date_end;

	private String position_id;
	private Integer position_eval;
	private String position_id2;
	private Integer position_eval2;

	private String category_id;
	private String[] category_ids;
	private String category_name;
	private String model_name;
	private String levelName;
	private Date agreed_date;
	private Integer ocm;
	private String ocmName;
	private Date inline_time;
	private Date inline_time_start;
	private Date inline_time_end;

	private Date arrival_plan_date;
	private Date arrival_date;
	private String bo_contents;
	private String section_id;
	private String section_name;
	private String processing_position;//进展工位显示process_code
	private Date dismantle_time;//拆镜时间
	private Date order_date;
	private Date dec_plan_date;
	private Date dec_finish_date;
	private String ns_processing_position;//ns进展工位
	private Date ns_plan_date;
	private Date ns_finish_date;
	private Date com_plan_date;
	private Date com_finish_date;
	private Integer am_pm;
	private String break_message;//TODO:工程内发现
	private String scheduled_manager_comment;
	
	private Date scheduled_assign_date;
	private String line_id;
	private String line_name;
	private Integer schedule_assigned;
	private Date scheduled_date_start;
	private Date scheduled_date_end;

	private Integer arrival_delay;
	private Integer expedition_diff;

	private Date support_date;
	private Integer support_count;

	private Integer direct_flg;
	private Integer in_pa;

	private Integer service_repair_flg;

	private Integer schedule_period;

	private Integer remain_minutes;

	private Date expected_finish_time;
	private Integer px;

	private Integer anml_exp;

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
	public Date getScheduled_assign_date() {
		return scheduled_assign_date;
	}
	public void setScheduled_assign_date(Date scheduled_assign_date) {
		this.scheduled_assign_date = scheduled_assign_date;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	public String getBreak_message() {
		return break_message;
	}
	public void setBreak_message(String break_message) {
		this.break_message = break_message;
	}
	public String getModel_name() {
		return model_name;
	}
	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}
	public String getLevelName() {
		return levelName;
	}
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}
	public Date getAgreed_date() {
		return agreed_date;
	}
	public void setAgreed_date(Date agreed_date) {
		this.agreed_date = agreed_date;
	}
	public Integer getOcm() {
		return ocm;
	}
	public void setOcm(Integer ocm) {
		this.ocm = ocm;
	}
	public String getOcmName() {
		return ocmName;
	}
	public void setOcmName(String ocmName) {
		this.ocmName = ocmName;
	}
	public Date getInline_time() {
		return inline_time;
	}
	public void setInline_time(Date inline_time) {
		this.inline_time = inline_time;
	}
	public Date getArrival_plan_date() {
		return arrival_plan_date;
	}
	public void setArrival_plan_date(Date arrival_plan_date) {
		this.arrival_plan_date = arrival_plan_date;
	}
	public Date getArrival_date() {
		return arrival_date;
	}
	public void setArrival_date(Date arrival_date) {
		this.arrival_date = arrival_date;
	}
	public String getBo_contents() {
		return bo_contents;
	}
	public void setBo_contents(String bo_contents) {
		this.bo_contents = bo_contents;
	}
	public String getSection_name() {
		return section_name;
	}
	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}
	public String getProcessing_position() {
		return processing_position;
	}
	public void setProcessing_position(String processing_position) {
		this.processing_position = processing_position;
	}
	public Date getDismantle_time() {
		return dismantle_time;
	}
	public void setDismantle_time(Date dismantle_time) {
		this.dismantle_time = dismantle_time;
	}
	public Date getOrder_date() {
		return order_date;
	}
	public void setOrder_date(Date order_date) {
		this.order_date = order_date;
	}
	public Date getDec_plan_date() {
		return dec_plan_date;
	}
	public void setDec_plan_date(Date dec_plan_date) {
		this.dec_plan_date = dec_plan_date;
	}
	public Date getDec_finish_date() {
		return dec_finish_date;
	}
	public void setDec_finish_date(Date dec_finish_date) {
		this.dec_finish_date = dec_finish_date;
	}
	public String getNs_processing_position() {
		return ns_processing_position;
	}
	public void setNs_processing_position(String ns_processing_position) {
		this.ns_processing_position = ns_processing_position;
	}
	public Date getNs_plan_date() {
		return ns_plan_date;
	}
	public void setNs_plan_date(Date ns_plan_date) {
		this.ns_plan_date = ns_plan_date;
	}
	public Date getNs_finish_date() {
		return ns_finish_date;
	}
	public void setNs_finish_date(Date ns_finish_date) {
		this.ns_finish_date = ns_finish_date;
	}
	public Date getCom_plan_date() {
		return com_plan_date;
	}
	public void setCom_plan_date(Date com_plan_date) {
		this.com_plan_date = com_plan_date;
	}
	public Date getCom_finish_date() {
		return com_finish_date;
	}
	public void setCom_finish_date(Date com_finish_date) {
		this.com_finish_date = com_finish_date;
	}
	public Integer getAm_pm() {
		return am_pm;
	}
	public void setAm_pm(Integer am_pm) {
		this.am_pm = am_pm;
	}
	public String getScheduled_manager_comment() {
		return scheduled_manager_comment;
	}
	public void setScheduled_manager_comment(String scheduled_manager_comment) {
		this.scheduled_manager_comment = scheduled_manager_comment;
	}
	public String getSorc_no() {
		return sorc_no;
	}
	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
	}
	public String getEsas_no() {
		return esas_no;
	}
	public void setEsas_no(String esas_no) {
		this.esas_no = esas_no;
	}
	public String getSerial_no() {
		return serial_no;
	}
	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Date getAgreed_date_start() {
		return agreed_date_start;
	}
	public void setAgreed_date_start(Date agreed_date_start) {
		this.agreed_date_start = agreed_date_start;
	}
	public Date getAgreed_date_end() {
		return agreed_date_end;
	}
	public void setAgreed_date_end(Date agreed_date_end) {
		this.agreed_date_end = agreed_date_end;
	}
	public Integer getScheduled_expedited() {
		return scheduled_expedited;
	}
	public void setScheduled_expedited(Integer scheduled_expedited) {
		this.scheduled_expedited = scheduled_expedited;
	}
	public Integer getBo_flg() {
		return bo_flg;
	}
	public void setBo_flg(Integer bo_flg) {
		this.bo_flg = bo_flg;
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
	public String getMaterial_id() {
		return material_id;
	}
	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}
	public String getModel_id() {
		return model_id;
	}
	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}
	public String getPosition_id() {
		return position_id;
	}
	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}
	public Integer getPosition_eval() {
		return position_eval;
	}
	public void setPosition_eval(Integer position_eval) {
		this.position_eval = position_eval;
	}
	public String getCategory_id() {
		return category_id;
	}
	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}
	public String getSection_id() {
		return section_id;
	}
	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}
	public Date getComplete_date_start() {
		return complete_date_start;
	}
	public void setComplete_date_start(Date complete_date_start) {
		this.complete_date_start = complete_date_start;
	}
	public Date getComplete_date_end() {
		return complete_date_end;
	}
	public void setComplete_date_end(Date complete_date_end) {
		this.complete_date_end = complete_date_end;
	}
	public String getPosition_id2() {
		return position_id2;
	}
	public void setPosition_id2(String position_id2) {
		this.position_id2 = position_id2;
	}
	public Integer getPosition_eval2() {
		return position_eval2;
	}
	public void setPosition_eval2(Integer position_eval2) {
		this.position_eval2 = position_eval2;
	}

	/**
	 * @return the schedule_assigned
	 */
	public Integer getSchedule_assigned() {
		return schedule_assigned;
	}

	/**
	 * @param schedule_assigned the schedule_assigned to set
	 */
	public void setSchedule_assigned(Integer schedule_assigned) {
		this.schedule_assigned = schedule_assigned;
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

	public Integer getArrival_delay() {
		return arrival_delay;
	}

	public void setArrival_delay(Integer arrival_delay) {
		this.arrival_delay = arrival_delay;
	}

	public Integer getExpedition_diff() {
		return expedition_diff;
	}

	public void setExpedition_diff(Integer expedition_diff) {
		this.expedition_diff = expedition_diff;
	}

	public Date getSupport_date() {
		return support_date;
	}

	public void setSupport_date(Date support_date) {
		this.support_date = support_date;
	}

	public Integer getSupport_count() {
		return support_count;
	}

	public void setSupport_count(Integer support_count) {
		this.support_count = support_count;
	}

	public Integer getDirect_flg() {
		return direct_flg;
	}

	public void setDirect_flg(Integer direct_flg) {
		this.direct_flg = direct_flg;
	}

	public Integer getIn_pa() {
		return in_pa;
	}

	public void setIn_pa(Integer in_pa) {
		this.in_pa = in_pa;
	}

	public Integer getService_repair_flg() {
		return service_repair_flg;
	}

	public void setService_repair_flg(Integer service_repair_flg) {
		this.service_repair_flg = service_repair_flg;
	}

	public Date getInline_time_start() {
		return inline_time_start;
	}

	public void setInline_time_start(Date inline_time_start) {
		this.inline_time_start = inline_time_start;
	}

	public Date getInline_time_end() {
		return inline_time_end;
	}

	public void setInline_time_end(Date inline_time_end) {
		this.inline_time_end = inline_time_end;
	}

	public Integer getSchedule_period() {
		return schedule_period;
	}

	public void setSchedule_period(Integer schedule_period) {
		this.schedule_period = schedule_period;
	}

	public Integer getRemain_minutes() {
		return remain_minutes;
	}

	public void setRemain_minutes(Integer remain_minutes) {
		this.remain_minutes = remain_minutes;
	}

	public Date getExpected_finish_time() {
		return expected_finish_time;
	}

	public void setExpected_finish_time(Date expected_finish_time) {
		this.expected_finish_time = expected_finish_time;
	}
	public String getUpper_limit() {
		return upper_limit;
	}
	public void setUpper_limit(String upper_limit) {
		this.upper_limit = upper_limit;
	}
	public Integer getPx() {
		return px;
	}
	public void setPx(Integer px) {
		this.px = px;
	}
	public String[] getCategory_ids() {
		return category_ids;
	}
	public void setCategory_ids(String[] category_ids) {
		this.category_ids = category_ids;
	}
	public Integer getAnml_exp() {
		return anml_exp;
	}
	public void setAnml_exp(Integer anml_exp) {
		this.anml_exp = anml_exp;
	}

}
