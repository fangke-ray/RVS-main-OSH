package com.osh.rvs.form.pda;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 
 * @Title PdaSupplyForm.java
 * @Project rvs
 * @Package com.osh.rvs.form.pda
 * @ClassName: PdaSupplyForm
 * @Description: 消耗品入库
 * @author lxb
 * @date 2015-5-29 上午10:38:46
 */
public class PdaSupplyForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 169831762501300718L;

	@BeanField(title = "零件ID", name = "partial_id", type = FieldType.String)
	private String partial_id;// 零件ID

	@BeanField(title = "零件Code", name = "code", type = FieldType.String)
	private String code;// 零件Code

	@BeanField(title = "类型", name = "type", type = FieldType.Integer)
	private String type;// 类型

	@BeanField(title = "类型名称", name = "type_name", type = FieldType.String)
	private String type_name;// 类型名称

	@BeanField(title = "有效数量", name = "available_inventory", type = FieldType.Integer)
	private String available_inventory;// 有效数量

	@BeanField(title = "在途数量", name = "on_passage", type = FieldType.Integer)
	private String on_passage;// 在途数量

	@BeanField(title = "库位", name = "stock_code", type = FieldType.String)
	private String stock_code;// 库位
	
	@BeanField(title = "入库数量", name = "quantity", type = FieldType.Integer)
	private String quantity;//入库数量

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType_name() {
		return type_name;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}

	public String getAvailable_inventory() {
		return available_inventory;
	}

	public void setAvailable_inventory(String available_inventory) {
		this.available_inventory = available_inventory;
	}

	public String getOn_passage() {
		return on_passage;
	}

	public void setOn_passage(String on_passage) {
		this.on_passage = on_passage;
	}

	public String getStock_code() {
		return stock_code;
	}

	public void setStock_code(String stock_code) {
		this.stock_code = stock_code;
	}

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

}
