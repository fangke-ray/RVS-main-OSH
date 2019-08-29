package com.osh.rvs.bean.qf;

import java.io.Serializable;
import java.util.Date;

/**
 * 间接人员作业信息
 * 
 * @author liuxb
 * 
 */
public class AfProductionFeatureEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3771707442379602746L;

	/**
	 * KEY
	 */
	private String af_pf_key;

	/**
	 * 作业内容
	 */
	private Integer production_type;

	/**
	 * 操作者 ID
	 */
	private String operator_id;

	/**
	 * 处理开始时间
	 */
	private Date action_time;

	/**
	 * 处理结束时间
	 */
	private Date finish_time;

	/**
	 * 对应工位
	 */
	private String position_id;

	/**
	 * 对应工位作业状态
	 */
	private String operate_result;

	/**
	 * 分类
	 */
	private Integer division;

	/**
	 * 统计数量
	 */
	private Integer cnt;

	public String getAf_pf_key() {
		return af_pf_key;
	}

	public void setAf_pf_key(String af_pf_key) {
		this.af_pf_key = af_pf_key;
	}

	public Integer getProduction_type() {
		return production_type;
	}

	public void setProduction_type(Integer production_type) {
		this.production_type = production_type;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
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

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public String getOperate_result() {
		return operate_result;
	}

	public void setOperate_result(String operate_result) {
		this.operate_result = operate_result;
	}

	public Integer getDivision() {
		return division;
	}

	public void setDivision(Integer division) {
		this.division = division;
	}

	public Integer getCnt() {
		return cnt;
	}

	public void setCnt(Integer cnt) {
		this.cnt = cnt;
	}
}
