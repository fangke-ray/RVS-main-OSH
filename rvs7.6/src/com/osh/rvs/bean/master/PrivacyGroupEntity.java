package com.osh.rvs.bean.master;

import java.io.Serializable;

import framework.huiqing.common.util.CodeListUtils;

public class PrivacyGroupEntity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -3138802840537248917L;
	
	private Integer system_module;
	/** 权限 ID */
	private String privacy_id1;
	/** 权限名 */
	private String p_name1;
	/** 权限说明 */
	private String p_comments1;
	/** 权限 ID */
	private String privacy_id2;
	/** 权限名 */
	private String p_name2;
	/** 权限说明 */
	private String p_comments2;
	/** 权限 ID */
	private String privacy_id3;
	/** 权限名 */
	private String p_name3;
	/** 权限说明 */
	private String p_comments3;
	/**
	 * 文字列化
	 * 
	 * @return 文字列
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.system_module).append(", ");
		buffer.append(this.privacy_id1).append(", ");
		buffer.append(this.privacy_id2).append(", ");
		buffer.append(this.privacy_id3).append(". ");
		return buffer.toString();
	}

	public Integer getSystem_module() {
		return system_module;
	}
	public void setSystem_module(Integer system_module) {
		this.system_module = system_module;
	}
	public String getPrivacy_id1() {
		return privacy_id1;
	}
	public void setPrivacy_id1(String privacy_id1) {
		this.privacy_id1 = privacy_id1;
	}
	public String getP_name1() {
		return p_name1;
	}
	public void setP_name1(String p_name1) {
		this.p_name1 = p_name1;
	}
	public String getP_comments1() {
		return p_comments1;
	}
	public void setP_comments1(String p_comments1) {
		this.p_comments1 = p_comments1;
	}
	public String getPrivacy_id2() {
		return privacy_id2;
	}
	public void setPrivacy_id2(String privacy_id2) {
		this.privacy_id2 = privacy_id2;
	}
	public String getP_name2() {
		return p_name2;
	}
	public void setP_name2(String p_name2) {
		this.p_name2 = p_name2;
	}
	public String getP_comments2() {
		return p_comments2;
	}
	public void setP_comments2(String p_comments2) {
		this.p_comments2 = p_comments2;
	}
	public String getPrivacy_id3() {
		return privacy_id3;
	}
	public void setPrivacy_id3(String privacy_id3) {
		this.privacy_id3 = privacy_id3;
	}
	public String getP_name3() {
		return p_name3;
	}
	public void setP_name3(String p_name3) {
		this.p_name3 = p_name3;
	}
	public String getP_comments3() {
		return p_comments3;
	}
	public void setP_comments3(String p_comments3) {
		this.p_comments3 = p_comments3;
	}

	public String toTr() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<tr><td class='ui-state-default'>").append(CodeListUtils.getValue("privacy_system_module", ""+system_module)).append("</td>");
		if (privacy_id1 == null) {
			buffer.append("<td class='navli'/>");
		} else {
			buffer.append("<td class='avli' code='").append(privacy_id1).append("' alt='").append(p_comments1).append("'>")
			.append(p_name1).append("</td>");
		}
		if (privacy_id2 == null) {
			buffer.append("<td class='navli'/>");
		} else {
			buffer.append("<td class='avli' code='").append(privacy_id2).append("' alt='").append(p_comments2).append("'>")
			.append(p_name2).append("</td>");
		}
		if (privacy_id3 == null) {
			buffer.append("<td class='navli'/>");
		} else {
			buffer.append("<td class='avli' code='").append(privacy_id3).append("' alt='").append(p_comments3).append("'>")
			.append(p_name3).append("</td>");
		}
		buffer.append("</tr>");
		return buffer.toString();
	}
}
