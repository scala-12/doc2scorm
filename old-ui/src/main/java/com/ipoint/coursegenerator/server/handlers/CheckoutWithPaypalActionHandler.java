package com.ipoint.coursegenerator.server.handlers;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.ipoint.coursegenerator.server.db.CourseGeneratorDAO;
import com.ipoint.coursegenerator.server.exception.PaypalSetCheckoutCodeException;
import com.ipoint.coursegenerator.server.paypal.PaypalUtils;
import com.ipoint.coursegenerator.shared.CheckoutWithPaypal;
import com.ipoint.coursegenerator.shared.CheckoutWithPaypalResult;
import com.ipoint.coursegenerator.shared.model.OrderPlan;

public class CheckoutWithPaypalActionHandler implements ActionHandler<CheckoutWithPaypal, CheckoutWithPaypalResult> {

	private final static String SERVER_NAME = "true".equals(System.getProperty("debugging")) ? "http://localhost:8888/"
			: "http://doc2scorm.ipoint-consulting.com/";

	@Autowired
	private HttpSession httpSession;

	@Autowired
	private PaypalUtils paypal;

	public CheckoutWithPaypalActionHandler() {
	}

	@Override
	public CheckoutWithPaypalResult execute(CheckoutWithPaypal action, ExecutionContext context) throws ActionException {

		PersistenceManager pm = CourseGeneratorDAO.getPersistenceManager();
		try {
			OrderPlan plan = pm.getObjectById(OrderPlan.class, Long.parseLong(action.getSubscriptionId()));
			plan = pm.detachCopy(plan);
			String token = paypal.setCheckoutCode(
					SERVER_NAME + "purchase?subscriptionId=" + action.getSubscriptionId(), SERVER_NAME + "Coursegenerator.html",
					String.valueOf(plan.getAmount()), "Any", "USD");
			httpSession.setAttribute("subscription", plan);
			pm.close();
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
