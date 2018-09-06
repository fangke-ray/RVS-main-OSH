package com.osh.rvs.form.data;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;
import framework.huiqing.common.util.CodeListUtils;

public class MaterialForm extends ActionForm {

	private static final long serialVersionUID = -1421398848658655883L;

	// ocm rank
	@BeanField(title = "OCM等级", name = "ocm_rank", type = FieldType.Integer, length = 2)
	private String ocm_rank;

	@BeanField(title = "维修对象ID", name = "material_id", type = FieldType.String, length = 11)
	private String material_id;
	@BeanField(title = "修理单号", name = "sorc_no", type = FieldType.String, length = 15)
	private String sorc_no;
	@BeanField(title = "ESAS No.", name = "esas_no", type = FieldType.String, length = 7)
	private String esas_no;
	@BeanField(title = "维修对象型号", name = "model_id", type = FieldType.String, length = 11, notNull = true)
	private String model_id;
	@BeanField(title = "机身号", name = "serial_no", type = FieldType.String, length = 12, notNull = true)
	private String serial_no;
	@BeanField(title = "委托处", name = "ocm", type = FieldType.Integer, length = 2, notNull = true)
	private String ocm;
	@BeanField(title = "等级", name = "level", type = FieldType.Integer, length = 2, notNull = true)
	private String level;
	@BeanField(title = "通箱编号", name = "package_no", type = FieldType.String)
	private String package_no;
	@BeanField(title = "仓储人员", name = "storager", type = FieldType.String, length = 20)
	private String storager;
	@BeanField(title = "直送", name = "direct_flg", type = FieldType.Integer, length = 1)
	private String direct_flg;
	@BeanField(title = "返修标记", name = "service_repair_flg", type = FieldType.Integer, length = 1)
	private String service_repair_flg;
	@BeanField(title = "导入时间", name = "reception_time", type = FieldType.DateTime)
	private String reception_time;
	@BeanField(title = "修理分类", name = "fix_type", type = FieldType.Integer, length = 1, notNull = true)
	private String fix_type;
	@BeanField(title = "受理人员", name = "operator_id", type = FieldType.String)
	private String operator_id;
	@BeanField(title = "投线时间", name = "inline_time", type = FieldType.Date)
	private String inline_time;
	@BeanField(title = "分配课室", name = "section_id", type = FieldType.String, length = 11)
	private String section_id;
	@BeanField(title = "WIP货架位置", name = "wip_location", type = FieldType.String, length = 5)
	private String wip_location;
	@BeanField(title = "AM/PM", name = "am_pm", type = FieldType.Integer, length = 1)
	private String am_pm;
	@BeanField(title = "计划加急", name = "scheduled_expedited", type = FieldType.Integer, length = 1)
	private String scheduled_expedited;
	@BeanField(title = "计划管理备注", name = "scheduled_manager_comment", type = FieldType.String, length = 200)
	private String scheduled_manager_comment;
	@BeanField(title = "完成时间", name = "outline_time", type = FieldType.Date)
	private String outline_time;
	@BeanField(title = "返还", name = "break_back_flg", type = FieldType.Integer, length = 1)
	private String break_back_flg;
	@BeanField(title = "客户同意时间", name = "agreed_date", type = FieldType.Date)
	private String agreed_date;
	@BeanField(title = "维修流程模板ID", name = "pat_id")
	private String pat_id;
	@BeanField(title = "工程检查票出检时间", name = "qa_check_time", type = FieldType.DateTime)
	private String qa_check_time;
	@BeanField(title = "归档时间", name = "filing_time", type = FieldType.DateTime)
	private String filing_time;
	@BeanField(title = "WIP入库时间", name = "wip_date", type = FieldType.Date)
	private String wip_date;
	@BeanField(title = "小票打印标记", name = "ticket_flg", type = FieldType.Integer, length = 1)
	private String ticket_flg;
	@BeanField(title = "Unrepair", name = "unrepair_flg", type = FieldType.Integer, length = 1)
	private String unrepair_flg;
	@BeanField(title = "优先报价", name = "quotation_first", type = FieldType.Integer, length = 1)
	private String quotation_first;
	@BeanField(title = "选择式报价", name = "selectable", type = FieldType.Integer, length = 1)
	private String selectable;

