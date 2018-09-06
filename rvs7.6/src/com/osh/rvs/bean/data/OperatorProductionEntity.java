package com.osh.rvs.bean.data;

import java.io.Serializable;
import java.util.Date;

public class OperatorProductionEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 905399184040522971L;

	
	private String operator_id;
	private String job_no;
	private String name;
	private String section_id;
	private String line_id;
	private String delete_flg;
	private String position_id;
	private String position_name;
	private Integer worktime;
	private String main_ability;
	
	/*Detail*/
	private Date action_time;
	private Date finish_time;
	private String sorc_no;
	private String model_name;
	private String process_code;
	private Date action_time_start;
	private Date action_time_end;
	private String line_name;
	private String operate_result;
	private String pace;
	
	private Integer reason;
	private String comments;
	private String leak;
	
	private Date pause_finish_time;
	private Date pause_start_time;
	
	private Integer overwork_reason;
	
	
	public String getOperate_result() {
		return operate_result;
	}
	public void setOperate_result(String operate_result) {
		this.operate_result = operate_result;
	}
	public String getPace() {
		return pace;
	}
	public void setPace(String pace) {
		this.pace = pace;
	}
	public Integer getOverwork_reason() {
		return overwork_reason;
	}
	public void setOverwork_reason(Integer overwork_reason) {
		this.overwork_reason = overwork_reason;
	}
	public Date getPause_finish_time() {
		return pause_finish_time;
	}
	public void setPause_finish_time(Date pause_finish_time) {
		this.pause_finish_time = pause_finish_time;
	}
	public Date getPause_start_time() {
		return pause_start_time;
	}
	public void setPause_start_time(Date pause_start_time) {
		this.pause_start_time = pause_start_time;
	}
	public String getLeak() {
		return leak;
	}
	public void setLeak(String leak) {
		this.leak = leak;
	}
	public Integer getReason() {
		return reason;
	}
	public void setReason(Integer reason) {
		this.reason = reason;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public String getLine_name() {
		return line_name;
	}
	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}
	public Date getAction_time_start() {
		return action_time_start;
	}
	public void setAction_time_start(Date action_time_start) {
		this.action_time_start = action_time_start;
	}
	public Date getAction_time_end() {
		return action_time_end;
	}
	public void setAction_time_end(Date action_time_end) {
		this.action_time_end = action_time_end;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getDelete_flg() {
		return delete_flg;
	}
	public void setDelete_flg(String delete_flg) {
		this.delete_flg = delete_flg;
	}
	public String getPosition_id() {
		return position_id;
	}
	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}
	public String getPosition_name() {
		return position_name;
	}
	public void setPosition_name(String position_name) {
		this.position_name = position_name;
	}
	public Integer getWorktime() {
		return worktime;
	}
	public void setWorktime(Integer worktime) {
		this.worktime = worktime;
	}
	public String getMain_ability() {
		return main_ability;
	}
	public void setMain_ability(String main_ability) {
		this.main_ability = main_ability;
	}
	public Date getAction_time() {
		return action_time;
	}
	public void setAction_time(Date action_time) {
		this.action_time = action_time;
	}
	public Date getFinish_time() {
		return finish_time;
	}
	public void setFinish_time(Date finish_time) {
		this.finish_time = finish_time;
	}
	public String getSorc_no() {
		return sorc_no;
	}
	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
	}
	public String getModel_name() {
		return model_name;
	}
	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}
	public String getProcess_code() {
		return process_code;
	}
	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}
	
	
}
