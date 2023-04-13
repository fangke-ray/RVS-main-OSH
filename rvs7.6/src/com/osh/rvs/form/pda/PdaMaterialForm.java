package com.osh.rvs.form.pda;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class PdaMaterialForm extends ActionForm {

	private static final long serialVersionUID = -1421398848658655883L;

	@BeanField(title = "维修对象ID", name = "material_id", type = FieldType.String, length = 11)
	private String material_id;
	@BeanField(title = "修理单号", name = "sorc_no", type = FieldType.String, length = 15)
	private String omr_notifi_no;
	@BeanField(title = "维修对象型号", name = "model_id", type = FieldType.String, length = 11, notNull = true)
	private String model_id;
	@BeanField(title = "机身号", name = "serial_no", type = FieldType.String, length = 20, notNull = true)
	private String serial_no;
	@BeanField(title = "等级", name = "level", type = FieldType.Integer, length = 2, notNull = true)
	private String level;
	@BeanField(title = "分配课室", name = "section_id", type = FieldType.String, length = 11)
	private String section_id;
	private String shelf;
	@BeanField(title = "WIP货架位置", name = "wip_location", type = FieldType.String, length = 5)
	private String wip_location;
	@BeanField(title = "客户同意时间", name = "agreed_date", type = FieldType.Date)
	private String agreed_date;

	@BeanField(title = "维修对象型号名称", name = "model_name")
	private String model_name;// 维修对象型号名称
	private String levelName;
	private String section_name;

	private String part_status;

	@BeanField(title = "投线流程", name = "pat_id", type = FieldType.String, length = 11)
	private String pat_id;
	private String pat_name;

	private String ccd_operate_result;

	public String getSection_name() {
		return section_name;
	}

	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}

	/**
	 * @return the material_id
	 */
	public String getMaterial_id() {
		return material_id;
	}

	/**
	 * @param material_id the material_id to set
	 */
	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	/**
	 * @return the omr_notifi_no
	 */
	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}

	/**
	 * @param omr_notifi_no the omr_notifi_no to set
	 */
	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}

	/**
	 * @return the model_id
	 */
	public String getModel_id() {
		return model_id;
	}

	/**
	 * @param model_id the model_id to set
	 */
	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	/**
	 * @return the serial_no
	 */
	public String getSerial_no() {
		return serial_no;
	}

	/**
	 * @param serial_no the serial_no to set
	 */
	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}

	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * @return the section_id
	 */
	public String getSection_id() {
		return section_id;
	}

	/**
	 * @param section_id the section_id to set
	 */
	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	/**
	 * @return the shelf
	 */
	public String getShelf() {
		return shelf;
	}

	/**
	 * @param shelf the shelf to set
	 */
	public void setShelf(String shelf) {
		this.shelf = shelf;
	}

	/**
	 * @return the wip_location
	 */
	public String getWip_location() {
		return wip_location;
	}

	/**
	 * @param wip_location the wip_location to set
	 */
	public void setWip_location(String wip_location) {
		this.wip_location = wip_location;
	}

	/**
	 * @return the agreed_date
	 */
	public String getAgreed_date() {
		return agreed_date;
	}

	/**
	 * @param agreed_date the agreed_date to set
	 */
	public void setAgreed_date(String agreed_date) {
		this.agreed_date = agreed_date;
	}

	/**
	 * @return the model_name
	 */
	public String getModel_name() {
		return model_name;
	}

	/**
	 * @param model_name the model_name to set
	 */
	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	/**
	 * @return the levelName
	 */
	public String getLevelName() {
		return levelName;
	}

	/**
	 * @param levelName the levelName to set
	 */
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	/**
	 * @return the part_status
	 */
	public String getPart_status() {
		return part_status;
	}

	/**
	 * @param part_status the part_status to set
	 */
	public void setPart_status(String part_status) {
		this.part_status = part_status;
	}

	public String getPat_id() {
		return pat_id;
	}

	public void setPat_id(String pat_id) {
		this.pat_id = pat_id;
	}

	public String getPat_name() {
		return pat_name;
	}

	public void setPat_name(String pat_name) {
		this.pat_name = pat_name;
	}

	public void reset() {
		this.material_id = "";
		this.omr_notifi_no = "";
		this.model_id = "";
		this.serial_no = "";
		this.level = "";
		this.section_id = "";
		this.shelf = "";
		this.wip_location = "";
		this.agreed_date = "";
		this.model_name = "";
		this.levelName = "";
		this.section_name = "";
		this.part_status = "";
		this.pat_id = "";
		this.pat_name = "";
	}

	public String getCcd_operate_result() {
		return ccd_operate_result;
	}

	public void setCcd_operate_result(String ccd_operate_result) {
		this.ccd_operate_result = ccd_operate_result;
	}
}
