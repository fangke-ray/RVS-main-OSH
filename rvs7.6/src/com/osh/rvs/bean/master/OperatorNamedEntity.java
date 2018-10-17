package com.osh.rvs.bean.master;

import java.io.Serializable;
import java.sql.Timestamp;

public class OperatorNamedEntity extends OperatorEntity implements Serializable {

	private static final long serialVersionUID = 5522961380094795800L;

	/** 担当人 ID */
	private String operator_id;
	/** 担当人姓名 */
	private String name;
	/** 工号 */
	private String job_no;
	/** 是否记录工时 */
	private Integer work_count_flg = 0;
	/** 邮箱地址 */
	private String email;
	/** 最后更新人 */
	private String updated_by;
	/** 最后更新时间 */
	private Timestamp updated_time;
	/** 课室名 */
	private String section_name;
	/** 工程名 */
	private String line_name;

	/** 所在角色名 */
	private String role_name;
	private Integer rank_kind;
	/** 分线 */
	private Integer px;

	public String getSection_name() {
		return section_name;
	}


	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}


	public String getLine_name() {
		return line_name;
	}


	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}


	public String getRole_name() {
		return role_name;
	}


	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}
	/**
	 * 取得用户名称
	 * @return name 用户名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 用户名称设定
	 * @param name 用户名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	public Integer getWork_count_flg() {
		return work_count_flg;
	}


	public void setWork_count_flg(Integer work_count_flg) {
		this.work_count_flg = work_count_flg;
	}

	/**
	 * 文字列化
	 * 
	 * @return 文字列
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.operator_id).append(", ");
		buffer.append(this.role_name).append(", ");
		buffer.append(this.name).append(". ");
		return buffer.toString();
	}


	public String getOperator_id() {
		return operator_id;
	}


	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}


	public String getJob_no() {
		return job_no;
	}


	public void setJob_no(String job_no) {
		this.job_no = job_no;
	}



	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getUpdated_by() {
		return updated_by;
	}


	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}


	public Timestamp getUpdated_time() {
		return updated_time;
	}


	public void setUpdated_time(Timestamp updated_time) {
		this.updated_time = updated_time;
	}


	public Integer getRank_kind() {
		return rank_kind;
	}


	public void setRank_kind(Integer rank_kind) {
		this.rank_kind = rank_kind;
	}

	public Integer getPx() {
		return px;
	}


	public void setPx(Integer px) {
		this.px = px;
	}
}
