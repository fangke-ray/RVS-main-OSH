package com.osh.rvs.form.support;

import java.io.Serializable;
import java.util.List;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 
 * @Description 物品申购单
 * @author liuxb
 * @date 2021-12-2 下午3:08:04
 */
public class SuppliesOrderForm extends ActionForm implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 8374432459718199357L;

	/**
	 * 物品申购单Key
	 */
	@BeanField(name = "order_key", title = "物品申购单Key", type = FieldType.String, length = 11, primaryKey = true)
	private String order_key;

	/**
	 * 申购单号
	 */
	@BeanField(name = "order_no", title = "申购单号", type = FieldType.String, length = 13, notNull = true)
	private String order_no;

	/**
	 * 订购日期
	 */
	@BeanField(name = "order_date", title = "订购日期", type = FieldType.Date, notNull = true)
	private String order_date;

	/**
	 * 订购人员ID
	 */
	@BeanField(name = "operator_id", title = "订购人员ID", type = FieldType.String, length = 11, notNull = true)
	private String operator_id;

	/**
	 * 订购人员
	 */
	@BeanField(name = "operator_name", title = "订购人员", type = FieldType.String, length = 8)
	private String operator_name;

	/**
	 * 经理印
	 */
	@BeanField(name = "sign_manager_id", title = "经理印", type = FieldType.String, length = 11)
	private String sign_manager_id;

	/**
	 * 经理名称
	 */
	@BeanField(name = "manager_name", title = "经理名称", type = FieldType.String, length = 8)
	private String manager_name;

	/**
	 * 经理工号
	 */
	@BeanField(name = "manager_job_no", title = "经理工号", type = FieldType.String, length = 8)
	private String manager_job_no;

	/**
	 * 部长印
	 */
	@BeanField(name = "sign_minister_id", title = "部长印", type = FieldType.String, length = 11)
	private String sign_minister_id;

	/**
	 * 部长名称
	 */
	@BeanField(name = "minister_name", title = "部长名称", type = FieldType.String, length = 8)
	private String minister_name;

	/**
	 * 部长工号
	 */
	@BeanField(name = "minister_job_no", title = "部长工号", type = FieldType.String, length = 8)
	private String minister_job_no;

	/**
	 * 含有“慧采”
	 */
	@BeanField(name = "spec", title = "含有“慧采”", type = FieldType.Integer, length = 1, notNull = true)
	private String spec;

	// 文件名称
	private List<String> file_names;

	// 盖章标记("1":"经理印","2":"部长印")
	private String confirm_flg;

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

	public String getOrder_date() {
		return order_date;
	}

	public void setOrder_date(String order_date) {
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

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public List<String> getFile_names() {
		return file_names;
	}

	public void setFile_names(List<String> file_names) {
		this.file_names = file_names;
	}

	public String getConfirm_flg() {
		return confirm_flg;
	}

	public void setConfirm_flg(String confirm_flg) {
		this.confirm_flg = confirm_flg;
	}

}