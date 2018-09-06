package com.osh.rvs.bean.infect;

import java.io.Serializable;

public class ElectricIronDeviceEntity implements Serializable {

	/**
	 * 电烙铁工具数据
	 */
	private static final long serialVersionUID = 121117771089162590L;

	//管理ID                                
	private String manage_id;               
	//温度点检序号	                        
	private String seq;                     
	//种类	                                
	private Integer kind;                   
	//温度下限	                            
	private Integer temperature_lower_limit;
	//温度上限	                            
	private Integer temperature_upper_limit;
	//所在课室
	private String section_id;
	private String section_name;
	//所在工位
	private String position_id;
	private String position_name;
	//管理编号
	private String devices_manage_id;
	private String manage_code;
	//品名
	private String devices_type_id;
	private String device_name;
	
	public String getManage_id() {
		return manage_id;
	}
	public void setManage_id(String manage_id) {
		this.manage_id = manage_id;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public Integer getKind() {
		return kind;
	}
	public void setKind(Integer kind) {
		this.kind = kind;
	}
	public Integer getTemperature_lower_limit() {
		return temperature_lower_limit;
	}
	public void setTemperature_lower_limit(Integer temperature_lower_limit) {
		this.temperature_lower_limit = temperature_lower_limit;
	}
	public Integer getTemperature_upper_limit() {
		return temperature_upper_limit;
	}
	public void setTemperature_upper_limit(Integer temperature_upper_limit) {
		this.temperature_upper_limit = temperature_upper_limit;
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
	public String getPosition_name() {
		return position_name;
	}
	public void setPosition_name(String position_name) {
		this.position_name = position_name;
	}
	public String getDevices_manage_id() {
		return devices_manage_id;
	}
	public void setDevices_manage_id(String devices_manage_id) {
		this.devices_manage_id = devices_manage_id;
	}
	public String getManage_code() {
		return manage_code;
	}
	public void setManage_code(String manage_code) {
		this.manage_code = manage_code;
	}
	public String getDevice_name() {
		return device_name;
	}
	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}
	public String getDevices_type_id() {
		return devices_type_id;
	}
	public void setDevices_type_id(String devices_type_id) {
		this.devices_type_id = devices_type_id;
	}
}
