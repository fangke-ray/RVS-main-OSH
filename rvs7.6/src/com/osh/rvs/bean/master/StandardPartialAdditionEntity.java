package com.osh.rvs.bean.master;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 标准工时零件补正实例对象
 * @author Ray.G
 *
 */
public class StandardPartialAdditionEntity implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3393843471097309265L;

	private String partial_id;

	private String partial_code;

	private String position_id;

	private String process_code;

	private BigDecimal addition;

	private String model_id;

	private String model_name;

	private String partial_name;

	private BigDecimal base_addition;

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

	public BigDecimal getAddition() {
		return addition;
	}

	public void setAddition(BigDecimal addition) {
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

	public BigDecimal getBase_addition() {
		return base_addition;
	}

	public void setBase_addition(BigDecimal base_addition) {
		this.base_addition = base_addition;
	}
}
