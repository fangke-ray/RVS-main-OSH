package com.osh.rvs.bean.report;

import java.io.Serializable;
import java.util.Date;

/**
 * @author liuxb
 * 
 */
public class FoundryEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1899505339872504841L;

	/** 课室ID **/
	private String section_id;

	/** 课室名称 **/
	private String section_name;

	/*** 工程ID */
	private String line_id;

	/** 工程名称 **/
	private String line_name;

	/** 作业开始时间 **/
	private Date finish_time_start;

	/** 作业结束时间 **/
	private Date finish_time_end;

	/** 工位代码 **/
	private String process_code;

	/** 代工时间 **/
	private Integer foundryWork;

	/** 作业时间 **/
	private Integer mainWork;

	private String svg;

	private String svg2;

	/** 担当人 **/
	private String operator_name;
	private String job_no;

	/** 主要负责 **/
	private String mainIncharge;

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getSection_name() {
		return section_name;
	}

	public void setSection_name(String section_name) {
		this.section_name = section_name;
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

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public Integer getFoundryWork() {
		return foundryWork;
	}

	public void setFoundryWork(Integer foundryWork) {
		this.foundryWork = foundryWork;
	}

	public Integer getMainWork() {
		return mainWork;
	}

	public void setMainWork(Integer mainWork) {
		this.mainWork = mainWork;
	}

	public String getSvg() {
		return svg;
	}

	public void setSvg(String svg) {
		this.svg = svg;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

	public String getSvg2() {
		return svg2;
	}

	public void setSvg2(String svg2) {
		this.svg2 = svg2;
	}

	public String getMainIncharge() {
		return mainIncharge;
	}

	public void setMainIncharge(String mainIncharge) {
		this.mainIncharge = mainIncharge;
	}

	public String getJob_no() {
		return job_no;
	}

	public void setJob_no(String job_no) {
		this.job_no = job_no;
	}

}