	@BeanField(title = "维修对象型号名称", name = "model_name")
	private String model_name;// 维修对象型号名称
	private String category_name;
	private String remark = "";
	private String operator_name;
	private String ocmName;
	private String levelName;
	private String sterilized;

	@BeanField(title = "导入时间", name = "reception_time_start", type = FieldType.Date)
	private String reception_time_start;
	@BeanField(title = "导入时间", name = "reception_time_end", type = FieldType.Date)
	private String reception_time_end;
	private String inline_time_start;
	private String inline_time_end;
	@BeanField(title = "完成时间", name = "finish_time_start", type = FieldType.Date)
	private String finish_time_start;
	@BeanField(title = "完成时间", name = "finish_time_end", type = FieldType.Date)
	private String finish_time_end;

	@BeanField(title = "纳期", name = "scheduled_date_start", type = FieldType.Date)
	private String scheduled_date_start;
	@BeanField(title = "纳期", name = "scheduled_date_end", type = FieldType.Date)
	private String scheduled_date_end;
	private String processing_position;
	private String processing_position2;
	private String section_name;
	private String category_id;
	@BeanField(title = "纳期", name = "scheduled_date", type = FieldType.Date)
	private String scheduled_date;
	private String doreception_time;// 受理时间
	@BeanField(title = "报价日期", name = "finish_time", type = FieldType.Date)
	private String finish_time;// 报价日期
	@BeanField(title = "报价日期", name = "quotation_time", type = FieldType.DateTime)
	private String quotation_time;// 报价时间

	private String status;
	private String operate_result;
	private String isHistory;

	@BeanField(title = "入库预定日", name = "arrival_plan_date", type = FieldType.Date)
	private String arrival_plan_date;
	@BeanField(title = "入库预定日开始", name = "arrival_plan_date_start", type = FieldType.Date)
	private String arrival_plan_date_start;
	@BeanField(title = "入库预定日结束", name = "arrival_plan_date_end", type = FieldType.Date)
	private String arrival_plan_date_end;

	@BeanField(title = "总组出货安排日开始", name = "complete_date_start", type = FieldType.Date)
	private String complete_date_start;
	@BeanField(title = "总组出货安排日结束", name = "complete_date_end", type = FieldType.Date)
	private String complete_date_end;

	@BeanField(title = "零件预订日开始", name = "partial_order_date_start", type = FieldType.Date)
	private String partial_order_date_start;
	@BeanField(title = "零件预订日结束", name = "partial_order_date_end", type = FieldType.Date)
	private String partial_order_date_end;
	@BeanField(title = "零件预订日", name = "partial_order_date", type = FieldType.Date)
	private String partial_order_date;

	@BeanField(title = "客户同意时间开始", name = "agreed_date_start", type = FieldType.Date)
	private String agreed_date_start;
	@BeanField(title = "客户同意时间结束", name = "agreed_date_end", type = FieldType.Date)
	private String agreed_date_end;

	private String is_late;

	/* 有无偿 */
	@BeanField(title = "有无偿", name = "service_free_flg", type = FieldType.Integer, length = 1)
	private String service_free_flg;

	// 客户ID
	@BeanField(title = "客户ID", name = "customer_id", length = 11)
	private String customer_id;
	@BeanField(title = "客户名", name = "customer_name", length = 80)
	private String customer_name;
	// OCM出库日期
	@BeanField(title = "OCM出库日期", name = "ocm_deliver_date", type = FieldType.Date)
	private String ocm_deliver_date;

	// OCM配送
	@BeanField(title = "OCM配送", name = "ocm_shipping_date", type = FieldType.Date)
	private String ocm_shipping_date;
	@BeanField(title="OCM配送",name="ocm_shipping_date_start",type=FieldType.Date)
	private String ocm_shipping_date_start;
	@BeanField(title="OCM配送",name="ocm_shipping_date_end",type=FieldType.Date)
	private String ocm_shipping_date_end;

	@BeanField(title = "直送发送区域", name = "bound_out_ocm", type = FieldType.Integer, length = 2)
	private String bound_out_ocm;

	@BeanField(title = "维修等级", name = "levels", type = FieldType.String, length = 100)
	private String levels;

