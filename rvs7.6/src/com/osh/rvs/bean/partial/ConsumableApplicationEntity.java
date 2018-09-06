package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ConsumableApplicationEntity implements Serializable {

	private static final long serialVersionUID = -447093140545075127L;

	/** 消耗品申请单Key **/
	private String consumable_application_key;

	/** 消耗品申请单编号 **/
	private String application_no;

	/** 课室ID **/
	private String section_id;

	/** 工程ID **/
	private String line_id;

	/** 申请日时 **/
	private Date apply_time;

	/** 维修对象ID **/
	private String material_id;

	/** 申请理由 **/
	private String apply_reason;

	/** 申请确认人ID **/
	private String confirmer_id;

	/** 发放提供人ID **/
	private String supplier_id;

	/** 发放提供时间 **/
	private Date supply_time;

	/** 发放完成标记 **/
	private Integer all_supplied;

	/** 申请日时开始 **/
	private Date apply_time_start;

	/** 申请日时结束 **/
	private Date apply_time_end;

	/** 即时领用 **/
	private Integer flg;

	/** 工程名称 **/
	private String line_name;

	/** 使用单号 **/
	private String use_no;

	/** 维修单号 */
	private String sorc_no;

	/** 工位ID **/
	private String position_id;

	private BigDecimal price;

	private String process_code;

	private String operator_name;

	public String getConsumable_application_key() {
		return consumable_application_key;
	}

	public void setConsumable_application_key(String consumable_application_key) {
		this.consumable_application_key = consumable_application_key;
	}

	public String getApplication_no() {
		return application_no;
	}

	public void setApplication_no(String application_no) {
		this.application_no = application_no;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public Date getApply_time() {
		return apply_time;
	}

	public void setApply_time(Date apply_time) {
		this.apply_time = apply_time;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getApply_reason() {
		return apply_reason;
	}

	public void setApply_reason(String apply_reason) {
		this.apply_reason = apply_reason;
	}

	public String getConfirmer_id() {
		return confirmer_id;
	}

	public void setConfirmer_id(String confirmer_id) {
		this.confirmer_id = confirmer_id;
	}

	public String getSupplier_id() {
		return supplier_id;
	}

	public void setSupplier_id(String supplier_id) {
		this.supplier_id = supplier_id;
	}

	public Date getSupply_time() {
		return supply_time;
	}

	public void setSupply_time(Date supply_time) {
		this.supply_time = supply_time;
	}

	public Integer getAll_supplied() {
		return all_supplied;
	}

	public void setAll_supplied(Integer all_supplied) {
		this.all_supplied = all_supplied;
	}

	public Date getApply_time_start() {
		return apply_time_start;
	}

	public void setApply_time_start(Date apply_time_start) {
		this.apply_time_start = apply_time_start;
	}

	public Date getApply_time_end() {
		return apply_time_end;
	}

	public void setApply_time_end(Date apply_time_end) {
		this.apply_time_end = apply_time_end;
	}

	public Integer getFlg() {
		return flg;
	}

	public void setFlg(Integer flg) {
		this.flg = flg;
	}

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	public String getUse_no() {
		return use_no;
	}

	public void setUse_no(String use_no) {
		this.use_no = use_no;
	}

	public String getSorc_no() {
		return sorc_no;
	}

	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}
}
