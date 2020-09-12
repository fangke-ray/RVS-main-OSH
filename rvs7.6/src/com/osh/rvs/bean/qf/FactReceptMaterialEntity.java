package com.osh.rvs.bean.qf;

import java.io.Serializable;
import java.util.Date;

public class FactReceptMaterialEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4904089381420064660L;

	/** 维修对象ID **/
	private String material_id;

	/** 机身号 **/
	private String serial_no;

	/** OCM等级 **/
	private Integer ocm;

	/** 直送 **/
	private Integer direct_flg;

	/** 受理时间 **/
	private Date expect_arrive_time;

	/** 型号 **/
	private String model_name;

	/** 型号ID **/
	private String model_id;

	/** 机种ID **/
	private String category_id;

	/** 种类 **/
	private Integer kind;

	/** 受理标记 **/
	private Integer fact_recept;

	/** 属性标签 **/
	private String tag_types;

	/** 故障说明 **/
	private String comment;

	/** 通箱库位 **/
	private String tc_location;

	/** 间接作业KEY **/
	private String af_pf_key;

	/** 维修品实物受理/测漏临时表主键 **/
	private String fact_recept_id;

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
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

	public Integer getDirect_flg() {
		return direct_flg;
	}

	public void setDirect_flg(Integer direct_flg) {
		this.direct_flg = direct_flg;
	}

	public Date getExpect_arrive_time() {
		return expect_arrive_time;
	}

	public void setExpect_arrive_time(Date expect_arrive_time) {
		this.expect_arrive_time = expect_arrive_time;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public Integer getKind() {
		return kind;
	}

	public void setKind(Integer kind) {
		this.kind = kind;
	}

	public Integer getFact_recept() {
		return fact_recept;
	}

	public void setFact_recept(Integer fact_recept) {
		this.fact_recept = fact_recept;
	}

	public String getTag_types() {
		return tag_types;
	}

	public void setTag_types(String tag_types) {
		this.tag_types = tag_types;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTc_location() {
		return tc_location;
	}

	public void setTc_location(String tc_location) {
		this.tc_location = tc_location;
	}

	public String getAf_pf_key() {
		return af_pf_key;
	}

	public void setAf_pf_key(String af_pf_key) {
		this.af_pf_key = af_pf_key;
	}

	public String getFact_recept_id() {
		return fact_recept_id;
	}

	public void setFact_recept_id(String fact_recept_id) {
		this.fact_recept_id = fact_recept_id;
	}

}
