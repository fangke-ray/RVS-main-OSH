package com.osh.rvs.bean.data;

import java.io.Serializable;
import java.util.Date;

public class PostMessageEntity implements Serializable {

	private static final long serialVersionUID = -2117970202269354398L;

	private String post_message_id;
	private Integer level;
	private Date occur_time;
	private Integer reason;
	private String sender_id;
	private String sender_name;
	private String root_post_message_id;
	private String receiver_id;
	private String receiver_name;
	private Integer red_flg;
	private Integer answered_flg;
	private String content;
	private String operator_name;

	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Date getOccur_time() {
		return occur_time;
	}
	public void setOccur_time(Date occur_time) {
		this.occur_time = occur_time;
	}
	public Integer getReason() {
		return reason;
	}
	public void setReason(Integer reason) {
		this.reason = reason;
	}
	public String getReceiver_id() {
		return receiver_id;
	}
	public void setReceiver_id(String eceiver_id) {
		this.receiver_id = eceiver_id;
	}
	public String getPost_message_id() {
		return post_message_id;
	}
	public void setPost_message_id(String post_message_id) {
		this.post_message_id = post_message_id;
	}
	public String getSender_id() {
		return sender_id;
	}
	public void setSender_id(String sender_id) {
		this.sender_id = sender_id;
	}
	public String getSender_name() {
		return sender_name;
	}
	public void setSender_name(String sender_name) {
		this.sender_name = sender_name;
	}
	public String getRoot_post_message_id() {
		return root_post_message_id;
	}
	public void setRoot_post_message_id(String root_post_message_id) {
		this.root_post_message_id = root_post_message_id;
	}
	public String getReceiver_name() {
		return receiver_name;
	}
	public void setReceiver_name(String reciever_name) {
		this.receiver_name = reciever_name;
	}
	public Integer getRed_flg() {
		return red_flg;
	}
	public void setRed_flg(Integer red_flg) {
		this.red_flg = red_flg;
	}
	public Integer getAnswered_flg() {
		return answered_flg;
	}
	public void setAnswered_flg(Integer answered_flg) {
		this.answered_flg = answered_flg;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getOperator_name() {
		return operator_name;
	}
	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}
}
