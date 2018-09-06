package com.osh.rvs.bean.data;

import java.io.Serializable;
import java.util.Date;

public class QaResultEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2351846339732435646L;

	private String process_code;//工位
	
	private String category_id;// 维修对象机种
	private String section_id;// 维修课室
	private Date reception_time_start;// 受理日期开始
	private Date reception_time_end;// 受理日期结束
	private Date complete_date_start;// 品保通过日期开始
	private Date complete_date_end;// 品保通过日期结束
	private Date scheduled_date_start;// 纳期开始日期
	private Date scheduled_date_end;// 纳期结束日期
	private String model_id;// 维修对象型号ID
	private String operator_id;// 出检人员ID
	private String sorc_no;// SORC No.
	private String serial_no;// 机身号
	private Date reception_time;// 受理日期 时间段
	private Date agreed_date;// 同意日期
	private String model_name;// 维修对象型号Name
	private Date finish_time;// 品保日期
	private Integer operate_result;// 品保结果
	private String operation_name;// 出检人员姓名
	private String fix_type;// 修理分类
	private String service_repair_flg;// 返修分类
	private String direct_flg;// 直送
	private String material_id;//维修对象ID

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public Date getReception_time_start() {
		return reception_time_start;
	}

	public void setReception_time_start(Date reception_time_start) {
		this.reception_time_start = reception_time_start;
	}

	public Date getReception_time_end() {
		return reception_time_end;
	}

	public void setReception_time_end(Date reception_time_end) {
		this.reception_time_end = reception_time_end;
	}

	public Date getComplete_date_start() {
		return complete_date_start;
	}

	public void setComplete_date_start(Date complete_date_start) {
		this.complete_date_start = complete_date_start;
	}

	public Date getComplete_date_end() {
		return complete_date_end;
	}

	public void setComplete_date_end(Date complete_date_end) {
		this.complete_date_end = complete_date_end;
	}

	public Date getScheduled_date_start() {
		return scheduled_date_start;
	}

	public void setScheduled_date_start(Date scheduled_date_start) {
		this.scheduled_date_start = scheduled_date_start;
	}

	public Date getScheduled_date_end() {
		return scheduled_date_end;
	}

	public void setScheduled_date_end(Date scheduled_date_end) {
		this.scheduled_date_end = scheduled_date_end;
	}

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public Date getReception_time() {
		return reception_time;
	}

	public void setReception_time(Date reception_time) {
		this.reception_time = reception_time;
	}

	public Date getAgreed_date() {
		return agreed_date;
	}

	public void setAgreed_date(Date agreed_date) {
		this.agreed_date = agreed_date;
	}

	public Date getFinish_time() {
		return finish_time;
	}

	public void setFinish_time(Date finish_time) {
		this.finish_time = finish_time;
	}

	public String getSorc_no() {
		return sorc_no;
	}

	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
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

	public Integer getOperate_result() {
		return operate_result;
	}

	public void setOperate_result(Integer operate_result) {
		this.operate_result = operate_result;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getOperation_name() {
		return operation_name;
	}

	public void setOperation_name(String operation_name) {
		this.operation_name = operation_name;
	}

	public String getFix_type() {
		return fix_type;
	}

	public void setFix_type(String fix_type) {
		this.fix_type = fix_type;
	}

	public String getService_repair_flg() {
		return service_repair_flg;
	}

	public void setService_repair_flg(String service_repair_flg) {
		this.service_repair_flg = service_repair_flg;
	}

	public String getDirect_flg() {
		return direct_flg;
	}

	public void setDirect_flg(String direct_flg) {
		this.direct_flg = direct_flg;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}
}
