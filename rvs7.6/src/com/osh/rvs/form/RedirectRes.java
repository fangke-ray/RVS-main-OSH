package com.osh.rvs.form;

import java.io.Serializable;

public class RedirectRes implements Serializable {

	private static final long serialVersionUID = -3368756225999515238L;

	private String redirect;

	public String getRedirect() {
		return redirect;
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}

	public String[] getErrors() {
		return new String[0];
	}
}
