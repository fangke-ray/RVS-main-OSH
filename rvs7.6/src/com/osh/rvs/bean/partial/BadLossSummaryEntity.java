package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.math.BigDecimal;

public class BadLossSummaryEntity implements Serializable {

	/**
	 * 损金总计
	 */
	private static final long serialVersionUID = -446662990675163614L;
	
	//报价差异
	private BigDecimal quotation;

	//分解
	private BigDecimal decomposition;

	//NS
	private BigDecimal ns;

	//工程内发现
	private BigDecimal project_discover;

	//工程内不良（操作不良）
	private BigDecimal project_nogood;

	//新品零件不良
	private BigDecimal new_partial_nogood;

	//保内返修
	private BigDecimal service_repair;

	// belongs分类
	private Integer belongs;

	// 损金
	private BigDecimal loss_price;

	// 出货月
	private String ocm_shipping_month;

	// SORC财年
	private String work_period;

	// 年份
	private Integer year;

	// 月份
	private Integer month;

	// 结算汇率（EUR->USD）
	private BigDecimal e_u_settlement;

	// 分室检查不良
	private BigDecimal ocm_check;

	// 最终检查不良
	private BigDecimal qa_check;

	// ENDOEYE
	private BigDecimal endoeye;

	// CCD有效长度
	private BigDecimal ccd_valid_length;

	// 零件成本(财务数据)
	private BigDecimal financy_budget;

	// 备注
	private String comment;

	public BigDecimal getQuotation() {
		return quotation;
	}

	public void setQuotation(BigDecimal quotation) {
		this.quotation = quotation;
	}

	public BigDecimal getDecomposition() {
		return decomposition;
	}

	public void setDecomposition(BigDecimal decomposition) {
		this.decomposition = decomposition;
	}

	public BigDecimal getNs() {
		return ns;
	}

	public void setNs(BigDecimal ns) {
		this.ns = ns;
	}

	public BigDecimal getProject_discover() {
		return project_discover;
	}

	public void setProject_discover(BigDecimal project_discover) {
		this.project_discover = project_discover;
	}

	public BigDecimal getProject_nogood() {
		return project_nogood;
	}

	public void setProject_nogood(BigDecimal project_nogood) {
		this.project_nogood = project_nogood;
	}

	public BigDecimal getNew_partial_nogood() {
		return new_partial_nogood;
	}

	public void setNew_partial_nogood(BigDecimal new_partial_nogood) {
		this.new_partial_nogood = new_partial_nogood;
	}

	public BigDecimal getService_repair() {
		return service_repair;
	}

	public void setService_repair(BigDecimal service_repair) {
		this.service_repair = service_repair;
	}

	public Integer getBelongs() {
		return belongs;
	}

	public void setBelongs(Integer belongs) {
		this.belongs = belongs;
	}

	public BigDecimal getLoss_price() {
		return loss_price;
	}

	public void setLoss_price(BigDecimal loss_price) {
		this.loss_price = loss_price;
	}

	public String getOcm_shipping_month() {
		return ocm_shipping_month;
	}

	public void setOcm_shipping_month(String ocm_shipping_month) {
		this.ocm_shipping_month = ocm_shipping_month;
	}

	public String getWork_period() {
		return work_period;
	}

	public void setWork_period(String work_period) {
		this.work_period = work_period;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public BigDecimal getE_u_settlement() {
		return e_u_settlement;
	}

	public void setE_u_settlement(BigDecimal e_u_settlement) {
		this.e_u_settlement = e_u_settlement;
	}

	public BigDecimal getOcm_check() {
		return ocm_check;
	}

	public void setOcm_check(BigDecimal ocm_check) {
		this.ocm_check = ocm_check;
	}

	public BigDecimal getQa_check() {
		return qa_check;
	}

	public void setQa_check(BigDecimal qa_check) {
		this.qa_check = qa_check;
	}

	public BigDecimal getEndoeye() {
		return endoeye;
	}

	public void setEndoeye(BigDecimal endoeye) {
		this.endoeye = endoeye;
	}

	public BigDecimal getCcd_valid_length() {
		return ccd_valid_length;
	}

	public void setCcd_valid_length(BigDecimal ccd_valid_length) {
		this.ccd_valid_length = ccd_valid_length;
	}

	public BigDecimal getFinancy_budget() {
		return financy_budget;
	}

	public void setFinancy_budget(BigDecimal financy_budget) {
		this.financy_budget = financy_budget;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
