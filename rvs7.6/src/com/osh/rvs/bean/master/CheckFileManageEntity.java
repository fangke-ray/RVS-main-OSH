package com.osh.rvs.bean.master;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.bean.master
 * @ClassName: CheckFileManageEntity
 * @Description: 点检表管理Entity
 * @author lxb
 * @date 2014-8-11 上午11:38:42
 * 
 */
public class CheckFileManageEntity implements Serializable {

	public static final Integer ACCESS_PLACE_DAILY = 1;
	public static final Integer ACCESS_PLACE_REGULAR = 2;
	public static final Integer ACCESS_PLACE_BEFORE_USE = 9;

	public static final Integer CYCLE_TYPE_WEEK_OF_MONTH = 1;
	public static final Integer CYCLE_TYPE_MONTH_OF_PERIOD = 2;
	public static final Integer CYCLE_TYPE_HALF_OF_PERIOD = 3;

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8951561635607202996L;

	private String check_file_manage_id;// 点检表管理ID
	private String check_manage_code;// 点检表管理编号
	private String sheet_file_name;// 点检表文件
	private String devices_type_id;// 设备工具品名ID
	private Integer access_place;// 类型
	private Integer cycle_type;// 归档周期
	private Integer delete_flg;// 删除标记
	private String updated_by;// 最后更新人ID
	private Date updated_time;// 最后更新时间

	private String name;// 使用设备工具品名；
	private String update_name;// 最后更新人名称

	private String devices_manage_id;
	private String model_name;
	private String position_id;
	private String process_code;
	private String manage_code;
	private Integer filing_means;//归档方式
	private String specified_model_name;//特定机型

	public String getCheck_file_manage_id() {
		return check_file_manage_id;
	}

	public void setCheck_file_manage_id(String check_file_manage_id) {
		this.check_file_manage_id = check_file_manage_id;
	}

	public String getCheck_manage_code() {
		return check_manage_code;
	}

	public void setCheck_manage_code(String check_manage_code) {
		this.check_manage_code = check_manage_code;
	}

	public String getSheet_file_name() {
		return sheet_file_name;
	}

	public void setSheet_file_name(String sheet_file_name) {
		this.sheet_file_name = sheet_file_name;
	}

	public String getDevices_type_id() {
		return devices_type_id;
	}

	public void setDevices_type_id(String devices_type_id) {
		this.devices_type_id = devices_type_id;
	}

	public Integer getAccess_place() {
		return access_place;
	}

	public void setAccess_place(Integer access_place) {
		this.access_place = access_place;
	}

	public Integer getCycle_type() {
		return cycle_type;
	}

	public void setCycle_type(Integer cycle_type) {
		this.cycle_type = cycle_type;
	}

	public Integer getDelete_flg() {
		return delete_flg;
	}

	public void setDelete_flg(Integer delete_flg) {
		this.delete_flg = delete_flg;
	}

	public String getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}

	public Date getUpdated_time() {
		return updated_time;
	}

	public void setUpdated_time(Date updated_time) {
		this.updated_time = updated_time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUpdate_name() {
		return update_name;
	}

	public void setUpdate_name(String update_name) {
		this.update_name = update_name;
	}

	public String getDevices_manage_id() {
		return devices_manage_id;
	}

	public void setDevices_manage_id(String devices_manage_id) {
		this.devices_manage_id = devices_manage_id;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
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

	public String getManage_code() {
		return manage_code;
	}

	public void setManage_code(String manage_code) {
		this.manage_code = manage_code;
	}

	public Integer getFiling_means() {
		return filing_means;
	}

	public void setFiling_means(Integer filing_means) {
		this.filing_means = filing_means;
	}

	public String getSpecified_model_name() {
		return specified_model_name;
	}

	public void setSpecified_model_name(String specified_model_name) {
		this.specified_model_name = specified_model_name;
	}

	
}
