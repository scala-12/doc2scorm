package com.ipoint.coursegenerator.shared;

import com.gwtplatform.dispatch.shared.Result;

public class BuyNowResult implements Result {

	private String redirectUrl;
	
	public BuyNowResult() {
	}
	
	public BuyNowResult(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	
	public String getRedirectUrl(){
		return redirectUrl;
	}
}
