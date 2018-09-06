package com.osh.rvs.bean.manage;

import java.io.Serializable;

public class InterfaceDataEntity implements Serializable {

	private static final long serialVersionUID = 3347910855015746501L;

	private Integer if_sap_message_key;

	private Integer seq;

	private String content;

	// 0:没处理 1:处理过 2:处理出错
	private Integer resolved;
	
	private String invalid_message;

	private String kind;

	public Integer getIf_sap_message_key() {
		return if_sap_message_key;
	}

	public void setIf_sap_message_key(Integer if_sap_message_key) {
		this.if_sap_message_key = if_sap_message_key;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getResolved() {
		return resolved;
	}

	public void setResolved(Integer resolved) {
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
}
