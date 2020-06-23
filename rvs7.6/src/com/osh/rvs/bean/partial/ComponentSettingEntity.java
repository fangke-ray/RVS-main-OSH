package com.osh.rvs.bean.partial;

import java.io.Serializable;

public class ComponentSettingEntity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -5960636192988879037L;

	/** 型号 ID */
	private String model_id;
	private String model_name;
	
	/** 组件 ID */
	private String component_partial_id;
	
	/** 识别代号 */
	private String identify_code;
	
	/** 安全库存*/
	private String safety_lever;
	
	/** 机种*/
	private String category_name;
	
	/** 组件代码 */
	private String component_code;
	
	/** 子零件代码 */
	private String partial_code;
	
	/** 子零件名称 */
	private String partial_name;
	
	/** 子零件待入库数 */
	private String cnt_partial_step0;
	
	/** 子零件已入库数 */
	private String cnt_partial_step1;
	
	/** 组装中数 */
	private String cnt_partial_step2;
	
	/** 组装完成数 */
	private String cnt_partial_step3;

	public String getPartial_name() {
		return partial_name;
	}

	public void setPartial_name(String partial_name) {
		this.partial_name = partial_name;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
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
