package com.osh.rvs.form.master;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class PartialBomForm extends ActionForm {

	/**
	 * 零件BOM管理
	 */

	private static final long serialVersionUID = 8931917335686576264L;
	/* 零件 ID */
	@BeanField(title = "零件 ID", name = "partial_id", type = FieldType.String, primaryKey = true, length = 11)
	private String partial_id;

	/* 型号 ID */
	@BeanField(title = "型号 ID", name = "model_id", type = FieldType.String, primaryKey = true, length = 11)
	private String model_id;
	
	/*等級*/
	@BeanField(title="等級",name="level",type=FieldType.Integer,primaryKey=true,length=1)
	private String level;

	/* 型号名称 */
	@BeanField(title="型号名称",name="model_name",length=50)
	private String model_name;
	
	/* 零件编码 */
	@BeanField(title = "零件编码", name = "code", length = 8, notNull = true)
	private String code;
	
	/* 零件名称 */
	@BeanField(title = "零件名称", name = "partial_name", length = 120)
	private String partial_name;
	
	/* 使用数量 */
	@BeanField(title = "使用数量", name = "quantity", type=FieldType.Integer,notNull = true,length = 2)
	private String quantity;
	/* 梯队 */
	@BeanField(title = "梯队", name = "echelon",type=FieldType.Integer,length = 1)
	private String echelon;
	public String getPartial_id() {
		return partial_id;
	}
	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}
	public String getModel_id() {
		return model_id;
	}
	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getModel_name() {
		return model_name;
	}
	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getPartial_name() {
		return partial_name;
	}
	public void setPartial_name(String partial_name) {
		this.partial_name = partial_name;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getEchelon() {
		return echelon;
	}
	public void setEchelon(String echelon) {
		this.echelon = echelon;
	}
	
	
	
}
