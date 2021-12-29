package com.osh.rvs.bean;

public class LoginAccess {
	private Long access_timestamp;

	private String base64_captcha;

	private String base64_key;

	private int solution;

	public Long getAccess_timestamp() {
		return access_timestamp;
	}

	public void setAccess_timestamp(Long access_timestamp) {
		this.access_timestamp = access_timestamp;
	}

	public String getBase64_captcha() {
		return base64_captcha;
	}

	public void setBase64_captcha(String base64_captcha) {
		this.base64_captcha = base64_captcha;
	}

	public String getBase64_key() {
		return base64_key;
	}

	public void setBase64_key(String base64_key) {
		this.base64_key = base64_key;
	}

	public int getSolution() {
		return solution;
	}

	public void setSolution(int solution) {
		this.solution = solution;
	}
}
