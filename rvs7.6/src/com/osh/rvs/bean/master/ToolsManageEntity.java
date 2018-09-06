package com.osh.rvs.bean.master;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class ToolsManageEntity implements Serializable {

	/**
	 * 治具管理
	 */
	private static final long serialVersionUID = -4606995639651748239L;
	//分发课室
	private String section_name;
	//责任工程
	private String line_name;
	//责任人
	private String responsible_operator;
	//工位
	private String process_code;
	//发放者
	private String provider;
	//治具管理ID
	private String tools_manage_id;                 
	//管理编号  
	private String manage_code;                     
	//治具NO.   
	private String tools_no;                        
	//治具品名ID
	private String tools_type_id;
	//治具名称  
	private String tools_name;                      
	//总价      
	private BigDecimal total_price; 
	//分类
	private String classify;
	//管理等级  
	private Integer manage_level;                    
	//管理员    
	private String manager_operator_id;             
	private String manager_name;
	//分发课室  
	private String section_id;                      
	//责任工程  
	private String line_id;                         
	//责任工位  
	private String position_id;         
	//放置位置  
	private String location;                        
	//导入日期  
	private Date import_date;                     
	//发放日期  
	private Date provide_date;                    
	//废弃日期  
	private Date waste_date;                      
	//删除标记  
	private Integer delete_flg;                      
	//最后更新人
	private String updated_by;                      
	//更新日期  
	private Timestamp updated_time;                    
	//状态      
	private String status;                          
	//责任人员  
	private String responsible_operator_id;         
	//备注      
	private String comment;

	private Date order_date;// 订购日期

	private Date order_date_start;// 订购日期开始

	private Date order_date_end;// 订购日期结束

	private Date import_date_start;// 导入日期开始

	private Date import_date_end;// 导入日期结束

	private Date waste_date_start;// 废弃日期开始

	private Date waste_date_end;// 废弃日期开始

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
	public BigDecimal getTotal_price() {
		return total_price;
	}
	public void setTotal_price(BigDecimal total_price) {
		this.total_price = total_price;
	}
	public Integer getManage_level() {
		return manage_level;
	}
	public void setManage_level(Integer manage_level) {
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
	public Date getImport_date() {
		return import_date;
	}
	public void setImport_date(Date import_date) {
		this.import_date = import_date;
	}
	public Date getProvide_date() {
		return provide_date;
	}
	public void setProvide_date(Date provide_date) {
		this.provide_date = provide_date;
	}
	public Date getWaste_date() {
		return waste_date;
	}
	public void setWaste_date(Date waste_date) {
		this.waste_date = waste_date;
	}
	public Integer getDelete_flg() {
		return delete_flg;
	}
	public void setDelete_flg(Integer delete_flg) {
		this.delete_flg = delete_flg;
	}
	public String getUpdated_by() {
		return updated_by;
	}
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}
	public Timestamp getUpdated_time() {
		return updated_time;
	}
	public void setUpdated_time(Timestamp updated_time) {
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
	public Date getOrder_date() {
		return order_date;
	}
	public void setOrder_date(Date order_date) {
		this.order_date = order_date;
	}
	public Date getOrder_date_start() {
		return order_date_start;
	}
	public void setOrder_date_start(Date order_date_start) {
		this.order_date_start = order_date_start;
	}
	public Date getOrder_date_end() {
		return order_date_end;
	}
	public void setOrder_date_end(Date order_date_end) {
		this.order_date_end = order_date_end;
	}

	public Date getImport_date_start() {
		return import_date_start;
	}

	public void setImport_date_start(Date import_date_start) {
		this.import_date_start = import_date_start;
	}

	public Date getImport_date_end() {
		return import_date_end;
	}

	public void setImport_date_end(Date import_date_end) {
		this.import_date_end = import_date_end;
	}

	public Date getWaste_date_start() {
		return waste_date_start;
	}

	public void setWaste_date_start(Date waste_date_start) {
		this.waste_date_start = waste_date_start;
	}

	public Date getWaste_date_end() {
		return waste_date_end;
	}

	public void setWaste_date_end(Date waste_date_end) {
		this.waste_date_end = waste_date_end;
	}
	public String getManager_name() {
		return manager_name;
	}
	public void setManager_name(String manager_name) {
		this.manager_name = manager_name;
	}

}
