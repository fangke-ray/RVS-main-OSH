package com.osh.rvs.form.data;

import java.io.Serializable;
import org.apache.struts.action.ActionForm;
import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/*
 * 完成维修一览Form
 */
public class QaResultForm extends ActionForm implements Serializable {
	private static final long serialVersionUID = -2050360496408904327L;

	@BeanField(title = "工位", name = "process_code")
	private String process_code;
	
	@BeanField(title = "维修对象机种", name = "category_id")
	private String category_id;// 维修对象机种

	@BeanField(title = "维修课室", name = "section_id")
	private String section_id;// 维修课室

	@BeanField(type = FieldType.Date, name = "reception_time_start", title = "受理日期开始")
	private String reception_time_start;// 受理日期开始

	@BeanField(title = "受理日期结束", name = "reception_time_end", type = FieldType.Date)
	private String reception_time_end;// 受理日期结束

	@BeanField(title = "品保通过日期开始", name = "complete_date_start", type = FieldType.Date)
	private String complete_date_start;// 品保通过日期开始

	@BeanField(title = "品保通过日期结束", name = "complete_date_end", type = FieldType.Date)
	private String complete_date_end;// 品保通过日期结束

	@BeanField(title = "纳期开始日期", name = "scheduled_date_start", type = FieldType.Date)
	private String scheduled_date_start;// 纳期开始日期

	@BeanField(title = "纳期结束日期", name = "scheduled_date_end", type = FieldType.Date)
	private String scheduled_date_end;// 纳期结束日期

	@BeanField(title = "维修对象型号", name = "model_id")
	private String model_id;// 维修对象型号ID

	@BeanField(title = "受理日期", name = "reception_time", type = FieldType.Date)
	private String reception_time;// 受理日期 时间段

	@BeanField(title = "同意日期", name = "agreed_date", type = FieldType.Date)
	private String agreed_date;// 同意日期

	@BeanField(title = "品保通过日期", name = "finish_time", type = FieldType.Date)
	private String finish_time;// 品保日期

	@BeanField(title = "修理单号", name = "sorc_no")
	private String sorc_no;// 修理单号

	@BeanField(title = "维修对象型号Name", name = "model_name")
	private String model_name;// 维修对象型号Name

	@BeanField(title = "机身号", name = "serial_no")
	private String serial_no;// 机身号

	@BeanField(title = "品保结果", name = "operate_result", type = FieldType.Integer)
	private String result;// 品保结果

	@BeanField(title = "出检人员ID", name = "operator_id")
	private String operator_id;// 出检人员ID

	@BeanField(title = "出检人员", name = "operation_name")
	private String operation_name;// 出检人员姓名

	@BeanField(title = "修理分类", name = "fix_type")
	private String fix_type;// 修理分类

	@BeanField(title = "返修分类", name = "service_repair_flg")
	private String service_repair_flg;// 返修分类

	@BeanField(title = "直送", name = "direct_flg")
	private String direct_flg;// 直送
	
	@BeanField(title="维修对象ID",name="material_id")
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

	public String getReception_time_start() {
		return reception_time_start;
	}

	public void setReception_time_start(String reception_time_start) {
		this.reception_time_start = reception_time_start;
	}

	public String getReception_time_end() {
		return reception_time_end;
	}

	public void setReception_time_end(String reception_time_end) {
		this.reception_time_end = reception_time_end;
	}

	public String getComplete_date_start() {
		return complete_date_start;
	}

	public void setComplete_date_start(String complete_date_start) {
		this.complete_date_start = complete_date_start;
	}

	public String getComplete_date_end() {
		return complete_date_end;
	}

	public void setComplete_date_end(String complete_date_end) {
		this.complete_date_end = complete_date_end;
	}

	public String getScheduled_date_start() {
		return scheduled_date_start;
	}

	public void setScheduled_date_start(String scheduled_date_start) {
		this.scheduled_date_start = scheduled_date_start;
	}

	public String getScheduled_date_end() {
		return scheduled_date_end;
	}

	public void setScheduled_date_end(String scheduled_date_end) {
		this.scheduled_date_end = scheduled_date_end;
	}

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getReception_time() {
		return reception_time;
	}

	public void setReception_time(String reception_time) {
		this.reception_time = reception_time;
	}

	public String getAgreed_date() {
		return agreed_date;
	}

	public void setAgreed_date(String agreed_date) {
		this.agreed_date = agreed_date;
	}

	public String getFinish_time() {
		return finish_time;
	}

	public void setFinish_time(String finish_time) {
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

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
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

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
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
