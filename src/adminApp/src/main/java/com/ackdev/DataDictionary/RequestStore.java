package com.ackdev.DataDictionary;

import javax.servlet.http.HttpServletRequest;

public class RequestStore {

	private HttpServletRequest request;

	public void clear() {
	}

	public void setRequest(HttpServletRequest request) {
		this.request=request;
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	
	
}
