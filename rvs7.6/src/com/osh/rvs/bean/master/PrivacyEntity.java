package com.osh.rvs.bean.master;

import java.io.Serializable;

public class PrivacyEntity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -5960636192988879037L;

	 
	/** 权限 ID */
	private String privacy_id;
	/** 权限名 */
	private String name;
	/** 权限说明 */
	private String comments;
	/** 删除类别 */
	private boolean delete_flg = false;

	/**
	 * 取得权限 ID
	 * @return privacy_id 权限 ID
	 */
	public String getPrivacy_id() {
		return privacy_id;
	}

	/**
	 * 权限 ID设定
	 * @param privacy_id 权限 ID
	 */
	public void setPrivacy_id(String privacy_id) {
		this.privacy_id = privacy_id;
	}

	/**
	 * 取得权限名
	 * @return name 权限名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 权限名设定
	 * @param name 权限名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 取得权限说明
	 * @return comments 权限说明
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * 权限说明设定
	 * @param comments 权限说明
	 */
	public void setComments(String comments) {
		this.comments = comments;
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
	 * 文字列化
	 * 
	 * @return 文字列
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.privacy_id).append(", ");
		buffer.append(this.name).append(", ");
		buffer.append(this.comments.length() > 15 ? this.comments.substring(0,15) : this.comments).append(". ");
		return buffer.toString();
	}
}
