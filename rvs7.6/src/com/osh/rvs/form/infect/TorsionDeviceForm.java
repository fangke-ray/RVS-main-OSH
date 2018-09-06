package com.osh.rvs.form.infect;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class TorsionDeviceForm extends ActionForm{

	/**
	 * 力矩设备画面
	 */
	private static final long serialVersionUID = 7631042544130299657L;

	//设备管理编号ID
	@BeanField(title="设备管理编号id",name="manage_id",type=FieldType.String)
	private String manage_id;                 
	
	//设备管理编号
	@BeanField(title="设备管理编号",name="manage_code",type=FieldType.String)
	private String manage_code;
	
	//力矩点检序号
	@BeanField(title="力矩点检序号",name="seq",type=FieldType.String)
	private String seq;      
	
	//规格力矩值
	@BeanField(title="规格力矩值",name="regular_torque",type=FieldType.UDouble,notNull=true)
	private String regular_torque;   
	
	//偏差值
	@BeanField(title="偏差值",name="deviation",type=FieldType.UDouble,notNull=true)
	private String deviation; 
	
	//使用的工程
	@BeanField(title="使用的工程",name="usage_point",type=FieldType.String)
	private String usage_point;   
	
	//点检设备精度
	@BeanField(title="点检设备精度",name="hp_scale",type=FieldType.Integer,notNull=true)
	private String hp_scale;   
	
	//点检力矩合格上限
	@BeanField(title="点检力矩合格上限",name="regular_torque_upper_limit",type=FieldType.UDouble)
	private String regular_torque_upper_limit;
	
	//点检力矩合格下限
	@BeanField(title="点检力矩合格下限",name="regular_torque_lower_limit",type=FieldType.UDouble)
	private String regular_torque_lower_limit;
	
	public String getManage_code() {
		return manage_code;
	}
	public void setManage_code(String manage_code) {
		this.manage_code = manage_code;
	}
	public String getManage_id() {
		return manage_id;
	}
	public void setManage_id(String manage_id) {
		this.manage_id = manage_id;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getRegular_torque() {
		return regular_torque;
	}
	public void setRegular_torque(String regular_torque) {
		this.regular_torque = regular_torque;
	}
	public String getDeviation() {
		return deviation;
	}
	public void setDeviation(String deviation) {
		this.deviation = deviation;
	}
	public String getUsage_point() {
		return usage_point;
	}
	public void setUsage_point(String usage_point) {
		this.usage_point = usage_point;
	}
	public String getHp_scale() {
		return hp_scale;
	}
	public void setHp_scale(String hp_scale) {
		this.hp_scale = hp_scale;
	}
	public String getRegular_torque_upper_limit() {
		return regular_torque_upper_limit;
	}
	public void setRegular_torque_upper_limit(String regular_torque_upper_limit) {
		this.regular_torque_upper_limit = regular_torque_upper_limit;
	}
	public String getRegular_torque_lower_limit() {
		return regular_torque_lower_limit;
	}
	public void setRegular_torque_lower_limit(String regular_torque_lower_limit) {
		this.regular_torque_lower_limit = regular_torque_lower_limit;
	}
}
