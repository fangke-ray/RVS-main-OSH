package com.osh.rvs.bean.inline;

import java.io.Serializable;
import java.util.Date;

public class ScheduleHistoryEntity implements Serializable {

	private static final long serialVersionUID = 8444974042088379825L;

	private Date scheduled_date;
	private String material_id;
	private Integer in_schedule_means;
	private Integer remove_flg;
	private String insert_operator_name;
	private String remove_operator_name;
	private Date arrival_plan_date;
	private Integer scheduled_expedited;
	private String alarm_messsage_id;
	private Integer plan_day_period;
	private Integer working_day_period;

	public Date getScheduled_date() {
		return scheduled_date;
	}
	public void setScheduled_date(Date scheduled_date) {
		this.scheduled_date = scheduled_date;
	}
	public String getMaterial_id() {
		return material_id;
	}
	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}
	public Integer getIn_schedule_means() {
		return in_schedule_means;
	}
	public void setIn_schedule_means(Integer in_schedule_means) {
		this.in_schedule_means = in_schedule_means;
	}
	public Integer getRemove_flg() {
		return remove_flg;
	}
	public void setRemove_flg(Integer remove_flg) {
		this.remove_flg = remove_flg;
	}
	public String getInsert_operator_name() {
		return insert_operator_name;
	}
	public void setInsert_operator_name(String insert_operator_name) {
		this.insert_operator_name = insert_operator_name;
	}
	public String getRemove_operator_name() {
		return remove_operator_name;
	}
	public void setRemove_operator_name(String remove_operator_name) {
		this.remove_operator_name = remove_operator_name;
	}
	public Date getArrival_plan_date() {
		return arrival_plan_date;
	}
	public void setArrival_plan_date(Date arrival_plan_date) {
		this.arrival_plan_date = arrival_plan_date;
	}
	public Integer getScheduled_expedited() {
		return scheduled_expedited;
	}
	public void setScheduled_expedited(Integer scheduled_expedited) {
		this.scheduled_expedited = scheduled_expedited;
	}
	public String getAlarm_messsage_id() {
		return alarm_messsage_id;
	}
	public void setAlarm_messsage_id(String alarm_messsage_id) {
		this.alarm_messsage_id = alarm_messsage_id;
	}
	public Integer getPlan_day_period() {
		return plan_day_period;
	}
	public void setPlan_day_period(Integer plan_day_period) {
		this.plan_day_period = plan_day_period;
	}
	public Integer getWorking_day_period() {
		return working_day_period;
	}
	public void setWorking_day_period(Integer working_day_period) {
		this.working_day_period = working_day_period;
	}
}
