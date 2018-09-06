package com.osh.rvs.bean.master;

import java.io.Serializable;
import java.sql.Timestamp;

public class RoleEntity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -9031293245498742658L;

	/** 角色 ID */
	private String role_id;
	/** 角色名称 */
	private String name;
	/** 等级类型 */
	private Integer rank_kind;
	/** 删除类别 */
	private boolean delete_flg = false;
	/** 最后更新人 */
	private String updated_by;
	/** 最后更新时间 */
	private Timestamp updated_time;

	/**
	 * 取得角色 ID
	 * @return role_id 角色 ID
	 */
	public String getRole_id() {
		return role_id;
	}

	/**
	 * 角色 ID设定
	 * @param role_id 角色 ID
	 */
	public void setRole_id(String role_id) {
		this.role_id = role_id;
	}

	/**
	 * 取得角色名称
	 * @return name 角色名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 角色名称设定
	 * @param name 角色名称
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
}
