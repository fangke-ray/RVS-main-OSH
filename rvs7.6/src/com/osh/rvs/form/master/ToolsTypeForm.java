package com.osh.rvs.form.master;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class ToolsTypeForm extends ActionForm implements Serializable {
	
	/**
	 * 治具品名
	 */
	private static final long serialVersionUID = -2553137784972788802L;

	//治具品名ID 
	@BeanField(title = "治具品名ID", name = "tools_type_id", length = 11,notNull = true)
	private String tools_type_id;    
	//品名        
	@BeanField(title = "品名", name = "name", length = 32,notNull = true)
	private String name;             
	//删除标记    
	@BeanField(title="删除标记",name="delete_flg",type=FieldType.Integer,length=1)
	private String delete_flg;       
	//最后更新人  
	@BeanField(title="删除标记",name="updated_by",type=FieldType.String)
	private String updated_by;       
	//最后更新时间
	@BeanField(title="删除标记",name="updated_time",type=FieldType.TimeStamp)
	private String updated_time;
	
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
	
	
}
