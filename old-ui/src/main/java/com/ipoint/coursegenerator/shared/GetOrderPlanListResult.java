package com.ipoint.coursegenerator.shared;

import java.util.List;

import com.gwtplatform.dispatch.shared.Result;
import com.ipoint.coursegenerator.shared.model.OrderPlan;

public class GetOrderPlanListResult implements Result {

	private List<OrderPlan> orderPlanList;

	@SuppressWarnings("unused")
	private GetOrderPlanListResult() {
		// For serialization only
	}

	public GetOrderPlanListResult(List<OrderPlan> orderPlanList) {
		this.orderPlanList = orderPlanList;
	}

	public List<OrderPlan> getOrderPlanList() {
		return orderPlanList;
	}
}
