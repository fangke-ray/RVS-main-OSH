package com.osh.rvs.bean.infect;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class DailyCheckResultEntity implements Serializable {

	/**
	 * 日常点检结果
	 */
	private static final long serialVersionUID = -55614080202302096L;
	//当前月的第一天
	private String firstDate;
	//当前月的最后一天
	private String lastDate;
	//合格台数
	private Integer qualified;
	//不合格台数
	private Integer unqualified;

	// 设备工具管理ID
	private String devices_manage_id;

	// 管理编号
	private String manage_code;

	// 品名
	private String name;

	// 设备工具品名ID
	private String devices_type_id;

	// 型号
	private String model_name;

	// 分发课室
	private String section_name;

	// 分发课室
	private String section_id;

	// 责任工程
	private String line_name;

	// 责任工程
	private String line_id;

	// 责任工位
	private String process_code;
	
	//工位名称
	private String position_name;

	// 责任工位
	private String position_id;

	// 管理员
	private String manager;

	// 管理员
	private String manager_operator_id;

	// 更新时间
	private Timestamp updated_time;

	// 点检日期
	private String check_confirm_time;

	// 点检状态
	private String checked_status;
	
	//点检人员
	private String operator_id;
	
	//点检人员
	private String operator;

	// 管理ID
	private String manage_id;

	// 点检表管理ID
	private String check_file_manage_id;

	// 项目序号
	private Integer item_seq;

	// 数值
	private BigDecimal digit;

	// 月天数
	private String one;
	private String two;
	private String three;
	private String four;
	private String five;
	private String six;
	private String seven;
	private String eight;
	private String nine;
	private String ten;
	private String eleven;
	private String twelve;
	private String thirteen;
	private String fourteen;
	private String fiveteen;
	private String sixteen;
	private String seventeen;
	private String eighteen;
	private String nineteen;
	private String twenty;
	private String twenty_one;
	private String twenty_two;
	private String twenty_three;
	private String twenty_four;
	private String twenty_five;
	private String twenty_six;
	private String twenty_seven;
	private String twenty_eight;
	private String twenty_nine;
	private String thirty;
	private String thirty_one;

	public String getPosition_name() {
		return position_name;
	}

	public void setPosition_name(String position_name) {
		this.position_name = position_name;
	}

	public String getFirstDate() {
		return firstDate;
	}

	public void setFirstDate(String firstDate) {
		this.firstDate = firstDate;
	}

	public String getLastDate() {
		return lastDate;
	}

	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Integer getQualified() {
		return qualified;
	}

	public void setQualified(Integer qualified) {
		this.qualified = qualified;
	}

	public Integer getUnqualified() {
		return unqualified;
	}

	public void setUnqualified(Integer unqualified) {
		this.unqualified = unqualified;
	}

	public void setFour(String four) {
		this.four = four;
	}

	public String getOne() {
		return one;
	}

	public void setOne(String one) {
		this.one = one;
	}

	public String getTwo() {
		return two;
	}

	public void setTwo(String two) {
		this.two = two;
	}

	public String getThree() {
		return three;
	}

	public void setThree(String three) {
		this.three = three;
	}

	public String getFour() {
		return four;
	}

	public void Checked_status(String four) {
		this.four = four;
	}

	public String getFive() {
		return five;
	}

	public void setFive(String five) {
		this.five = five;
	}

	public String getSix() {
		return six;
	}

	public void setSix(String six) {
		this.six = six;
	}

	public String getSeven() {
		return seven;
	}

	public void setSeven(String seven) {
		this.seven = seven;
	}

	public String getEight() {
		return eight;
	}

	public void setEight(String eight) {
		this.eight = eight;
	}

	public String getNine() {
		return nine;
	}

	public void setNine(String nine) {
		this.nine = nine;
	}

	public String getTen() {
		return ten;
	}

	public void setTen(String ten) {
		this.ten = ten;
	}

	public String getEleven() {
		return eleven;
	}

	public void setEleven(String eleven) {
		this.eleven = eleven;
	}

	public String getTwelve() {
		return twelve;
	}

	public void setTwelve(String twelve) {
		this.twelve = twelve;
	}

	public String getThirteen() {
		return thirteen;
	}

	public void setThirteen(String thirteen) {
		this.thirteen = thirteen;
	}

	public String getFourteen() {
		return fourteen;
	}

	public void setFourteen(String fourteen) {
		this.fourteen = fourteen;
	}

	public String getFiveteen() {
		return fiveteen;
	}

	public void setFiveteen(String fiveteen) {
		this.fiveteen = fiveteen;
	}

	public String getSixteen() {
		return sixteen;
	}

	public void setSixteen(String sixteen) {
		this.sixteen = sixteen;
	}

	public String getSeventeen() {
		return seventeen;
	}

	public void setSeventeen(String seventeen) {
		this.seventeen = seventeen;
	}

	public String getEighteen() {
		return eighteen;
	}

	public void setEighteen(String eighteen) {
		this.eighteen = eighteen;
	}

	public String getNineteen() {
		return nineteen;
	}

	public void setNineteen(String nineteen) {
		this.nineteen = nineteen;
	}

	public String getTwenty() {
		return twenty;
	}

	public void setTwenty(String twenty) {
		this.twenty = twenty;
	}

	public String getTwenty_one() {
		return twenty_one;
	}

	public void setTwenty_one(String twenty_one) {
		this.twenty_one = twenty_one;
	}

	public String getTwenty_two() {
		return twenty_two;
	}

	public void setTwenty_two(String twenty_two) {
		this.twenty_two = twenty_two;
	}

	public String getTwenty_three() {
		return twenty_three;
	}

	public void setTwenty_three(String twenty_three) {
		this.twenty_three = twenty_three;
	}

	public String getTwenty_four() {
		return twenty_four;
	}

	public void setTwenty_four(String twenty_four) {
		this.twenty_four = twenty_four;
	}

	public String getTwenty_five() {
		return twenty_five;
	}

	public void setTwenty_five(String twenty_five) {
		this.twenty_five = twenty_five;
	}

	public String getTwenty_six() {
		return twenty_six;
	}

	public void setTwenty_six(String twenty_six) {
		this.twenty_six = twenty_six;
	}

	public String getTwenty_seven() {
		return twenty_seven;
	}

	public void setTwenty_seven(String twenty_seven) {
		this.twenty_seven = twenty_seven;
	}

	public String getTwenty_eight() {
		return twenty_eight;
	}

	public void setTwenty_eight(String twenty_eight) {
		this.twenty_eight = twenty_eight;
	}

	public String getTwenty_nine() {
		return twenty_nine;
	}

	public void setTwenty_nine(String twenty_nine) {
		this.twenty_nine = twenty_nine;
	}

	public String getThirty() {
		return thirty;
	}

	public void setThirty(String thirty) {
		this.thirty = thirty;
	}

	public String getThirty_one() {
		return thirty_one;
	}

	public void setThirty_one(String thirty_one) {
		this.thirty_one = thirty_one;
	}

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

	public BigDecimal getDigit() {
		return digit;
	}

	public void setDigit(BigDecimal digit) {
		this.digit = digit;
	}

	public String getDevices_manage_id() {
		return devices_manage_id;
	}

	public void setDevices_manage_id(String devices_manage_id) {
		this.devices_manage_id = devices_manage_id;
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

	public String getDevices_type_id() {
		return devices_type_id;
	}

	public void setDevices_type_id(String devices_type_id) {
		this.devices_type_id = devices_type_id;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getSection_name() {
		return section_name;
	}

	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getManager_operator_id() {
		return manager_operator_id;
	}

	public void setManager_operator_id(String manager_operator_id) {
		this.manager_operator_id = manager_operator_id;
	}

	public Timestamp getUpdated_time() {
		return updated_time;
	}

	public void setUpdated_time(Timestamp updated_time) {
		this.updated_time = updated_time;
	}

	public String getCheck_confirm_time() {
		return check_confirm_time;
	}

	public void setCheck_confirm_time(String check_confirm_time) {
		this.check_confirm_time = check_confirm_time;
	}

	public String getChecked_status() {
		return checked_status;
	}

	public void setChecked_status(String checked_status) {
		this.checked_status = checked_status;
	}

}
