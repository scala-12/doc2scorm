package com.ipoint.coursegenerator.server.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.ipoint.coursegenerator.server.db.model.User;
import com.ipoint.coursegenerator.shared.GetOrderPlanList;
import com.ipoint.coursegenerator.shared.GetOrderPlanListResult;
import com.ipoint.coursegenerator.shared.model.OrderPlan;

public class GetOrderPlanListActionHandler implements ActionHandler<GetOrderPlanList, GetOrderPlanListResult> {

	public static final PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");
	
	private static double DAY_IN_MILLISECONDS = 86400000.0;
	
	@Autowired
	private HttpSession httpSession;
	
	public GetOrderPlanListActionHandler() {
	}

	@Override
	public GetOrderPlanListResult execute(GetOrderPlanList action, ExecutionContext context) throws ActionException {
		PersistenceManager pm = pmfInstance.getPersistenceManager();
		User user = pm.getObjectById(User.class, httpSession.getAttribute("userId"));
		long daysRemains = -1;
		if (user.getDomain() != null ) {
			daysRemains = Math.round(Math.floor((user.getDomain().getExpirationDate().getTime() - System.currentTimeMillis())/DAY_IN_MILLISECONDS));
		} else {
			daysRemains =  Math.round(Math.floor((user.getExpirationDate().getTime() - System.currentTimeMillis())/DAY_IN_MILLISECONDS));
		}
		String username = user.getUserEmail();
		List<OrderPlan> orderPlanList = new ArrayList<OrderPlan>();
		@SuppressWarnings("unchecked")
		List<OrderPlan> results = (List<OrderPlan>)pm.newQuery(OrderPlan.class).execute();
		for (OrderPlan plan : results) {
			if (plan.isActive()) {
				orderPlanList.add(plan);
			}
		}
		pm.close();
		return new GetOrderPlanListResult(orderPlanList, username, (int)daysRemains);
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
