package com.osh.rvs.bean.inline;

import java.io.Serializable;
import java.util.Date;

public class ComposeStorageEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5715398731723600298L;

	private String material_id;// 维修对象ID
	private String category_id;// 维修对象机种ID
	private String category_name;// 维修对象机种Name
	private String sorc_no;// SORC No.
	private String serial_no;// 机身号
	private String section_id;// 分配课室ID
	private String section_name;// 分配课室Name
	private Date scheduled_date;// 纳期
	private Date scheduled_date_start;// 纳期开始
	private Date scheduled_date_end;// 纳期结束
	private Date arrival_plan_date;// 入库预定日
	private Date arrival_plan_date_start;// 入库预定日开始
	private Date arrival_plan_date_end;// 入库预定日结束
	private Integer bo_flg;// BO
	private Date com_scheduled_date;// 总组出货(产出)安排
	private Date com_scheduled_date_start;// 总组出货(产出)安排开始
	private Date com_scheduled_date_end;// 总组出货(产出)安排结束
	private Date dec_finish_date;// 分解产出日
	private Date ns_finish_date;// NS产出日
	private String bo_contents;// 零件缺失信息
	private String case_code;// 库位
	private String shelf_name;// 总组库位货架
	private String goods_id;// 存放对象ID
	private String scan_code;//扫描码
	private String line_id;
	private Integer px;
	private Integer spec_type;
	private Date refresh_time;
	
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

	public Date getScheduled_date() {
		return scheduled_date;
	}

	public void setScheduled_date(Date scheduled_date) {
		this.scheduled_date = scheduled_date;
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

	public Date getArrival_plan_date() {
		return arrival_plan_date;
	}

	public void setArrival_plan_date(Date arrival_plan_date) {
		this.arrival_plan_date = arrival_plan_date;
	}

	public Date getArrival_plan_date_start() {
		return arrival_plan_date_start;
	}

	public void setArrival_plan_date_start(Date arrival_plan_date_start) {
		this.arrival_plan_date_start = arrival_plan_date_start;
	}

	public Date getArrival_plan_date_end() {
		return arrival_plan_date_end;
	}

	public void setArrival_plan_date_end(Date arrival_plan_date_end) {
		this.arrival_plan_date_end = arrival_plan_date_end;
	}

	public Integer getBo_flg() {
		return bo_flg;
	}

	public void setBo_flg(Integer bo_flg) {
		this.bo_flg = bo_flg;
	}

	public Date getCom_scheduled_date() {
		return com_scheduled_date;
	}

	public void setCom_scheduled_date(Date com_scheduled_date) {
		this.com_scheduled_date = com_scheduled_date;
	}

	public Date getCom_scheduled_date_start() {
		return com_scheduled_date_start;
	}

	public void setCom_scheduled_date_start(Date com_scheduled_date_start) {
		this.com_scheduled_date_start = com_scheduled_date_start;
	}

	public Date getCom_scheduled_date_end() {
		return com_scheduled_date_end;
	}

	public void setCom_scheduled_date_end(Date com_scheduled_date_end) {
		this.com_scheduled_date_end = com_scheduled_date_end;
	}

	public Date getDec_finish_date() {
		return dec_finish_date;
	}

	public void setDec_finish_date(Date dec_finish_date) {
		this.dec_finish_date = dec_finish_date;
	}

	public Date getNs_finish_date() {
		return ns_finish_date;
	}

	public void setNs_finish_date(Date ns_finish_date) {
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

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public Integer getPx() {
		return px;
	}

	public void setPx(Integer px) {
		this.px = px;
	}

	public Integer getSpec_type() {
		return spec_type;
	}

	public void setSpec_type(Integer spec_type) {
		this.spec_type = spec_type;
	}

	public Date getRefresh_time() {
		return refresh_time;
	}

	public void setRefresh_time(Date refresh_time) {
		this.refresh_time = refresh_time;
	}

}
