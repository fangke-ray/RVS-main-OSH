package com.osh.rvs.form.master;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 标准工时零件补正表单
 * @author Ray.G
 *
 */
public class StandardPartialAdditionForm extends ActionForm implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -3451842835076186476L;

	@BeanField(title = "零件 ID", name = "partial_id", type = FieldType.String, length = 11, primaryKey = true)
	private String partial_id;

	private String partial_code;

	@BeanField(title = "工位 ID", name = "position_id", type = FieldType.String, length = 11, primaryKey = true)
	private String position_id;

	private String process_code;

	@BeanField(title = "补正分钟", name = "addition", type = FieldType.Double, length = 5, scale = 1)
	private String addition;

	@BeanField(title = "修理型号 ID", name = "model_id", type = FieldType.String, length = 11)
	private String model_id;

	private String model_name;

	private String partial_name;

	@BeanField(title = "补正分钟", name = "addition", type = FieldType.Double, length = 5, scale = 1)
	private String base_addition;

	public String getPartial_id() {
		return partial_id;
	}

	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}

	public String getPartial_code() {
		return partial_code;
	}

	public void setPartial_code(String partial_code) {
		this.partial_code = partial_code;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getAddition() {
		return addition;
	}

	public void setAddition(String addition) {
		this.addition = addition;
	}

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
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

	public String getBase_addition() {
		return base_addition;
	}

	public void setBase_addition(String base_addition) {
		this.base_addition = base_addition;
	}
}
