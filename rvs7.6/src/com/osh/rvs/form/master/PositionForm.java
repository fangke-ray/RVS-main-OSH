package com.osh.rvs.form.master;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class PositionForm extends ActionForm {

	/** serialVersionUID */
	private static final long serialVersionUID = -4179616864069539324L;

	/** 工位 ID */
	@BeanField(title = "工位 ID", name = "position_id", primaryKey = true, length = 11)
	private String id;
	/** 工位名称 */
	@BeanField(title = "工位名称", name = "name", notNull = true, length = 15)
	private String name;
	/** 工程 ID */
	@BeanField(title = "工程 ID", name = "line_id", notNull = true)
	private String line_id;
	/** 工程名称 */
	@BeanField(title = "工程名称", name = "line_name")
	private String line_name;
	/** 进度代码 */
	@BeanField(title = "进度代码", name = "process_code", notNull = true, length = 3)
	private String process_code;
	/** 最后更新人 */
	@BeanField(title = "更新者", name = "updated_by")
	private String updated_by;
	/** 最后更新时间 */
	@BeanField(title = "更新时间", name = "updated_time", type = FieldType.TimeStamp)
	private String updated_time;

	/** 小修理工时比率 **/
	@BeanField(title = "小修理工时比率", name = "light_worktime_rate", type = FieldType.Integer, length = 3)
	private String light_worktime_rate;

	/** 独立小修理工位标记 **/
	@BeanField(title = "独立小修理工位标记", name = "light_division_flg", type = FieldType.Integer, length = 1)
	private String light_division_flg;

	/**
	 * 取得工位 ID
	 * 
	 * @return id 工位 ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 工位 ID设定
	 * 
	 * @param id
	 *            工位 ID
	 */
	public void setId(String id) {
		this.id = id;
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
	 * 角色工位名称定
	 * 
	 * @param name
	 *            工位名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 取得工程 ID
	 * 
	 * @return line_id 工程 ID
	 */
	public String getLine_id() {
		return line_id;
	}

	/**
	 * 工程 ID设定
	 * 
	 * @param line_id
	 *            工程 ID
	 */
	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	/**
	 * 取得工程名称
	 * 
	 * @return line_name 工程名称
	 */
	public String getLine_name() {
		return line_name;
	}

	/**
	 * 工程名称设定
	 * 
	 * @param line_name
	 *            工程名称
	 */
	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	/**
	 * 取得进度代码
	 * 
	 * @return line_name 进度代码
	 */
	public String getProcess_code() {
		return process_code;
	}

	/**
	 * 进度代码设定
	 * 
	 * @param line_name
	 *            进度代码
	 */
	public void setProcess_code(String process_code) {
		this.process_code = process_code;
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
	public String getUpdated_time() {
		return updated_time;
	}

	/**
	 * 最后更新时间设定
	 * 
	 * @param updated_time
	 *            最后更新时间
	 */
	public void setUpdated_time(String updated_time) {
		this.updated_time = updated_time;
	}

	public String getLight_worktime_rate() {
		return light_worktime_rate;
	}

	public void setLight_worktime_rate(String light_worktime_rate) {
		this.light_worktime_rate = light_worktime_rate;
	}

	public String getLight_division_flg() {
		return light_division_flg;
	}

	public void setLight_division_flg(String light_division_flg) {
		this.light_division_flg = light_division_flg;
	}

}
