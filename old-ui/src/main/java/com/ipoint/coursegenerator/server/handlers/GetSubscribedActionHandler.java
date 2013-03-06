package com.ipoint.coursegenerator.server.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.ipoint.coursegenerator.server.db.CourseGeneratorDAO;
import com.ipoint.coursegenerator.server.db.model.User;
import com.ipoint.coursegenerator.shared.GetSubscribed;
import com.ipoint.coursegenerator.shared.GetSubscribedResult;
import com.ipoint.coursegenerator.shared.model.OrderPlan;

public class GetSubscribedActionHandler implements ActionHandler<GetSubscribed, GetSubscribedResult> {
	
	private static double DAY_IN_MILLISECONDS = 86400000.0;
	
	public static final String IPOINT_DOMAIN = "ipoint.ru";
	
	@Autowired
	private HttpSession httpSession;
	
	public GetSubscribedActionHandler() {
	}

	@Override
	public GetSubscribedResult execute(GetSubscribed action, ExecutionContext context) throws ActionException {
		PersistenceManager pm = CourseGeneratorDAO.getPersistenceManager();
		User user = pm.getObjectById(User.class, (String)httpSession.getAttribute("userId"));
		long daysRemains = -1;
		if (user.getDomain() != null ) {
			daysRemains = Math.round(Math.floor((user.getDomain().getExpirationDate().getTime() - System.currentTimeMillis())/DAY_IN_MILLISECONDS));
			if (user.getDomain().getName().equals(IPOINT_DOMAIN)) {
				daysRemains = 999999999;
			}
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
		return new GetSubscribedResult(orderPlanList, username, (int)daysRemains, false, user.getUserId());
	}

	@Override
	public void undo(GetSubscribed action, GetSubscribedResult result, ExecutionContext context)
			throws ActionException {
	}

	@Override
	public Class<GetSubscribed> getActionType() {
		return GetSubscribed.class;
	}
}
