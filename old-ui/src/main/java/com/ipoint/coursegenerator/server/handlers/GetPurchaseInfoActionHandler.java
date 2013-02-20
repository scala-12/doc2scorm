package com.ipoint.coursegenerator.server.handlers;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.ipoint.coursegenerator.server.db.CourseGeneratorDAO;
import com.ipoint.coursegenerator.server.paypal.PaypalUtils;
import com.ipoint.coursegenerator.shared.GetPurchaseInfo;
import com.ipoint.coursegenerator.shared.GetPurchaseInfoResult;
import com.ipoint.coursegenerator.shared.model.OrderPlan;

public class GetPurchaseInfoActionHandler implements ActionHandler<GetPurchaseInfo, GetPurchaseInfoResult> {

	@Autowired
	private HttpSession httpSession;
		
	public GetPurchaseInfoActionHandler() {
	}

	@Override
	public GetPurchaseInfoResult execute(GetPurchaseInfo action, ExecutionContext context) throws ActionException {
		PaypalUtils paypal = new PaypalUtils();
		paypal.getCheckoutCode((String)httpSession.getAttribute("paypalToken"));
		Object subscription = httpSession.getAttribute("subscription");
		OrderPlan plan = null;
		if (subscription != null) {
			plan = (OrderPlan) subscription;
		} else {
			PersistenceManager pm = CourseGeneratorDAO.getPersistenceManager();
			plan = pm.getObjectById(OrderPlan.class, Long.parseLong(action.getSubscriptionId()));
			pm.close();
		}
		return new GetPurchaseInfoResult(plan);
	}

	@Override
	public void undo(GetPurchaseInfo action, GetPurchaseInfoResult result, ExecutionContext context) throws ActionException {
	}

	@Override
	public Class<GetPurchaseInfo> getActionType() {
		return GetPurchaseInfo.class;
	}
}
