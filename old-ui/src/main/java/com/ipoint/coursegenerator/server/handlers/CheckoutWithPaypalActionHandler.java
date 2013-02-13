package com.ipoint.coursegenerator.server.handlers;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.ipoint.coursegenerator.server.exception.PaypalSetCheckoutCodeException;
import com.ipoint.coursegenerator.server.paypal.PaypalUtils;
import com.ipoint.coursegenerator.shared.CheckoutWithPaypal;
import com.ipoint.coursegenerator.shared.CheckoutWithPaypalResult;
import com.ipoint.coursegenerator.shared.model.OrderPlan;

public class CheckoutWithPaypalActionHandler implements ActionHandler<CheckoutWithPaypal, CheckoutWithPaypalResult> {

	private final static String SERVER_NAME = "http://localhost:8888/";

	public static final PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");

	@Autowired
	private HttpSession httpSession;
	
	public CheckoutWithPaypalActionHandler() {
	}

	@Override
	public CheckoutWithPaypalResult execute(CheckoutWithPaypal action, ExecutionContext context) throws ActionException {
		PaypalUtils paypal = new PaypalUtils();
		PersistenceManager pm = pmfInstance.getPersistenceManager();
		try {
			OrderPlan plan = pm.getObjectById(OrderPlan.class, Long.parseLong(action.getSubscriptionId()));
			String token = paypal.setCheckoutCode(
					SERVER_NAME + "purchase?subscriptionId=" + action.getSubscriptionId(), SERVER_NAME + "cancel.html",
					String.valueOf(plan.getAmount()), "Any", "USD");
			httpSession.setAttribute("subscription", plan);
			return new CheckoutWithPaypalResult(token);
		} catch (PaypalSetCheckoutCodeException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void undo(CheckoutWithPaypal action, CheckoutWithPaypalResult result, ExecutionContext context)
			throws ActionException {
	}

	@Override
	public Class<CheckoutWithPaypal> getActionType() {
		return CheckoutWithPaypal.class;
	}
}
