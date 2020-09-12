package com.osh.rvs.form.qf;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 现品维修对象作业
 * 
 * @author liuxb
 * 
 */
public class FactMaterialForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8406210111024685941L;

	@BeanField(title = "现品作业KEY", name = "af_pf_key", type = FieldType.String, length = 11, notNull = true)
	private String af_pf_key;

	@BeanField(title = "维修对象 ID", name = "material_id", type = FieldType.String, length = 11, notNull = true)
	private String material_id;
	
	@BeanField(title = "作业类型", name = "production_type", type = FieldType.Integer, length = 3)
	private String production_type;
	
	@BeanField(title = "操作者 ID", name = "operator_id", type = FieldType.String, length = 11)
	private String operator_id;
	
	@BeanField(title = "开始日期", name = "action_time_start", type = FieldType.Date)
	private String action_time_start;
	
	@BeanField(title = "接收日期", name = "action_time_end", type = FieldType.Date)
	private String action_time_end;

	public String getAf_pf_key() {
		return af_pf_key;
	}

	public void setAf_pf_key(String af_pf_key) {
		this.af_pf_key = af_pf_key;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getProduction_type() {
		return production_type;
	}

	public void setProduction_type(String production_type) {
		this.production_type = production_type;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getAction_time_start() {
		return action_time_start;
	}

	public void setAction_time_start(String action_time_start) {
		this.action_time_start = action_time_start;
	}

	public String getAction_time_end() {
		return action_time_end;
	}

	public void setAction_time_end(String action_time_end) {
		this.action_time_end = action_time_end;
	}

	
}
