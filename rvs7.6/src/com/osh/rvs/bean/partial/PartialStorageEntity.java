package com.osh.rvs.bean.partial;

import java.io.Serializable;
import java.util.Date;

public class PartialStorageEntity implements Serializable{

	private static final long serialVersionUID = 3336438995975494323L;

	private String partial_id;
	private Date storage_date;
	private Integer identification;
	private Integer quantity;
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
	public Date getStorage_date() {
		return storage_date;
	}
	/**
	 * @param storage_date the storage_date to set
	 */
	public void setStorage_date(Date storage_date) {
		this.storage_date = storage_date;
	}
	/**
	 * @return the identification
	 */
	public Integer getIdentification() {
		return identification;
	}
	/**
	 * @param identification the identification to set
	 */
	public void setIdentification(Integer identification) {
		this.identification = identification;
	}
	/**
	 * @return the quantity
	 */
	public Integer getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Integer quantity) {
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