	@BeanField(title = "备注内容", name = "comment", type = FieldType.String, length = 250)
	private String comment;

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getOperate_result() {
		return operate_result;
	}

	public void setOperate_result(String operate_result) {
		this.operate_result = operate_result;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFinish_time() {
		return finish_time;
	}

	public void setFinish_time(String finish_time) {
		this.finish_time = finish_time;
	}

	public String getScheduled_date() {
		return scheduled_date;
	}

	public void setScheduled_date(String scheduled_date) {
		this.scheduled_date = scheduled_date;
	}

	public String getProcessing_position() {
		return processing_position;
	}

	public void setProcessing_position(String processing_position) {
		this.processing_position = processing_position;
	}

	public String getSection_name() {
		return section_name;
	}

	public void setSection_name(String section_name) {
		this.section_name = section_name;
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

	public String getRemark() {
		remark = "";
		if (direct_flg != null) {
			remark += CodeListUtils.getValue("material_direct", direct_flg);
		}
		if (service_repair_flg != null) {
			remark += CodeListUtils.getValue("material_service_repair",
					service_repair_flg);
		}
		if (fix_type != null) {
			remark += CodeListUtils.getValue("material_fix_type", fix_type);
		}
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

	public String getOcmName() {
		if (ocm != null) {
			return CodeListUtils.getValue("material_ocm", ocm);
		}
		return ocmName;
	}

	public void setOcmName(String ocmName) {
		this.ocmName = ocmName;
	}

	public String getLevelName() {
		if (level != null) {
			return CodeListUtils.getValue("material_level", level);
		}
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public String getSterilized() {
		return sterilized;
	}

	public void setSterilized(String sterilized) {
		this.sterilized = sterilized;
	}

	public String getInline_time() {
		return inline_time;
	}

	public void setInline_time(String inline_time) {
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

	public String getAm_pm() {
		return am_pm;
	}

	public void setAm_pm(String am_pm) {
		this.am_pm = am_pm;
	}

	public String getScheduled_expedited() {
		return scheduled_expedited;
	}

	public void setScheduled_expedited(String scheduled_expedited) {
		this.scheduled_expedited = scheduled_expedited;
	}

	public String getScheduled_manager_comment() {
		return scheduled_manager_comment;
	}

	public void setScheduled_manager_comment(String scheduled_manager_comment) {
		this.scheduled_manager_comment = scheduled_manager_comment;
	}

	public String getOutline_time() {
		return outline_time;
	}

	public void setOutline_time(String outline_time) {
		this.outline_time = outline_time;
	}

	public String getBreak_back_flg() {
		return break_back_flg;
	}

	public void setBreak_back_flg(String break_back_flg) {
		this.break_back_flg = break_back_flg;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getAgreed_date() {
		return agreed_date;
	}

	public void setAgreed_date(String agreed_date) {
		this.agreed_date = agreed_date;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getService_repair_flg() {
		return service_repair_flg;
	}

	public void setService_repair_flg(String service_repair_flg) {
		this.service_repair_flg = service_repair_flg;
	}

	public String getFix_type() {
		return fix_type;
	}

	public void setFix_type(String fix_type) {
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

	public String getOcm() {
		return ocm;
	}

	public void setOcm(String ocm) {
		this.ocm = ocm;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
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

	public String getDirect_flg() {
		return direct_flg;
	}

	public void setDirect_flg(String direct_flg) {
		this.direct_flg = direct_flg;
	}

	public String getReception_time() {
		return reception_time;
	}

	public void setReception_time(String reception_time) {
		this.reception_time = reception_time;
	}

	public String getQuotation_time() {
		return quotation_time;
	}

	public void setQuotation_time(String quotation_time) {
		this.quotation_time = quotation_time;
	}

	public String getPat_id() {
		return pat_id;
	}

	public void setPat_id(String pat_id) {
		this.pat_id = pat_id;
	}

	public String getQa_check_time() {
		return qa_check_time;
	}

	public void setQa_check_time(String qa_check_time) {
		this.qa_check_time = qa_check_time;
	}

	public String getFiling_time() {
		return filing_time;
	}

	public void setFiling_time(String filing_time) {
		this.filing_time = filing_time;
	}

	public String getWip_date() {
		return wip_date;
	}

	public void setWip_date(String wip_date) {
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
	 * @return the doreception_time
	 */
	public String getDoreception_time() {
		return doreception_time;
	}

	/**
	 * @param doreception_time
	 *            the doreception_time to set
	 */
	public void setDoreception_time(String doreception_time) {
		this.doreception_time = doreception_time;
	}

	public String getTicket_flg() {
		return ticket_flg;
	}

	public void setTicket_flg(String ticket_flg) {
		this.ticket_flg = ticket_flg;
	}

	public String getQuotation_first() {
		return quotation_first;
	}

	public void setQuotation_first(String quotation_first) {
		this.quotation_first = quotation_first;
	}

	public String getUnrepair_flg() {
		return unrepair_flg;
	}

	public void setUnrepair_flg(String unrepair_flg) {
		this.unrepair_flg = unrepair_flg;
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

	public String getSelectable() {
		return selectable;
	}

	public void setSelectable(String selectable) {
		this.selectable = selectable;
	}

	public String getIsHistory() {
		return isHistory;
	}

	public void setIsHistory(String isHistory) {
		this.isHistory = isHistory;
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

	public String getArrival_plan_date() {
		return arrival_plan_date;
	}

	public void setArrival_plan_date(String arrival_plan_date) {
		this.arrival_plan_date = arrival_plan_date;
	}

	public String getFinish_time_start() {
		return finish_time_start;
	}

	public void setFinish_time_start(String finish_time_start) {
		this.finish_time_start = finish_time_start;
	}

	public String getFinish_time_end() {
		return finish_time_end;
	}

	public void setFinish_time_end(String finish_time_end) {
		this.finish_time_end = finish_time_end;
	}

	public String getPartial_order_date_start() {
		return partial_order_date_start;
	}

	public void setPartial_order_date_start(String partial_order_date_start) {
		this.partial_order_date_start = partial_order_date_start;
	}

	public String getPartial_order_date_end() {
		return partial_order_date_end;
	}

	public void setPartial_order_date_end(String partial_order_date_end) {
		this.partial_order_date_end = partial_order_date_end;
	}

	public String getPartial_order_date() {
		return partial_order_date;
	}

	public void setPartial_order_date(String partial_order_date) {
		this.partial_order_date = partial_order_date;
	}

	public String getAgreed_date_start() {
		return agreed_date_start;
	}

	public void setAgreed_date_start(String agreed_date_start) {
		this.agreed_date_start = agreed_date_start;
	}

	public String getAgreed_date_end() {
		return agreed_date_end;
	}

	public void setAgreed_date_end(String agreed_date_end) {
		this.agreed_date_end = agreed_date_end;
	}

	public String getIs_late() {
		return is_late;
	}

	public void setIs_late(String is_late) {
		this.is_late = is_late;
	}

	public String getService_free_flg() {
		return service_free_flg;
	}

	public void setService_free_flg(String service_free_flg) {
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

	public String getOcm_rank() {
		return ocm_rank;
	}

	public void setOcm_rank(String ocm_rank) {
		this.ocm_rank = ocm_rank;
	}

	public String getOcm_deliver_date() {
		return ocm_deliver_date;
	}

	public void setOcm_deliver_date(String ocm_deliver_date) {
		this.ocm_deliver_date = ocm_deliver_date;
	}

	public String getOcm_shipping_date() {
		return ocm_shipping_date;
	}

	public void setOcm_shipping_date(String ocm_shipping_date) {
		this.ocm_shipping_date = ocm_shipping_date;
	}

	public String getOcm_shipping_date_start() {
		return ocm_shipping_date_start;
	}

	public void setOcm_shipping_date_start(String ocm_shipping_date_start) {
		this.ocm_shipping_date_start = ocm_shipping_date_start;
	}

	public String getOcm_shipping_date_end() {
		return ocm_shipping_date_end;
	}

	public void setOcm_shipping_date_end(String ocm_shipping_date_end) {
		this.ocm_shipping_date_end = ocm_shipping_date_end;
	}

	public String getBound_out_ocm() {
		return bound_out_ocm;
	}

	public void setBound_out_ocm(String bound_out_ocm) {
		this.bound_out_ocm = bound_out_ocm;
	}

	public String getLevels() {
		return levels;
	}

	public void setLevels(String levels) {
		this.levels = levels;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
