package com.osh.rvs.form.partial;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 
 * @Title PremakePartialForm.java
 * @Project rvs
 * @Package com.osh.rvs.form.partial
 * @ClassName: PremakePartialForm
 * @Description: 零件预制
 * @author lxb
 * @date 2016-3-24 下午3:45:23
 */
public class PremakePartialForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7835221012320823999L;

	@BeanField(title = "零件ID", name = "partial_id", type = FieldType.String, notNull = true, length = 11)
	private String partial_id;

	@BeanField(title = "型号名称", name = "model_id", type = FieldType.String, notNull = true, length = 11)
	private String model_id;

	@BeanField(title = "标配零件", name = "standard_flg", type = FieldType.Integer, notNull = true, length = 1)
	private String standard_flg;

	@BeanField(title = "零件编码", name = "code", type = FieldType.String, notNull = true)
	private String code;

	@BeanField(title = "型号名称", name = "model_name", type = FieldType.String)
	private String model_name;

	@BeanField(title = "零件描述", name = "partial_name", type = FieldType.String)
	private String partial_name;

	@BeanField(title = "数量", name = "quantity", type = FieldType.UInteger, notNull = true, length = 2)
	private String quantity;

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

	public String getStandard_flg() {
		return standard_flg;
	}

	public void setStandard_flg(String standard_flg) {
		this.standard_flg = standard_flg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
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

}
