package com.osh.rvs.bean.data;

import java.io.Serializable;
import java.util.Date;

public class AlarmMesssageSendationEntity implements Serializable {
	private static final long serialVersionUID = -1902701461917969954L;

	private String alarm_messsage_id;
	private String sendation_id;
	private Date resolve_time;
	private Integer red_flg;
	private String comment;

	private String sendation_name;

	public String getAlarm_messsage_id() {
		return alarm_messsage_id;
	}

	public void setAlarm_messsage_id(String alarm_messsage_id) {
		this.alarm_messsage_id = alarm_messsage_id;
	}

	public String getSendation_id() {
		return sendation_id;
	}

	public void setSendation_id(String sendation_id) {
		this.sendation_id = sendation_id;
	}

	public Date getResolve_time() {
		return resolve_time;
	}

	public void setResolve_time(Date resolve_time) {
		this.resolve_time = resolve_time;
	}

	public Integer getRed_flg() {
		return red_flg;
	}

	public void setRed_flg(Integer red_flg) {
		this.red_flg = red_flg;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getSendation_name() {
		return sendation_name;
	}

	public void setSendation_name(String sendation_name) {
		this.sendation_name = sendation_name;
	}
}
