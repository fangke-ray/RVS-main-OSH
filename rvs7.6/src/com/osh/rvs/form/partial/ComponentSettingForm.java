package com.osh.rvs.form.partial;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;

public class ComponentSettingForm extends ActionForm {

	private static final long serialVersionUID = 2413471756814818982L;

	/** 型号 ID */
	@BeanField(title = "型号 ID", name = "model_id", length = 11, notNull = true)
	private String model_id;
	private String model_name;
	
	/** 组件 ID */
	@BeanField(title = "组件 ID", name = "component_partial_id", length = 11, notNull = true)
	private String component_partial_id;
	
	/** 识别代号 */
	@BeanField(title = "识别代号", name = "identify_code", length = 2, notNull = true)
	private String identify_code;

	/** 安全库存*/
	@BeanField(title = "安全库存", name = "safety_lever", length = 3)
	private String safety_lever;
	
	/** 机种*/
	@BeanField(title = "机种", name = "category_name")
	private String category_name;
	
	/** 组件代码 */
	@BeanField(title = "组件代码", name = "component_code")
	private String component_code;
	
	/** 子零件代码 */
	@BeanField(title = "子零件代码", name = "partial_code")
	private String partial_code;
	
	/** 子零件待入库数 */
	@BeanField(title = "子零件待入库数", name = "cnt_partial_step0")
	private String cnt_partial_step0;
	
	/** 子零件已入库数 */
	@BeanField(title = "子零件已入库数", name = "cnt_partial_step1")
	private String cnt_partial_step1;
	
	/** 组装中数 */
	@BeanField(title = "组装中数", name = "cnt_partial_step2")
	private String cnt_partial_step2;
	
	/** 组装完成数 */
	@BeanField(title = "组装完成数", name = "cnt_partial_step3")
	private String cnt_partial_step3;

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

	public String getComponent_partial_id() {
		return component_partial_id;
	}

	public void setComponent_partial_id(String component_partial_id) {
		this.component_partial_id = component_partial_id;
	}

	public String getIdentify_code() {
		return identify_code;
	}

	public void setIdentify_code(String identify_code) {
		this.identify_code = identify_code;
	}

	public String getSafety_lever() {
		return safety_lever;
	}

	public void setSafety_lever(String safety_lever) {
		this.safety_lever = safety_lever;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getComponent_code() {
		return component_code;
	}

	public void setComponent_code(String component_code) {
		this.component_code = component_code;
	}

	public String getPartial_code() {
		return partial_code;
	}

	public void setPartial_code(String partial_code) {
		this.partial_code = partial_code;
	}

	public String getCnt_partial_step0() {
		return cnt_partial_step0;
	}

	public void setCnt_partial_step0(String cnt_partial_step0) {
		this.cnt_partial_step0 = cnt_partial_step0;
	}

	public String getCnt_partial_step1() {
		return cnt_partial_step1;
	}

	public void setCnt_partial_step1(String cnt_partial_step1) {
		this.cnt_partial_step1 = cnt_partial_step1;
	}

	public String getCnt_partial_step2() {
		return cnt_partial_step2;
	}

	public void setCnt_partial_step2(String cnt_partial_step2) {
		this.cnt_partial_step2 = cnt_partial_step2;
	}

	public String getCnt_partial_step3() {
		return cnt_partial_step3;
	}

	public void setCnt_partial_step3(String cnt_partial_step3) {
		this.cnt_partial_step3 = cnt_partial_step3;
	}
	
}
