package com.osh.rvs.bean.master;

import java.io.Serializable;
import java.sql.Timestamp;

public class LineEntity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -4881587195110044046L;
	/** 工程 ID */
	private String line_id;
	/** 工程名称 */
	private String name;
	/** 是否计划管理对象 */
	private Boolean inline_flg = false;
	/** 删除类别 */
	private boolean delete_flg = false;
	/** 最后更新人 */
	private String updated_by;
	/** 最后更新时间 */
	private Timestamp updated_time;

	/**
	 * 取得工程 ID
	 * @return line_id 工程 ID
	 */
	public String getLine_id() {
		return line_id;
	}

	/**
	 * 工程 ID设定
	 * @param line_id 工程 ID
	 */
	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	/**
	 * 取得工程名称
	 * @return name 工程名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 工程名称设定
	 * @param name 工程名称
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
		buffer.append(this.line_id).append(", ");
		buffer.append(this.name).append(". ");
		return buffer.toString();
	}

	public Boolean getInline_flg() {
		return inline_flg;
	}

	public void setInline_flg(Boolean inline_flg) {
		this.inline_flg = inline_flg;
	}
}
