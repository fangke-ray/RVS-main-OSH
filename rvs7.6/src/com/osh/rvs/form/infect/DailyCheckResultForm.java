package com.osh.rvs.form.infect;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class DailyCheckResultForm extends ActionForm {

	/**
	 * 日常点检结果
	 */
	private static final long serialVersionUID = 5777122242850845512L;
	//合格台数
	@BeanField(title="合格台数",name="qualified",type=FieldType.Integer)
	private Integer qualified;
	
	//不合格台数
	@BeanField(title="不合格台数",name="unqualified",type=FieldType.Integer)
	private Integer unqualified;
	
	//月天数
	@BeanField(title="one",name="one",type=FieldType.String)
	private String one;
	@BeanField(title="two",name="two",type=FieldType.String)
	private String two;
	@BeanField(title="three",name="three",type=FieldType.String)
	private String three;
	@BeanField(title="four",name="four",type=FieldType.String)
	private String four;
	@BeanField(title="five",name="five",type=FieldType.String)
	private String five;
	@BeanField(title="six",name="six",type=FieldType.String)
	private String six;
	@BeanField(title="seven",name="seven",type=FieldType.String)
	private String seven;
	@BeanField(title="eight",name="eight",type=FieldType.String)
	private String eight;
	@BeanField(title="nine",name="nine",type=FieldType.String)
	private String nine;
	@BeanField(title="ten",name="ten",type=FieldType.String)
	private String ten;
	@BeanField(title="eleven",name="eleven",type=FieldType.String)
	private String eleven;
	@BeanField(title="twelve",name="twelve",type=FieldType.String)
	private String twelve;
	@BeanField(title="thirteen",name="thirteen",type=FieldType.String)
	private String thirteen;
	@BeanField(title="fourteen",name="fourteen",type=FieldType.String)
	private String fourteen;
	@BeanField(title="fiveteen",name="fiveteen",type=FieldType.String)
	private String fiveteen;
	@BeanField(title="sixteen",name="sixteen",type=FieldType.String)
	private String sixteen;
	@BeanField(title="seventeen",name="seventeen",type=FieldType.String)
	private String seventeen;
	@BeanField(title="eighteen",name="eighteen",type=FieldType.String)
	private String eighteen;
	@BeanField(title="nineteen",name="nineteen",type=FieldType.String)
	private String nineteen;
	@BeanField(title="twenty",name="twenty",type=FieldType.String)
	private String twenty;
	@BeanField(title="twenty_one",name="twenty_one",type=FieldType.String)
	private String twenty_one;
	@BeanField(title="twenty_two",name="twenty_two",type=FieldType.String)
	private String twenty_two;
	@BeanField(title="twenty_three",name="twenty_three",type=FieldType.String)
	private String twenty_three;
	@BeanField(title="twenty_four",name="twenty_four",type=FieldType.String)
	private String twenty_four;
	@BeanField(title="twenty_five",name="twenty_five",type=FieldType.String)
	private String twenty_five;
	@BeanField(title="twenty_six",name="twenty_six",type=FieldType.String)
	private String twenty_six;
	@BeanField(title="twenty_seven",name="twenty_seven",type=FieldType.String)
	private String twenty_seven;
	@BeanField(title="twenty_eight",name="twenty_eight",type=FieldType.String)
	private String twenty_eight;
	@BeanField(title="twenty_nine",name="twenty_nine",type=FieldType.String)
	private String twenty_nine;
	@BeanField(title="thirty",name="thirty",type=FieldType.String)
	private String thirty;
	@BeanField(title="thirty_one",name="thirty_one",type=FieldType.String)
	private String thirty_one;
	

	//设备工具管理ID
	@BeanField(title="设备工具管理ID",name="devices_manage_id",type=FieldType.String,length = 11)
	private String devices_manage_id; 

	//管理编号      
	@BeanField(title="管理编号",name="manage_code",type=FieldType.String, notNull = true)
	private String manage_code;      
	
	//品名   
	@BeanField(title="品名",name="name",type=FieldType.String,length = 32)
	private String name;     
	
	//设备工具品名ID
	@BeanField(title="设备工具品名ID",name="devices_type_id",type=FieldType.String,length = 11, notNull = true)
	private String devices_type_id;   
	
	//型号          
	@BeanField(title="型号",name="model_name",type=FieldType.String,length = 32)
	private String model_name;       
	
	//分发课室
	@BeanField(title="分发课室",name="section_name",type=FieldType.String)
	private String section_name;
	
	//分发课室   
	@BeanField(title="分发课室 ",name="section_id",type=FieldType.String,length = 11, notNull = true)
	private String section_id;   
	
	//责任工程
	@BeanField(title="责任工程",name="line_name",type=FieldType.String)
	private String line_name;
	
	//责任工程      
	@BeanField(title="责任工程",name="line_id",type=FieldType.String,length = 11)
	private String line_id;     
	
    //责任工位
	@BeanField(title="责任工位",name="process_code",type=FieldType.String)
	private String process_code;
	
	//责任工位名称
	@BeanField(title="责任工位名称",name="position_name",type=FieldType.String)
	private String position_name;
    
	//责任工位      
	@BeanField(title="责任工位",name="position_id",type=FieldType.String,length = 11)
	private String position_id;
	
	//管理员
	@BeanField(title="管理员",name="manager",type=FieldType.String)
	private String manager;
	
	//管理员        
	@BeanField(title="管理员",name="manager_operator_id",type=FieldType.String,length = 11, notNull = true)
	private String manager_operator_id;   
	          
	//点检人员
	@BeanField(title="点检人员",name="operator_id",type=FieldType.String,length = 11)
	private String operator_id;
	
	//点检人员
	@BeanField(title="点检人员",name="operator",type=FieldType.String)
	private String operator;

	//更新时间    
	@BeanField(title="更新时间",name="updated_time",type=FieldType.TimeStamp)
	private String updated_time; 
	
	//点检日期
	@BeanField(title="点检日期",name="check_confirm_time",type=FieldType.String)
	private String check_confirm_time ; 
	
	//点检状态
	@BeanField(title="点检状态",name="checked_status",type=FieldType.TimeStamp)
	private String checked_status;
	
	//管理ID
	@BeanField(title="管理ID",name="manage_id",type=FieldType.String,length = 11)
	private String manage_id;
	
	//点检表管理ID
	@BeanField(title="点检表管理ID",name="check_file_manage_id",type=FieldType.String,length = 11)
	private String check_file_manage_id;
	
	//项目序号
	@BeanField(title="项目序号",name="item_seq",type=FieldType.Integer)
	private String item_seq;

	//数值
	@BeanField(title="数值",name="digit",type=FieldType.Double)
	private String digit;

	
	public String getPosition_name() {
		return position_name;
	}

	public void setPosition_name(String position_name) {
		this.position_name = position_name;
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

	public void setFour(String four) {
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

	public String getItem_seq() {
		return item_seq;
	}

	public void setItem_seq(String item_seq) {
		this.item_seq = item_seq;
	}

	public String getDigit() {
		return digit;
	}

	public void setDigit(String digit) {
		this.digit = digit;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
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

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getUpdated_time() {
		return updated_time;
	}

	public void setUpdated_time(String updated_time) {
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
