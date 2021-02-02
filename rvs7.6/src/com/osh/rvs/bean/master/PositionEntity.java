package com.osh.rvs.bean.master;

import java.io.Serializable;
import java.sql.Timestamp;

public class PositionEntity implements Serializable {

	private static final long serialVersionUID = 6743605232513994881L;
	/** 工位 ID */
	private String position_id;
	/** 工位名称 */
	private String name;

	/** 工程 ID */
	private String line_id;
	/** 工程名称 */
	private String line_name;
	/** 进度代码 */
	private String process_code;
	/** 删除类别 */
	private Integer delete_flg = 0;
	/** 最后更新人 */
	private String updated_by;
	/** 最后更新时间 */
	private Timestamp updated_time;

	/** 小修理工时比率 **/
	private Integer light_worktime_rate;

	/** 独立小修理工位标记 **/
	private Integer light_division_flg;

	/** 特殊工位页面 */
	private String special_page;

	/** 映射工位 ID */
	private String mapping_position_id;
	private String unitized_position_id;

	private Integer chief;

	/**
	 * 取得工位 ID
	 * 
	 * @return position_id 工位 ID
	 */
	public String getPosition_id() {
		return position_id;
	}

	/**
	 * 工位 ID设定
	 * 
	 * @param section_id
	 *            工位 ID
	 */
	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	/**
	 * 取得工位名称
	 * 
	 * @return name 工位名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 工位名称设定
	 * 
	 * @param name
	 *            工位名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 取得删除标记
	 * 
	 * @return delete_flg 删除标记
	 */
	public Integer getDelete_flg() {
		return delete_flg;
	}

	/**
	 * 删除标记设定
	 * 
	 * @param delete_flg
	 *            删除标记
	 */
	public void setDelete_flg(Integer delete_flg) {
		this.delete_flg = delete_flg;
	}

	/**
	 * 取得最后更新人
	 * 
	 * @return updated_by 最后更新人
	 */
	public String getUpdated_by() {
		return updated_by;
	}

	/**
	 * 最后更新人设定
	 * 
	 * @param updated_by
	 *            最后更新人
	 */
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}

	/**
	 * 取得最后更新时间
	 * 
	 * @return updated_time 最后更新时间
	 */
	public Timestamp getUpdated_time() {
		return updated_time;
	}

	/**
	 * 最后更新时间设定
	 * 
	 * @param updated_time
	 *            最后更新时间
	 */
	public void setUpdated_time(Timestamp updated_time) {
		this.updated_time = updated_time;
	}

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public Integer getLight_worktime_rate() {
		return light_worktime_rate;
	}

	public void setLight_worktime_rate(Integer light_worktime_rate) {
		this.light_worktime_rate = light_worktime_rate;
	}

	public Integer getLight_division_flg() {
		return light_division_flg;
	}

	public void setLight_division_flg(Integer light_division_flg) {
		this.light_division_flg = light_division_flg;
	}

	/**
	 * 文字列化
	 * 
	 * @return 文字列
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.position_id).append(", ");
		buffer.append(this.process_code).append(", ");
		buffer.append(this.line_id).append(". ");
		return buffer.toString();
	}

	public String getMapping_position_id() {
		return mapping_position_id;
	}

	public void setMapping_position_id(String mapping_position_id) {
		this.mapping_position_id = mapping_position_id;
	}

	public Integer getChief() {
		return chief;
	}

	public void setChief(Integer chief) {
		this.chief = chief;
	}

	public String getSpecial_page() {
		return special_page;
	}

	public void setSpecial_page(String special_page) {
		this.special_page = special_page;
	}

	public String getUnitized_position_id() {
		return unitized_position_id;
	}

	public void setUnitized_position_id(String unitized_position_id) {
		this.unitized_position_id = unitized_position_id;
	}
}
