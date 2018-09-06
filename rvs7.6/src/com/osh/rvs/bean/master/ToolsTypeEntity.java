package com.osh.rvs.bean.master;

import java.io.Serializable;
import java.sql.Timestamp;

public class ToolsTypeEntity implements Serializable {
	/**
	 *治具品名 
	 */
	private static final long serialVersionUID = -2443905085120088206L;
	
	//治具品名ID  
	private String tools_type_id;    
	//品名        
	private String name;             
	//删除标记    
	private Integer delete_flg;       
	//最后更新人  
	private String updated_by;       
	//最后更新时间
	private Timestamp updated_time;
	
	public String getTools_type_id() {
		return tools_type_id;
	}
	public void setTools_type_id(String tools_type_id) {
		this.tools_type_id = tools_type_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
}
