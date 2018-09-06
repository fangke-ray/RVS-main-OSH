package com.osh.rvs.form.master;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class ProcessAssignForm extends ActionForm {

	private static final long serialVersionUID = -8666237499009445166L;

	/** 参照类型 */
	@BeanField(title = "参照类型", name = "refer_type", type = FieldType.Integer, primaryKey = true)
	private Integer refer_type;
	/** 参照 ID */
	@BeanField(title = "参照 ID", name = "refer_id", primaryKey = true, length = 11)
	private String refer_id;
	/** 分枝 */
	@BeanField(title = "分枝", name = "line_id", length = 11)
	private String line_id;
	/** 工位 ID */
	@BeanField(title = "工位 ID", name = "position_id", length = 11)
	private String position_id;
	/** 顺位标记 */
	@BeanField(title = "顺位标记", name = "sign_position_id", length = 11)
	private String sign_position_id;
	/** 先决标记 */
	@BeanField(title = "先决标记", name = "prev_position_id", length = 11)
	private String prev_position_id;
	/** 后位标记 */
	@BeanField(title = "后位标记", name = "next_position_id", length = 11)
	private String next_position_id;

	public Integer getRefer_type() {
		return refer_type;
	}

	public void setRefer_type(Integer refer_type) {
		this.refer_type = refer_type;
	}

	public String getRefer_id() {
		return refer_id;
	}

	public void setRefer_id(String refer_id) {
		this.refer_id = refer_id;
	}

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public String getSign_position_id() {
		return sign_position_id;
	}

	public void setSign_position_id(String sign_position_id) {
		this.sign_position_id = sign_position_id;
	}

	public String getPrev_position_id() {
		return prev_position_id;
	}

	public void setPrev_position_id(String prev_position_id) {
		this.prev_position_id = prev_position_id;
	}

	public String getNext_position_id() {
		return next_position_id;
	}

	public void setNext_position_id(String next_position_id) {
		this.next_position_id = next_position_id;
	}


}
