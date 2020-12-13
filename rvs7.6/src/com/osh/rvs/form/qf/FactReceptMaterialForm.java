package com.osh.rvs.form.qf;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class FactReceptMaterialForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5179715665717090264L;

	@BeanField(title = "维修对象ID", name = "material_id", type = FieldType.String, length = 11)
	private String material_id;

	@BeanField(title = "机身号", name = "serial_no", type = FieldType.String, length = 20, notNull = true)
	private String serial_no;

	@BeanField(title = "OCM等级", name = "ocm", type = FieldType.Integer, length = 1)
	private String ocm;

	@BeanField(title = "直送", name = "direct_flg", type = FieldType.Integer, length = 1)
	private String direct_flg;

	@BeanField(title = "受理时间", name = "expect_arrive_time", type = FieldType.Date)
	private String expect_arrive_time;

	@BeanField(title = "维修对象型号名称", name = "model_name", type = FieldType.String, length = 50)
	private String model_name;

	@BeanField(title = "维修对象型号ID", name = "model_id", type = FieldType.String, length = 11)
	private String model_id;

	@BeanField(title = "机种ID", name = "category_id", type = FieldType.String)
	private String category_id;

	@BeanField(title = "种类", name = "kind", type = FieldType.Integer, length = 1)
	private String kind;

	@BeanField(title = "受理标记", name = "fact_recept", type = FieldType.Integer, length = 1)
	private String fact_recept;

	@BeanField(title = "属性标签", name = "tag_types", type = FieldType.String)
	private String tag_types;

	@BeanField(title = "故障说明", name = "comment", type = FieldType.String, length = 500)
	private String comment;

	@BeanField(title = "间接作业KEY", name = "af_pf_key", type = FieldType.String, length = 11)
	private String af_pf_key;

	@BeanField(title = "维修品实物受理/测漏临时表主键", name = "fact_recept_id", type = FieldType.String, length = 11)
	private String fact_recept_id;

	private String flag;

	@BeanField(title = "查询范围", name = "search_range", type = FieldType.Integer, length = 1)
	private String search_range;

	/** 通箱库位 **/
	@BeanField(title = "通箱库位", name = "tc_location", type = FieldType.String)
	private String tc_location;

	@BeanField(title = "实物受理时间", name = "reception_time", type = FieldType.DateTime)
	private String reception_time;

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

	public String getOcm() {
		return ocm;
	}

	public void setOcm(String ocm) {
		this.ocm = ocm;
	}

	public String getDirect_flg() {
		return direct_flg;
	}

	public void setDirect_flg(String direct_flg) {
		this.direct_flg = direct_flg;
	}

	public String getExpect_arrive_time() {
		return expect_arrive_time;
	}

	public void setExpect_arrive_time(String expect_arrive_time) {
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

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getFact_recept() {
		return fact_recept;
	}

	public void setFact_recept(String fact_recept) {
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

	public String getFact_recept_id() {
		return fact_recept_id;
	}

	public void setFact_recept_id(String fact_recept_id) {
		this.fact_recept_id = fact_recept_id;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getAf_pf_key() {
		return af_pf_key;
	}

	public void setAf_pf_key(String af_pf_key) {
		this.af_pf_key = af_pf_key;
	}

	public String getSearch_range() {
		return search_range;
	}

	public void setSearch_range(String search_range) {
		this.search_range = search_range;
	}

	public String getTc_location() {
		return tc_location;
	}

	public void setTc_location(String tc_location) {
		this.tc_location = tc_location;
	}

	public String getReception_time() {
		return reception_time;
	}

	public void setReception_time(String reception_time) {
		this.reception_time = reception_time;
	}

}
