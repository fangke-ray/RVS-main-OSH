package com.osh.rvs.bean.report;

import java.io.Serializable;
import java.util.Date;

public class WaittingTimeReportEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3499770929141866847L;

	/** 维修对象ID **/
	private String material_id;

	/** 机种ID **/
	private String category_id;

	/** 机种名称 **/
	private String category_name;

	/** 修理单号 **/
	private String omr_notifi_no;

	/** 机身号 **/
	private String serial_no;

	/** 型号ID **/
	private String model_id;

	/** 型号名称 **/
	private String model_name;

	/** 等级 **/
	private Integer level;

	/** 课室ID **/
	private String section_id;

	/** 课室名称 **/
	private String section_name;

	/** 零件BO **/
	private Integer bo_flg;

	/** 加急 **/
	private Integer scheduled_expedited;

	/** 返工 **/
	private Integer rework;

	/** 直送 **/
	private Integer direct_flg;

	/** 完成开始时间 **/
	private Date outline_time_start;

	/** 完成结束时间 **/
	private Date outline_time_end;

	/** 零件订购开始时间 **/
	private Date order_date_start;

	/** 零件订购结束时间 **/
	private Date order_date_end;

	/** 零件发放开始时间 **/
	private Date arrival_date_start;

	/** 零件发放结束时间 **/
	private Date arrival_date_end;

	private Integer isExists;

	private Integer number;

	/** 投线时间 **/
	private Date inline_time;

	/** 产出时间（491完成） **/
	private Date outline_time;

	/** 等待零件发放时间 **/
	private Integer wait_partial_distrubute_time;

	/** 分解作业时间 **/
	private Integer desc_work_time;

	/** 分解等待时间 **/
	private Integer desc_wait_time;

	/** 分解烘干时间 **/
	private Integer desc_drying_time;

	/** NS作业时间 **/
	private Integer ns_work_time;

	/** NS等待时间 **/
	private Integer ns_wait_time;

	/** NS烘干时间 **/
	private Integer ns_drying_time;

	/** 总组等待时间 **/
	private Integer com_wait_time;

	/** 总组作业时间 **/
	private Integer com_work_time;

	/** 总组烘干时间 **/
	private Integer com_drying_time;

	/** 等待BO零件时间 **/
	private Integer wait_bo_partial_time;

	/** 异常中断时间 **/
	private Integer exception_break_time;

	/** 等待时间合计 **/
	private Integer total_wait_time;

	/** 总维修周期 **/
	private Integer total_work_time;

	private String svg1;

	private String svg2;

	private String svg3;

	private String svg4;

	private Date outline_date;

	/** 分解分线 **/
	private Integer dec_px;

	/** NS分线 **/
	private Integer ns_px;

	/** 总组分线 **/
	private Integer com_px;

	/** 投线操作时间 **/
	private Integer inline_trans_time;

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

	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}

	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}

	public String getSerial_no() {
		return serial_no;
	}

	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
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

	public Integer getBo_flg() {
		return bo_flg;
	}

	public void setBo_flg(Integer bo_flg) {
		this.bo_flg = bo_flg;
	}

	public Integer getScheduled_expedited() {
		return scheduled_expedited;
	}

	public void setScheduled_expedited(Integer scheduled_expedited) {
		this.scheduled_expedited = scheduled_expedited;
	}

	public Integer getRework() {
		return rework;
	}

	public void setRework(Integer rework) {
		this.rework = rework;
	}

	public Integer getDirect_flg() {
		return direct_flg;
	}

	public void setDirect_flg(Integer direct_flg) {
		this.direct_flg = direct_flg;
	}

	public Date getOutline_time_start() {
		return outline_time_start;
	}

	public void setOutline_time_start(Date outline_time_start) {
		this.outline_time_start = outline_time_start;
	}

	public Date getOutline_time_end() {
		return outline_time_end;
	}

	public void setOutline_time_end(Date outline_time_end) {
		this.outline_time_end = outline_time_end;
	}

	public Date getOrder_date_start() {
		return order_date_start;
	}

	public void setOrder_date_start(Date order_date_start) {
		this.order_date_start = order_date_start;
	}

	public Date getOrder_date_end() {
		return order_date_end;
	}

	public void setOrder_date_end(Date order_date_end) {
		this.order_date_end = order_date_end;
	}

	public Date getArrival_date_start() {
		return arrival_date_start;
	}

	public void setArrival_date_start(Date arrival_date_start) {
		this.arrival_date_start = arrival_date_start;
	}

	public Date getArrival_date_end() {
		return arrival_date_end;
	}

	public void setArrival_date_end(Date arrival_date_end) {
		this.arrival_date_end = arrival_date_end;
	}

	public Integer getIsExists() {
		return isExists;
	}

	public void setIsExists(Integer isExists) {
		this.isExists = isExists;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Date getInline_time() {
		return inline_time;
	}

	public void setInline_time(Date inline_time) {
		this.inline_time = inline_time;
	}

	public Date getOutline_time() {
		return outline_time;
	}

	public void setOutline_time(Date outline_time) {
		this.outline_time = outline_time;
	}

	public Integer getWait_partial_distrubute_time() {
		return wait_partial_distrubute_time;
	}

	public void setWait_partial_distrubute_time(
			Integer wait_partial_distrubute_time) {
		this.wait_partial_distrubute_time = wait_partial_distrubute_time;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public Integer getDesc_work_time() {
		return desc_work_time;
	}

	public void setDesc_work_time(Integer desc_work_time) {
		this.desc_work_time = desc_work_time;
	}

	public Integer getDesc_wait_time() {
		return desc_wait_time;
	}

	public void setDesc_wait_time(Integer desc_wait_time) {
		this.desc_wait_time = desc_wait_time;
	}

	public Integer getDesc_drying_time() {
		return desc_drying_time;
	}

	public void setDesc_drying_time(Integer desc_drying_time) {
		this.desc_drying_time = desc_drying_time;
	}

	public Integer getNs_work_time() {
		return ns_work_time;
	}

	public void setNs_work_time(Integer ns_work_time) {
		this.ns_work_time = ns_work_time;
	}

	public Integer getNs_wait_time() {
		return ns_wait_time;
	}

	public void setNs_wait_time(Integer ns_wait_time) {
		this.ns_wait_time = ns_wait_time;
	}

	public Integer getNs_drying_time() {
		return ns_drying_time;
	}

	public void setNs_drying_time(Integer ns_drying_time) {
		this.ns_drying_time = ns_drying_time;
	}

	public Integer getCom_wait_time() {
		return com_wait_time;
	}

	public void setCom_wait_time(Integer com_wait_time) {
		this.com_wait_time = com_wait_time;
	}

	public Integer getCom_work_time() {
		return com_work_time;
	}

	public void setCom_work_time(Integer com_work_time) {
		this.com_work_time = com_work_time;
	}

	public Integer getCom_drying_time() {
		return com_drying_time;
	}

	public void setCom_drying_time(Integer com_drying_time) {
		this.com_drying_time = com_drying_time;
	}

	public Integer getWait_bo_partial_time() {
		return wait_bo_partial_time;
	}

	public void setWait_bo_partial_time(Integer wait_bo_partial_time) {
		this.wait_bo_partial_time = wait_bo_partial_time;
	}

	public Integer getException_break_time() {
		return exception_break_time;
	}

	public void setException_break_time(Integer exception_break_time) {
		this.exception_break_time = exception_break_time;
	}

	public Integer getTotal_wait_time() {
		return total_wait_time;
	}

	public void setTotal_wait_time(Integer total_wait_time) {
		this.total_wait_time = total_wait_time;
	}

	public Integer getTotal_work_time() {
		return total_work_time;
	}

	public void setTotal_work_time(Integer total_work_time) {
		this.total_work_time = total_work_time;
	}

	public String getSvg1() {
		return svg1;
	}

	public void setSvg1(String svg1) {
		this.svg1 = svg1;
	}

	public String getSvg2() {
		return svg2;
	}

	public void setSvg2(String svg2) {
		this.svg2 = svg2;
	}

	public String getSvg3() {
		return svg3;
	}

	public void setSvg3(String svg3) {
		this.svg3 = svg3;
	}

	public String getSvg4() {
		return svg4;
	}

	public void setSvg4(String svg4) {
		this.svg4 = svg4;
	}

	public Date getOutline_date() {
		return outline_date;
	}

	public void setOutline_date(Date outline_date) {
		this.outline_date = outline_date;
	}

	public Integer getDec_px() {
		return dec_px;
	}

	public void setDec_px(Integer dec_px) {
		this.dec_px = dec_px;
	}

	public Integer getNs_px() {
		return ns_px;
	}

	public void setNs_px(Integer ns_px) {
		this.ns_px = ns_px;
	}

	public Integer getCom_px() {
		return com_px;
	}

	public void setCom_px(Integer com_px) {
		this.com_px = com_px;
	}

	public Integer getInline_trans_time() {
		return inline_trans_time;
	}

	public void setInline_trans_time(Integer inline_trans_time) {
		this.inline_trans_time = inline_trans_time;
	}

}
