package com.osh.rvs.bean.qa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ServiceRepairResolveEntity implements Serializable {

	/**
	 * 保内返品对策对应
	 */
	//部长确认人
	private String minister_confirmer;
	
	public String getMinister_confirmer() {
		return minister_confirmer;
	}
	public void setMinister_confirmer(String minister_confirmer) {
		this.minister_confirmer = minister_confirmer;
	}
	//担当人job_no
	private String job_no;
	
	public String getJob_no() {
		return job_no;
	}
	public void setJob_no(String job_no) {
		this.job_no = job_no;
	}
	private static final long serialVersionUID = -5851339374197260366L;
	// 再修理方案(manage表的对策)
	private String countermeasures;
	// SORC受理日
	private Date reception_time;
	// 检索条件--QA分析日开始
	private Date qa_reception_time;
	// QA判定时间
	private Date qa_referee_time;
	// 检索条件--QA判定日开始
	private Date qa_referee_time_start;
	// 检索条件--QA判定日结束
	private Date qa_referee_time_end;
	// 检索条件--QA分析日开始
	private Date qa_reception_time_start;
	// 检索条件--QA分析日结束
	private Date qa_reception_time_end;

	/** 保内QIS管理--分析表详细数据-start **/
	private String analysis_no;
	private Integer customer_id;
	private String customer_name;
	private String fix_demand;
	private String trouble_cause;
	private String trouble_discribe;
	private Integer analysis_result;
	private Integer manufactory_flg;
	private String append_component;
	private Integer quantity;
	private BigDecimal loss_amount;
	private String last_sorc_no;
	private Date last_shipping_date;
	private String last_rank;
	// 上次OCM等级
	private String last_ocm_rank;
	private String last_trouble_feature;
	private String wash_feature;
	private String disinfect_feature;
	private String steriliza_feature;
	private String usage_frequency;
	private String analysis_correspond_suggestion;
	/** 保内QIS管理--分析表详细数据-end **/

	/* 型号 */
	private String model_name;
	/* 机身号 */
	private String serial_no;
	/* 修理编号 */
	private String sorc_no;
	/* 责任区分 */
	private Integer liability_flg;
	// RC邮件发送日
	private Date rc_mailsend_date;
	// 对策提出人
	private Integer solution_raiser;
	// 对策确定人
	private Integer solution_confirmer;
	// 对策作成日
	private Date solution_date;
	// 对策内容
	private String solution_content;
	// 对策完成执行人
	private Integer resolve_handler;
	// 对策完成确定人
	private String resolve_confirmer;
	// 技术分析人
	private Integer technical_analysts;
	// 对策完成日
	private Date resolve_date;
	// 对策完成内容
	private String resolve_content;
	
	
	public Date getQa_reception_time() {
		return qa_reception_time;
	}
	public void setQa_reception_time(Date qa_reception_time) {
		this.qa_reception_time = qa_reception_time;
	}
	public String getCountermeasures() {
		return countermeasures;
	}
	public void setCountermeasures(String countermeasures) {
		this.countermeasures = countermeasures;
	}
	
	public Date getReception_time() {
		return reception_time;
	}
	public void setReception_time(Date reception_time) {
		this.reception_time = reception_time;
	}
	public Date getQa_referee_time() {
		return qa_referee_time;
	}
	public void setQa_referee_time(Date qa_referee_time) {
		this.qa_referee_time = qa_referee_time;
	}
	public Date getQa_referee_time_start() {
		return qa_referee_time_start;
	}
	public void setQa_referee_time_start(Date qa_referee_time_start) {
		this.qa_referee_time_start = qa_referee_time_start;
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
	public Date getQa_reception_time_end() {
		return qa_reception_time_end;
	}
	public void setQa_reception_time_end(Date qa_reception_time_end) {
		this.qa_reception_time_end = qa_reception_time_end;
	}
	public String getAnalysis_no() {
		return analysis_no;
	}
	public void setAnalysis_no(String analysis_no) {
		this.analysis_no = analysis_no;
	}
	public Integer getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
	}
	public String getCustomer_name() {
		return customer_name;
	}
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}
	public String getFix_demand() {
		return fix_demand;
	}
	public void setFix_demand(String fix_demand) {
		this.fix_demand = fix_demand;
	}
	public String getTrouble_cause() {
		return trouble_cause;
	}
	public void setTrouble_cause(String trouble_cause) {
		this.trouble_cause = trouble_cause;
	}
	public String getTrouble_discribe() {
		return trouble_discribe;
	}
	public void setTrouble_discribe(String trouble_discribe) {
		this.trouble_discribe = trouble_discribe;
	}
	public Integer getAnalysis_result() {
		return analysis_result;
	}
	public void setAnalysis_result(Integer analysis_result) {
		this.analysis_result = analysis_result;
	}
	public Integer getManufactory_flg() {
		return manufactory_flg;
	}
	public void setManufactory_flg(Integer manufactory_flg) {
		this.manufactory_flg = manufactory_flg;
	}
	public String getAppend_component() {
		return append_component;
	}
	public void setAppend_component(String append_component) {
		this.append_component = append_component;
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
	public String getLast_sorc_no() {
		return last_sorc_no;
	}
	public void setLast_sorc_no(String last_sorc_no) {
		this.last_sorc_no = last_sorc_no;
	}
	public Date getLast_shipping_date() {
		return last_shipping_date;
	}
	public void setLast_shipping_date(Date last_shipping_date) {
		this.last_shipping_date = last_shipping_date;
	}
	public String getLast_rank() {
		return last_rank;
	}
	public void setLast_rank(String last_rank) {
		this.last_rank = last_rank;
	}
	public String getLast_ocm_rank() {
		return last_ocm_rank;
	}
	public void setLast_ocm_rank(String last_ocm_rank) {
		this.last_ocm_rank = last_ocm_rank;
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
	public String getUsage_frequency() {
		return usage_frequency;
	}
	public void setUsage_frequency(String usage_frequency) {
		this.usage_frequency = usage_frequency;
	}
	public String getAnalysis_correspond_suggestion() {
		return analysis_correspond_suggestion;
	}
	public void setAnalysis_correspond_suggestion(String analysis_correspond_suggestion) {
		this.analysis_correspond_suggestion = analysis_correspond_suggestion;
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
	public Integer getLiability_flg() {
		return liability_flg;
	}
	public void setLiability_flg(Integer liability_flg) {
		this.liability_flg = liability_flg;
	}
	public Date getRc_mailsend_date() {
		return rc_mailsend_date;
	}
	public void setRc_mailsend_date(Date rc_mailsend_date) {
		this.rc_mailsend_date = rc_mailsend_date;
	}
	public Integer getSolution_raiser() {
		return solution_raiser;
	}
	public void setSolution_raiser(Integer solution_raiser) {
		this.solution_raiser = solution_raiser;
	}
	public Integer getSolution_confirmer() {
		return solution_confirmer;
	}
	public void setSolution_confirmer(Integer solution_confirmer) {
		this.solution_confirmer = solution_confirmer;
	}
	public Date getSolution_date() {
		return solution_date;
	}
	public void setSolution_date(Date solution_date) {
		this.solution_date = solution_date;
	}
	public String getSolution_content() {
		return solution_content;
	}
	public void setSolution_content(String solution_content) {
		this.solution_content = solution_content;
	}
	public Integer getResolve_handler() {
		return resolve_handler;
	}
	public void setResolve_handler(Integer resolve_handler) {
		this.resolve_handler = resolve_handler;
	}
	public String getResolve_confirmer() {
		return resolve_confirmer;
	}
	public void setResolve_confirmer(String resolve_confirmer) {
		this.resolve_confirmer = resolve_confirmer;
	}
	public Integer getTechnical_analysts() {
		return technical_analysts;
	}
	public void setTechnical_analysts(Integer technical_analysts) {
		this.technical_analysts = technical_analysts;
	}
	public Date getResolve_date() {
		return resolve_date;
	}
	public void setResolve_date(Date resolve_date) {
		this.resolve_date = resolve_date;
	}
	public String getResolve_content() {
		return resolve_content;
	}
	public void setResolve_content(String resolve_content) {
		this.resolve_content = resolve_content;
	}
	
}
