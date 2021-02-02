package com.osh.rvs.bean.master;

import java.io.Serializable;

public class OperatorNotifyEntity implements Serializable {

	private static final long serialVersionUID = -385224615219785208L;

	/** 担当人 ID */
	private String operator_id;
	/** 担当人姓名 */
	private String name;
	/** 工号 */
	private String job_no;

	/** 主要担当工位 ID */
	private String position_id;

	private String category_id;

	//管理员
	private String manager_operator_id;      

	//管理员
	private String manager_operator_name;

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJob_no() {
		return job_no;
	}

	public void setJob_no(String job_no) {
		this.job_no = job_no;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getManager_operator_id() {
		return manager_operator_id;
	}

	public void setManager_operator_id(String manager_operator_id) {
		this.manager_operator_id = manager_operator_id;
	}

	public String getManager_operator_name() {
		return manager_operator_name;
	}

	public void setManager_operator_name(String manager_operator_name) {
		this.manager_operator_name = manager_operator_name;
	}      

}
