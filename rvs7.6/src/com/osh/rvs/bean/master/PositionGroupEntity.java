package com.osh.rvs.bean.master;

import java.io.Serializable;

public class PositionGroupEntity implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6515046143861910172L;
	/** 虚拟组工位 */
	private String group_position_id;
	/** 所属实际工位 */
	private String sub_position_id;

	/** 后序检测工位 */
	private String next_position_id;
	/** 仕挂监测数量 */
	private Integer control_trigger;
	public String getGroup_position_id() {
		return group_position_id;
	}
	public void setGroup_position_id(String group_position_id) {
		this.group_position_id = group_position_id;
	}
	public String getSub_position_id() {
		return sub_position_id;
	}
	public void setSub_position_id(String sub_position_id) {
		this.sub_position_id = sub_position_id;
	}
	public String getNext_position_id() {
		return next_position_id;
	}
	public void setNext_position_id(String next_position_id) {
		this.next_position_id = next_position_id;
	}
	public Integer getControl_trigger() {
		return control_trigger;
	}
	public void setControl_trigger(Integer control_trigger) {
		this.control_trigger = control_trigger;
	}

}
