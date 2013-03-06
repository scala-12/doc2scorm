package com.ipoint.coursegenerator.shared;

import java.util.List;

import com.gwtplatform.dispatch.shared.Result;
import com.ipoint.coursegenerator.shared.model.OrderPlan;

public class GetSubscribedResult implements Result {

	private List<OrderPlan> orderPlanList;
	
	private String username;
	
	private String userId;
	
	private int daysRemains;
	
	private boolean subscribed;

	@SuppressWarnings("unused")
	private GetSubscribedResult() {
		// For serialization only
	}

	public GetSubscribedResult(List<OrderPlan> orderPlanList, String username, int daysRemains, boolean subscribed, String userId) {
		this.orderPlanList = orderPlanList;
		this.username = username;
		this.daysRemains = daysRemains;
		this.subscribed = subscribed;
		this.userId = userId;
	}

	public List<OrderPlan> getOrderPlanList() {
		return orderPlanList;
	}
	
	public String getUsername() {
		return username;
	}
	
	public int getDaysRemains() {
		return daysRemains;
	}
	
	public boolean isSubscribed() {
		return subscribed;
	}

	public String getUserId() {
		return userId;
	}
}
