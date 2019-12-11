package com.osh.rvs.bean.data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author fxc
 */
public class MaterialEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7498550404024660749L;

	// ocm rank
	private Integer ocm_rank;
	// ocm 派送日
	private Date ocm_deliver_date;
	
	private String material_id;
	private String sorc_no;// SORC No.
	private String esas_no;
	private String model_id;// 维修对象型号ID
	private String serial_no;// 机身号
	private Integer ocm;// 客户ID
	private Integer level;// 等级
	private String package_no;
	private String storager;
	private Integer direct_flg;// 直送
	private Integer service_repair_flg;// 返修标记
	private Date reception_time;// 受理时间
	private Integer fix_type;
	private String operator_id;
	private Date inline_time;
	private String section_id;// 维修课室ID
	private String wip_location;
	private Integer am_pm;
	private Integer scheduled_expedited;
	private String scheduled_manager_comment;
	private Date outline_time;
	private Integer break_back_flg;
	private Date agreed_date;// 客户同意日
	private String model_name;// 型号名称
	private String operator_name;
	private Integer sterilized;
	private String pat_id;
	private Date qa_check_time;
	private Date filing_time;
	private Integer ticket_flg;
	private Date wip_date;
	private Integer unrepair_flg;
	private Integer selectable;
	private Integer quotation_first;
	private Integer ns_partial_order;
	private Integer symbol1;

	private Integer isHistory;

	private String category_id;// 维修对象机种ID
	private String category_name;// 维修对象机种Name
	private String kind;
	private Date reception_time_start;
	private Date reception_time_end;
	private String inline_time_start;
	private String inline_time_end;
	private Date scheduled_date_start;
	private Date scheduled_date_end;
	private String processing_position;
	private String processing_position2;
	private String section_name;

	private Date scheduled_date;
	private Date finish_time;
	private Date finish_time_start;
	private Date finish_time_end;
	private Date quotation_time;
	private Integer operate_result;
	private String find_history;

	private Date arrival_plan_date_start;
	private Date arrival_plan_date_end;
	private String arrival_plan_date;

	private Date complete_date_start;
	private Date complete_date_end;

	private Date partial_order_date_start;
	private Date partial_order_date_end;
	private String partial_order_date;

	private Date agreed_date_start;
	private Date agreed_date_end;

	// 作业等待类型
	private Integer now_operate_result;
	// 暂停理由
	private Integer now_pause_reason;
	/*有无偿*/
	private Integer service_free_flg;

	// 客户ID
	private String customer_id;
	private String customer_name;

	// 出货指示日
	private Date ocm_shipping_date;

	private Date ocm_shipping_date_start;
	private Date ocm_shipping_date_end;

	private Integer bound_out_ocm;
	private String levels;
	private Integer avaliable_end_date_flg;
	//环序号
	private String ring_code;

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
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

	public String getInline_time_start() {
		return inline_time_start;
	}

	public void setInline_time_start(String inline_time_start) {
		this.inline_time_start = inline_time_start;
	}

	public String getInline_time_end() {
		return inline_time_end;
	}

	public void setInline_time_end(String inline_time_end) {
		this.inline_time_end = inline_time_end;
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

	public Integer getOperate_result() {
		return operate_result;
	}

	public void setOperate_result(Integer operate_result) {
		this.operate_result = operate_result;
	}

	public Date getFinish_time() {
		return finish_time;
	}

	public void setFinish_time(Date finish_time) {
		this.finish_time = finish_time;
	}

	public Date getScheduled_date() {
		return scheduled_date;
	}

	public void setScheduled_date(Date scheduled_date) {
		this.scheduled_date = scheduled_date;
	}

	public String getSection_name() {
		return section_name;
	}

	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}

	public String getProcessing_position() {
		return processing_position;
	}

	public void setProcessing_position(String processing_position) {
		this.processing_position = processing_position;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

	public Integer getSterilized() {
		return sterilized;
	}

	public void setSterilized(Integer sterilized) {
		this.sterilized = sterilized;
	}

	public Date getInline_time() {
		return inline_time;
	}

	public void setInline_time(Date inline_time) {
		this.inline_time = inline_time;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getWip_location() {
		return wip_location;
	}

	public void setWip_location(String wip_location) {
		this.wip_location = wip_location;
	}

	public Integer getAm_pm() {
		return am_pm;
	}

	public void setAm_pm(Integer am_pm) {
		this.am_pm = am_pm;
	}

	public Integer getScheduled_expedited() {
		return scheduled_expedited;
	}

	public void setScheduled_expedited(Integer scheduled_expedited) {
		this.scheduled_expedited = scheduled_expedited;
	}

	public String getScheduled_manager_comment() {
		return scheduled_manager_comment;
	}

	public void setScheduled_manager_comment(String scheduled_manager_comment) {
		this.scheduled_manager_comment = scheduled_manager_comment;
	}

	public Date getOutline_time() {
		return outline_time;
	}

	public void setOutline_time(Date outline_time) {
		this.outline_time = outline_time;
	}

	public Integer getBreak_back_flg() {
		return break_back_flg;
	}

	public void setBreak_back_flg(Integer break_back_flg) {
		this.break_back_flg = break_back_flg;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public Date getAgreed_date() {
		return agreed_date;
	}

	public void setAgreed_date(Date agreed_date) {
		this.agreed_date = agreed_date;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public Integer getService_repair_flg() {
		return service_repair_flg;
	}

	public void setService_repair_flg(Integer service_repair_flg) {
		this.service_repair_flg = service_repair_flg;
	}

	public Integer getFix_type() {
		return fix_type;
	}

	public void setFix_type(Integer fix_type) {
		this.fix_type = fix_type;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getSorc_no() {
		return sorc_no;
	}

	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
	}

	public String getEsas_no() {
		return esas_no;
	}

	public void setEsas_no(String esas_no) {
		this.esas_no = esas_no;
	}

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public String getSerial_no() {
		return serial_no;
	}

	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}

	public Integer getOcm() {
		return ocm;
	}

	public void setOcm(Integer ocm) {
		this.ocm = ocm;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getPackage_no() {
		return package_no;
	}

	public void setPackage_no(String package_no) {
		this.package_no = package_no;
	}

	public String getStorager() {
		return storager;
	}

	public void setStorager(String storager) {
		this.storager = storager;
	}

	public Integer getDirect_flg() {
		return direct_flg;
	}

	public void setDirect_flg(Integer direct_flg) {
		this.direct_flg = direct_flg;
	}

	public Date getReception_time() {
		return reception_time;
	}

	public void setReception_time(Date reception_time) {
		this.reception_time = reception_time;
	}

	public String getPat_id() {
		return pat_id;
	}

	public void setPat_id(String pat_id) {
		this.pat_id = pat_id;
	}

	public Integer getNow_operate_result() {
		return now_operate_result;
	}

	public void setNow_operate_result(Integer now_operate_result) {
		this.now_operate_result = now_operate_result;
	}

	public Integer getNow_pause_reason() {
		return now_pause_reason;
	}

	public void setNow_pause_reason(Integer now_pause_reason) {
		this.now_pause_reason = now_pause_reason;
	}

	public Date getQuotation_time() {
		return quotation_time;
	}

	public void setQuotation_time(Date quotation_time) {
		this.quotation_time = quotation_time;
	}

	public Date getQa_check_time() {
		return qa_check_time;
	}

	public void setQa_check_time(Date qa_check_time) {
		this.qa_check_time = qa_check_time;
	}

	public Date getFiling_time() {
		return filing_time;
	}

	public void setFiling_time(Date filing_time) {
		this.filing_time = filing_time;
	}

	public Integer getTicket_flg() {
		return ticket_flg;
	}

	public void setTicket_flg(Integer ticket_flg) {
		this.ticket_flg = ticket_flg;
	}

	public Date getWip_date() {
		return wip_date;
	}

	public void setWip_date(Date wip_date) {
		this.wip_date = wip_date;
	}

	/**
	 * @return the category_name
	 */
	public String getCategory_name() {
		return category_name;
	}

	/**
	 * @param category_name
	 *            the category_name to set
	 */
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	/**
	 * @return the unrepair_flg
	 */
	public Integer getUnrepair_flg() {
		return unrepair_flg;
	}

	/**
	 * @param unrepair_flg
	 *            the unrepair_flg to set
	 */
	public void setUnrepair_flg(Integer unrepair_flg) {
		this.unrepair_flg = unrepair_flg;
	}

	public Integer getSelectable() {
		return selectable;
	}

	public void setSelectable(Integer selectable) {
		this.selectable = selectable;
	}

	public Integer getQuotation_first() {
		return quotation_first;
	}

	public void setQuotation_first(Integer quotation_first) {
		this.quotation_first = quotation_first;
	}

	public Integer getNs_partial_order() {
		return ns_partial_order;
	}

	public void setNs_partial_order(Integer ns_partial_order) {
		this.ns_partial_order = ns_partial_order;
	}

	public Integer getSymbol1() {
		return symbol1;
	}

	public void setSymbol1(Integer symbol1) {
		this.symbol1 = symbol1;
	}

	/**
	 * @return the processing_position2
	 */
	public String getProcessing_position2() {
		return processing_position2;
	}

	/**
	 * @param processing_position2
	 *            the processing_position2 to set
	 */
	public void setProcessing_position2(String processing_position2) {
		this.processing_position2 = processing_position2;
	}

	/**
	 * @return the isHistory
	 */
	public Integer getIsHistory() {
		return isHistory;
	}

	/**
	 * @param isHistory
	 *            the isHistory to set
	 */
	public void setIsHistory(Integer isHistory) {
		this.isHistory = isHistory;
	}

	public String getFind_history() {
		return find_history;
	}

	public void setFind_history(String find_history) {
		this.find_history = find_history;
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

	public String getArrival_plan_date() {
		return arrival_plan_date;
	}

	public void setArrival_plan_date(String arrival_plan_date) {
		this.arrival_plan_date = arrival_plan_date;
	}

	public Date getFinish_time_start() {
		return finish_time_start;
	}

	public void setFinish_time_start(Date finish_time_start) {
		this.finish_time_start = finish_time_start;
	}

	public Date getFinish_time_end() {
		return finish_time_end;
	}

	public void setFinish_time_end(Date finish_time_end) {
		this.finish_time_end = finish_time_end;
	}

	public Date getPartial_order_date_start() {
		return partial_order_date_start;
	}

	public void setPartial_order_date_start(Date partial_order_date_start) {
		this.partial_order_date_start = partial_order_date_start;
	}

	public Date getPartial_order_date_end() {
		return partial_order_date_end;
	}

	public void setPartial_order_date_end(Date partial_order_date_end) {
		this.partial_order_date_end = partial_order_date_end;
	}

	public String getPartial_order_date() {
		return partial_order_date;
	}

	public void setPartial_order_date(String partial_order_date) {
		this.partial_order_date = partial_order_date;
	}

	public Date getAgreed_date_start() {
		return agreed_date_start;
	}

	public void setAgreed_date_start(Date agreed_date_start) {
		this.agreed_date_start = agreed_date_start;
	}

	public Date getAgreed_date_end() {
		return agreed_date_end;
	}

	public void setAgreed_date_end(Date agreed_date_end) {
		this.agreed_date_end = agreed_date_end;
	}

	public Integer getService_free_flg() {
		return service_free_flg;
	}

	public void setService_free_flg(Integer service_free_flg) {
		this.service_free_flg = service_free_flg;
	}

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public Integer getOcm_rank() {
		return ocm_rank;
	}

	public void setOcm_rank(Integer ocm_rank) {
		this.ocm_rank = ocm_rank;
	}

	public Date getOcm_shipping_date() {
		return ocm_shipping_date;
	}

	public void setOcm_shipping_date(Date ocm_shipping_date) {
		this.ocm_shipping_date = ocm_shipping_date;
	}

	public Date getOcm_deliver_date() {
		return ocm_deliver_date;
	}

	public void setOcm_deliver_date(Date ocm_deliver_date) {
		this.ocm_deliver_date = ocm_deliver_date;
	}

	public Date getOcm_shipping_date_start() {
		return ocm_shipping_date_start;
	}

	public void setOcm_shipping_date_start(Date ocm_shipping_date_start) {
		this.ocm_shipping_date_start = ocm_shipping_date_start;
	}

	public Date getOcm_shipping_date_end() {
		return ocm_shipping_date_end;
	}

	public void setOcm_shipping_date_end(Date ocm_shipping_date_end) {
		this.ocm_shipping_date_end = ocm_shipping_date_end;
	}

	public Integer getBound_out_ocm() {
		return bound_out_ocm;
	}

	public void setBound_out_ocm(Integer bound_out_ocm) {
		this.bound_out_ocm = bound_out_ocm;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getLevels() {
		return levels;
	}

	public void setLevels(String levels) {
		this.levels = levels;
	}

	public Integer getAvaliable_end_date_flg() {
		return avaliable_end_date_flg;
	}

	public void setAvaliable_end_date_flg(Integer avaliable_end_date_flg) {
		this.avaliable_end_date_flg = avaliable_end_date_flg;
	}

	public String getRing_code() {
		return ring_code;
	}

	public void setRing_code(String ring_code) {
		this.ring_code = ring_code;
	}
	
}
