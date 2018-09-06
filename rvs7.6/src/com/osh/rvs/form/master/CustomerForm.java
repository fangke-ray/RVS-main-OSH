package com.osh.rvs.form.master;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 
 * @Title CustomerForm.java
 * @Project rvs
 * @Package com.osh.rvs.form.master
 * @ClassName: CustomerForm
 * @Description: 客户管理Form
 * @author lxb
 * @date 2014-12-3 上午11:32:20
 */
public class CustomerForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3180566089037365399L;

	@BeanField(title = "客户ID", name = "customer_id", type = FieldType.String, length = 11, notNull = true, primaryKey = true)
	private String customer_id;

	@BeanField(title = "客户名称", name = "name", type = FieldType.String, length = 100, notNull = true)
	private String name;

	@BeanField(title = "分室", name = "ocm", type = FieldType.Integer)
	private String ocm;

	@BeanField(title = "最后更新人", name = "updated_by", type = FieldType.String, length = 11, notNull = true)
	private String updated_by;

	@BeanField(title = "最后更新时间", name = "updated_time", type = FieldType.DateTime, notNull = true)
	private String updated_time;

	@BeanField(title = "优先对应客户", name = "vip", type = FieldType.Integer, notNull = true)
	private String vip;

	@BeanField(title = "最后更新者", name = "operation_name", type = FieldType.String)
	private String operation_name;// 最后更新者

	@BeanField(title = "归并源ID", name = "original_customer_id", type = FieldType.String)
	private String original_customer_id;// 归并源ID

	@BeanField(title = "归并目标ID", name = "target_customer_id", type = FieldType.String)
	private String target_customer_id;// 归并目标ID

	@BeanField(title = "归并源VIP", name = "original_vip", type = FieldType.Integer)
	private String original_vip;// 归并源VIP

	@BeanField(title = "归并目标VIP", name = "targer_vip", type = FieldType.Integer)
	private String targer_vip;// 归并目标VIP

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOcm() {
		return ocm;
	}

	public void setOcm(String ocm) {
		this.ocm = ocm;
	}

	public String getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}

	public String getUpdated_time() {
		return updated_time;
	}

	public void setUpdated_time(String updated_time) {
		this.updated_time = updated_time;
	}

	public String getVip() {
		return vip;
	}

	public void setVip(String vip) {
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

	public String getOriginal_vip() {
		return original_vip;
	}

	public void setOriginal_vip(String original_vip) {
		this.original_vip = original_vip;
	}

	public String getTarger_vip() {
		return targer_vip;
	}

	public void setTarger_vip(String targer_vip) {
		this.targer_vip = targer_vip;
	}

	

}
