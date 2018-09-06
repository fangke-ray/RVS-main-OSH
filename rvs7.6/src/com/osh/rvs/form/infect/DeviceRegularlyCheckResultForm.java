package com.osh.rvs.form.infect;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.form.infect
 * @ClassName: DeviceRegularlyCheckResultForm
 * @Description: 设备工具定期点检Form
 * @author lxb
 * @date 2014-8-19 上午9:44:55
 * 
 */
public class DeviceRegularlyCheckResultForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -71883525797543607L;

	@BeanField(title = "管理ID", name = "manage_id", type = FieldType.String, length = 11, primaryKey = true, notNull = true)
	private String manage_id;// 管理ID

	@BeanField(title = "点检表管理ID", name = "check_file_manage_id", type = FieldType.String, length = 11, primaryKey = true, notNull = true)
	private String check_file_manage_id;// 点检表管理ID

	@BeanField(title = "项目序号", name = "item_seq", type = FieldType.Integer, length = 2, primaryKey = true, notNull = true)
	private String item_seq;// 项目序号

	@BeanField(title = "点检人员ID", name = "operator_id", type = FieldType.String, length = 11, primaryKey = true, notNull = true)
	private String operator_id;// 点检人员ID

	@BeanField(title = "点检时间", name = "check_confirm_time", type = FieldType.DateTime, primaryKey = true, notNull = true)
	private String check_confirm_time;// 点检时间

	@BeanField(title = "数值", name = "digit", type = FieldType.Double, length = 9, scale = 3)
	private String digit;// 数值

	@BeanField(title = "结果", name = "checked_status", type = FieldType.Integer, length = 1, notNull = true)
	private String checked_status;// 结果

	@BeanField(title = "管理编号", name = "manage_code", type = FieldType.String)
	private String manage_code;// 管理编号

	@BeanField(title = "品名", name = "name", type = FieldType.String)
	private String name;// 品名

	@BeanField(title = "型号", name = "model_name", type = FieldType.String)
	private String model_name;// 型号

	@BeanField(title = " 分发课室ID", name = "section_id", type = FieldType.String)
	private String section_id;// 分发课室ID

	@BeanField(title = "分发课室名称", name = "section_name", type = FieldType.String)
	private String section_name;// 分发课室名称

	@BeanField(title = "责任工程ID", name = "responsible_line_id", type = FieldType.String)
	private String responsible_line_id;// 责任工程ID

	@BeanField(title = "责任工程名称", name = "responsible_line_name", type = FieldType.String)
	private String responsible_line_name;// 责任工程名称

	@BeanField(title = "责任工位ID", name = "responsible_position_id", type = FieldType.String)
	private String responsible_position_id;// 责任工位ID

	@BeanField(title = "责任工位名称", name = "responsible_position_name", type = FieldType.String)
	private String responsible_position_name;// 责任工位名称

	@BeanField(title = "责任人员ID", name = "responsible_operator_id", type = FieldType.String)
	private String responsible_operator_id;// 责任人员ID

	@BeanField(title = "责任人员名称", name = "responsible_operator_name", type = FieldType.String)
	private String responsible_operator_name;// 责任人员名称

	@BeanField(title = "一月结果", name = "janurary_checked_status", type = FieldType.Integer)
	private String janurary_checked_status;// 一月结果

	@BeanField(title = " 二月结果", name = "february_checked_status", type = FieldType.Integer)
	private String february_checked_status;// 二月结果

	@BeanField(title = "三月结果", name = "march_checked_status", type = FieldType.Integer)
	private String march_checked_status;// 三月结果

	@BeanField(title = "四月结果", name = "april_checked_status", type = FieldType.Integer)
	private String april_checked_status;// 四月结果

	@BeanField(title = "五月结果", name = "may_checked_status", type = FieldType.Integer)
	private String may_checked_status;// 五月结果

	@BeanField(title = "六月结果", name = "june_checked_status", type = FieldType.Integer)
	private String june_checked_status;// 六月结果

	@BeanField(title = "七月结果", name = "july_checked_status", type = FieldType.Integer)
	private String july_checked_status;// 七月结果

	@BeanField(title = "八月结果", name = "august_checked_status", type = FieldType.Integer)
	private String august_checked_status;// 八月结果

	@BeanField(title = "九月结果", name = "september_checked_status", type = FieldType.Integer)
	private String september_checked_status;// 九月结果

	@BeanField(title = "十月结果", name = "october_checked_status", type = FieldType.Integer)
	private String october_checked_status;// 十月结果

	@BeanField(title = "十一月结果", name = "november_checked_status", type = FieldType.Integer)
	private String november_checked_status;// 十一月结果

	@BeanField(title = "十二月结果", name = "december_checked_status", type = FieldType.Integer)
	private String december_checked_status;// 十二月结果

	@BeanField(title = "上半年结果", name = "upper_half_year_status", type = FieldType.Integer)
	private String upper_half_year_status;// 上半年结果

	@BeanField(title = "下半年结果", name = "lower_half_year_status", type = FieldType.Integer)
	private String lower_half_year_status;// 下半年结果

	@BeanField(title = "详细画面类型", name = "type", type = FieldType.Integer)
	private String type;// 详细画面类型
	
	@BeanField(title = "第一周", name = "one_week_status", type = FieldType.Integer)
	private String one_week_status;//第一周
	
	@BeanField(title = "第二周", name = "two_week_status", type = FieldType.Integer)
	private String two_week_status;//第二周
	
	@BeanField(title = "第三周", name = "three_week_status", type = FieldType.Integer)
	private String three_week_status;//第三周
	
	@BeanField(title = "第四周", name = "four_week_status", type = FieldType.Integer)
	private String four_week_status;//第四周
	
	@BeanField(title = "第五周", name = "five_week_status", type = FieldType.Integer)
	private String five_week_status;//第五周
	
	@BeanField(title = "第六周", name = "six_week_status", type = FieldType.Integer)
	private String six_week_status;//第六周
	
	@BeanField(title = "设备工具品名ID", name = "devices_type_id", type = FieldType.String)
	private String devices_type_id;//设备工具品名ID

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

	public String getItem_seq() {
		return item_seq;
	}

	public void setItem_seq(String item_seq) {
		this.item_seq = item_seq;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getCheck_confirm_time() {
		return check_confirm_time;
	}

	public void setCheck_confirm_time(String check_confirm_time) {
		this.check_confirm_time = check_confirm_time;
	}

	public String getDigit() {
		return digit;
	}

	public void setDigit(String digit) {
		this.digit = digit;
	}

	public String getChecked_status() {
		return checked_status;
	}

	public void setChecked_status(String checked_status) {
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

	public String getJanurary_checked_status() {
		return janurary_checked_status;
	}

	public void setJanurary_checked_status(String janurary_checked_status) {
		this.janurary_checked_status = janurary_checked_status;
	}

	public String getFebruary_checked_status() {
		return february_checked_status;
	}

	public void setFebruary_checked_status(String february_checked_status) {
		this.february_checked_status = february_checked_status;
	}

	public String getMarch_checked_status() {
		return march_checked_status;
	}

	public void setMarch_checked_status(String march_checked_status) {
		this.march_checked_status = march_checked_status;
	}

	public String getApril_checked_status() {
		return april_checked_status;
	}

	public void setApril_checked_status(String april_checked_status) {
		this.april_checked_status = april_checked_status;
	}

	public String getMay_checked_status() {
		return may_checked_status;
	}

	public void setMay_checked_status(String may_checked_status) {
		this.may_checked_status = may_checked_status;
	}

	public String getJune_checked_status() {
		return june_checked_status;
	}

	public void setJune_checked_status(String june_checked_status) {
		this.june_checked_status = june_checked_status;
	}

	public String getJuly_checked_status() {
		return july_checked_status;
	}

	public void setJuly_checked_status(String july_checked_status) {
		this.july_checked_status = july_checked_status;
	}

	public String getAugust_checked_status() {
		return august_checked_status;
	}

	public void setAugust_checked_status(String august_checked_status) {
		this.august_checked_status = august_checked_status;
	}

	public String getSeptember_checked_status() {
		return september_checked_status;
	}

	public void setSeptember_checked_status(String september_checked_status) {
		this.september_checked_status = september_checked_status;
	}

	public String getOctober_checked_status() {
		return october_checked_status;
	}

	public void setOctober_checked_status(String october_checked_status) {
		this.october_checked_status = october_checked_status;
	}

	public String getNovember_checked_status() {
		return november_checked_status;
	}

	public void setNovember_checked_status(String november_checked_status) {
		this.november_checked_status = november_checked_status;
	}

	public String getDecember_checked_status() {
		return december_checked_status;
	}

	public void setDecember_checked_status(String december_checked_status) {
		this.december_checked_status = december_checked_status;
	}

	public String getUpper_half_year_status() {
		return upper_half_year_status;
	}

	public void setUpper_half_year_status(String upper_half_year_status) {
		this.upper_half_year_status = upper_half_year_status;
	}

	public String getLower_half_year_status() {
		return lower_half_year_status;
	}

	public void setLower_half_year_status(String lower_half_year_status) {
		this.lower_half_year_status = lower_half_year_status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOne_week_status() {
		return one_week_status;
	}

	public void setOne_week_status(String one_week_status) {
		this.one_week_status = one_week_status;
	}

	public String getTwo_week_status() {
		return two_week_status;
	}

	public void setTwo_week_status(String two_week_status) {
		this.two_week_status = two_week_status;
	}

	public String getThree_week_status() {
		return three_week_status;
	}

	public void setThree_week_status(String three_week_status) {
		this.three_week_status = three_week_status;
	}

	public String getFour_week_status() {
		return four_week_status;
	}

	public void setFour_week_status(String four_week_status) {
		this.four_week_status = four_week_status;
	}

	public String getFive_week_status() {
		return five_week_status;
	}

	public void setFive_week_status(String five_week_status) {
		this.five_week_status = five_week_status;
	}

	public String getSix_week_status() {
		return six_week_status;
	}

	public void setSix_week_status(String six_week_status) {
		this.six_week_status = six_week_status;
	}

	public String getDevices_type_id() {
		return devices_type_id;
	}

	public void setDevices_type_id(String devices_type_id) {
		this.devices_type_id = devices_type_id;
	}

	

}
