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
		//TODO: search user
		//User user = pm.getObjectById(User.class, (String) httpSession.getAttribute("userId"));
		long daysRemains = -1;
		//TODO: add checking user domain (user.getDomain() != null)
		if (true) {
			//TODO: if debug-mode and admin user (user.getDomain().getName().equals(IPOINT_DOMAIN) && !ApplicationProperties.debugEnabled())
			daysRemains = (true) ?
					999999999 
					: 0
					//daysRemains = Math.round(Math.floor((user.getDomain().getExpirationDate().getTime() - System.currentTimeMillis()) / DAY_IN_MILLISECONDS));
					;
		} else {
			//TODO: add checking count of days for simple user without domain
			//daysRemains = Math.round(Math.floor((user.getExpirationDate().getTime() - System.currentTimeMillis())	/ DAY_IN_MILLISECONDS));
		}
		//TODO: get user name from Email
		//String username = user.getUserEmail();
		String username = "Somebody";
		List<OrderPlan> orderPlanList = new ArrayList<OrderPlan>();
		@SuppressWarnings("unchecked")
		List<OrderPlan> results = (List<OrderPlan>) pm.newQuery(OrderPlan.class).execute();
		for (OrderPlan plan : results) {
			if (plan.isActive()) {
				orderPlanList.add(plan);
			}
		}
		
		return new GetSubscribedResult(orderPlanList,
				username,
				(int) daysRemains,
				//TODO: remove "true" and comments
				true
				//user.getDomain() != null ?
				//		user.getDomain().isSubscribed() 
				//		: user.isSubscribed()
				,
				//TODO: remove "0" and comments
				"0"
				//user.getUserId()
				);
	}

	@Override
	public void undo(GetSubscribed action, GetSubscribedResult result, ExecutionContext context) throws ActionException {
	}

	@Override
	public Class<GetSubscribed> getActionType() {
		return GetSubscribed.class;
	}
}
