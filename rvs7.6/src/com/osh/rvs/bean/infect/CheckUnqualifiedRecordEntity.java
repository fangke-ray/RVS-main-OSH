package com.osh.rvs.bean.infect;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.bean.infect
 * @ClassName: CheckUnqualifiedRecordEntity
 * @Description: 点检不合格记录Entity
 * @author lxb
 * @date 2014-8-13 下午4:04:48
 * 
 */
public class CheckUnqualifiedRecordEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7358326364912024164L;

	private String check_unqualified_record_key;// 点检不合格记录key
	private String manage_id;// 管理ID
	private Integer object_type;// 对象类型
	private Date happen_time;// 发生时间
	private String check_item;// 不合格项目
	private String unqualified_status;// 不合格状态
	private Integer product_content;// 产品处理内容
	private Integer position_handle;// 工位对处
	private Integer object_handle;// 管理对象对处
	private String borrow_object_id;// 借用物品ID
	private String product_result;// 产品处理结果
	private String responsible_operator_id;// 责任人ID
	private String line_leader_id;// 线长ID
	private Date line_leader_handle_time;// 线长处理时间
	private String manager_id;// 经理ID
	private Date manager_handle_time;// 经理处理时间
	private String technology_id;// 设备管理员ID
	private Date technology_handle_time;// 设备管理员处理时间
	private Integer object_final_handle_result;// 管理对象对处结果
	private Date repair_date_start;// 维修开始日期
	private Date repair_date_end;// 维修结束日期

	private String manage_code;// 管理编号
	private String name;// 品名
	private String model_name;// 型号
	private Date happen_time_start;// 发生日期开始
	private Date happen_time_end;// 发生日期结束
	private Date repair_date_start_start;// 维修开始日期开始
	private Date repair_date_start_end;// 维修开始日期结束
	private String responsible_operator_name;// 责任人名称
	private String line_leader_name;// 线长名称
	private String manager_name;// 经理名称
	private String technology_name;// 设备管理员名称
	private String borrow_object_name;// 借用物品名称

	private String devices_type_id;// 设备工具品名ID
	private String tools_type_id;// 治具品名ID
	private String borrow_model_name;// 借用设备的型号
	private String tools_no;// 治具NO.

	private String section_name;// 工位名称
	private String process_code;// 工位代码
	
	private String technology_comment;//设备管理员备注

	private String borrow_manage_no; // 借用品管理编号
	private String alarm_message_id;
	private String position_id;
	private String section_id;

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

	public Integer getObject_type() {
		return object_type;
	}

	public void setObject_type(Integer object_type) {
		this.object_type = object_type;
	}

	public Date getHappen_time() {
		return happen_time;
	}

	public void setHappen_time(Date happen_time) {
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

	public Integer getProduct_content() {
		return product_content;
	}

	public void setProduct_content(Integer product_content) {
		this.product_content = product_content;
	}

	public Integer getPosition_handle() {
		return position_handle;
	}

	public void setPosition_handle(Integer position_handle) {
		this.position_handle = position_handle;
	}

	public Integer getObject_handle() {
		return object_handle;
	}

	public void setObject_handle(Integer object_handle) {
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

	public Date getLine_leader_handle_time() {
		return line_leader_handle_time;
	}

	public void setLine_leader_handle_time(Date line_leader_handle_time) {
		this.line_leader_handle_time = line_leader_handle_time;
	}

	public String getManager_id() {
		return manager_id;
	}

	public void setManager_id(String manager_id) {
		this.manager_id = manager_id;
	}

	public Date getManager_handle_time() {
		return manager_handle_time;
	}

	public void setManager_handle_time(Date manager_handle_time) {
		this.manager_handle_time = manager_handle_time;
	}

	public String getTechnology_id() {
		return technology_id;
	}

	public void setTechnology_id(String technology_id) {
		this.technology_id = technology_id;
	}

	public Date getTechnology_handle_time() {
		return technology_handle_time;
	}

	public void setTechnology_handle_time(Date technology_handle_time) {
		this.technology_handle_time = technology_handle_time;
	}

	public Integer getObject_final_handle_result() {
		return object_final_handle_result;
	}

	public void setObject_final_handle_result(Integer object_final_handle_result) {
		this.object_final_handle_result = object_final_handle_result;
	}

	public Date getRepair_date_start() {
		return repair_date_start;
	}

	public void setRepair_date_start(Date repair_date_start) {
		this.repair_date_start = repair_date_start;
	}

	public Date getRepair_date_end() {
		return repair_date_end;
	}

	public void setRepair_date_end(Date repair_date_end) {
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

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public Date getHappen_time_start() {
		return happen_time_start;
	}

	public void setHappen_time_start(Date happen_time_start) {
		this.happen_time_start = happen_time_start;
	}

	public Date getHappen_time_end() {
		return happen_time_end;
	}

	public void setHappen_time_end(Date happen_time_end) {
		this.happen_time_end = happen_time_end;
	}

	public Date getRepair_date_start_start() {
		return repair_date_start_start;
	}

	public void setRepair_date_start_start(Date repair_date_start_start) {
		this.repair_date_start_start = repair_date_start_start;
	}

	public Date getRepair_date_start_end() {
		return repair_date_start_end;
	}

	public void setRepair_date_start_end(Date repair_date_start_end) {
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

	public String getBorrow_manage_no() {
		return borrow_manage_no;
	}

	public void setBorrow_manage_no(String borrow_manage_no) {
		this.borrow_manage_no = borrow_manage_no;
	}

	public String getAlarm_message_id() {
		return alarm_message_id;
	}

	public void setAlarm_message_id(String alarm_message_id) {
		this.alarm_message_id = alarm_message_id;
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
}