package com.ipoint.coursegenerator.shared;

import com.gwtplatform.dispatch.shared.Result;

public class CheckoutWithPaypalResult implements Result {

	private String token;
	
	public CheckoutWithPaypalResult() {
	}
	
	public CheckoutWithPaypalResult(String token) {
		this.token = token;
	}
	
	public String getToken() {
		return token;
	}
}
