package com.osh.rvs.form.infect;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.form.infect
 * @ClassName: CheckUnqualifiedRecordForm
 * @Description: 点检不合格记录Form
 * @author lxb
 * @date 2014-8-13 下午4:04:33
 * 
 */
public class CheckUnqualifiedRecordForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6212731472119154581L;

	@BeanField(title = "点检不合格记录key", name = "check_unqualified_record_key", type = FieldType.String, length = 11, primaryKey = true, notNull = true)
	private String check_unqualified_record_key;// 点检不合格记录key

	@BeanField(title = "管理ID", name = "manage_id", type = FieldType.String, length = 11, notNull = true)
	private String manage_id;// 管理ID

	@BeanField(title = "对象类型", name = "object_type", type = FieldType.Integer, length = 1, notNull = true)
	private String object_type;// 对象类型

	@BeanField(title = "发生时间", name = "happen_time", type = FieldType.DateTime, notNull = true)
	private String happen_time;// 发生时间

	@BeanField(title = "不合格项目", name = "check_item", type = FieldType.String, length = 32)
	private String check_item;// 不合格项目

	@BeanField(title = "不合格状态", name = "unqualified_status", type = FieldType.String, length = 64)
	private String unqualified_status;// 不合格状态

	@BeanField(title = "产品处理内容", name = "product_content", type = FieldType.Integer, length = 1)
	private String product_content;// 产品处理内容

	@BeanField(title = "工位对处", name = "position_handle", type = FieldType.Integer, length = 1)
	private String position_handle;// 工位对处

	@BeanField(title = "管理对象对处", name = "object_handle", type = FieldType.Integer, length = 1)
	private String object_handle;// 管理对象对处

	@BeanField(title = "借用物品ID", name = "borrow_object_id", type = FieldType.String, length = 11)
	private String borrow_object_id;// 借用物品ID

	@BeanField(title = "产品处理结果", name = "product_result", type = FieldType.String, length = 64)
	private String product_result;// 产品处理结果

	@BeanField(title = "责任人ID", name = "responsible_operator_id", type = FieldType.String, length = 11)
	private String responsible_operator_id;// 责任人ID

	@BeanField(title = "线长ID", name = "line_leader_id", type = FieldType.String, length = 11)
	private String line_leader_id;// 线长ID

	@BeanField(title = "线长处理时间", name = "line_leader_handle_time", type = FieldType.DateTime)
	private String line_leader_handle_time;// 线长处理时间

	@BeanField(title = "经理ID", name = "manager_id", type = FieldType.String, length = 11)
	private String manager_id;// 经理ID

	@BeanField(title = "经理处理时间", name = "manager_handle_time", type = FieldType.DateTime)
	private String manager_handle_time;// 经理处理时间

	@BeanField(title = "设备管理员ID", name = "technology_id", type = FieldType.String, length = 11)
	private String technology_id;// 设备管理员ID

	@BeanField(title = "设备管理员处理时间", name = "technology_handle_time", type = FieldType.DateTime)
	private String technology_handle_time;// 设备管理员处理时间

	@BeanField(title = "管理对象对处结果", name = "object_final_handle_result", type = FieldType.Integer, length = 1)
	private String object_final_handle_result;// 管理对象对处结果

	@BeanField(title = "维修开始日期", name = "repair_date_start", type = FieldType.Date)
	private String repair_date_start;// 维修开始日期

	@BeanField(title = "维修结束日期", name = "repair_date_end", type = FieldType.Date)
	private String repair_date_end;// 维修结束日期

	@BeanField(title = "管理编号", name = "manage_code", type = FieldType.String)
	private String manage_code;// 管理编号

	@BeanField(title = "品名", name = "name", type = FieldType.String)
	private String name;// 品名

	@BeanField(title = "型号", name = "model_name", type = FieldType.String)
	private String model_name;// 型号

	@BeanField(title = "发生日期开始", name = "happen_time_start", type = FieldType.Date)
	private String happen_time_start;// 发生日期开始

	@BeanField(title = "发生日期结束", name = "happen_time_end", type = FieldType.Date)
	private String happen_time_end;// 发生日期结束

	@BeanField(title = "维修开始日期开始", name = "repair_date_start_start", type = FieldType.Date)
	private String repair_date_start_start;// 维修开始日期开始

	@BeanField(title = "维修开始日期结束", name = "repair_date_start_end", type = FieldType.Date)
	private String repair_date_start_end;// 维修开始日期结束

	@BeanField(title = "责任人名称", name = "responsible_operator_name", type = FieldType.String)
	private String responsible_operator_name;// 责任人名称

	@BeanField(title = "线长名称", name = "line_leader_name", type = FieldType.String)
	private String line_leader_name;// 线长名称

	@BeanField(title = "经理名称", name = "manager_name", type = FieldType.String)
	private String manager_name;// 经理名称

	@BeanField(title = "设备管理员名称", name = "technology_name", type = FieldType.String)
	private String technology_name;// 设备管理员名称
	
	@BeanField(title="借用物品名称",name="borrow_object_name",type=FieldType.String)
	private String borrow_object_name;// 借用物品名称
	
	@BeanField(title="设备工具品名ID",name="devices_type_id",type=FieldType.String)
	private String devices_type_id;//设备工具品名ID
	
	
	@BeanField(title="治具品名ID",name="tools_type_id",type=FieldType.String)
	private String tools_type_id;//治具品名ID

	@BeanField(title = "借用状态", name = "borrow_status", type = FieldType.Integer, length = 1)
	private String borrow_status;

	@BeanField(title="借用设备的型号",name="borrow_model_name",type=FieldType.String)
	private String borrow_model_name;//借用设备的型号
	
	@BeanField(title="治具NO.",name="tools_no",type=FieldType.String)
	private String tools_no;//治具NO.
	
	@BeanField(title="工位名称",name="section_name",type=FieldType.String)
	private String section_name;//工位名称
	
	@BeanField(title="工位代码",name="process_code",type=FieldType.String)
	private String process_code;//工位代码
	
	@BeanField(title="设备管理员备注",name="technology_comment",type=FieldType.String)
	private String technology_comment;//设备管理员备注

	@BeanField(title="借用品管理编号",name="borrow_manage_no",type=FieldType.String)
	private String borrow_manage_no; // 借用品管理编号

	private String position_id;
	private String section_id;

	@BeanField(title = "替换品收回日期", name = "borrow_until", type = FieldType.Date)
	private String borrow_until;

	public String getCheck_unqualified_record_key() {
		return check_unqualified_record_key;
	}

	public void setCheck_unqualified_record_key(String check_unqualified_record_key) {
		this.check_unqualified_record_key = check_unqualified_record_key;
	}

	public String getManage_id() {
		return manage_id;
	}

	public void setManage_id(String manage_id) {
		this.manage_id = manage_id;
	}

	public String getObject_type() {
		return object_type;
	}

	public void setObject_type(String object_type) {
		this.object_type = object_type;
	}

	public String getHappen_time() {
		return happen_time;
	}

	public void setHappen_time(String happen_time) {
		this.happen_time = happen_time;
	}

	public String getCheck_item() {
		return check_item;
	}

	public void setCheck_item(String check_item) {
		this.check_item = check_item;
	}

	public String getUnqualified_status() {
		return unqualified_status;
	}

	public void setUnqualified_status(String unqualified_status) {
		this.unqualified_status = unqualified_status;
	}

	public String getProduct_content() {
		return product_content;
	}

	public void setProduct_content(String product_content) {
		this.product_content = product_content;
	}

	public String getPosition_handle() {
		return position_handle;
	}

	public void setPosition_handle(String position_handle) {
		this.position_handle = position_handle;
	}

	public String getObject_handle() {
		return object_handle;
	}

	public void setObject_handle(String object_handle) {
		this.object_handle = object_handle;
	}

	public String getBorrow_object_id() {
		return borrow_object_id;
	}

	public void setBorrow_object_id(String borrow_object_id) {
		this.borrow_object_id = borrow_object_id;
	}

	public String getProduct_result() {
		return product_result;
	}

	public void setProduct_result(String product_result) {
		this.product_result = product_result;
	}

	public String getResponsible_operator_id() {
		return responsible_operator_id;
	}

	public void setResponsible_operator_id(String responsible_operator_id) {
		this.responsible_operator_id = responsible_operator_id;
	}

	public String getLine_leader_id() {
		return line_leader_id;
	}

	public void setLine_leader_id(String line_leader_id) {
		this.line_leader_id = line_leader_id;
	}

	public String getLine_leader_handle_time() {
		return line_leader_handle_time;
	}

	public void setLine_leader_handle_time(String line_leader_handle_time) {
		this.line_leader_handle_time = line_leader_handle_time;
	}

	public String getManager_id() {
		return manager_id;
	}

	public void setManager_id(String manager_id) {
		this.manager_id = manager_id;
	}

	public String getManager_handle_time() {
		return manager_handle_time;
	}

	public void setManager_handle_time(String manager_handle_time) {
		this.manager_handle_time = manager_handle_time;
	}

	public String getTechnology_id() {
		return technology_id;
	}

	public void setTechnology_id(String technology_id) {
		this.technology_id = technology_id;
	}

	public String getTechnology_handle_time() {
		return technology_handle_time;
	}

	public void setTechnology_handle_time(String technology_handle_time) {
		this.technology_handle_time = technology_handle_time;
	}

	public String getObject_final_handle_result() {
		return object_final_handle_result;
	}

	public void setObject_final_handle_result(String object_final_handle_result) {
		this.object_final_handle_result = object_final_handle_result;
	}

	public String getRepair_date_start() {
		return repair_date_start;
	}

	public void setRepair_date_start(String repair_date_start) {
		this.repair_date_start = repair_date_start;
	}

	public String getRepair_date_end() {
		return repair_date_end;
	}

	public void setRepair_date_end(String repair_date_end) {
		this.repair_date_end = repair_date_end;
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

	public String getHappen_time_start() {
		return happen_time_start;
	}

	public void setHappen_time_start(String happen_time_start) {
		this.happen_time_start = happen_time_start;
	}

	public String getHappen_time_end() {
		return happen_time_end;
	}

	public void setHappen_time_end(String happen_time_end) {
		this.happen_time_end = happen_time_end;
	}

	public String getRepair_date_start_start() {
		return repair_date_start_start;
	}

	public void setRepair_date_start_start(String repair_date_start_start) {
		this.repair_date_start_start = repair_date_start_start;
	}

	public String getRepair_date_start_end() {
		return repair_date_start_end;
	}

	public void setRepair_date_start_end(String repair_date_start_end) {
		this.repair_date_start_end = repair_date_start_end;
	}

	public String getResponsible_operator_name() {
		return responsible_operator_name;
	}

	public void setResponsible_operator_name(String responsible_operator_name) {
		this.responsible_operator_name = responsible_operator_name;
	}

	public String getLine_leader_name() {
		return line_leader_name;
	}

	public void setLine_leader_name(String line_leader_name) {
		this.line_leader_name = line_leader_name;
	}

	public String getManager_name() {
		return manager_name;
	}

	public void setManager_name(String manager_name) {
		this.manager_name = manager_name;
	}

	public String getTechnology_name() {
		return technology_name;
	}

	public void setTechnology_name(String technology_name) {
		this.technology_name = technology_name;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getBorrow_object_name() {
		return borrow_object_name;
	}

	public void setBorrow_object_name(String borrow_object_name) {
		this.borrow_object_name = borrow_object_name;
	}

	public String getDevices_type_id() {
		return devices_type_id;
	}

	public void setDevices_type_id(String devices_type_id) {
		this.devices_type_id = devices_type_id;
	}

	public String getTools_type_id() {
		return tools_type_id;
	}

	public void setTools_type_id(String tools_type_id) {
		this.tools_type_id = tools_type_id;
	}

	public String getBorrow_model_name() {
		return borrow_model_name;
	}

	public void setBorrow_model_name(String borrow_model_name) {
		this.borrow_model_name = borrow_model_name;
	}

	public String getTools_no() {
		return tools_no;
	}

	public void setTools_no(String tools_no) {
		this.tools_no = tools_no;
	}

	public String getSection_name() {
		return section_name;
	}

	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getTechnology_comment() {
		return technology_comment;
	}

	public void setTechnology_comment(String technology_comment) {
		this.technology_comment = technology_comment;
	}

	public String getBorrow_manage_no() {
		return borrow_manage_no;
	}

	public void setBorrow_manage_no(String borrow_manage_no) {
		this.borrow_manage_no = borrow_manage_no;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getBorrow_status() {
		return borrow_status;
	}

	public void setBorrow_status(String borrow_status) {
		this.borrow_status = borrow_status;
	}

	public String getBorrow_until() {
		return borrow_until;
	}

	public void setBorrow_until(String borrow_until) {
		this.borrow_until = borrow_until;
	}
}
