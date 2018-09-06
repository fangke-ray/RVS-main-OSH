package com.osh.rvs.form.partial;

import com.osh.rvs.form.UploadForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class PartialStorageForm extends UploadForm {

	private static final long serialVersionUID = -4000231462202951543L;

	@BeanField(title = "零件ID", name = "partial_id", primaryKey = true, type = FieldType.String, length=11)
	private String partial_id;
	@BeanField(title = "日期", name = "storage_date", primaryKey = true, type = FieldType.Date)
	private String storage_date;
	@BeanField(title = "识别", name = "identification", primaryKey = true, type = FieldType.Integer, length=1)
	private String identification;
	@BeanField(title = "数量", name = "quantity", type = FieldType.Integer, length=5)
	private String quantity;
	private String code;
	private String partial_name;

	/**
	 * @return the partial_id
	 */
	public String getPartial_id() {
		return partial_id;
	}
	/**
	 * @param partial_id the partial_id to set
	 */
	public void setPartial_id(String partial_id) {
		this.partial_id = partial_id;
	}
	/**
	 * @return the storage_date
	 */
	public String getStorage_date() {
		return storage_date;
	}
	/**
	 * @param storage_date the storage_date to set
	 */
	public void setStorage_date(String storage_date) {
		this.storage_date = storage_date;
	}
	/**
	 * @return the identification
	 */
	public String getIdentification() {
		return identification;
	}
	/**
	 * @param identification the identification to set
	 */
	public void setIdentification(String identification) {
		this.identification = identification;
	}
	/**
	 * @return the quantity
	 */
	public String getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the partial_name
	 */
	public String getPartial_name() {
		return partial_name;
	}
	/**
	 * @param partial_name the partial_name to set
	 */
	public void setPartial_name(String partial_name) {
		this.partial_name = partial_name;
	}
}
