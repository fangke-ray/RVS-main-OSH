package com.osh.rvs.bean.inline;

import java.io.Serializable;
import java.util.Date;

public class MaterialOgzEntity implements Serializable {

	private static final long serialVersionUID = -2290225030973570941L;

	private String material_id;
	private String sorc_no;
	private String sfdc_no;
	private String esas_no;
	private String model_id;
	private String model_name;
	private String serial_no;
	private Integer ocm;
	private Integer ocm_rank;
	private Integer level;
	private String customer_id;
	private String customer_name;
	private Date ocm_deliver_date;
	private Date reception_time;
	private Date reception_time_start;
	private Date reception_time_end;
	private Date sterilization_time;
	private Date quotation_complete_time;
	private Date agreed_date;
	private Date agreed_date_start;
	private Date agreed_date_end;
	private Date inline_time;
	private Date dec_complete_time;
	private Date ns_complete_time;
	private Date com_complete_time;
	private Date outline_time;
	private Date outline_time_start;
	private Date outline_time_end;
	private Date shipping_time;
	private Date ocm_shipping_date;
	private Integer service_repair_flg;
	private Integer direct_flg;
	private Integer break_back_flg;

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

	public String getSfdc_no() {
		return sfdc_no;
	}

	public void setSfdc_no(String sfdc_no) {
		this.sfdc_no = sfdc_no;
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

	public Integer getOcm() {
		return ocm;
	}

	public void setOcm(Integer ocm) {
		this.ocm = ocm;
	}

	public Integer getOcm_rank() {
		return ocm_rank;
	}

	public void setOcm_rank(Integer ocm_rank) {
		this.ocm_rank = ocm_rank;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
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

	public Date getOcm_deliver_date() {
		return ocm_deliver_date;
	}

	public void setOcm_deliver_date(Date ocm_deliver_date) {
		this.ocm_deliver_date = ocm_deliver_date;
	}

	public Date getReception_time() {
		return reception_time;
	}

	public void setReception_time(Date reception_time) {
		this.reception_time = reception_time;
	}

	public Date getSterilization_time() {
		return sterilization_time;
	}

	public void setSterilization_time(Date sterilization_time) {
		this.sterilization_time = sterilization_time;
	}

	public Date getQuotation_complete_time() {
		return quotation_complete_time;
	}

	public void setQuotation_complete_time(Date quotation_complete_time) {
		this.quotation_complete_time = quotation_complete_time;
	}

	public Date getAgreed_date() {
		return agreed_date;
	}

	public void setAgreed_date(Date agreed_date) {
		this.agreed_date = agreed_date;
	}

	public Date getInline_time() {
		return inline_time;
	}

	public void setInline_time(Date inline_time) {
		this.inline_time = inline_time;
	}

	public Date getDec_complete_time() {
		return dec_complete_time;
	}

	public void setDec_complete_time(Date dec_complete_time) {
		this.dec_complete_time = dec_complete_time;
	}

	public Date getNs_complete_time() {
		return ns_complete_time;
	}

	public void setNs_complete_time(Date ns_complete_time) {
		this.ns_complete_time = ns_complete_time;
	}

	public Date getCom_complete_time() {
		return com_complete_time;
	}

	public void setCom_complete_time(Date com_complete_time) {
		this.com_complete_time = com_complete_time;
	}

	public Date getOutline_time() {
		return outline_time;
	}

	public void setOutline_time(Date outline_time) {
		this.outline_time = outline_time;
	}

	public Date getShipping_time() {
		return shipping_time;
	}

	public void setShipping_time(Date shipping_time) {
		this.shipping_time = shipping_time;
	}

	public Date getOcm_shipping_date() {
		return ocm_shipping_date;
	}

	public void setOcm_shipping_date(Date ocm_shipping_date) {
		this.ocm_shipping_date = ocm_shipping_date;
	}

	public Integer getService_repair_flg() {
		return service_repair_flg;
	}

	public void setService_repair_flg(Integer service_repair_flg) {
		this.service_repair_flg = service_repair_flg;
	}

	public Integer getDirect_flg() {
		return direct_flg;
	}

	public void setDirect_flg(Integer direct_flg) {
		this.direct_flg = direct_flg;
	}

	public Integer getBreak_back_flg() {
		return break_back_flg;
	}

	public void setBreak_back_flg(Integer break_back_flg) {
		this.break_back_flg = break_back_flg;
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

}
