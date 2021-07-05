package com.osh.rvs.bean.infect;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.bean.infect
 * @ClassName: ExternalCheckEntity
 * @Description: 检查机器校正Entity
 * @author lxb
 * @date 2014-9-6 下午4:30:51
 * 
 */
public class ExternalAdjustmentEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7341879928633021239L;

	private String devices_manage_id;// 点检表管理ID
	private Date checked_date;// 校验日期
	private Date available_end_date;// 过期日期
	private Integer effect_interval;// 有效期
	private BigDecimal check_cost;// 校验费用
	private Integer organization_type;// 校验单位
	private String institution_name;// 校验机构名称
	private Integer checking_flg;// 校验中
	private String comment;// 备注

	private String devices_type_id;// 设备工具品名ID
	private String manage_code;// 管理编号
	private String name;// 品名
	private String brand;// 厂商
	private String model_name;// 型号
	private String section_id;// 分发课室ID
	private String section_name;// 分发课室Name
	private String line_id;// 责任工程ID
	private String line_name;// 责任工程Name
	private String products_code;// 出厂编号
	private Date checked_date_start;// 校验日期开始
	private Date checked_date_end;// 校验日期结束
	private Date available_end_date_start;// 过期日期开始
	private Date available_end_date_end;// 过期日期结束
	private Integer isover;// 是否过期
	private Integer manage_level;// 管理等级
	private BigDecimal total_check_cost;//费用总计
	private Integer object_type;//类型
	private String tools_manage_id;//治具管理ID

	public String getDevices_manage_id() {
		return devices_manage_id;
	}

	public void setDevices_manage_id(String devices_manage_id) {
		this.devices_manage_id = devices_manage_id;
	}

	public Date getChecked_date() {
		return checked_date;
	}

	public void setChecked_date(Date checked_date) {
		this.checked_date = checked_date;
	}

	public Date getAvailable_end_date() {
		return available_end_date;
	}

	public void setAvailable_end_date(Date available_end_date) {
		this.available_end_date = available_end_date;
	}

	public Integer getEffect_interval() {
		return effect_interval;
	}

	public void setEffect_interval(Integer effect_interval) {
		this.effect_interval = effect_interval;
	}

	public BigDecimal getCheck_cost() {
		return check_cost;
	}

	public void setCheck_cost(BigDecimal check_cost) {
		this.check_cost = check_cost;
	}

	public Integer getOrganization_type() {
		return organization_type;
	}

	public void setOrganization_type(Integer organization_type) {
		this.organization_type = organization_type;
	}

	public String getInstitution_name() {
		return institution_name;
	}

	public void setInstitution_name(String institution_name) {
		this.institution_name = institution_name;
	}

	public Integer getChecking_flg() {
		return checking_flg;
	}

	public void setChecking_flg(Integer checking_flg) {
		this.checking_flg = checking_flg;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDevices_type_id() {
		return devices_type_id;
	}

	public void setDevices_type_id(String devices_type_id) {
		this.devices_type_id = devices_type_id;
	}

	public String getManage_code() {
		return manage_code;
	}

	public void setManage_code(String manage_code) {
		this.manage_code = manage_code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getSection_name() {
		return section_name;
	}

	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	public String getProducts_code() {
		return products_code;
	}

	public void setProducts_code(String products_code) {
		this.products_code = products_code;
	}

	public Date getChecked_date_start() {
		return checked_date_start;
	}

	public void setChecked_date_start(Date checked_date_start) {
		this.checked_date_start = checked_date_start;
	}

	public Date getChecked_date_end() {
		return checked_date_end;
	}

	public void setChecked_date_end(Date checked_date_end) {
		this.checked_date_end = checked_date_end;
	}

	public Date getAvailable_end_date_start() {
		return available_end_date_start;
	}

	public void setAvailable_end_date_start(Date available_end_date_start) {
		this.available_end_date_start = available_end_date_start;
	}

	public Date getAvailable_end_date_end() {
		return available_end_date_end;
	}

	public void setAvailable_end_date_end(Date available_end_date_end) {
		this.available_end_date_end = available_end_date_end;
	}

	public Integer getIsover() {
		return isover;
	}

	public void setIsover(Integer isover) {
		this.isover = isover;
	}

	public Integer getManage_level() {
		return manage_level;
	}

	public void setManage_level(Integer manage_level) {
		this.manage_level = manage_level;
	}

	public BigDecimal getTotal_check_cost() {
		return total_check_cost;
	}

	public void setTotal_check_cost(BigDecimal total_check_cost) {
		this.total_check_cost = total_check_cost;
	}

	public Integer getObject_type() {
		return object_type;
	}

	public void setObject_type(Integer object_type) {
		this.object_type = object_type;
	}

	public String getTools_manage_id() {
		return tools_manage_id;
	}

	public void setTools_manage_id(String tools_manage_id) {
		this.tools_manage_id = tools_manage_id;
	}

}
