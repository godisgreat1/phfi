package com.smh.mailsender.model;

import java.io.Serializable;

public class Response implements Serializable {

	private static final long serialVersionUID = 1L;

	private String errorCode;

	private String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}