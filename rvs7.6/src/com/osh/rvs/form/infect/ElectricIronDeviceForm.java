package com.osh.rvs.form.infect;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class ElectricIronDeviceForm extends ActionForm {

	/**
	 * 电烙铁工具数据
	 */
	private static final long serialVersionUID = 6363898683173358038L;

	// 管理ID
	@BeanField(title="管理ID",name="manage_id",type=FieldType.String,length=11,notNull=true)
	private String manage_id;
	// 温度点检序号
	@BeanField(title="温度点检序号",name="seq",type=FieldType.String,length=2,notNull=true)
	private String seq;
	// 种类
	@BeanField(title="种类",name="kind",type=FieldType.Integer,notNull=true)
	private String kind;
	// 温度下限
	@BeanField(title="温度下限",name="temperature_lower_limit",type=FieldType.Integer,length=3,notNull=true)
	private String temperature_lower_limit;
	// 温度上限
	@BeanField(title="温度上限",name="temperature_upper_limit",type=FieldType.Integer,length=3,notNull=true)
	private String temperature_upper_limit;
	
	//所在课室
	@BeanField(title="所在课室",name="section_id",type=FieldType.String,length=11)
	private String section_id;
	@BeanField(title="课室",name="section_name",type=FieldType.String)
	private String section_name;
	//所在工位
	@BeanField(title="所在工位",name="position_id",type=FieldType.String,length=11)
	private String position_id;
	@BeanField(title="工位",name="position_name",type=FieldType.String)
	private String position_name;
	//管理编号
	@BeanField(title="管理编号ID",name="devices_manage_id",type=FieldType.String,length=11)
	private String devices_manage_id;
	@BeanField(title="管理编号",name="manage_code",type=FieldType.String)
	private String manage_code;
	//品名
	@BeanField(title="品名ID",name="devices_type_id",type=FieldType.String,length=11)
	private String devices_type_id;
	@BeanField(title="品名",name="device_name",type=FieldType.String)
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
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getTemperature_lower_limit() {
		return temperature_lower_limit;
	}
	public void setTemperature_lower_limit(String temperature_lower_limit) {
		this.temperature_lower_limit = temperature_lower_limit;
	}
	public String getTemperature_upper_limit() {
		return temperature_upper_limit;
	}
	public void setTemperature_upper_limit(String temperature_upper_limit) {
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
