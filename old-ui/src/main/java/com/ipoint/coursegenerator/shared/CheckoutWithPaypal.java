package com.ipoint.coursegenerator.shared;

import com.gwtplatform.dispatch.shared.UnsecuredActionImpl;

public class CheckoutWithPaypal extends UnsecuredActionImpl<CheckoutWithPaypalResult> {
	
	private String subscriptionId;
	
	public CheckoutWithPaypal() {
	}

	public CheckoutWithPaypal(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	
	public String getSubscriptionId() {
		return this.subscriptionId;
	}
}
