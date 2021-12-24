package com.osh.rvs.bean.qa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ServiceRepairManageEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1717898670489095598L;

	// 图片显示序号
	private Integer seq_no;

	// 上传图片id
	private String image_uuid;

	// SORC受理日
	private Date reception_time;

	public Date getReception_time() {
		return reception_time;
	}

	public void setReception_time(Date reception_time) {
		this.reception_time = reception_time;
	}

	/** 保内QIS管理--分析表详细数据-start **/
	// 分析对应建议
	private String analysis_correspond_suggestion;
	private String analysis_no;
	private Integer customer_id;
	private String customer_name;
	private String fix_demand;
	private String trouble_cause;
	private String trouble_discribe;
	private Integer analysis_result;
	private Integer liability_flg;
	private Integer manufactory_flg;
	private String append_component;
	private Integer quantity;
	private BigDecimal loss_amount;
	private String last_sorc_no;
	private Date last_shipping_date;
	private String last_ocm_rank;
	private String last_rank;
	private String last_trouble_feature;
	private String wash_feature;
	private String disinfect_feature;
	private String steriliza_feature;
	private String usage_frequency;
	/** 保内QIS管理--分析表详细数据-end **/

	/** 保内QIS管理表数据-start **/
	private String material_id;
	private String model_name;
	private String serial_no;
	private String sorc_no;
	private Integer service_repair_flg;
	private Date rc_mailsend_date;
	private Date rc_ship_assign_date;
	private Date qa_reception_time;
	private Date qa_reception_time_end;
	private Date qa_referee_time_end;
	private Date qa_referee_time;
	private Integer answer_in_deadline;
	private Integer service_free_flg;
	private Date qa_secondary_referee_date;
	private String rank;
	private Integer workshop;
	private String countermeasures;
	private String comment;
	private String mention;

	private Date reception_date;
	private Date quotation_date;
	private Date agreed_date;
	private Date inline_date;
	private Date outline_date;
	private Integer unfix_back_flg;

	private Date qa_reception_time_start;
	private Date qa_referee_time_start;

	/* 后期添加 */
	private String quality_info_no;

	private String qis_invoice_no;

	private Date qis_invoice_date;

	private String include_month;

	private BigDecimal charge_amount;

	private Integer corresponse_flg;// 处理方式

	private Integer entity_send_flg;// 实物处理

	private Date trouble_item_reception_date;// 故障品接收

	private Date trouble_item_in_bussiness_date;// 故障品提交给业务

	private Date trouble_item_out_bussiness_date;// 故障品发送（业务）

	private Date qis2_date;// QIS2

	private Date qis3_date;// QIS3

	private Date waste_certificated_date;// 废弃证明

	private Integer quality_judgment;// 质量判定

	private Integer qis_isuse;// 发行QIS

	private Integer kind;// 产品分类

	private String m_trouble_phenomenon_confirm;// 故障现象确认(工厂)

	private String m_judgment_result;// 判定结果(工厂)

	private String m_analysis_result_brief;// 分析结果简述(工厂)

	private String m_correspond_method;// 对应方法(工厂)

	private String m_solutions;// 对策(工厂)

	private String etq_no;// ETQ单号

	private String pae_no;// PAE编号

	private Date setup_date;// 购买/安装日期

	private Date trouble_happen_date;// 故障发生日期

	private String use_count;// 使用累计次数

	private String use_elapse;// 使用累计时间

	private String actual_fault;// 实发故障

	public String getAnalysis_correspond_suggestion() {
		return analysis_correspond_suggestion;
	}

	public void setAnalysis_correspond_suggestion(
			String analysis_correspond_suggestion) {
		this.analysis_correspond_suggestion = analysis_correspond_suggestion;
	}

	public String getLast_ocm_rank() {
		return last_ocm_rank;
	}

	public void setLast_ocm_rank(String last_ocm_rank) {
		this.last_ocm_rank = last_ocm_rank;
	}

	public Date getLast_shipping_date() {
		return last_shipping_date;
	}

	public void setLast_shipping_date(Date last_shipping_date) {
		this.last_shipping_date = last_shipping_date;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getLoss_amount() {
		return loss_amount;
	}

	public void setLoss_amount(BigDecimal loss_amount) {
		this.loss_amount = loss_amount;
	}

	public String getTrouble_discribe() {
		return trouble_discribe;
	}

	public void setTrouble_discribe(String trouble_discribe) {
		this.trouble_discribe = trouble_discribe;
	}

	public String getFix_demand() {
		return fix_demand;
	}

	public void setFix_demand(String fix_demand) {
		this.fix_demand = fix_demand;
	}

	public Integer getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
	}

	public Integer getAnalysis_result() {
		return analysis_result;
	}

	public void setAnalysis_result(Integer analysis_result) {
		this.analysis_result = analysis_result;
	}

	public Integer getLiability_flg() {
		return liability_flg;
	}

	public void setLiability_flg(Integer liability_flg) {
		this.liability_flg = liability_flg;
	}

	public Integer getManufactory_flg() {
		return manufactory_flg;
	}

	public void setManufactory_flg(Integer manufactory_flg) {
		this.manufactory_flg = manufactory_flg;
	}

	public String getUsage_frequency() {
		return usage_frequency;
	}

	public void setUsage_frequency(String usage_frequency) {
		this.usage_frequency = usage_frequency;
	}

	/** 作业状态 */
	private Integer operate_result;

	/** 保内QIS管理表数据-end **/

	public String getQuality_info_no() {
		return quality_info_no;
	}

	public String getAnalysis_no() {
		return analysis_no;
	}

	public void setAnalysis_no(String analysis_no) {
		this.analysis_no = analysis_no;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public String getTrouble_cause() {
		return trouble_cause;
	}

	public void setTrouble_cause(String trouble_cause) {
		this.trouble_cause = trouble_cause;
	}

	public String getAppend_component() {
		return append_component;
	}

	public void setAppend_component(String append_component) {
		this.append_component = append_component;
	}

	public String getLast_sorc_no() {
		return last_sorc_no;
	}

	public void setLast_sorc_no(String last_sorc_no) {
		this.last_sorc_no = last_sorc_no;
	}

	public String getLast_rank() {
		return last_rank;
	}

	public void setLast_rank(String last_rank) {
		this.last_rank = last_rank;
	}

	public String getLast_trouble_feature() {
		return last_trouble_feature;
	}

	public void setLast_trouble_feature(String last_trouble_feature) {
		this.last_trouble_feature = last_trouble_feature;
	}

	public String getWash_feature() {
		return wash_feature;
	}

	public void setWash_feature(String wash_feature) {
		this.wash_feature = wash_feature;
	}

	public String getDisinfect_feature() {
		return disinfect_feature;
	}

	public void setDisinfect_feature(String disinfect_feature) {
		this.disinfect_feature = disinfect_feature;
	}

	public String getSteriliza_feature() {
		return steriliza_feature;
	}

	public void setSteriliza_feature(String steriliza_feature) {
		this.steriliza_feature = steriliza_feature;
	}

	public void setQuality_info_no(String quality_info_no) {
		this.quality_info_no = quality_info_no;
	}

	public String getQis_invoice_no() {
		return qis_invoice_no;
	}

	public void setQis_invoice_no(String qis_invoice_no) {
		this.qis_invoice_no = qis_invoice_no;
	}

	public Date getQis_invoice_date() {
		return qis_invoice_date;
	}

	public void setQis_invoice_date(Date qis_invoice_date) {
		this.qis_invoice_date = qis_invoice_date;
	}

	public String getInclude_month() {
		return include_month;
	}

	public void setInclude_month(String include_month) {
		this.include_month = include_month;
	}

	public BigDecimal getCharge_amount() {
		return charge_amount;
	}

	public void setCharge_amount(BigDecimal charge_amount) {
		this.charge_amount = charge_amount;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
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

	public String getSorc_no() {
		return sorc_no;
	}

	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
	}

	public Integer getService_repair_flg() {
		return service_repair_flg;
	}

	public void setService_repair_flg(Integer service_repair_flg) {
		this.service_repair_flg = service_repair_flg;
	}

	public Date getRc_mailsend_date() {
		return rc_mailsend_date;
	}

	public void setRc_mailsend_date(Date rc_mailsend_date) {
		this.rc_mailsend_date = rc_mailsend_date;
	}

	public Date getRc_ship_assign_date() {
		return rc_ship_assign_date;
	}

	public void setRc_ship_assign_date(Date rc_ship_assign_date) {
		this.rc_ship_assign_date = rc_ship_assign_date;
	}

	public Date getQa_reception_time() {
		return qa_reception_time;
	}

	public void setQa_reception_time(Date qa_reception_time) {
		this.qa_reception_time = qa_reception_time;
	}

	public Date getQa_referee_time() {
		return qa_referee_time;
	}

	public void setQa_referee_time(Date qa_referee_time) {
		this.qa_referee_time = qa_referee_time;
	}

	public Integer getAnswer_in_deadline() {
		return answer_in_deadline;
	}

	public void setAnswer_in_deadline(Integer answer_in_deadline) {
		this.answer_in_deadline = answer_in_deadline;
	}

	public Integer getService_free_flg() {
		return service_free_flg;
	}

	public void setService_free_flg(Integer service_free_flg) {
		this.service_free_flg = service_free_flg;
	}

	public Date getQa_secondary_referee_date() {
		return qa_secondary_referee_date;
	}

	public void setQa_secondary_referee_date(Date qa_secondary_referee_date) {
		this.qa_secondary_referee_date = qa_secondary_referee_date;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public Integer getWorkshop() {
		return workshop;
	}

	public void setWorkshop(Integer workshop) {
		this.workshop = workshop;
	}

	public String getCountermeasures() {
		return countermeasures;
	}

	public void setCountermeasures(String countermeasures) {
		this.countermeasures = countermeasures;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getMention() {
		return mention;
	}

	public void setMention(String mention) {
		this.mention = mention;
	}

	public Date getReception_date() {
		return reception_date;
	}

	public void setReception_date(Date reception_date) {
		this.reception_date = reception_date;
	}

	public Date getQuotation_date() {
		return quotation_date;
	}

	public void setQuotation_date(Date quotation_date) {
		this.quotation_date = quotation_date;
	}

	public Date getAgreed_date() {
		return agreed_date;
	}

	public void setAgreed_date(Date agreed_date) {
		this.agreed_date = agreed_date;
	}

	public Date getInline_date() {
		return inline_date;
	}

	public void setInline_date(Date inline_date) {
		this.inline_date = inline_date;
	}

	public Date getOutline_date() {
		return outline_date;
	}

	public void setOutline_date(Date outline_date) {
		this.outline_date = outline_date;
	}

	public Integer getUnfix_back_flg() {
		return unfix_back_flg;
	}

	public void setUnfix_back_flg(Integer unfix_back_flg) {
		this.unfix_back_flg = unfix_back_flg;
	}

	public Date getQa_reception_time_end() {
		return qa_reception_time_end;
	}

	public void setQa_reception_time_end(Date qa_reception_time_end) {
		this.qa_reception_time_end = qa_reception_time_end;
	}

	public Date getQa_referee_time_end() {
		return qa_referee_time_end;
	}

	public void setQa_referee_time_end(Date qa_referee_time_end) {
		this.qa_referee_time_end = qa_referee_time_end;
	}

	public Date getQa_reception_time_start() {
		return qa_reception_time_start;
	}

	public void setQa_reception_time_start(Date qa_reception_time_start) {
		this.qa_reception_time_start = qa_reception_time_start;
	}

	public Date getQa_referee_time_start() {
		return qa_referee_time_start;
	}

	public void setQa_referee_time_start(Date qa_referee_time_start) {
		this.qa_referee_time_start = qa_referee_time_start;
	}

	public Integer getOperate_result() {
		return operate_result;
	}

	public void setOperate_result(Integer operate_result) {
		this.operate_result = operate_result;
	}

	public void setRc_mailsend_date(String rc_mailsend_date2) {

	}

	public String getImage_uuid() {
		return image_uuid;
	}

	public void setImage_uuid(String image_uuid) {
		this.image_uuid = image_uuid;
	}

	public Integer getSeq_no() {
		return seq_no;
	}

	public void setSeq_no(Integer seq_no) {
		this.seq_no = seq_no;
	}

	public Integer getCorresponse_flg() {
		return corresponse_flg;
	}

	public void setCorresponse_flg(Integer corresponse_flg) {
		this.corresponse_flg = corresponse_flg;
	}

	public Integer getEntity_send_flg() {
		return entity_send_flg;
	}

	public void setEntity_send_flg(Integer entity_send_flg) {
		this.entity_send_flg = entity_send_flg;
	}

	public Date getTrouble_item_reception_date() {
		return trouble_item_reception_date;
	}

	public void setTrouble_item_reception_date(Date trouble_item_reception_date) {
		this.trouble_item_reception_date = trouble_item_reception_date;
	}

	public Date getTrouble_item_in_bussiness_date() {
		return trouble_item_in_bussiness_date;
	}

	public void setTrouble_item_in_bussiness_date(
			Date trouble_item_in_bussiness_date) {
		this.trouble_item_in_bussiness_date = trouble_item_in_bussiness_date;
	}

	public Date getTrouble_item_out_bussiness_date() {
		return trouble_item_out_bussiness_date;
	}

	public void setTrouble_item_out_bussiness_date(
			Date trouble_item_out_bussiness_date) {
		this.trouble_item_out_bussiness_date = trouble_item_out_bussiness_date;
	}

	public Date getQis2_date() {
		return qis2_date;
	}

	public void setQis2_date(Date qis2_date) {
		this.qis2_date = qis2_date;
	}

	public Date getQis3_date() {
		return qis3_date;
	}

	public void setQis3_date(Date qis3_date) {
		this.qis3_date = qis3_date;
	}

	public Date getWaste_certificated_date() {
		return waste_certificated_date;
	}

	public void setWaste_certificated_date(Date waste_certificated_date) {
		this.waste_certificated_date = waste_certificated_date;
	}

	public Integer getQuality_judgment() {
		return quality_judgment;
	}

	public void setQuality_judgment(Integer quality_judgment) {
		this.quality_judgment = quality_judgment;
	}

	public Integer getQis_isuse() {
		return qis_isuse;
	}

	public void setQis_isuse(Integer qis_isuse) {
		this.qis_isuse = qis_isuse;
	}

	public Integer getKind() {
		return kind;
	}

	public void setKind(Integer kind) {
		this.kind = kind;
	}

	public String getM_trouble_phenomenon_confirm() {
		return m_trouble_phenomenon_confirm;
	}

	public void setM_trouble_phenomenon_confirm(
			String m_trouble_phenomenon_confirm) {
		this.m_trouble_phenomenon_confirm = m_trouble_phenomenon_confirm;
	}

	public String getM_judgment_result() {
		return m_judgment_result;
	}

	public void setM_judgment_result(String m_judgment_result) {
		this.m_judgment_result = m_judgment_result;
	}

	public String getM_analysis_result_brief() {
		return m_analysis_result_brief;
	}

	public void setM_analysis_result_brief(String m_analysis_result_brief) {
		this.m_analysis_result_brief = m_analysis_result_brief;
	}

	public String getM_correspond_method() {
		return m_correspond_method;
	}

	public void setM_correspond_method(String m_correspond_method) {
		this.m_correspond_method = m_correspond_method;
	}

	public String getM_solutions() {
		return m_solutions;
	}

	public void setM_solutions(String m_solutions) {
		this.m_solutions = m_solutions;
	}

	public String getEtq_no() {
		return etq_no;
	}

	public void setEtq_no(String etq_no) {
		this.etq_no = etq_no;
	}

	public Date getSetup_date() {
		return setup_date;
	}

	public void setSetup_date(Date setup_date) {
		this.setup_date = setup_date;
	}

	public Date getTrouble_happen_date() {
		return trouble_happen_date;
	}

	public void setTrouble_happen_date(Date trouble_happen_date) {
		this.trouble_happen_date = trouble_happen_date;
	}

	public String getUse_count() {
		return use_count;
	}

	public void setUse_count(String use_count) {
		this.use_count = use_count;
	}

	public String getUse_elapse() {
		return use_elapse;
	}

	public void setUse_elapse(String use_elapse) {
		this.use_elapse = use_elapse;
	}

	public String getPae_no() {
		return pae_no;
	}

	public void setPae_no(String pae_no) {
		this.pae_no = pae_no;
	}

	public String getActual_fault() {
		return actual_fault;
	}

	public void setActual_fault(String actual_fault) {
		this.actual_fault = actual_fault;
	}

}
