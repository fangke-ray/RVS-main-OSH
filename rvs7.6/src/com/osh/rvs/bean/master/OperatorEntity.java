package com.osh.rvs.bean.master;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class OperatorEntity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -727508957958906413L;
	/** 主要角色 name */
	private String role_name;

	/** 担当人 ID */
	private String operator_id;
	/** 担当人姓名 */
	private String name;
	/** 工号 */
	private String job_no;
	/** 密码 */
	private String pwd;
	/** 密码修改日 */
	private Date pwd_date;
	/** 是否记录工时 */
	private Integer work_count_flg = 0;
	/** 课室 ID */
	private String section_id;
	/** 线 ID */
	private String line_id;
	/** 主要角色 ID */
	private String role_id;
	/** 主要担当工位 ID */
	private String position_id;
	/** 邮箱地址 */
	private String email;
	/** 删除类别 */
	private boolean delete_flg = false;
	/** 最后更新人 */
	private String updated_by;
	/** 最后更新时间 */
	private Timestamp updated_time;
	private Integer rank_kind;

	private String process_code;
	/** 分线 */
	private Integer px;

	private Integer af_ability;

	/**
	 * 取得角色 ID
	 * @return role_id 角色 ID
	 */
	public String getRole_id() {
		return role_id;
	}

	public String getRole_name() {
		return role_name;
	}

	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}

	/**
	 * 角色 ID设定
	 * @param role_id 角色 ID
	 */
	public void setRole_id(String role_id) {
		this.role_id = role_id;
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

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public Date getPwd_date() {
		return pwd_date;
	}

	public void setPwd_date(Date pwd_date) {
		this.pwd_date = pwd_date;
	}

	public Integer getWork_count_flg() {
		return work_count_flg;
	}


	public void setWork_count_flg(Integer work_count_flg) {
		this.work_count_flg = work_count_flg;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	/**
	 * 文字列化
	 * 
	 * @return 文字列
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.operator_id).append(", ");
		buffer.append(this.role_id).append(", ");
		buffer.append(this.name).append(". ");
		return buffer.toString();
	}

	public Integer getRank_kind() {
		return rank_kind;
	}

	public void setRank_kind(Integer rank_kind) {
		this.rank_kind = rank_kind;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public Integer getPx() {
		return px;
	}

	public void setPx(Integer px) {
		this.px = px;
	}

	public Integer getAf_ability() {
		return af_ability;
	}

	public void setAf_ability(Integer af_ability) {
		this.af_ability = af_ability;
	}
}
