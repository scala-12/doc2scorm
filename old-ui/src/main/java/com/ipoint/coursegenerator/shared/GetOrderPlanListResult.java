package com.ipoint.coursegenerator.shared;

import java.util.List;

import com.gwtplatform.dispatch.shared.Result;
import com.ipoint.coursegenerator.shared.model.OrderPlan;

public class GetOrderPlanListResult implements Result {

	private List<OrderPlan> orderPlanList;
	
	private String username;
	
	private int daysRemains;

	@SuppressWarnings("unused")
	private GetOrderPlanListResult() {
		// For serialization only
	}

	public GetOrderPlanListResult(List<OrderPlan> orderPlanList, String username, int daysRemains) {
		this.orderPlanList = orderPlanList;
		this.username = username;
		this.daysRemains = daysRemains;
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
}
