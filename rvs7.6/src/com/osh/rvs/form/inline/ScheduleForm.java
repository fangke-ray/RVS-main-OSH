package com.osh.rvs.form.inline;

import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;
import framework.huiqing.common.util.CodeListUtils;

public class ScheduleForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6102646980930227090L;

	@BeanField(title = "最大产能",name = "upper_limit",type = FieldType.String)
	private String upper_limit;
	
	@BeanField(title = "维修对象ID", name = "material_id", type = FieldType.String, length = 11)
	private String material_id;

	@BeanField(title = "修理单号", name = "sorc_no", type = FieldType.String, length = 15)
	private String sorc_no;
	@BeanField(title = "ESAS No.", name = "esas_no", type = FieldType.String, length = 7)
	private String esas_no;
	@BeanField(title = "维修对象机种ID", name = "category_id", type = FieldType.String)
	private String category_id;
	private String category_name;
	@BeanField(title = "维修对象型号 ID", name = "model_id", type = FieldType.String, length = 11)
	private String model_id;
	private String model_name;
	@BeanField(title = "机身号", name = "serial_no", type = FieldType.String)
	private String serial_no;
	@BeanField(title = "等级", name = "level", type = FieldType.Integer, length = 2)
	private String level;
	private String levelName;
	@BeanField(title = "同意日起", name = "agreed_date_start", type = FieldType.Date)
	private String agreed_date_start;
	@BeanField(title = "同意日止", name = "agreed_date_end", type = FieldType.Date)
	private String agreed_date_end;
	@BeanField(title = "同意时间", name = "agreed_date", type = FieldType.Date)
	private String agreed_date;
	@BeanField(title = "纳期起", name = "complete_date_start", type = FieldType.Date)
	private String complete_date_start;
	@BeanField(title = "纳期止", name = "complete_date_end", type = FieldType.Date)
	private String complete_date_end;
	@BeanField(title = "委托处", name = "ocm", type = FieldType.Integer, length = 2)
	private String ocm;
	private String ocmName;
	@BeanField(title="投线时间", name="inline_time", type = FieldType.Date)
	private String inline_time;
	@BeanField(title="投线时间", name="inline_time_start", type = FieldType.Date)
	private String inline_time_start;
	@BeanField(title="投线时间", name="inline_time_end", type = FieldType.Date)
	private String inline_time_end;
	@BeanField(title = "入库预定日", name = "arrival_plan_date", type = FieldType.Date)
	private String arrival_plan_date;
	@BeanField(title = "零件到货日", name = "arrival_date", type = FieldType.Date)
	private String arrival_date;
	@BeanField(title = "零件BO", name = "bo_flg", type = FieldType.Integer, length = 1)
	private String bo_flg;
	@BeanField(title = "零件缺品备注", name = "bo_contents", type = FieldType.String, length = 200)
	private String bo_contents;
	@BeanField(title="维修课", name="section_id", type = FieldType.String, length = 11)
	private String section_id;
	private String section_name;
	private String processing_position;//进展工位显示process_code
	@BeanField(title = "拆镜时间", name = "dismantle_time", type = FieldType.Date)
	private String dismantle_time;
	@BeanField(title = "零件订购日期", name = "order_date", type = FieldType.Date)
	private String order_date;
	@BeanField(title="", name="dec_plan_date", type = FieldType.Date)
	private String dec_plan_date;
	@BeanField(title="", name="dec_finish_date", type = FieldType.Date)
	private String dec_finish_date;
	private String ns_processing_position;//ns进展工位
	@BeanField(title="", name="ns_plan_date", type = FieldType.Date)
	private String ns_plan_date;
	@BeanField(title="", name="ns_finish_date", type = FieldType.Date)
	private String ns_finish_date;
	@BeanField(title="", name="com_plan_date", type = FieldType.Date)
	private String com_plan_date;
	@BeanField(title="", name="com_finish_date", type = FieldType.Date)
	private String com_finish_date;
	@BeanField(title="AM/PM", name="am_pm", type = FieldType.Integer, length = 1)
	private String am_pm;
	@BeanField(title = "加急", name = "scheduled_expedited", type = FieldType.Integer, length = 1)
	private String scheduled_expedited;
	@BeanField(title = "已排入计划", name = "schedule_assigned", type = FieldType.Integer, length = 1)
	private String schedule_assigned;

	private String break_message;
	private String break_message_level;

	@BeanField(title="计划管理备注", name="scheduled_manager_comment", type = FieldType.String, length = 200)
	private String scheduled_manager_comment;
	
	
	@BeanField(title = "入库预定日起", name = "arrival_plan_date_start", type = FieldType.Date)
	private String arrival_plan_date_start;
	@BeanField(title = "入库预定日止", name = "arrival_plan_date_end", type = FieldType.Date)
	private String arrival_plan_date_end;
	@BeanField(title = "工位ID", name = "position_id", type = FieldType.String, length = 11)
	private String position_id;
	@BeanField(title = "工位进展", name = "position_eval", type = FieldType.Integer, length = 1)
	private String position_eval;
	@BeanField(title = "工位ID2", name = "position_id2", type = FieldType.String, length = 11)
	private String position_id2;
	@BeanField(title = "工位进展2", name = "position_eval2", type = FieldType.Integer, length = 1)
	private String position_eval2;
	@BeanField(title = "计划纳入优先日", name = "scheduled_assign_date", type = FieldType.Date)
	private String scheduled_assign_date;
	private String line_id; //用于过滤分组
	private String line_name;

	@BeanField(title = "5天纳期起", name = "scheduled_date_start", type = FieldType.Date)
	private String scheduled_date_start;
	@BeanField(title = "5天纳期止", name = "scheduled_date_end", type = FieldType.Date)
	private String scheduled_date_end;

	@BeanField(title = "arrival_delay", type = FieldType.Integer, name = "arrival_delay")
	private String arrival_delay;
	@BeanField(title = "expedition_diff", type = FieldType.Integer, name = "expedition_diff")
	private String expedition_diff;

	private String ids; //用于更新，删除
	private String lineIds;//用于更新，删除
	@BeanField(title = "产出时间", name = "finish_date", type = FieldType.Date)
	private String finish_date;

	@BeanField(title = "排定计划辅助针对日期", name = "support_date", type = FieldType.Date)
	private String support_date;
	@BeanField(title = "排定计划预选产能", name = "support_count", type = FieldType.Integer)
	private String support_count;

	private String is_late;
	private String remain_days;

	@BeanField(title = "直送标记", name = "direct_flg", type = FieldType.Integer, length = 1)
	private String direct_flg;
	@BeanField(title = "待解决区域", name = "in_pa", type = FieldType.Integer, length = 1)
	private String in_pa;

	@BeanField(title = "返修标记", name = "service_repair_flg", type = FieldType.Integer, length = 1)
	private String service_repair_flg;

	@BeanField(title = "计划期间", name = "schedule_period", type = FieldType.Integer, length = 1)
	private String schedule_period;

	@BeanField(title = "预计完成日", name = "expected_finish_time", type = FieldType.DateTime)
	private String expected_finish_time;

	/**
	 * 倒计时工时数
	 */
	@BeanField(title = "剩余标准工时", name = "remain_minutes", type = FieldType.Integer, length = 4)
	private String countdown;

	@BeanField(title = "分线", name = "px", type = FieldType.Integer, length = 1)
	private String px;
	private String px_ns;

	private String row_no;

	@BeanField(title = "动物实验用", name = "anml_exp", type = FieldType.Integer, length = 1)
	private String anml_exp;

	@BeanField(title = "CCD 线更换", name = "ccd_target", type = FieldType.Integer, length = 1)
	private String ccd_target;

	public String getFinish_date() {
		return finish_date;
	}
	public void setFinish_date(String finish_date) {
		this.finish_date = finish_date;
	}

	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public String getLineIds() {
		return lineIds;
	}
	public void setLineIds(String lineIds) {
		this.lineIds = lineIds;
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
	public String getMaterial_id() {
		return material_id;
	}
	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
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
	public String getCategory_id() {
		return category_id;
	}
	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}
	public String getModel_name() {
		return model_name;
	}
	public void setModel_name(String model_name) {
		this.model_name = model_name;
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
	public String getAgreed_date() {
		return agreed_date;
	}
	public void setAgreed_date(String agreed_date) {
		this.agreed_date = agreed_date;
	}
	public String getOcm() {
		return ocm;
	}
	public void setOcm(String ocm) {
		this.ocm = ocm;
	}
	public String getOcmName() {
		if (ocm != null) {
			return CodeListUtils.getValue("material_ocm", ocm);
		}
		return ocmName;
	}
	public void setOcmName(String ocmName) {
		this.ocmName = ocmName;
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
	public String getBo_contents() {
		return bo_contents;
	}
	public void setBo_contents(String bo_contents) {
		this.bo_contents = bo_contents;
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
	public String getProcessing_position() {
		return processing_position;
	}
	public void setProcessing_position(String processing_position) {
		this.processing_position = processing_position;
	}
	public String getDismantle_time() {
		return dismantle_time;
	}
	public void setDismantle_time(String dismantle_time) {
		this.dismantle_time = dismantle_time;
	}
	public String getOrder_date() {
		return order_date;
	}
	public void setOrder_date(String order_date) {
		this.order_date = order_date;
	}
	public String getDec_plan_date() {
		return dec_plan_date;
	}
	public void setDec_plan_date(String dec_plan_date) {
		this.dec_plan_date = dec_plan_date;
	}
	public String getDec_finish_date() {
		return dec_finish_date;
	}
	public void setDec_finish_date(String dec_finish_date) {
		this.dec_finish_date = dec_finish_date;
	}
	public String getNs_processing_position() {
		return ns_processing_position;
	}
	public void setNs_processing_position(String ns_processing_position) {
		this.ns_processing_position = ns_processing_position;
	}
	public String getNs_plan_date() {
		return ns_plan_date;
	}
	public void setNs_plan_date(String ns_plan_date) {
		this.ns_plan_date = ns_plan_date;
	}
	public String getNs_finish_date() {
		return ns_finish_date;
	}
	public void setNs_finish_date(String ns_finish_date) {
		this.ns_finish_date = ns_finish_date;
	}
	public String getCom_plan_date() {
		return com_plan_date;
	}
	public void setCom_plan_date(String com_plan_date) {
		this.com_plan_date = com_plan_date;
	}
	public String getCom_finish_date() {
		return com_finish_date;
	}
	public void setCom_finish_date(String com_finish_date) {
		this.com_finish_date = com_finish_date;
	}
	public String getAm_pm() {
		return am_pm;
	}
	public void setAm_pm(String am_pm) {
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
	public String getModel_id() {
		return model_id;
	}
	public void setModel_id(String model_id) {
		this.model_id = model_id;
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
	public String getAgreed_date_start() {
		return agreed_date_start;
	}
	public void setAgreed_date_start(String agreed_date_start) {
		this.agreed_date_start = agreed_date_start;
	}
	public String getAgreed_date_end() {
		return agreed_date_end;
	}
	public void setAgreed_date_end(String agreed_date_end) {
		this.agreed_date_end = agreed_date_end;
	}
	public String getScheduled_expedited() {
		return scheduled_expedited;
	}
	public void setScheduled_expedited(String scheduled_expedited) {
		this.scheduled_expedited = scheduled_expedited;
	}

	public String getBo_flg() {
		return bo_flg;
	}
	public void setBo_flg(String bo_flg) {
		this.bo_flg = bo_flg;
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
	public String getPosition_id() {
		return position_id;
	}
	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}
	public String getPosition_eval() {
		return position_eval;
	}
	public void setPosition_eval(String position_eval) {
		this.position_eval = position_eval;
	}
	
	public String filterSpecialValue(String value, String preName) {
		if ("bo_flg".equals(preName)) {
			return "1".equals(value) ? "BO" : "";
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
		} else if ("am_pm".equals(preName)) {
			return CodeListUtils.getValue("material_time", value);
		} else if ("scheduled_expedited".equals(preName)) {
			return "1".equals(value) ? "加急" : "";
		} else if ("arrival_plan_date".equals(preName)) {
			return "9999/12/31".equals(value) ? "未定":value;
		}
		return value;
	}
	public String getComplete_date_start() {
		return complete_date_start;
	}
	public void setComplete_date_start(String complete_date_start) {
		this.complete_date_start = complete_date_start;
	}
	public String getComplete_date_end() {
		return complete_date_end;
	}
	public void setComplete_date_end(String complete_date_end) {
		this.complete_date_end = complete_date_end;
	}
	public String getPosition_id2() {
		return position_id2;
	}
	public void setPosition_id2(String position_id2) {
		this.position_id2 = position_id2;
	}
	public String getPosition_eval2() {
		return position_eval2;
	}
	public void setPosition_eval2(String position_eval2) {
		this.position_eval2 = position_eval2;
	}
	public String getSchedule_assigned() {
		return schedule_assigned;
	}
	public void setSchedule_assigned(String schedule_assigned) {
		this.schedule_assigned = schedule_assigned;
	}
	/**
	 * @return the break_message_level
	 */
	public String getBreak_message_level() {
		return break_message_level;
	}
	/**
	 * @param break_message_level the break_message_level to set
	 */
	public void setBreak_message_level(String break_message_level) {
		this.break_message_level = break_message_level;
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
	public String getArrival_delay() {
		return arrival_delay;
	}
	public void setArrival_delay(String arrival_delay) {
		this.arrival_delay = arrival_delay;
	}
	public String getExpedition_diff() {
		return expedition_diff;
	}
	public void setExpedition_diff(String expedition_diff) {
		this.expedition_diff = expedition_diff;
	}
	public String getCountdown() {
		return countdown;
	}
	public void setCountdown(String countdown) {
		this.countdown = countdown;
	}
	public String getSupport_date() {
		return support_date;
	}
	public void setSupport_date(String support_date) {
		this.support_date = support_date;
	}
	public String getSupport_count() {
		return support_count;
	}
	public void setSupport_count(String support_count) {
		this.support_count = support_count;
	}
	public String getIs_late() {
		return is_late;
	}
	public void setIs_late(String is_late) {
		this.is_late = is_late;
	}
	public String getDirect_flg() {
		return direct_flg;
	}
	public void setDirect_flg(String direct_flg) {
		this.direct_flg = direct_flg;
	}
	public String getRemain_days() {
		return remain_days;
	}
	public void setRemain_days(String remain_days) {
		this.remain_days = remain_days;
	}
	public String getIn_pa() {
		return in_pa;
	}
	public void setIn_pa(String in_pa) {
		this.in_pa = in_pa;
	}
	public String getService_repair_flg() {
		return service_repair_flg;
	}
	public void setService_repair_flg(String service_repair_flg) {
		this.service_repair_flg = service_repair_flg;
	}
	public String getInline_time_start() {
		return inline_time_start;
	}
	public void setInline_time_start(String inline_time_start) {
		this.inline_time_start = inline_time_start;
	}
	public String getInline_time_end() {
		return inline_time_end;
	}
	public void setInline_time_end(String inline_time_end) {
		this.inline_time_end = inline_time_end;
	}
	public String getSchedule_period() {
		return schedule_period;
	}
	public void setSchedule_period(String schedule_period) {
		this.schedule_period = schedule_period;
	}
	public String getExpected_finish_time() {
		return expected_finish_time;
	}
	public void setExpected_finish_time(String expected_finish_time) {
		this.expected_finish_time = expected_finish_time;
	}
	public String getUpper_limit() {
		return upper_limit;
	}
	public void setUpper_limit(String upper_limit) {
		this.upper_limit = upper_limit;
	}
	public String getPx() {
		return px;
	}
	public void setPx(String px) {
		this.px = px;
	}
	public String getRow_no() {
		return row_no;
	}
	public void setRow_no(String row_no) {
		this.row_no = row_no;
	}
	public String getPx_ns() {
		return px_ns;
	}
	public void setPx_ns(String px_ns) {
		this.px_ns = px_ns;
	}
	public String getAnml_exp() {
		return anml_exp;
	}
	public void setAnml_exp(String anml_exp) {
		this.anml_exp = anml_exp;
	}
	public String getCcd_target() {
		return ccd_target;
	}
	public void setCcd_target(String ccd_target) {
		this.ccd_target = ccd_target;
	}
}
