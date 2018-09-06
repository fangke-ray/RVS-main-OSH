package com.osh.rvs.form.data;

import org.apache.struts.action.ActionForm;

import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.service.PauseFeatureService;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;

public class OperatorProductionForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6600483649158437624L;

	private String operator_id;
	private String job_no;
	private String name;
	private String section_id;
	private String line_id;
	private String delete_flg;
	private String position_id;
	private String position_name;
	private String worktime;
	private String main_ability;
	
	/*Detail*/
	@BeanField(title="开始时间",name="action_time", type=FieldType.Date)
	private String action_time;
	@BeanField(title="结束时间",name="finish_time", type=FieldType.Date)
	private String finish_time;
	private String sorc_no;
	private String model_name;
	private String process_code;
	private String operate_result;
	private String pace;
	
	@BeanField(title="开始时间起", name="action_time_start", type=FieldType.Date)
	private String action_time_start;
	@BeanField(title="开始时间止", name="action_time_end", type=FieldType.Date)
	private String action_time_end;
	private String line_name;
	
	@BeanField(title="原因", name="reason", type=FieldType.Integer)
	private String reason;
	private String reasonText;
	@BeanField(title="加班理由", name="comments", type=FieldType.String)
	private String comments;
	private String leak;
	@BeanField(title="暂停结束时间", name="pause_finish_time", type=FieldType.DateTime)
	private String pause_finish_time; 
	@BeanField(title="暂停开始时间", name="pause_start_time", type=FieldType.DateTime)
	private String pause_start_time;
	
	@BeanField(title="记号", name="overwork_reason", type=FieldType.Integer)
	private String overwork_reason;
	private String overwork_reason_name;
	
	public String getOverwork_reason_name() {
		if (overwork_reason != null) {
			return CodeListUtils.getValue("plan_overwork_reason", overwork_reason);
		}
		return overwork_reason_name;
	}
	public void setOverwork_reason_name(String overwork_reason_name) {
		this.overwork_reason_name = overwork_reason_name;
	}
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
	public String getOverwork_reason() {
		return overwork_reason;
	}
	public void setOverwork_reason(String overwork_reason) {
		this.overwork_reason = overwork_reason;
	}
	public String getPause_finish_time() {
		return pause_finish_time;
	}
	public void setPause_finish_time(String pause_finish_time) {
		this.pause_finish_time = pause_finish_time;
	}
	public String getPause_start_time() {
		return pause_start_time;
	}
	public void setPause_start_time(String pause_start_time) {
		this.pause_start_time = pause_start_time;
	}
	public String getLeak() {
		return leak;
	}
	public void setLeak(String leak) {
		this.leak = leak;
	}
	
	public String getReasonText() {
		if (reason != null) {
			return PauseFeatureService.getPauseReasonByCode(reason);
		}
		return reasonText;
	}
	public void setReasonText(String reasonText) {
		this.reasonText = reasonText;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
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
	public String getAction_time_start() {
		return action_time_start;
	}
	public void setAction_time_start(String action_time_start) {
		this.action_time_start = action_time_start;
	}
	public String getAction_time_end() {
		return action_time_end;
	}
	public void setAction_time_end(String action_time_end) {
		this.action_time_end = action_time_end;
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
	public String getWorktime() {
		if (worktime != null) {
			return RvsUtils.formatMinutes(Integer.valueOf(worktime)/60);
		}
		return worktime;
	}
	public void setWorktime(String worktime) {
		this.worktime = worktime;
	}
	public String getMain_ability() {
		return main_ability;
	}
	public void setMain_ability(String main_ability) {
		this.main_ability = main_ability;
	}
	public String getAction_time() {
		return action_time;
	}
	public void setAction_time(String action_time) {
		this.action_time = action_time;
	}
	public String getFinish_time() {
		return finish_time;
	}
	public void setFinish_time(String finish_time) {
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

	public String filterSpecialValue(String value, String preName) {
		String rt = "";
		if ("comments".equals(preName)) {
			if (!CommonStringUtil.isEmpty(reasonText)) {
				rt += reasonText + " ";
			}
			if (!CommonStringUtil.isEmpty(value)) {
				rt += value;
			}
			return rt;
		}
		return value;
	}
	
}
