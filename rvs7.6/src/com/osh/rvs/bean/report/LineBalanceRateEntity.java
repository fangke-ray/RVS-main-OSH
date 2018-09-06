package com.osh.rvs.bean.report;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @Title LineBalanceRateEntity.java
 * @Project rvs
 * @Package com.osh.rvs.bean.report
 * @ClassName: LineBalanceRateEntity
 * @Description: 流水线平衡率分析
 * @author houp
 * @date 2016-10-8 下午1:56:26
 */
public class LineBalanceRateEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -865710436360379850L;

	/** 机种ID **/
	private String category_id;

	/** 型号ID **/
	private String model_id;

	/** 等级 **/
	private Integer level;

	/** 课室ID **/
	private String section_id;

	/** 工位代码 **/
	private String process_code;

	/** 工位名 **/
	private String position_name;

	/** 工位ID **/
	private String line_id;

	/** 是否包含返工 **/
	private Integer rework;

	/** 作业开始时间 **/
	private Date finish_time_start;

	/** 作业结束时间 **/
	private Date finish_time_end;

	/** 平均耗时 **/
	private Integer avgWorkTime;

	/** 分线 **/
	private Integer px;

	private String process_codes;

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public Integer getRework() {
		return rework;
	}

	public void setRework(Integer rework) {
		this.rework = rework;
	}

	public Date getFinish_time_start() {
		return finish_time_start;
	}

	public void setFinish_time_start(Date finish_time_start) {
		this.finish_time_start = finish_time_start;
	}

	public Date getFinish_time_end() {
		return finish_time_end;
	}

	public void setFinish_time_end(Date finish_time_end) {
		this.finish_time_end = finish_time_end;
	}

	public Integer getAvgWorkTime() {
		return avgWorkTime;
	}

	public void setAvgWorkTime(Integer avgWorkTime) {
		this.avgWorkTime = avgWorkTime;
	}

	public Integer getPx() {
		return px;
	}

	public void setPx(Integer px) {
		this.px = px;
	}

	public String getProcess_codes() {
		return process_codes;
	}

	public void setProcess_codes(String process_codes) {
		this.process_codes = process_codes;
	}

	public String getPosition_name() {
		return position_name;
	}

	public void setPosition_name(String position_name) {
		this.position_name = position_name;
	}

}
