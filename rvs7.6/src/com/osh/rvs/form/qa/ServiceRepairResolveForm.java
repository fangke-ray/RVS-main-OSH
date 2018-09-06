package com.osh.rvs.form.qa;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class ServiceRepairResolveForm extends ActionForm implements Serializable {

	private static final long serialVersionUID = 5771086322656300396L;
	/**
	 * 保内返品对策对应
	 */
	
	//部长确认人
	@BeanField(title = "部长确认", name = "minister_confirmer", type = FieldType.String)
	private String minister_confirmer;
		
	public String getMinister_confirmer() {
		return minister_confirmer;
	}

	public void setMinister_confirmer(String minister_confirmer) {
		this.minister_confirmer = minister_confirmer;
	}

	// QA判定日
	@BeanField(title = "QA判定日", name = "qa_referee_time", type = FieldType.Date)
	private String qa_referee_time;
	public String getQa_referee_time() {
		return qa_referee_time;
	}

	public void setQa_referee_time(String qa_referee_time) {
		this.qa_referee_time = qa_referee_time;
	}

	public String getQa_reception_time() {
		return qa_reception_time;
	}

	public void setQa_reception_time(String qa_reception_time) {
		this.qa_reception_time = qa_reception_time;
	}

	// QA分析日
	@BeanField(title = "QA分析日", name = "qa_reception_time", type = FieldType.Date)
	private String qa_reception_time;
	// 此次受理日(SORC受理日)
	@BeanField(title = "此次受理日", name = "reception_time", type = FieldType.Date)
	private String reception_time;
	// 再修理方案（对策）
	@BeanField(title = "再修理方案", name = "countermeasures", length = 120)
	private String countermeasures;
	// QA判定日开始
	@BeanField(title = "QA判定日开始", name = "qa_referee_time_start", type = FieldType.Date)
	private String qa_referee_time_start;
	// QA判定日结束
	@BeanField(title = "QA判定日结束", name = "qa_referee_time_end", type = FieldType.Date)
	private String qa_referee_time_end;
	// QA分析日开始
	@BeanField(title = "QA分析日开始", name = "qa_reception_time_start", type = FieldType.Date)
	private String qa_reception_time_start;
	// QA分析日结束
	@BeanField(title = "QA分析日结束", name = "qa_reception_time_end", type = FieldType.Date)
	private String qa_reception_time_end;
	// 分析表编号
	@BeanField(title = "分析表编号", name = "analysis_no", length = 20)
	private String analysis_no;
	// 客户编号
	@BeanField(title = "客户编号", name = "customer_id", type = FieldType.Integer, length = 11)
	private String customer_id;
	// 客户名
	@BeanField(title = "客户名", name = "customer_name", length = 60)
	private String customer_name;
	// 不良内容
	@BeanField(title = "不良内容", name = "fix_demand", length = 500)
	private String fix_demand;
	// 不良内容
	@BeanField(title = "故障描述", name = "trouble_discribe", length = 500)
	private String trouble_discribe;
	// 故障原因
	@BeanField(title = "故障原因", name = "trouble_cause", length = 500)
	private String trouble_cause;
	// 分析结果
	@BeanField(title = "分析结果", name = "analysis_result", type = FieldType.Integer, length = 2)
	private String analysis_result;
	// 维修场所标记
	@BeanField(title = "维修场所标记", name = "manufactory_flg", type = FieldType.Integer, length = 1)
	private String manufactory_flg;
	// 追加部件
	@BeanField(title = "追加部件", name = "append_component", length = 20)
	private String append_component;
	// 数量
	@BeanField(title = "数量", name = "quantity", type = FieldType.Integer, length = 2)
	private String quantity;
	// 损金数额
	@BeanField(title = "损金数额", name = "loss_amount", type = FieldType.UDouble)
	private String loss_amount;
	// 上次修理编号
	@BeanField(title = "上次修理编号", name = "last_sorc_no", length = 18)
	private String last_sorc_no;
	// 上次出货日期
	@BeanField(title = "上次出货日期", name = "last_shipping_date", type = FieldType.Date, length = 20)
	private String last_shipping_date;
	// 上次等级
	@BeanField(title = "上次等级", name = "last_rank", length = 6)
	private String last_rank;
	// 上次OCM等级
	@BeanField(title = "上次OCM等级", name = "last_ocm_rank", length = 6)
	private String last_ocm_rank;
	// 上次故障内容
	@BeanField(title = "上次故障内容", name = "last_trouble_feature", length = 500)
	private String last_trouble_feature;
	// 清洗
	@BeanField(title = "清洗", name = "wash_feature", length = 20)
	private String wash_feature;
	// 消毒
	@BeanField(title = "消毒", name = "disinfect_feature", length = 20)
	private String disinfect_feature;
	// 灭菌
	@BeanField(title = "灭菌", name = "steriliza_feature", length = 20)
	private String steriliza_feature;
	// 使用频率
	@BeanField(title = "使用频率", name = "usage_frequency", length = 20)
	private String usage_frequency;
	// 分析对应建议
	@BeanField(title = "分析对应建议", name = "analysis_correspond_suggestion", type = FieldType.String, length = 500)
	private String analysis_correspond_suggestion;
	/* 型号 */
	@BeanField(title = "型号", name = "model_name", length = 30, notNull = true, primaryKey = true)
	private String model_name;
	/* 机身号 */
	@BeanField(title = "机身号", name = "serial_no", length = 20, notNull = true, primaryKey = true)
	private String serial_no;
	/* SORC No.(修理编号) */
	@BeanField(title = "修理编号", name = "sorc_no", length = 14)
	private String sorc_no;
	/* 责任区分 */
	@BeanField(title = "责任区分", name = "liability_flg", type = FieldType.Integer, length = 1)
	private String liability_flg;
	// RC邮件发送日
	@BeanField(title = "RC邮件发送日", name = "rc_mailsend_date", type = FieldType.Date)
	private String rc_mailsend_date;
	// 对策提出人
	@BeanField(title = "对策提出人", name = "solution_raiser", type = FieldType.Integer, length = 11)
	private String solution_raiser;
	// 对策确定人
	@BeanField(title = "对策确定人", name = "solution_confirmer", type = FieldType.Integer, length = 11)
	private String solution_confirmer;
	// 对策作成日
	@BeanField(title = "对策作成日", name = "solution_date", type = FieldType.Date)
	private String solution_date;
	// 对策内容
	@BeanField(title = "对策内容", name = "solution_content", type = FieldType.String, length = 1024)
	private String solution_content;
	// 对策完成执行人
	@BeanField(title = "对策完成执行人", name = "resolve_handler", type = FieldType.Integer, length = 11)
	private String resolve_handler;
	// 对策完成确定人
	@BeanField(title = "对策完成确定人", name = "resolve_confirmer", type = FieldType.String)
	private String resolve_confirmer;
	// 技术分析人
	@BeanField(title = "技术分析人", name = "technical_analysts", type = FieldType.Integer, length = 11)
	private String technical_analysts;
	// 对策完成日
	@BeanField(title = "对策完成日", name = "resolve_date", type = FieldType.Date)
	private String resolve_date;
	// 对策完成内容
	@BeanField(title = "对策完成内容", name = "resolve_content", type = FieldType.String, length = 1024)
	private String resolve_content;

	public String getReception_time() {
		return reception_time;
	}

	public void setReception_time(String reception_time) {
		this.reception_time = reception_time;
	}

	public String getCountermeasures() {
		return countermeasures;
	}

	public void setCountermeasures(String countermeasures) {
		this.countermeasures = countermeasures;
	}

	public String getQa_referee_time_start() {
		return qa_referee_time_start;
	}

	public void setQa_referee_time_start(String qa_referee_time_start) {
		this.qa_referee_time_start = qa_referee_time_start;
	}

	public String getQa_referee_time_end() {
		return qa_referee_time_end;
	}

	public void setQa_referee_time_end(String qa_referee_time_end) {
		this.qa_referee_time_end = qa_referee_time_end;
	}

	public String getQa_reception_time_start() {
		return qa_reception_time_start;
	}

	public void setQa_reception_time_start(String qa_reception_time_start) {
		this.qa_reception_time_start = qa_reception_time_start;
	}

	public String getQa_reception_time_end() {
		return qa_reception_time_end;
	}

	public void setQa_reception_time_end(String qa_reception_time_end) {
		this.qa_reception_time_end = qa_reception_time_end;
	}

	public String getAnalysis_no() {
		return analysis_no;
	}

	public void setAnalysis_no(String analysis_no) {
		this.analysis_no = analysis_no;
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

	public String getFix_demand() {
		return fix_demand;
	}

	public void setFix_demand(String fix_demand) {
		this.fix_demand = fix_demand;
	}

	public String getTrouble_discribe() {
		return trouble_discribe;
	}

	public void setTrouble_discribe(String trouble_discribe) {
		this.trouble_discribe = trouble_discribe;
	}

	public String getTrouble_cause() {
		return trouble_cause;
	}

	public void setTrouble_cause(String trouble_cause) {
		this.trouble_cause = trouble_cause;
	}

	public String getAnalysis_result() {
		return analysis_result;
	}

	public void setAnalysis_result(String analysis_result) {
		this.analysis_result = analysis_result;
	}

	public String getManufactory_flg() {
		return manufactory_flg;
	}

	public void setManufactory_flg(String manufactory_flg) {
		this.manufactory_flg = manufactory_flg;
	}

	public String getAppend_component() {
		return append_component;
	}

	public void setAppend_component(String append_component) {
		this.append_component = append_component;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getLoss_amount() {
		return loss_amount;
	}

	public void setLoss_amount(String loss_amount) {
		this.loss_amount = loss_amount;
	}

	public String getLast_sorc_no() {
		return last_sorc_no;
	}

	public void setLast_sorc_no(String last_sorc_no) {
		this.last_sorc_no = last_sorc_no;
	}

	public String getLast_shipping_date() {
		return last_shipping_date;
	}

	public void setLast_shipping_date(String last_shipping_date) {
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

	public String getLiability_flg() {
		return liability_flg;
	}

	public void setLiability_flg(String liability_flg) {
		this.liability_flg = liability_flg;
	}

	public String getRc_mailsend_date() {
		return rc_mailsend_date;
	}

	public void setRc_mailsend_date(String rc_mailsend_date) {
		this.rc_mailsend_date = rc_mailsend_date;
	}

	public String getSolution_raiser() {
		return solution_raiser;
	}

	public void setSolution_raiser(String solution_raiser) {
		this.solution_raiser = solution_raiser;
	}

	public String getSolution_confirmer() {
		return solution_confirmer;
	}

	public void setSolution_confirmer(String solution_confirmer) {
		this.solution_confirmer = solution_confirmer;
	}

	public String getSolution_date() {
		return solution_date;
	}

	public void setSolution_date(String solution_date) {
		this.solution_date = solution_date;
	}

	public String getSolution_content() {
		return solution_content;
	}

	public void setSolution_content(String solution_content) {
		this.solution_content = solution_content;
	}

	public String getResolve_handler() {
		return resolve_handler;
	}

	public void setResolve_handler(String resolve_handler) {
		this.resolve_handler = resolve_handler;
	}

	public String getResolve_confirmer() {
		return resolve_confirmer;
	}

	public void setResolve_confirmer(String resolve_confirmer) {
		this.resolve_confirmer = resolve_confirmer;
	}

	public String getTechnical_analysts() {
		return technical_analysts;
	}

	public void setTechnical_analysts(String technical_analysts) {
		this.technical_analysts = technical_analysts;
	}

	public String getResolve_date() {
		return resolve_date;
	}

	public void setResolve_date(String resolve_date) {
		this.resolve_date = resolve_date;
	}

	public String getResolve_content() {
		return resolve_content;
	}

	public void setResolve_content(String resolve_content) {
		this.resolve_content = resolve_content;
	}

}
