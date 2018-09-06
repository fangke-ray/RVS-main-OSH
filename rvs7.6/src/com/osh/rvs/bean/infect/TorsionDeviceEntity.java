package com.osh.rvs.bean.infect;

import java.io.Serializable;
import java.math.BigDecimal;


public class TorsionDeviceEntity implements Serializable {

	/**
	 * 力矩设备画面
	 */
	private static final long serialVersionUID = 905332801112281967L;

	//设备管理编号ID
	private String manage_id;                 
	
	//设备管理编号
	private String manage_code;
	
	//力矩点检序号
	private String seq;      
	
	//规格力矩值
	private BigDecimal regular_torque;   
	
	//偏差值
	private BigDecimal deviation; 
	
	//使用的工程
	private String usage_point;   
	
	//点检设备精度
	private Integer hp_scale;   
	
	//点检力矩合格上限
	private BigDecimal regular_torque_upper_limit;
	
	//点检力矩合格下限
	private BigDecimal regular_torque_lower_limit;

	private String section_id;
	private String position_id;
	private String line_id;

	public String getManage_id() {
		return manage_id;
	}

	public void setManage_id(String manage_id) {
		this.manage_id = manage_id;
	}

	public String getManage_code() {
		return manage_code;
	}

	public void setManage_code(String manage_code) {
		this.manage_code = manage_code;
	}
	
	public BigDecimal getRegular_torque() {
		return regular_torque;
	}

	public void setRegular_torque(BigDecimal regular_torque) {
		this.regular_torque = regular_torque;
	}

	public BigDecimal getDeviation() {
		return deviation;
	}

	public void setDeviation(BigDecimal deviation) {
		this.deviation = deviation;
	}

	public String getUsage_point() {
		return usage_point;
	}

	public void setUsage_point(String usage_point) {
		this.usage_point = usage_point;
	}

	public Integer getHp_scale() {
		return hp_scale;
	}

	public void setHp_scale(Integer hp_scale) {
		this.hp_scale = hp_scale;
	}

	public BigDecimal getRegular_torque_upper_limit() {
		return regular_torque_upper_limit;
	}

	public void setRegular_torque_upper_limit(BigDecimal regular_torque_upper_limit) {
		this.regular_torque_upper_limit = regular_torque_upper_limit;
	}

	public BigDecimal getRegular_torque_lower_limit() {
		return regular_torque_lower_limit;
	}

	public void setRegular_torque_lower_limit(BigDecimal regular_torque_lower_limit) {
		this.regular_torque_lower_limit = regular_torque_lower_limit;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}
}
