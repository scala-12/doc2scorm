package com.ipoint.coursegenerator.server.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.ipoint.coursegenerator.shared.GetOrderPlanList;
import com.ipoint.coursegenerator.shared.GetOrderPlanListResult;
import com.ipoint.coursegenerator.shared.model.OrderPlan;

public class GetOrderPlanListActionHandler implements ActionHandler<GetOrderPlanList, GetOrderPlanListResult> {

	public static final PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");
	
	public GetOrderPlanListActionHandler() {
	}

	@Override
	public GetOrderPlanListResult execute(GetOrderPlanList action, ExecutionContext context) throws ActionException {
		PersistenceManager pm = pmfInstance.getPersistenceManager();
		List<OrderPlan> orderPlanList = new ArrayList<OrderPlan>();
		List results = (List)pm.newQuery(OrderPlan.class).execute();
		for (Object obj : results) {
			OrderPlan plan = (OrderPlan)obj;
			if (plan.isActive()) {
				orderPlanList.add(plan);
			}
		}
		pm.close();
		return new GetOrderPlanListResult(orderPlanList);
	}

	@Override
	public void undo(GetOrderPlanList action, GetOrderPlanListResult result, ExecutionContext context)
			throws ActionException {
	}

	@Override
	public Class<GetOrderPlanList> getActionType() {
		return GetOrderPlanList.class;
	}
}
