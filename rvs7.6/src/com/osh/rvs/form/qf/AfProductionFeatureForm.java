package com.osh.rvs.form.qf;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;

/**
 * 间接人员作业信息
 * 
 * @author liuxb
 * 
 */
public class AfProductionFeatureForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1769146470055844433L;

	@BeanField(title = "KEY", name = "af_pf_key", type = FieldType.String, length = 11, notNull = true)
	private String af_pf_key;

	@BeanField(title = "作业内容", name = "production_type", type = FieldType.Integer, length = 3, notNull = true)
	private String production_type;

	@BeanField(title = "操作者 ID", name = "operator_id", type = FieldType.String, length = 11, notNull = true)
	private String operator_id;

	@BeanField(title = "处理开始时间", name = "action_time", type = FieldType.DateTime, notNull = true)
	private String action_time;

	@BeanField(title = "处理结束时间", name = "finish_time", type = FieldType.DateTime)
	private String finish_time;

	private String production_type_name;

	public String getAf_pf_key() {
		return af_pf_key;
	}

	public void setAf_pf_key(String af_pf_key) {
		this.af_pf_key = af_pf_key;
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

	public String getProduction_type_name() {
		if (!CommonStringUtil.isEmpty(production_type)) {
			return CodeListUtils.getValue("qf_production_type", production_type);
		}
		return production_type_name;
	}

	public void setProduction_type_name(String production_type_name) {
		this.production_type_name = production_type_name;
	}

}
