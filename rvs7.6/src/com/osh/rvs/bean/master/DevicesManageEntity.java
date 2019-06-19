package com.osh.rvs.bean.master;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class DevicesManageEntity implements Serializable {

	/**
	 * 设备工具管理
	 */
	private static final long serialVersionUID = 5383107418320149405L;
	//点检表管理编号
	private String daily_sheet_manage_no;
	
	//点检表管理编号
	private String regular_sheet_manage_no;
	
	//对应类型
	private Integer access_place;
	
	//发放者
	private String provider;

	//分发课室
	private String section_name;
	
	//责任工程
	private String line_name;
	
    //责任工位
	private String process_code;
	
	//管理员
	private String manager;
	
	//责任人
	private String responsible_operator;
	
	//设备工具管理ID
	private String devices_manage_id;        
	//管理编号      
	private String manage_code;              
	//设备工具品名ID
	private String devices_type_id;          
	//品名          
	private String name;                     
	//型号          
	private String model_name;               
	//放置位置      
	private String location;                 
	//管理员        
	private String manager_operator_id;      
	//管理等级      
	private Integer manage_level;             
	//管理内容      
	private String manage_content;           
	//出厂编号      
	private String products_code;            
	//厂商          
	private String brand;                    
	//分发课室      
	private String section_id;               
	//责任工程      
	private String line_id;                  
	//责任工位      
	private String position_id;              
	//责任人员      
	private String responsible_operator_id;  
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
	//更新时间      
	private Timestamp updated_time;             
	//状态          
	private String status;                   
	//备注          
	private String comment;
	// 特定设备工具种类
	private Integer specialized;
	
	//发放日期开始     
	private Date provide_date_start;        
	
	//发放日期结束
	private Date provide_date_end;                    
          
	private String check_result; 

	private Integer backup_evaluation;

	private String corresponding;

	private Integer borrowed;

	// 可替换状况
	private Integer free_displace_flg;

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
	public Integer getAccess_place() {
		return access_place;
	}
	public void setAccess_place(Integer access_place) {
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
	public Integer getManage_level() {
		return manage_level;
	}
	public void setManage_level(Integer manage_level) {
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
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getProvide_date_start() {
		return provide_date_start;
	}
	public void setProvide_date_start(Date provide_date_start) {
		this.provide_date_start = provide_date_start;
	}
	public Date getProvide_date_end() {
		return provide_date_end;
	}
	public void setProvide_date_end(Date provide_date_end) {
		this.provide_date_end = provide_date_end;
	}
	/**
	 * @return the check_result
	 */
	public String getCheck_result() {
		return check_result;
	}
	/**
	 * @param check_result the check_result to set
	 */
	public void setCheck_result(String check_result) {
		this.check_result = check_result;
	}
	public Integer getSpecialized() {
		return specialized;
	}
	public void setSpecialized(Integer specialized) {
		this.specialized = specialized;
	}
	public Integer getFree_displace_flg() {
		return free_displace_flg;
	}
	public void setFree_displace_flg(Integer free_displace_flg) {
		this.free_displace_flg = free_displace_flg;
	}
	public Integer getBackup_evaluation() {
		return backup_evaluation;
	}
	public void setBackup_evaluation(Integer backup_evaluation) {
		this.backup_evaluation = backup_evaluation;
	}
	public String getCorresponding() {
		return corresponding;
	}
	public void setCorresponding(String corresponding) {
		this.corresponding = corresponding;
	}
	public Integer getBorrowed() {
		return borrowed;
	}
	public void setBorrowed(Integer borrowed) {
		this.borrowed = borrowed;
	}
}
