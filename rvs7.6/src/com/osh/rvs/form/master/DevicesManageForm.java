package com.osh.rvs.form.master;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class DevicesManageForm extends ActionForm {

	/**
	 * 设备工具管理
	 */
	private static final long serialVersionUID = 9032591518153998306L;
	
	//同时废弃掉旧品
	private String waste_old_products;

	// 交付条件
	@BeanField(title = "交付分发课室", name = "compare_section_id", type = FieldType.String)
	private String compare_section_id;
	@BeanField(title = "交付责任工程", name = "compare_line_id", type = FieldType.String)
	private String compare_line_id;
	@BeanField(title = "交付责任工位", name = "compare_position_id", type = FieldType.String)
	private String compare_position_id;
	@BeanField(title = "交付管理员", name = "compare_manager_operator_id", type = FieldType.String)
	private String compare_manager_operator_id;

	// 日常点检表管理编号
	@BeanField(title = "日常点检表管理编号", name = "daily_sheet_manage_no", type = FieldType.String, length = 16)
	private String daily_sheet_manage_no;

	// 定期点检表管理编号
	@BeanField(title = "定期点检表管理编号", name = "regular_sheet_manage_no", type = FieldType.String, length = 16)
	private String regular_sheet_manage_no;

	// 对应类型
	@BeanField(title = "对应类型", name = "access_place", type = FieldType.Integer)
	private String access_place;

	// 发放者
	@BeanField(title = "发放者", name = "provider", type = FieldType.String, length = 11)
	private String provider;

	// 分发课室
	@BeanField(title = "分发课室", name = "section_name", type = FieldType.String)
	private String section_name;

	// 责任工程
	@BeanField(title = "责任工程", name = "line_name", type = FieldType.String)
	private String line_name;

	// 责任工位
	@BeanField(title = "责任工位", name = "process_code", type = FieldType.String)
	private String process_code;

	// 管理员
	@BeanField(title = "管理员", name = "manager", type = FieldType.String)
	private String manager;

	// 责任人员
	@BeanField(title = "责任人员", name = "responsible_operator", type = FieldType.String)
	private String responsible_operator;

	// 设备工具管理ID
	@BeanField(title = "备工具管理ID", name = "devices_manage_id", type = FieldType.String, length = 11)
	private String devices_manage_id;
	// 管理编号
	@BeanField(title = "管理编号", name = "manage_code", type = FieldType.String, notNull = true)
	private String manage_code;
	// 设备工具品名ID
	@BeanField(title = "设备工具品名ID", name = "devices_type_id", type = FieldType.String, length = 11, notNull = true)
	private String devices_type_id;
	// 品名
	@BeanField(title = "品名", name = "name", type = FieldType.String, length = 32)
	private String name;
	// 型号
	@BeanField(title = "型号", name = "model_name", type = FieldType.String, length = 32)
	private String model_name;
	// 放置位置
	@BeanField(title = "放置位置", name = "location", type = FieldType.String, length = 10)
	private String location;
	// 管理员
	@BeanField(title = "管理员", name = "manager_operator_id", type = FieldType.String, length = 11, notNull = true)
	private String manager_operator_id;
	// 管理等级
	@BeanField(title = "管理等级", name = "manage_level", type = FieldType.Integer, length = 1, notNull = true)
	private String manage_level;
	// 管理内容
	@BeanField(title = "管理内容", name = "manage_content", type = FieldType.String, length = 64)
	private String manage_content;
	// 出厂编号
	@BeanField(title = "出厂编号", name = "products_code", type = FieldType.String, length = 15)
	private String products_code;
	// 厂商
	@BeanField(title = "厂商", name = "brand", type = FieldType.String, length = 32)
	private String brand;
	// 分发课室
	@BeanField(title = "分发课室 ", name = "section_id", type = FieldType.String, length = 11, notNull = true)
	private String section_id;
	// 责任工程
	@BeanField(title = "责任工程", name = "line_id", type = FieldType.String, length = 11)
	private String line_id;
	// 责任工位
	@BeanField(title = "责任工位", name = "position_id", type = FieldType.String, length = 11)
	private String position_id;
	// 责任人员
	@BeanField(title = "责任人员", name = "responsible_operator_id", type = FieldType.String, length = 11)
	private String responsible_operator_id;
	// 导入日期
	@BeanField(title = "导入日期", name = "import_date", type = FieldType.Date, notNull = true)
	private String import_date;
	// 发放日期
	@BeanField(title = "发放日期", name = "provide_date", type = FieldType.Date)
	private String provide_date;
	// 废弃日期
	@BeanField(title = "废弃日期", name = "waste_date", type = FieldType.Date)
	private String waste_date;
	// 删除标记
	@BeanField(title = "删除标记", name = "delete_flg", type = FieldType.Integer)
	private String delete_flg;
	// 最后更新人
	@BeanField(title = "最后更新人", name = "updated_by", type = FieldType.String, length = 11)
	private String updated_by;
	// 更新时间
	@BeanField(title = "更新时间", name = "updated_time", type = FieldType.TimeStamp)
	private String updated_time;
	// 状态
	@BeanField(title = "状态", name = "status", type = FieldType.String, notNull = true)
	private String status;
	// 备注
	@BeanField(title = "备注", name = "comment", type = FieldType.String, length = 255)
	private String comment;

	@BeanField(title = "发放日期开始", name = "provide_date_start", type = FieldType.Date)
	private String provide_date_start;// 发放日期开始

	@BeanField(title = "发放日期结束", name = "provide_date_end", type = FieldType.Date)
	private String provide_date_end;// 发放日期结束

	public String getCompare_section_id() {
		return compare_section_id;
	}

	public void setCompare_section_id(String compare_section_id) {
		this.compare_section_id = compare_section_id;
	}

	public String getCompare_line_id() {
		return compare_line_id;
	}

	public void setCompare_line_id(String compare_line_id) {
		this.compare_line_id = compare_line_id;
	}

	public String getCompare_position_id() {
		return compare_position_id;
	}

	public void setCompare_position_id(String compare_position_id) {
		this.compare_position_id = compare_position_id;
	}

	public String getCompare_manager_operator_id() {
		return compare_manager_operator_id;
	}

	public void setCompare_manager_operator_id(String compare_manager_operator_id) {
		this.compare_manager_operator_id = compare_manager_operator_id;
	}

	public String getDaily_sheet_manage_no() {
		return daily_sheet_manage_no;
	}

	public void setDaily_sheet_manage_no(String daily_sheet_manage_no) {
		this.daily_sheet_manage_no = daily_sheet_manage_no;
	}

	public String getRegular_sheet_manage_no() {
		return regular_sheet_manage_no;
	}

	public void setRegular_sheet_manage_no(String regular_sheet_manage_no) {
		this.regular_sheet_manage_no = regular_sheet_manage_no;
	}

	public String getAccess_place() {
		return access_place;
	}

	public void setAccess_place(String access_place) {
		this.access_place = access_place;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getResponsible_operator() {
		return responsible_operator;
	}

	public void setResponsible_operator(String responsible_operator) {
		this.responsible_operator = responsible_operator;
	}

	public String getSection_name() {
		return section_name;
	}

	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
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

	public String getDevices_type_id() {
		return devices_type_id;
	}

	public void setDevices_type_id(String devices_type_id) {
		this.devices_type_id = devices_type_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getManager_operator_id() {
		return manager_operator_id;
	}

	public void setManager_operator_id(String manager_operator_id) {
		this.manager_operator_id = manager_operator_id;
	}

	public String getManage_level() {
		return manage_level;
	}

	public void setManage_level(String manage_level) {
		this.manage_level = manage_level;
	}

	public String getManage_content() {
		return manage_content;
	}

	public void setManage_content(String manage_content) {
		this.manage_content = manage_content;
	}

	public String getProducts_code() {
		return products_code;
	}

	public void setProducts_code(String products_code) {
		this.products_code = products_code;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public String getResponsible_operator_id() {
		return responsible_operator_id;
	}

	public void setResponsible_operator_id(String responsible_operator_id) {
		this.responsible_operator_id = responsible_operator_id;
	}

	public String getImport_date() {
		return import_date;
	}

	public void setImport_date(String import_date) {
		this.import_date = import_date;
	}

	public String getProvide_date() {
		return provide_date;
	}

	public void setProvide_date(String provide_date) {
		this.provide_date = provide_date;
	}

	public String getWaste_date() {
		return waste_date;
	}

	public void setWaste_date(String waste_date) {
		this.waste_date = waste_date;
	}

	public String getDelete_flg() {
		return delete_flg;
	}

	public void setDelete_flg(String delete_flg) {
		this.delete_flg = delete_flg;
	}

	public String getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}

	public String getUpdated_time() {
		return updated_time;
	}

	public void setUpdated_time(String updated_time) {
		this.updated_time = updated_time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getProvide_date_start() {
		return provide_date_start;
	}

	public void setProvide_date_start(String provide_date_start) {
		this.provide_date_start = provide_date_start;
	}

	public String getProvide_date_end() {
		return provide_date_end;
	}

	public void setProvide_date_end(String provide_date_end) {
		this.provide_date_end = provide_date_end;
	}

	public String getWaste_old_products() {
		return waste_old_products;
	}

	public void setWaste_old_products(String waste_old_products) {
		this.waste_old_products = waste_old_products;
	}

}
