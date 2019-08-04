package com.osh.rvs.bean.master;

import java.io.Serializable;
import java.sql.Timestamp;

public class CategoryEntity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -5960636192988879037L;

	/** 维修对象机种 ID */
	private String category_id;
	/** 维修对象机种名称 */
	private String name;
	/** 维修对象种类 */
	private Integer kind = 0;
	/** 默认流程 ID */
	private String default_pat_id;
	/** 附周转箱 */
	private Integer with_case;
	/** 删除类别 */
	private boolean delete_flg = false;
	/** 最后更新人 */
	private String updated_by;
	/** 最后更新时间 */
	private Timestamp updated_time;

	/**
	 * 取得维修对象机种 ID
	 * @return category_id 维修对象机种 ID
	 */
	public String getCategory_id() {
		return category_id;
	}

	/**
	 * 维修对象机种 ID设定
	 * @param category_id 维修对象机种 ID
	 */
	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	/**
	 * 取得维修对象机种名称
	 * @return name 维修对象机种名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 维修对象机种名称设定
	 * @param name 维修对象机种名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 取得维修对象种类
	 * @return kind 维修对象种类
	 */
	public Integer getKind() {
		return kind;
	}

	/**
	 * 维修对象种类设定
	 * @param kind 维修对象种类
	 */
	public void setKind(Integer kind) {
		this.kind = kind;
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

	public String getDefault_pat_id() {
		return default_pat_id;
	}

	public void setDefault_pat_id(String default_pat_id) {
		this.default_pat_id = default_pat_id;
	}

	/**
	 * 文字列化
	 * 
	 * @return 文字列
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.category_id).append(", ");
		buffer.append(this.name).append(", ");
		buffer.append(this.kind).append(". ");
		return buffer.toString();
	}

	public Integer getWith_case() {
		return with_case;
	}

	public void setWith_case(Integer with_case) {
		this.with_case = with_case;
	}
}
