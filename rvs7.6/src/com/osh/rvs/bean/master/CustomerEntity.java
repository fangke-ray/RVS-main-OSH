package com.osh.rvs.bean.master;

import java.io.Serializable;
import java.sql.Timestamp;

public class CustomerEntity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -4881587195110044046L;
	/** 客户（医院） ID */
	private String customer_id;
	/** 客户（医院）名称 */
	private String name;
	/** 分室 */
	private Integer ocm = 0;
	/** 删除类别 */
	private boolean delete_flg = false;
	/** 最后更新人 */
	private String updated_by;
	/** 最后更新时间 */
	private Timestamp updated_time;
	
	private Integer vip;//优先对应客户
	private String operation_name;//最后更新者
	private String original_customer_id;//归并源ID
	private String target_customer_id;//归并目标ID
	private Integer original_vip;//归并源VIP
	private Integer targer_vip;//归并目标VIP
	
	/**
	 * 取得客户（医院） ID
	 * @return line_id 客户（医院） ID
	 */
	public String getCustomer_id() {
		return customer_id;
	}

	/**
	 * 客户（医院） ID设定
	 * @param line_id 客户（医院） ID
	 */
	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	/**
	 * 取得客户（医院）名称
	 * @return name 客户（医院）名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 客户（医院）名称设定
	 * @param name 客户（医院）名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 取得删除标记
	 * @return delete_flg 删除标记
	 */
	public boolean isDelete_flg() {
		return delete_flg;
	}

	/**
	 * 删除标记设定
	 * @param delete_flg 删除标记
	 */
	public void setDelete_flg(boolean delete_flg) {
		this.delete_flg = delete_flg;
	}

	/**
	 * 取得最后更新人
	 * @return updated_by 最后更新人
	 */
	public String getUpdated_by() {
		return updated_by;
	}

	/**
	 * 最后更新人设定
	 * @param updated_by 最后更新人
	 */
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}

	/**
	 * 取得最后更新时间
	 * @return updated_time 最后更新时间
	 */
	public Timestamp getUpdated_time() {
		return updated_time;
	}

	/**
	 * 最后更新时间设定
	 * @param updated_time 最后更新时间
	 */
	public void setUpdated_time(Timestamp updated_time) {
		this.updated_time = updated_time;
	}

	/**
	 * 文字列化
	 * 
	 * @return 文字列
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.customer_id).append(", ");
		buffer.append(this.name).append(". ");
		return buffer.toString();
	}

	public Integer getOcm() {
		return ocm;
	}

	public void setOcm(Integer ocm) {
		this.ocm = ocm;
	}

	public Integer getVip() {
		return vip;
	}

	public void setVip(Integer vip) {
		this.vip = vip;
	}

	public String getOperation_name() {
		return operation_name;
	}

	public void setOperation_name(String operation_name) {
		this.operation_name = operation_name;
	}

	public String getOriginal_customer_id() {
		return original_customer_id;
	}

	public void setOriginal_customer_id(String original_customer_id) {
		this.original_customer_id = original_customer_id;
	}

	public String getTarget_customer_id() {
		return target_customer_id;
	}

	public void setTarget_customer_id(String target_customer_id) {
		this.target_customer_id = target_customer_id;
	}

	public Integer getOriginal_vip() {
		return original_vip;
	}

	public void setOriginal_vip(Integer original_vip) {
		this.original_vip = original_vip;
	}

	public Integer getTarger_vip() {
		return targer_vip;
	}

	public void setTarger_vip(Integer targer_vip) {
		this.targer_vip = targer_vip;
	}
	
	
}
