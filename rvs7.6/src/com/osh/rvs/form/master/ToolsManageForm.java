package com.osh.rvs.form.master;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class ToolsManageForm extends ActionForm {

	/**
	 * 治具管理
	 */
	private static final long serialVersionUID = -7981722018931352325L;
	//同时废弃掉旧品
	private String waste_old_products;
	
	//交付条件
	@BeanField(title = "交付分发课室", name = "compare_section_id", type = FieldType.String)
	private String compare_section_id;
	@BeanField(title = "交付责任工程", name = "compare_line_id", type = FieldType.String)
	private String compare_line_id;
	@BeanField(title = "交付责任工位", name = "compare_position_id", type = FieldType.String)
	private String compare_position_id;
	@BeanField(title = "交付责任人", name = "compare_responsible_operator_id", type = FieldType.String)
	private String compare_responsible_operator_id;
	@BeanField(title = "交付管理员", name = "compare_manager_operator_id", type = FieldType.String)
	private String compare_manager_operator_id;
	// 分发课室
	@BeanField(title = "分发课室", name = "section_name", type = FieldType.String)
	private String section_name;
	// 责任工程
	@BeanField(title = "责任工程", name = "line_name", type = FieldType.String)
	private String line_name;
	// 责任人
	@BeanField(title = "责任人", name = "responsible_operator", type = FieldType.String)
	private String responsible_operator;
	// 工位
	@BeanField(title = "工位", name = "process_code", type = FieldType.String)
	private String process_code;
	// 发放者
	@BeanField(title = "发放者", name = "provider", type = FieldType.String, length = 11)
	private String provider;
	// 治具管理ID
	@BeanField(title = "治具管理ID", name = "tools_manage_id", type = FieldType.String, length = 11)
	private String tools_manage_id;
	// 管理编号
	@BeanField(title = "管理编号", name = "manage_code", type = FieldType.String, notNull = true)
	private String manage_code;
	// 治具NO.
	@BeanField(title = "治具NO. ", name = "tools_no", type = FieldType.String, notNull = true)
	private String tools_no;
	// 治具品名ID
	@BeanField(title = "治具品名ID", name = "tools_type_id", type = FieldType.String, length = 11)
	private String tools_type_id;
	// 治具名称
	@BeanField(title = "治具名称", name = "tools_name", type = FieldType.String)
	private String tools_name;

	@BeanField(title = "总价", name = "total_price", length = 11, type = FieldType.Double)
	private String total_price;
	// 分类
	@BeanField(title = "分类", name = "classify", type = FieldType.String)
	private String classify;
	// 管理等级
	@BeanField(title = "管理等级", name = "manage_level", type = FieldType.Integer, notNull = true)
	private String manage_level;
	// 管理员
	@BeanField(title = "管理员", name = "manager_operator_id", type = FieldType.String, length = 11)
	private String manager_operator_id;
	@BeanField(title = "管理员", name = "manager_name", type = FieldType.String)
	private String manager_operator;
	// 分发课室
	@BeanField(title = "分发课室", name = "section_id", type = FieldType.String, length = 11, notNull = true)
	private String section_id;
	// 责任工程
	@BeanField(title = "责任工程", name = "line_id", type = FieldType.String, length = 11)
	private String line_id;
	// 责任工位
	@BeanField(title = "责任工位", name = "position_id", type = FieldType.String, length = 11)
	private String position_id;
	// 放置位置
	@BeanField(title = "放置位置", name = "location", type = FieldType.String)
	private String location;
	// 导入日期
	@BeanField(title = "导入日期", name = "import_date", type = FieldType.Date)
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
	// 更新日期
	@BeanField(title = "更新日期", name = "updated_time", type = FieldType.TimeStamp)
	private String updated_time;
	// 状态
	@BeanField(title = "状态", name = "status", type = FieldType.String, notNull = true)
	private String status;
	// 责任人员
	@BeanField(title = "责任人员", name = "responsible_operator_id", type = FieldType.String, length = 11)
	private String responsible_operator_id;
	// 备注
	@BeanField(title = "备注", name = "comment", type = FieldType.String, length = 225)
	private String comment;

	@BeanField(title = "订购日期", name = "order_date", type = FieldType.Date)
	private String order_date;// 订购日期

	@BeanField(title = "订购日期开始", name = "order_date_start", type = FieldType.Date)
	private String order_date_start;// 订购日期开始

	@BeanField(title = "订购日期结束", name = "order_date_end", type = FieldType.Date)
	private String order_date_end;// 订购日期结束

	@BeanField(title = "导入日期开始", name = "import_date_start", type = FieldType.Date)
	private String import_date_start;// 导入日期开始

	@BeanField(title = "导入日期结束", name = "import_date_end", type = FieldType.Date)
	private String import_date_end;// 导入日期结束

	@BeanField(title = "废弃日期开始", name = "waste_date_start", type = FieldType.Date)
	private String waste_date_start;// 废弃日期开始

	@BeanField(title = "废弃日期开始", name = "waste_date_end", type = FieldType.Date)
	private String waste_date_end;// 废弃日期开始

	public String getCompare_manager_operator_id() {
		return compare_manager_operator_id;
	}

	public void setCompare_manager_operator_id(String compare_manager_operator_id) {
		this.compare_manager_operator_id = compare_manager_operator_id;
	}

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

	public String getCompare_responsible_operator_id() {
		return compare_responsible_operator_id;
	}

	public void setCompare_responsible_operator_id(
			String compare_responsible_operator_id) {
		this.compare_responsible_operator_id = compare_responsible_operator_id;
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

	public String getResponsible_operator() {
		return responsible_operator;
	}

	public void setResponsible_operator(String responsible_operator) {
		this.responsible_operator = responsible_operator;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getClassify() {
		return classify;
	}

	public void setClassify(String classify) {
		this.classify = classify;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getTools_manage_id() {
		return tools_manage_id;
	}

	public void setTools_manage_id(String tools_manage_id) {
		this.tools_manage_id = tools_manage_id;
	}

	public String getManage_code() {
		return manage_code;
	}

	public void setManage_code(String manage_code) {
		this.manage_code = manage_code;
	}

	public String getTools_no() {
		return tools_no;
	}

	public void setTools_no(String tools_no) {
		this.tools_no = tools_no;
	}

	public String getTools_type_id() {
		return tools_type_id;
	}

	public void setTools_type_id(String tools_type_id) {
		this.tools_type_id = tools_type_id;
	}

	public String getTools_name() {
		return tools_name;
	}

	public void setTools_name(String tools_name) {
		this.tools_name = tools_name;
	}

	public String getTotal_price() {
		return total_price;
	}

	public void setTotal_price(String total_price) {
		this.total_price = total_price;
	}

	public String getManage_level() {
		return manage_level;
	}

	public void setManage_level(String manage_level) {
		this.manage_level = manage_level;
	}

	public String getManager_operator_id() {
		return manager_operator_id;
	}

	public void setManager_operator_id(String manager_operator_id) {
		this.manager_operator_id = manager_operator_id;
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public String getResponsible_operator_id() {
		return responsible_operator_id;
	}

	public void setResponsible_operator_id(String responsible_operator_id) {
		this.responsible_operator_id = responsible_operator_id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getOrder_date() {
		return order_date;
	}

	public void setOrder_date(String order_date) {
		this.order_date = order_date;
	}

	public String getOrder_date_start() {
		return order_date_start;
	}

	public void setOrder_date_start(String order_date_start) {
		this.order_date_start = order_date_start;
	}

	public String getOrder_date_end() {
		return order_date_end;
	}

	public void setOrder_date_end(String order_date_end) {
		this.order_date_end = order_date_end;
	}

	public String getImport_date_start() {
		return import_date_start;
	}

	public void setImport_date_start(String import_date_start) {
		this.import_date_start = import_date_start;
	}

	public String getImport_date_end() {
		return import_date_end;
	}

	public void setImport_date_end(String import_date_end) {
		this.import_date_end = import_date_end;
	}

	public String getWaste_date_start() {
		return waste_date_start;
	}

	public void setWaste_date_start(String waste_date_start) {
		this.waste_date_start = waste_date_start;
	}

	public String getWaste_date_end() {
		return waste_date_end;
	}

	public void setWaste_date_end(String waste_date_end) {
		this.waste_date_end = waste_date_end;
	}

	public String getManager_operator() {
		return manager_operator;
	}

	public void setManager_operator(String manager_operator) {
		this.manager_operator = manager_operator;
	}

	public String getWaste_old_products() {
		return waste_old_products;
	}

	public void setWaste_old_products(String waste_old_products) {
		this.waste_old_products = waste_old_products;
	}
}
