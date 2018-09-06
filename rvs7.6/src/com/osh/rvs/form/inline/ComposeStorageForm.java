package com.osh.rvs.form.inline;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class ComposeStorageForm extends ActionForm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6948289084903181749L;

	@BeanField(title = "维修对象ID", name = "material_id", length = 11)
	private String material_id;// 维修对象ID

	@BeanField(title = "维修对象机种ID", name = "category_id")
	private String category_id;// 维修对象机种ID

	@BeanField(title = "维修对象机种Name", name = "category_name")
	private String category_name;// 维修对象机种Name

	@BeanField(title = "修理单号", name = "sorc_no")
	private String sorc_no;// SORC No.

	@BeanField(title = "机身号", name = "serial_no")
	private String serial_no;// 机身号

	@BeanField(title = "分配课室ID", name = "section_id", length = 11)
	private String section_id;// 分配课室ID

	@BeanField(title = "分配课室Name", name = "section_name")
	private String section_name;// 分配课室Name

	@BeanField(title = "纳期", name = "scheduled_date", type = FieldType.Date)
	private String scheduled_date;// 纳期

	@BeanField(title = "纳期开始", name = "scheduled_date_start", type = FieldType.Date)
	private String scheduled_date_start;// 纳期开始

	@BeanField(title = "纳期结束", name = "scheduled_date_end", type = FieldType.Date)
	private String scheduled_date_end;// 纳期结束

	@BeanField(title = "入库预定日", name = "arrival_plan_date", type = FieldType.Date)
	private String arrival_plan_date;// 入库预定日

	@BeanField(title = "入库预定日开始", name = "arrival_plan_date_start", type = FieldType.Date)
	private String arrival_plan_date_start;// 入库预定日开始

	@BeanField(title = "入库预定日结束", name = "arrival_plan_date_end", type = FieldType.Date)
	private String arrival_plan_date_end;// 入库预定日结束

	@BeanField(title = "BO", name = "bo_flg", type = FieldType.Integer)
	private String bo_flg;// BO

	@BeanField(title = "总组出货(产出)安排", name = "com_scheduled_date", type = FieldType.Date)
	private String com_scheduled_date;// 总组出货(产出)安排

	@BeanField(title = "总组出货(产出)安排开始", name = "com_scheduled_date_start", type = FieldType.Date)
	private String com_scheduled_date_start;// 总组出货(产出)安排开始

	@BeanField(title = "总组出货(产出)安排结束", name = "com_scheduled_date_end", type = FieldType.Date)
	private String com_scheduled_date_end;// 总组出货(产出)安排结束

	@BeanField(title = "分解产出日", name = "dec_finish_date", type = FieldType.Date)
	private String dec_finish_date;// 分解产出日

	@BeanField(title = "NS产出日", name = "ns_finish_date", type = FieldType.Date)
	private String ns_finish_date;// NS产出日

	@BeanField(title = "零件缺失信息", name = "bo_contents", length = 200)
	private String bo_contents;// 零件缺失信息

	@BeanField(title = "库位", name = "case_code", length = 4)
	private String case_code;// 库位

	@BeanField(title = "总组库位货架", name = "shelf_name", length = 10)
	private String shelf_name;// 总组库位货架

	@BeanField(title = "存放对象ID", name = "goods_id", length = 11)
	private String goods_id;// 存放对象ID

	@BeanField(title = "扫描码", name = "scan_code", length = 6)
	private String scan_code;// 扫描码

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getSorc_no() {
		return sorc_no;
	}

	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
	}

	public String getSerial_no() {
		return serial_no;
	}

	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
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

	public String getScheduled_date() {
		return scheduled_date;
	}

	public void setScheduled_date(String scheduled_date) {
		this.scheduled_date = scheduled_date;
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

	public String getArrival_plan_date() {
		return arrival_plan_date;
	}

	public void setArrival_plan_date(String arrival_plan_date) {
		this.arrival_plan_date = arrival_plan_date;
	}

	public String getArrival_plan_date_start() {
		return arrival_plan_date_start;
	}

	public void setArrival_plan_date_start(String arrival_plan_date_start) {
		this.arrival_plan_date_start = arrival_plan_date_start;
	}

	public String getArrival_plan_date_end() {
		return arrival_plan_date_end;
	}

	public void setArrival_plan_date_end(String arrival_plan_date_end) {
		this.arrival_plan_date_end = arrival_plan_date_end;
	}

	public String getBo_flg() {
		return bo_flg;
	}

	public void setBo_flg(String bo_flg) {
		this.bo_flg = bo_flg;
	}

	public String getCom_scheduled_date() {
		return com_scheduled_date;
	}

	public void setCom_scheduled_date(String com_scheduled_date) {
		this.com_scheduled_date = com_scheduled_date;
	}

	public String getCom_scheduled_date_start() {
		return com_scheduled_date_start;
	}

	public void setCom_scheduled_date_start(String com_scheduled_date_start) {
		this.com_scheduled_date_start = com_scheduled_date_start;
	}

	public String getCom_scheduled_date_end() {
		return com_scheduled_date_end;
	}

	public void setCom_scheduled_date_end(String com_scheduled_date_end) {
		this.com_scheduled_date_end = com_scheduled_date_end;
	}

	public String getDec_finish_date() {
		return dec_finish_date;
	}

	public void setDec_finish_date(String dec_finish_date) {
		this.dec_finish_date = dec_finish_date;
	}

	public String getNs_finish_date() {
		return ns_finish_date;
	}

	public void setNs_finish_date(String ns_finish_date) {
		this.ns_finish_date = ns_finish_date;
	}

	public String getBo_contents() {
		return bo_contents;
	}

	public void setBo_contents(String bo_contents) {
		this.bo_contents = bo_contents;
	}

	public String getCase_code() {
		return case_code;
	}

	public void setCase_code(String case_code) {
		this.case_code = case_code;
	}

	public String getShelf_name() {
		return shelf_name;
	}

	public void setShelf_name(String shelf_name) {
		this.shelf_name = shelf_name;
	}

	public String getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}

	public String getScan_code() {
		return scan_code;
	}

	public void setScan_code(String scan_code) {
		this.scan_code = scan_code;
	}

}
