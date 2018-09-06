package com.osh.rvs.bean.data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author fxc
 * PS: String 在Mapper.XML中IF判断条件 !=NULL and !='';
 * 		其他  !=NULL
 */
public class ProductionFeatureEntity implements Serializable  {

	private static final long serialVersionUID = 2466864489515207180L;

	private String material_id;
	
	private String position_id;
	private String process_code;
	private String position_name;
	private String section_id;
	private String section_name;
	private String line_id;
	private String line_name;
	private Integer pace;
	
	private String operator_id;
	private String operator_name;
	private String job_no;
	/**
	 * 花费时间
	 */
	private Integer use_seconds;
	private Integer operate_result;
	
	private Date action_time;
	
	private Date finish_time;
	
	private String pcs_inputs;
	
	private String pcs_comments;
	
	private Integer rework;
	private String beforeRework;
	
	private String noRework; 
	private String onlyFinish;

	private String serial_no; // for temp TODO
	private String jam_code;
	
	
	public String getNoRework() {
		return noRework;
	}

	public void setNoRework(String noRework) {
		this.noRework = noRework;
	}

	public String getOnlyFinish() {
		return onlyFinish;
	}

	public void setOnlyFinish(String onlyFinish) {
		this.onlyFinish = onlyFinish;
	}
	public String getBeforeRework() {
		return beforeRework;
	}

	public void setBeforeRework(String beforeRework) {
		this.beforeRework = beforeRework;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public Integer getPace() {
		return pace;
	}

	public void setPace(Integer pace) {
		this.pace = pace;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public Integer getOperate_result() {
		return operate_result;
	}

	public void setOperate_result(Integer operate_result) {
		this.operate_result = operate_result;
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
	
	public String getPcs_inputs() {
		return pcs_inputs;
	}

	public void setPcs_inputs(String pcs_inputs) {
		this.pcs_inputs = pcs_inputs;
	}

	public String getPcs_comments() {
		return pcs_comments;
	}

	public String getJob_no() {
		return job_no;
	}

	public void setJob_no(String job_no) {
		this.job_no = job_no;
	}

	public void setPcs_comments(String pcs_comments) {
		this.pcs_comments = pcs_comments;
	}

	public Integer getRework() {
		return rework;
	}

	public void setRework(Integer rework) {
		this.rework = rework;
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

	public Integer getUse_seconds() {
		return use_seconds;
	}

	public void setUse_seconds(Integer use_seconds) {
		this.use_seconds = use_seconds;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getPosition_name() {
		return position_name;
	}

	public void setPosition_name(String position_name) {
		this.position_name = position_name;
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

	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
		
	}
	public String getSerial_no() {
		return this.serial_no;
	}

	public String getJam_code() {
		return jam_code;
	}

	public void setJam_code(String jam_code) {
		this.jam_code = jam_code;
	}

}
