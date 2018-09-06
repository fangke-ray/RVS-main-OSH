package com.osh.rvs.bean.inline;

import java.io.Serializable;

public class ForSolutionAreaEventEntity implements Serializable {

	private static final long serialVersionUID = -1254265607923970119L;

	private String for_solution_area_key;
	private String reciever_id;
	private Integer event_type;
	private Integer red_flg;

	public String getFor_solution_area_key() {
		return for_solution_area_key;
	}

	public void setFor_solution_area_key(String for_solution_area_key) {
		this.for_solution_area_key = for_solution_area_key;
	}

	public String getReciever_id() {
		return reciever_id;
	}

	public void setReciever_id(String reciever_id) {
		this.reciever_id = reciever_id;
	}

	public Integer getEvent_type() {
		return event_type;
	}

	public void setEvent_type(Integer event_type) {
		this.event_type = event_type;
	}

	public Integer getRed_flg() {
		return red_flg;
	}

	public void setRed_flg(Integer red_flg) {
		this.red_flg = red_flg;
	}


}
