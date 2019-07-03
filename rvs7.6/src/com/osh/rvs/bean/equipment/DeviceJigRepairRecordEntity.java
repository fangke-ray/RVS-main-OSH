package com.osh.rvs.bean.equipment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 设备工具治具维修记录
 * 
 * @author gonglm
 * 
 */

public class DeviceJigRepairRecordEntity implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4649384919586993762L;

	private String device_jig_repair_record_key;

	private String check_unqualified_record_key;

	private Date submit_time;
	private Date submit_time_start;
	private Date submit_time_end;

	private String phenomenon;

	private Date repair_complete_time;
	private Date repair_complete_time_start;
	private Date repair_complete_time_end;

	private String fault_causes;

	private String countermeasure;

	private Integer photo_flg;


	private String maintainer_id;

	private String maintainer_name;


	private String device_type_id;

	private String device_type_name;

	private String model_name;

	private BigDecimal price;

	private Integer quantity;

	private BigDecimal outsourcing_price;


	private String line_id;

	private String line_name;

	private String submitter_id;

	private String submitter_name;

	private Integer object_type;// 对象类型

	private String manage_code;

	private String manage_id;// 管理ID

	private String object_name;

	private Integer device_halt;
	private String confirmer_id;
	private String confirmer_name;

	private String comment;
	private String consumable;

	public String getDevice_jig_repair_record_key() {
		return device_jig_repair_record_key;
	}
	public void setDevice_jig_repair_record_key(String device_jig_repair_record_key) {
		this.device_jig_repair_record_key = device_jig_repair_record_key;
	}
	public String getCheck_unqualified_record_key() {
		return check_unqualified_record_key;
	}
	public void setCheck_unqualified_record_key(String check_unqualified_record_key) {
		this.check_unqualified_record_key = check_unqualified_record_key;
	}

	public String getPhenomenon() {
		return phenomenon;
	}
	public void setPhenomenon(String phenomenon) {
		this.phenomenon = phenomenon;
	}

	public String getFault_causes() {
		return fault_causes;
	}
	public void setFault_causes(String fault_causes) {
		this.fault_causes = fault_causes;
	}

	public String getCountermeasure() {
		return countermeasure;
	}
	public void setCountermeasure(String countermeasure) {
		this.countermeasure = countermeasure;
	}

	public String getMaintainer_id() {
		return maintainer_id;
	}
	public void setMaintainer_id(String maintainer_id) {
		this.maintainer_id = maintainer_id;
	}
	public String getMaintainer_name() {
		return maintainer_name;
	}
	public void setMaintainer_name(String maintainer_name) {
		this.maintainer_name = maintainer_name;
	}
	public String getDevice_type_id() {
		return device_type_id;
	}
	public void setDevice_type_id(String device_type_id) {
		this.device_type_id = device_type_id;
	}
	public String getDevice_type_name() {
		return device_type_name;
	}
	public void setDevice_type_name(String device_type_name) {
		this.device_type_name = device_type_name;
	}
	public String getModel_name() {
		return model_name;
	}
	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getLine_id() {
		return line_id;
	}
	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}
	public String getLine_name() {
		return line_name;
	}
	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}
	public String getSubmitter_id() {
		return submitter_id;
	}
	public void setSubmitter_id(String submitter_id) {
		this.submitter_id = submitter_id;
	}
	public String getSubmitter_name() {
		return submitter_name;
	}
	public void setSubmitter_name(String submitter_name) {
		this.submitter_name = submitter_name;
	}

	public String getManage_code() {
		return manage_code;
	}
	public void setManage_code(String manage_code) {
		this.manage_code = manage_code;
	}
	public String getManage_id() {
		return manage_id;
	}
	public void setManage_id(String manage_id) {
		this.manage_id = manage_id;
	}
	public String getObject_name() {
		return object_name;
	}
	public void setObject_name(String object_name) {
		this.object_name = object_name;
	}
	public Date getSubmit_time() {
		return submit_time;
	}
	public void setSubmit_time(Date submit_time) {
		this.submit_time = submit_time;
	}
	public Date getRepair_complete_time() {
		return repair_complete_time;
	}
	public void setRepair_complete_time(Date repair_complete_time) {
		this.repair_complete_time = repair_complete_time;
	}
	public Integer getPhoto_flg() {
		return photo_flg;
	}
	public void setPhoto_flg(Integer photo_flg) {
		this.photo_flg = photo_flg;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getOutsourcing_price() {
		return outsourcing_price;
	}
	public void setOutsourcing_price(BigDecimal outsourcing_price) {
		this.outsourcing_price = outsourcing_price;
	}
	public Integer getObject_type() {
		return object_type;
	}
	public void setObject_type(Integer object_type) {
		this.object_type = object_type;
	}
	public Date getSubmit_time_start() {
		return submit_time_start;
	}
	public void setSubmit_time_start(Date submit_time_start) {
		this.submit_time_start = submit_time_start;
	}
	public Date getSubmit_time_end() {
		return submit_time_end;
	}
	public void setSubmit_time_end(Date submit_time_end) {
		this.submit_time_end = submit_time_end;
	}
	public Date getRepair_complete_time_start() {
		return repair_complete_time_start;
	}
	public void setRepair_complete_time_start(Date repair_complete_time_start) {
		this.repair_complete_time_start = repair_complete_time_start;
	}
	public Date getRepair_complete_time_end() {
		return repair_complete_time_end;
	}
	public void setRepair_complete_time_end(Date repair_complete_time_end) {
		this.repair_complete_time_end = repair_complete_time_end;
	}
	public Integer getDevice_halt() {
		return device_halt;
	}
	public void setDevice_halt(Integer device_halt) {
		this.device_halt = device_halt;
	}
	public String getConfirmer_id() {
		return confirmer_id;
	}
	public void setConfirmer_id(String confirmer_id) {
		this.confirmer_id = confirmer_id;
	}
	public String getConfirmer_name() {
		return confirmer_name;
	}
	public void setConfirmer_name(String confirmer_name) {
		this.confirmer_name = confirmer_name;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getConsumable() {
		return consumable;
	}
	public void setConsumable(String consumable) {
		this.consumable = consumable;
	}
	
}
