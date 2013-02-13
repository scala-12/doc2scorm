package com.ipoint.coursegenerator.shared;

import com.gwtplatform.dispatch.shared.Result;
import com.ipoint.coursegenerator.shared.model.OrderPlan;

public class GetPurchaseInfoResult implements Result {

	private OrderPlan plan;
	
	public GetPurchaseInfoResult() {
	}
	
	public GetPurchaseInfoResult(OrderPlan plan) {
		this.plan = plan;
	}
	
	public OrderPlan getPlan() {
		return this.plan;
	}
}
