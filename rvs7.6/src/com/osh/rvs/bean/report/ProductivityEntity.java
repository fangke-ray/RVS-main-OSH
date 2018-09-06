package com.osh.rvs.bean.report;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @Title ProductivityEntity.java
 * @Project rvs
 * @Package com.osh.rvs.bean.report
 * @ClassName: ProductivityEntity
 * @Description: 生产效率
 * @author houp
 * @date 2016-10-9 下午1:56:26
 */
public class ProductivityEntity implements Serializable {

	private static final long serialVersionUID = -1411522207849281228L;

	/** 作业开始时间 **/
	private String start_date;

	/** 作业结束时间 **/
	private String end_date;

	private String outline_date;

	private String outline_year;

	private String outline_month;

	private Integer outline_quantity;

	private BigDecimal avalible_productive;

	private String updated_by;

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getOutline_date() {
		return outline_date;
	}

	public void setOutline_date(String outline_date) {
		this.outline_date = outline_date;
	}

	public String getOutline_year() {
		return outline_year;
	}

	public void setOutline_year(String outline_year) {
		this.outline_year = outline_year;
	}

	public String getOutline_month() {
		return outline_month;
	}

	public void setOutline_month(String outline_month) {
		this.outline_month = outline_month;
	}

	public Integer getOutline_quantity() {
		return outline_quantity;
	}

	public void setOutline_quantity(Integer outline_quantity) {
		this.outline_quantity = outline_quantity;
	}

	public BigDecimal getAvalible_productive() {
		return avalible_productive;
	}

	public void setAvalible_productive(BigDecimal avalible_productive) {
		this.avalible_productive = avalible_productive;
	}

	public String getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}
}
