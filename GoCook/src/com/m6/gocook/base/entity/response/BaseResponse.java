package com.m6.gocook.base.entity.response;


public class BaseResponse {

	public static final String RESULT = "result";
	public static final String ERROR = "errorcode";
	
	protected int result;
	
	protected int errorCode;

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	
}
