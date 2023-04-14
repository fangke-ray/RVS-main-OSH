package com.osh.rvs.bean.data;

import java.io.Serializable;
import java.util.Date;

/**
 * 维修品时间节点
 * @version Rvs 10
 * @author Gonglm
 */
public class MaterialTimeNodeEntity implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1964441393834502807L;

	private String material_id;

	// 工厂收货时间
	private Date sorc_reception;
	// 客户同意时间
	private Date customer_agreement;
	// SAP投线时间
	private Date sap_inline;
	// 出货装载时间
	private Date shipment;

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
	 * @return the 工厂收货时间
	 */
	public Date getSorc_reception() {
		return sorc_reception;
	}
	/**
	 * @param sorc_reception the 工厂收货时间 to set
	 */
	public void setSorc_reception(Date sorc_reception) {
		this.sorc_reception = sorc_reception;
	}
	/**
	 * @return the 客户同意时间
	 */
	public Date getCustomer_agreement() {
		return customer_agreement;
	}
	/**
	 * @param customer_agreement the 客户同意时间 to set
	 */
	public void setCustomer_agreement(Date customer_agreement) {
		this.customer_agreement = customer_agreement;
	}
	/**
	 * @return the SAP投线时间
	 */
	public Date getSap_inline() {
		return sap_inline;
	}
	/**
	 * @param sap_inline the SAP投线时间 to set
	 */
	public void setSap_inline(Date sap_inline) {
		this.sap_inline = sap_inline;
	}
	/**
	 * @return the 出货装载时间
	 */
	public Date getShipment() {
		return shipment;
	}
	/**
	 * @param shipment the 出货装载时间 to set
	 */
	public void setShipment(Date shipment) {
		this.shipment = shipment;
	}

}
