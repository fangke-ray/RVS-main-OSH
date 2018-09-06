package com.osh.rvs.form.partial;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class BadLossSummaryForm extends ActionForm implements Serializable {

	/**
	 * 损金总计
	 */
	private static final long serialVersionUID = 7440775954205402839L;
	
		@BeanField(title = "报价差异", name = "quotation", type = FieldType.UDouble)
		private String quotation;
		
		@BeanField(title = "分解", name = "decomposition", type = FieldType.UDouble)
		private String decomposition;
		
		@BeanField(title = "NS", name = "ns", type = FieldType.UDouble)
		private String ns;
		
		@BeanField(title = "工程内发现", name = "project_discover", type = FieldType.UDouble)
		private String project_discover;
		
		@BeanField(title = "工程内不良", name = "project_nogood", type = FieldType.UDouble)
		private String project_nogood;
		
		@BeanField(title = "新品零件不良", name = "new_partial_nogood", type = FieldType.UDouble)
		private String new_partial_nogood;
		
		@BeanField(title = "保内返修", name = "service_repair", type = FieldType.UDouble)
		private String service_repair;
	
		//出货月
		@BeanField(title = "出货月", name = "ocm_shipping_month", type = FieldType.String)
		private String ocm_shipping_month;
		
		@BeanField(title = "不良区分", name = "belongs", type = FieldType.Integer)
		private String belongs;
		
		//不同的belongs不一样
		@BeanField(title = "损金金额", name = "loss_price", type = FieldType.UDouble)
		private String loss_price;
		

		public String getQuotation() {
			return quotation;
		}
	
		public void setQuotation(String quotation) {
			this.quotation = quotation;
		}
	
		public String getDecomposition() {
			return decomposition;
		}
	
		public void setDecomposition(String decomposition) {
			this.decomposition = decomposition;
		}
	
		public String getNs() {
			return ns;
		}
	
		public void setNs(String ns) {
			this.ns = ns;
		}
	
		public String getProject_discover() {
			return project_discover;
		}
	
		public void setProject_discover(String project_discover) {
			this.project_discover = project_discover;
		}
	
		public String getProject_nogood() {
			return project_nogood;
		}
	
		public void setProject_nogood(String project_nogood) {
			this.project_nogood = project_nogood;
		}
	
		public String getNew_partial_nogood() {
			return new_partial_nogood;
		}
	
		public void setNew_partial_nogood(String new_partial_nogood) {
			this.new_partial_nogood = new_partial_nogood;
		}
	
		public String getService_repair() {
			return service_repair;
		}
	
		public void setService_repair(String service_repair) {
			this.service_repair = service_repair;
		}

		
		public String getBelongs() {
			return belongs;
		}

		public void setBelongs(String belongs) {
			this.belongs = belongs;
		}

		public String getOcm_shipping_month() {
			return ocm_shipping_month;
		}
	
		public void setOcm_shipping_month(String ocm_shipping_month) {
			this.ocm_shipping_month = ocm_shipping_month;
		}
		
	    public String getLoss_price() {
			return loss_price;
		}

		public void setLoss_price(String loss_price) {
			this.loss_price = loss_price;
		}

		//SORC财年
	    @BeanField(title = "SORC财年", name = "work_period", type = FieldType.String)
		private String work_period;
		
		//年份
		@BeanField(title = "年份", name = "year", type = FieldType.Integer)
		private String year;
		
		//月份
		@BeanField(title = "月份", name = "month", type = FieldType.Integer)
		private String month;
		
		//结算汇率（EUR->USD）
		@BeanField(title = "结算汇率", name = "e_u_settlement", type = FieldType.UDouble)
		private String e_u_settlement;
		
		//分室检查不良
		@BeanField(title = "分室检查不良", name = "ocm_check", type = FieldType.UDouble)
		private String ocm_check;
		
		//最终检查不良
		@BeanField(title = "最终检查不良", name = "qa_check", type = FieldType.UDouble)
		private String qa_check;
		
		//ENDOEYE
		@BeanField(title = "ENDOEYE", name = "endoeye", type = FieldType.UDouble)
		private String endoeye;
		
	    //CCD有效长度
		@BeanField(title = "CCD有效长度", name = "ccd_valid_length", type = FieldType.UDouble)
		private String ccd_valid_length;
		
		//零件成本(财务数据)
		@BeanField(title = "零件成本(财务数据)", name = "financy_budget", type = FieldType.UDouble)
		private String financy_budget;
		
		//备注
		@BeanField(title = "备注", name = "comment", type = FieldType.String)
		private String comment;

		public String getWork_period() {
			return work_period;
		}

		public void setWork_period(String work_period) {
			this.work_period = work_period;
		}

		public String getYear() {
			return year;
		}

		public void setYear(String year) {
			this.year = year;
		}

		public String getMonth() {
			return month;
		}

		public void setMonth(String month) {
			this.month = month;
		}

		public String getE_u_settlement() {
			return e_u_settlement;
		}

		public void setE_u_settlement(String e_u_settlement) {
			this.e_u_settlement = e_u_settlement;
		}

		public String getOcm_check() {
			return ocm_check;
		}

		public void setOcm_check(String ocm_check) {
			this.ocm_check = ocm_check;
		}

		public String getQa_check() {
			return qa_check;
		}

		public void setQa_check(String qa_check) {
			this.qa_check = qa_check;
		}

		public String getEndoeye() {
			return endoeye;
		}

		public void setEndoeye(String endoeye) {
			this.endoeye = endoeye;
		}

		public String getCcd_valid_length() {
			return ccd_valid_length;
		}

		public void setCcd_valid_length(String ccd_valid_length) {
			this.ccd_valid_length = ccd_valid_length;
		}

		public String getFinancy_budget() {
			return financy_budget;
		}

		public void setFinancy_budget(String financy_budget) {
			this.financy_budget = financy_budget;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

}
