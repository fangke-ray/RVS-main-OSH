package com.osh.rvs.bean.infect;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.bean.infect
 * @ClassName: DeviceRegularlyCheckResultEntity
 * @Description: 设备工具定期点检Entity
 * @author lxb
 * @date 2014-8-19 上午9:29:28
 * 
 */
public class DeviceRegularlyCheckResultEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7634360545086838309L;

	private String manage_id;// 管理ID
	private String check_file_manage_id;// 点检表管理ID
	private Integer item_seq;// 项目序号
	private String operator_id;// 点检人员ID
	private Date check_confirm_time;// 点检时间
	private BigDecimal digit;// 数值
	private Integer checked_status;// 结果

	private String manage_code;// 管理编号
	private String name;// 品名
	private String model_name;// 型号
	private String section_id;// 分发课室ID
	private String section_name;// 分发课室名称
	private String responsible_line_id;// 责任工程ID
	private String responsible_line_name;// 责任工程名称
	private String responsible_position_id;// 责任工位ID
	private String responsible_position_name;// 责任工位名称
	private String responsible_operator_id;// 责任人员ID
	private String responsible_operator_name;// 责任人员名称

	private Integer janurary_checked_status;// 一月结果
	private Integer february_checked_status;// 二月结果
	private Integer march_checked_status;// 三月结果
	private Integer april_checked_status;// 四月结果
	private Integer may_checked_status;// 五月结果
	private Integer june_checked_status;// 六月结果
	private Integer july_checked_status;// 七月结果
	private Integer august_checked_status;// 八月结果
	private Integer september_checked_status;// 九月结果
	private Integer october_checked_status;// 十月结果
	private Integer november_checked_status;// 十一月结果
	private Integer december_checked_status;// 十二月结果

	private Integer upper_half_year_status;// 上半年结果
	private Integer lower_half_year_status;// 下半年结果
	
	private Integer one_week_status;
	private Integer two_week_status;
	private Integer three_week_status;
	private Integer four_week_status;
	private Integer five_week_status;
	private Integer six_week_status;
	

	private Date date_start;//
	private Date date_end;//
	
	private Integer type;//详细画面类型

	private Integer interval;
	
	private String devices_type_id;//设备工具品名ID
	private Integer cycle_type;//归档周期

	public String getManage_id() {
		return manage_id;
	}

	public void setManage_id(String manage_id) {
		this.manage_id = manage_id;
	}

	public String getCheck_file_manage_id() {
		return check_file_manage_id;
	}

	public void setCheck_file_manage_id(String check_file_manage_id) {
		this.check_file_manage_id = check_file_manage_id;
	}

	public Integer getItem_seq() {
		return item_seq;
	}

	public void setItem_seq(Integer item_seq) {
		this.item_seq = item_seq;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public Date getCheck_confirm_time() {
		return check_confirm_time;
	}

	public void setCheck_confirm_time(Date check_confirm_time) {
		this.check_confirm_time = check_confirm_time;
	}

	public BigDecimal getDigit() {
		return digit;
	}

	public void setDigit(BigDecimal digit) {
		this.digit = digit;
	}

	public Integer getChecked_status() {
		return checked_status;
	}

	public void setChecked_status(Integer checked_status) {
		this.checked_status = checked_status;
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

	public String getResponsible_line_id() {
		return responsible_line_id;
	}

	public void setResponsible_line_id(String responsible_line_id) {
		this.responsible_line_id = responsible_line_id;
	}

	public String getResponsible_line_name() {
		return responsible_line_name;
	}

	public void setResponsible_line_name(String responsible_line_name) {
		this.responsible_line_name = responsible_line_name;
	}

	public String getResponsible_position_id() {
		return responsible_position_id;
	}

	public void setResponsible_position_id(String responsible_position_id) {
		this.responsible_position_id = responsible_position_id;
	}

	public String getResponsible_position_name() {
		return responsible_position_name;
	}

	public void setResponsible_position_name(String responsible_position_name) {
		this.responsible_position_name = responsible_position_name;
	}

	public String getResponsible_operator_id() {
		return responsible_operator_id;
	}

	public void setResponsible_operator_id(String responsible_operator_id) {
		this.responsible_operator_id = responsible_operator_id;
	}

	public String getResponsible_operator_name() {
		return responsible_operator_name;
	}

	public void setResponsible_operator_name(String responsible_operator_name) {
		this.responsible_operator_name = responsible_operator_name;
	}

	public Date getDate_start() {
		return date_start;
	}

	public void setDate_start(Date date_start) {
		this.date_start = date_start;
	}

	public Integer getJanurary_checked_status() {
		return janurary_checked_status;
	}

	public void setJanurary_checked_status(Integer janurary_checked_status) {
		this.janurary_checked_status = janurary_checked_status;
	}

	public Integer getFebruary_checked_status() {
		return february_checked_status;
	}

	public void setFebruary_checked_status(Integer february_checked_status) {
		this.february_checked_status = february_checked_status;
	}

	public Integer getMarch_checked_status() {
		return march_checked_status;
	}

	public void setMarch_checked_status(Integer march_checked_status) {
		this.march_checked_status = march_checked_status;
	}

	public Integer getApril_checked_status() {
		return april_checked_status;
	}

	public void setApril_checked_status(Integer april_checked_status) {
		this.april_checked_status = april_checked_status;
	}

	public Integer getMay_checked_status() {
		return may_checked_status;
	}

	public void setMay_checked_status(Integer may_checked_status) {
		this.may_checked_status = may_checked_status;
	}

	public Integer getJune_checked_status() {
		return june_checked_status;
	}

	public void setJune_checked_status(Integer june_checked_status) {
		this.june_checked_status = june_checked_status;
	}

	public Integer getJuly_checked_status() {
		return july_checked_status;
	}

	public void setJuly_checked_status(Integer july_checked_status) {
		this.july_checked_status = july_checked_status;
	}

	public Integer getAugust_checked_status() {
		return august_checked_status;
	}

	public void setAugust_checked_status(Integer august_checked_status) {
		this.august_checked_status = august_checked_status;
	}

	public Integer getSeptember_checked_status() {
		return september_checked_status;
	}

	public void setSeptember_checked_status(Integer september_checked_status) {
		this.september_checked_status = september_checked_status;
	}

	public Integer getOctober_checked_status() {
		return october_checked_status;
	}

	public void setOctober_checked_status(Integer october_checked_status) {
		this.october_checked_status = october_checked_status;
	}

	public Integer getNovember_checked_status() {
		return november_checked_status;
	}

	public void setNovember_checked_status(Integer november_checked_status) {
		this.november_checked_status = november_checked_status;
	}

	public Integer getDecember_checked_status() {
		return december_checked_status;
	}

	public void setDecember_checked_status(Integer december_checked_status) {
		this.december_checked_status = december_checked_status;
	}

	public Integer getUpper_half_year_status() {
		return upper_half_year_status;
	}

	public void setUpper_half_year_status(Integer upper_half_year_status) {
		this.upper_half_year_status = upper_half_year_status;
	}

	public Integer getLower_half_year_status() {
		return lower_half_year_status;
	}

	public void setLower_half_year_status(Integer lower_half_year_status) {
		this.lower_half_year_status = lower_half_year_status;
	}

	public Integer getInterval() {
		return interval;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	public Date getDate_end() {
		return date_end;
	}

	public void setDate_end(Date date_end) {
		this.date_end = date_end;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getOne_week_status() {
		return one_week_status;
	}

	public void setOne_week_status(Integer one_week_status) {
		this.one_week_status = one_week_status;
	}

	public Integer getTwo_week_status() {
		return two_week_status;
	}

	public void setTwo_week_status(Integer two_week_status) {
		this.two_week_status = two_week_status;
	}

	public Integer getThree_week_status() {
		return three_week_status;
	}

	public void setThree_week_status(Integer three_week_status) {
		this.three_week_status = three_week_status;
	}

	public Integer getFour_week_status() {
		return four_week_status;
	}

	public void setFour_week_status(Integer four_week_status) {
		this.four_week_status = four_week_status;
	}

	public Integer getFive_week_status() {
		return five_week_status;
	}

	public void setFive_week_status(Integer five_week_status) {
		this.five_week_status = five_week_status;
	}

	public Integer getSix_week_status() {
		return six_week_status;
	}

	public void setSix_week_status(Integer six_week_status) {
		this.six_week_status = six_week_status;
	}

	public String getDevices_type_id() {
		return devices_type_id;
	}

	public void setDevices_type_id(String devices_type_id) {
		this.devices_type_id = devices_type_id;
	}

	public Integer getCycle_type() {
		return cycle_type;
	}

	public void setCycle_type(Integer cycle_type) {
		this.cycle_type = cycle_type;
	}

	

}
