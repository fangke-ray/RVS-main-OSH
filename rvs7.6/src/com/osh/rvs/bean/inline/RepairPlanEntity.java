package com.osh.rvs.bean.inline;

import java.io.Serializable;

/**
 * 维修计划
 * @author Gong
 *
 */
public class RepairPlanEntity implements Serializable {

	private static final long serialVersionUID = 3183669813402069763L;

	/* 年份 */
	String planYear = null;

	/* 月份 */
	String planMonth = null;

	/* 出货 */
	String shipment = null;

	public String getPlanYear() {
		return planYear;
	}

	public void setPlanYear(String planYear) {
		this.planYear = planYear;
	}

	public String getPlanMonth() {
		return planMonth;
	}

	public void setPlanMonth(String planMonth) {
		this.planMonth = planMonth;
	}

	public String getShipment() {
		return shipment;
	}

	public void setShipment(String shipment) {
		this.shipment = shipment;
	}
}