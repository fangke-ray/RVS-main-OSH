package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.util.Date;

/**
 * 废弃零件整理
 * 
 * @Description
 * @author dell
 * @date 2019-12-26 下午3:02:26
 */
public class WastePartialArrangementEntity implements Serializable {
	private static final long serialVersionUID = 6045357183384067040L;

	/**
	 * 维修品ID
	 */
	private String material_id;

	/**
	 * 回收部分
	 */
	private Integer part;

	/**
	 * 收集操作者ID
	 */
	private String operator_id;

	/**
	 * 收集时间
	 */
	private Date collect_time;

	/**
	 * 废弃零件回收箱ID
	 */
	private String collect_case_id;

	/**
	 * 装箱编号
	 */
	private String case_code;

	/**
	 * 收集开始时间
	 */
	private Date collect_time_start;

	/**
	 * 收集结束时间
	 */
	private Date collect_time_end;

	/**
	 * 修理单号
	 */
	private String omr_notifi_no;

	/**
	 * 型号名称
	 */
	private String model_name;

	/**
	 * 型号ID
	 */
	private String model_id;

	/**
	 * 机身号
	 */
	private String serial_no;

	/**
	 * 等级
	 */
	private Integer level;

	/**
	 * 返修分类
	 */
	private Integer service_repair_flg;

	/**
	 * 收集操作者名称
	 */
	private String operator_name;

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public Integer getPart() {
		return part;
	}

	public void setPart(Integer part) {
		this.part = part;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public Date getCollect_time() {
		return collect_time;
	}

	public void setCollect_time(Date collect_time) {
		this.collect_time = collect_time;
	}

	public String getCollect_case_id() {
		return collect_case_id;
	}

	public void setCollect_case_id(String collect_case_id) {
		this.collect_case_id = collect_case_id;
	}

	public String getCase_code() {
		return case_code;
	}

	public void setCase_code(String case_code) {
		this.case_code = case_code;
	}

	public Date getCollect_time_start() {
		return collect_time_start;
	}

	public void setCollect_time_start(Date collect_time_start) {
		this.collect_time_start = collect_time_start;
	}

	public Date getCollect_time_end() {
		return collect_time_end;
	}

	public void setCollect_time_end(Date collect_time_end) {
		this.collect_time_end = collect_time_end;
	}

	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}

	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getSerial_no() {
		return serial_no;
	}

	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getService_repair_flg() {
		return service_repair_flg;
	}

	public void setService_repair_flg(Integer service_repair_flg) {
		this.service_repair_flg = service_repair_flg;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

}
