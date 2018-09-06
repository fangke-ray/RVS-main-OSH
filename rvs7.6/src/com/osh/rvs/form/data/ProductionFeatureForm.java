package com.osh.rvs.form.data;

import org.apache.struts.action.ActionForm;

import com.osh.rvs.common.RvsUtils;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;
import framework.huiqing.common.util.CodeListUtils;

public class ProductionFeatureForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6251321026467233690L;

	private String material_id;
	private String position_id;
	private String position_name;
	private String process_code;
	
	@BeanField(title="开始时间", name="action_time", type=FieldType.DateTime)
	private String action_time;
	@BeanField(title="完成时间", name="finish_time", type=FieldType.DateTime)
	private String finish_time;
	
	private String operator_id;
	private String operator_name;
	
	
	@BeanField(title="花费时间", name="use_seconds", type=FieldType.Integer)
	private String use_seconds;
	@BeanField(title="操作结果", name="operate_result", type=FieldType.Integer)
	private String operate_result;
	
	@BeanField(title="返工作业", name="rework", type=FieldType.Integer)
	private String rework;
	private String beforeRework;
	
	private String noRework; 
	private String onlyFinish;
	
	
	public String getNoRework() {
		return noRework;
	}

	public void setNoRework(String noRework) {
		this.noRework = noRework;
	}

	public String getOnlyFinish() {
		return onlyFinish;
	}

	public void setOnlyFinish(String onlyFinish) {
		this.onlyFinish = onlyFinish;
	}

	public String getBeforeRework() {
		return beforeRework;
	}

	public void setBeforeRework(String beforeRework) {
		this.beforeRework = beforeRework;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}
	public String getProcess_code() {
		return process_code;
	}
	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}
	public String getRework() {
		return rework;
	}
	public void setRework(String rework) {
		this.rework = rework;
	}
	public String getMaterial_id() {
		return material_id;
	}
	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}
	public String getPosition_id() {
		return position_id;
	}
	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}
	public String getPosition_name() {
		return position_name;
	}
	public void setPosition_name(String position_name) {
		this.position_name = position_name;
	}
	public String getAction_time() {
		return action_time;
	}

	public void setAction_time(String action_time) {
		this.action_time = action_time;
	}

	public String getFinish_time() {
		return finish_time;
	}

	public void setFinish_time(String finish_time) {
		this.finish_time = finish_time;
	}

	public String getOperator_id() {
		return operator_id;
	}
	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}
	public String getUse_seconds() {
		if (use_seconds != null) {
			return RvsUtils.formatMinutes(Integer.valueOf(use_seconds)/60);
		}
		return use_seconds;
	}
	public void setUse_seconds(String use_seconds) {
		this.use_seconds = use_seconds;
	}
	public String getOperate_result() {
		if (operate_result != null) {
			return CodeListUtils.getValue("material_operate_result", operate_result);
		}
		return operate_result;
	}
	public void setOperate_result(String operate_result) {
		this.operate_result = operate_result;
	}
	
	
	
}
