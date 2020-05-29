package com.osh.rvs.form.infect;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.form.infect
 * @ClassName: ExternalCheckForm
 * @Description: 检查机器校正Form
 * @author lxb
 * @date 2014-9-6 下午4:29:55
 * 
 */
public class ExternalAdjustmentForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4387043459446934121L;

	@BeanField(title = "点检表管理ID", name = "devices_manage_id", type = FieldType.String,primaryKey = true, notNull = true)
	private String devices_manage_id;// 点检表管理ID

	@BeanField(title = "校验日期", name = "checked_date", type = FieldType.Date, notNull = true)
	private String checked_date;// 校验日期

	@BeanField(title = "过期日期", name = "available_end_date", type = FieldType.Date, notNull = true)
	private String available_end_date;// 过期日期

	@BeanField(title = "有效期", name = "effect_interval", type = FieldType.Integer, length = 2, notNull = true)
	private String effect_interval;// 有效期

	@BeanField(title = "校验费用", name = "check_cost", type = FieldType.Double, length = 7)
	private String check_cost;// 校验费用

	@BeanField(title = "校验单位", name = "organization_type", type = FieldType.Integer, length = 1, notNull = true)
	private String organization_type;// 校验单位

	@BeanField(title = "校验机构名称", name = "institution_name", type = FieldType.String, length = 60)
	private String institution_name;// 校验机构名称

	@BeanField(title = "校验中", name = "checking_flg", type = FieldType.Integer, length = 1, notNull = true)
	private String checking_flg;// 校验中

	@BeanField(title = "备注", name = "comment", type = FieldType.String, length = 250)
	private String comment;// 备注

	@BeanField(title = "设备工具品名ID", name = "devices_type_id", type = FieldType.String, length = 11)
	private String devices_type_id;// 设备工具品名ID

	@BeanField(title = "管理编号", name = "manage_code", type = FieldType.String)
	private String manage_code;// 管理编号

	@BeanField(title = "品名", name = "name", type = FieldType.String)
	private String name;// 品名

	@BeanField(title = "厂商", name = "brand", type = FieldType.String)
	private String brand;// 厂商

	@BeanField(title = "型号", name = "model_name", type = FieldType.String)
	private String model_name;// 型号

	@BeanField(title = "分发课室ID", name = "section_id", type = FieldType.String, length = 11)
	private String section_id;// 分发课室ID

	@BeanField(title = "分发课室Name", name = "section_name", type = FieldType.String)
	private String section_name;// 分发课室Name

	@BeanField(title = "责任工程ID", name = "line_id", type = FieldType.String, length = 11)
	private String line_id;// 责任工程ID

	@BeanField(title = "责任工程Name", name = "line_name", type = FieldType.String)
	private String line_name;// 责任工程Name

	@BeanField(title = "出厂编号", name = "products_code", type = FieldType.String)
	private String products_code;// 出厂编号

	@BeanField(title = "校验日期开始", name = "checked_date_start", type = FieldType.Date)
	private String checked_date_start;// 校验日期开始

	@BeanField(title = "校验日期结束", name = "checked_date_end", type = FieldType.Date)
	private String checked_date_end;// 校验日期结束

	@BeanField(title = "过期日期开始", name = "available_end_date_start", type = FieldType.Date)
	private String available_end_date_start;// 过期日期开始

	@BeanField(title = "过期日期结束", name = "available_end_date_end", type = FieldType.Date)
	private String available_end_date_end;// 过期日期结束

	@BeanField(title = "是否过期", name = "isover", type = FieldType.Integer)
	private String isover;// 是否过期

	@BeanField(title = "管理等级", name = "manage_level", type = FieldType.Integer)
	private String manage_level;// 管理等级
	
	@BeanField(title = "类型", name = "object_type", type = FieldType.Integer,notNull=true)
	private String object_type;//类型
	
	@BeanField(title = "治具管理ID", name = "tools_manage_id", type = FieldType.String)
	private String tools_manage_id;//治具管理ID

	public String getDevices_manage_id() {
		return devices_manage_id;
	}

	public void setDevices_manage_id(String devices_manage_id) {
		this.devices_manage_id = devices_manage_id;
	}

	public String getChecked_date() {
		return checked_date;
	}

	public void setChecked_date(String checked_date) {
		this.checked_date = checked_date;
	}

	public String getAvailable_end_date() {
		return available_end_date;
	}

	public void setAvailable_end_date(String available_end_date) {
		this.available_end_date = available_end_date;
	}

	public String getEffect_interval() {
		return effect_interval;
	}

	public void setEffect_interval(String effect_interval) {
		this.effect_interval = effect_interval;
	}

	public String getCheck_cost() {
		return check_cost;
	}

	public void setCheck_cost(String check_cost) {
		this.check_cost = check_cost;
	}

	public String getOrganization_type() {
		return organization_type;
	}

	public void setOrganization_type(String organization_type) {
		this.organization_type = organization_type;
	}

	public String getInstitution_name() {
		return institution_name;
	}

	public void setInstitution_name(String institution_name) {
		this.institution_name = institution_name;
	}

	public String getChecking_flg() {
		return checking_flg;
	}

	public void setChecking_flg(String checking_flg) {
		this.checking_flg = checking_flg;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDevices_type_id() {
		return devices_type_id;
	}

	public void setDevices_type_id(String devices_type_id) {
		this.devices_type_id = devices_type_id;
	}

	public String getManage_code() {
		return manage_code;
	}

	public void setManage_code(String manage_code) {
		this.manage_code = manage_code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
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

	public String getProducts_code() {
		return products_code;
	}

	public void setProducts_code(String products_code) {
		this.products_code = products_code;
	}

	public String getChecked_date_start() {
		return checked_date_start;
	}

	public void setChecked_date_start(String checked_date_start) {
		this.checked_date_start = checked_date_start;
	}

	public String getChecked_date_end() {
		return checked_date_end;
	}

	public void setChecked_date_end(String checked_date_end) {
		this.checked_date_end = checked_date_end;
	}

	public String getAvailable_end_date_start() {
		return available_end_date_start;
	}

	public void setAvailable_end_date_start(String available_end_date_start) {
		this.available_end_date_start = available_end_date_start;
	}

	public String getAvailable_end_date_end() {
		return available_end_date_end;
	}

	public void setAvailable_end_date_end(String available_end_date_end) {
		this.available_end_date_end = available_end_date_end;
	}

	public String getIsover() {
		return isover;
	}

	public void setIsover(String isover) {
		this.isover = isover;
	}

	public String getManage_level() {
		return manage_level;
	}

	public void setManage_level(String manage_level) {
		this.manage_level = manage_level;
	}

	public String getObject_type() {
		return object_type;
	}

	public void setObject_type(String object_type) {
		this.object_type = object_type;
	}

	public String getTools_manage_id() {
		return tools_manage_id;
	}

	public void setTools_manage_id(String tools_manage_id) {
		this.tools_manage_id = tools_manage_id;
	}

}
