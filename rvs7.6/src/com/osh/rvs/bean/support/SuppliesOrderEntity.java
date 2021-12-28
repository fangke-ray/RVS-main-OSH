package com.osh.rvs.bean.support;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @Description 物品申购单
 * @author liuxb
 * @date 2021-12-2 下午3:03:23
 */
public class SuppliesOrderEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 554483421550321776L;

	/**
	 * 物品申购单Key
	 */
	private String order_key;

	/**
	 * 申购单号
	 */
	private String order_no;

	/**
	 * 订购日期
	 */
	private Date order_date;

	/**
	 * 订购人员ID
	 */
	private String operator_id;

	/**
	 * 订购人员
	 */
	private String operator_name;

	/**
	 * 经理印
	 */
	private String sign_manager_id;

	/**
	 * 经理名称
	 */
	private String manager_name;

	/**
	 * 经理工号
	 */
	private String manager_job_no;

	/**
	 * 部长印
	 */
	private String sign_minister_id;

	/**
	 * 部长名称
	 */
	private String minister_name;

	/**
	 * 部长工号
	 */
	private String minister_job_no;

	/**
	 * 含有“慧采”
	 */
	private Integer spec;

	/**
	 * 课室全称
	 */
	private String section_full_name;

	public String getOrder_key() {
		return order_key;
	}

	public void setOrder_key(String order_key) {
		this.order_key = order_key;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public Date getOrder_date() {
		return order_date;
	}

	public void setOrder_date(Date order_date) {
		this.order_date = order_date;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

	public String getSign_manager_id() {
		return sign_manager_id;
	}

	public void setSign_manager_id(String sign_manager_id) {
		this.sign_manager_id = sign_manager_id;
	}

	public String getManager_name() {
		return manager_name;
	}

	public void setManager_name(String manager_name) {
		this.manager_name = manager_name;
	}

	public String getManager_job_no() {
		return manager_job_no;
	}

	public void setManager_job_no(String manager_job_no) {
		this.manager_job_no = manager_job_no;
	}

	public String getSign_minister_id() {
		return sign_minister_id;
	}

	public void setSign_minister_id(String sign_minister_id) {
		this.sign_minister_id = sign_minister_id;
	}

	public String getMinister_name() {
		return minister_name;
	}

	public void setMinister_name(String minister_name) {
		this.minister_name = minister_name;
	}

	public String getMinister_job_no() {
		return minister_job_no;
	}

	public void setMinister_job_no(String minister_job_no) {
		this.minister_job_no = minister_job_no;
	}

	public Integer getSpec() {
		return spec;
	}

	public void setSpec(Integer spec) {
		this.spec = spec;
	}

	public String getSection_full_name() {
		return section_full_name;
	}

	public void setSection_full_name(String section_full_name) {
		this.section_full_name = section_full_name;
	}

}