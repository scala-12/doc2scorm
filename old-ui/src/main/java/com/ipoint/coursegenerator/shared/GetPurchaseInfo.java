package com.ipoint.coursegenerator.shared;

import com.gwtplatform.dispatch.shared.UnsecuredActionImpl;
import com.ipoint.coursegenerator.shared.GetPurchaseInfoResult;

public class GetPurchaseInfo extends UnsecuredActionImpl<GetPurchaseInfoResult> {

	private String subscriptionId;
	
	public GetPurchaseInfo() {
	
	}
	
	public GetPurchaseInfo(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	
	public String getSubscriptionId() {
		return this.subscriptionId;
	}
}
