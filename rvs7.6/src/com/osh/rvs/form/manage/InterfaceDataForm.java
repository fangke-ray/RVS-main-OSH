package com.osh.rvs.form.manage;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class InterfaceDataForm extends ActionForm {

	private static final long serialVersionUID = -6772953557829159713L;

	/** 维修对象机种 ID */
	@BeanField(title = "", name = "if_sap_message_key", primaryKey = true, length = 11)
	private String if_sap_message_key;
	/** 最后更新时间 */
	@BeanField(title = "", name = "seq", primaryKey = true, length = 3)
	private String seq;
	/** 处理结果 */
	@BeanField(title = "", name = "content", type = FieldType.String, length = 500)
	private String content;
	/** 处理结果 */
	@BeanField(title = "", name = "resolved", type = FieldType.Integer, length = 4)
	private String resolved;
	/** 报错信息 */
	@BeanField(title = "", name = "invalid_message", type = FieldType.String, length = 500)
	private String invalid_message;
	/** 最后更新时间 */
	@BeanField(title = "", name = "kind", type = FieldType.String, length = 15)
	private String kind;
	/** OMR通知单号 */
	private String OMRNotifiNo;

	public String getIf_sap_message_key() {
		return if_sap_message_key;
	}
	public void setIf_sap_message_key(String if_sap_message_key) {
		this.if_sap_message_key = if_sap_message_key;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getResolved() {
		return resolved;
	}
	public void setResolved(String resolved) {
		this.resolved = resolved;
	}
	public String getInvalid_message() {
		return invalid_message;
	}
	public void setInvalid_message(String invalid_message) {
		this.invalid_message = invalid_message;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getOMRNotifiNo() {
		return OMRNotifiNo;
	}
	public void setOMRNotifiNo(String oMRNotifiNo) {
		OMRNotifiNo = oMRNotifiNo;
	}
}
