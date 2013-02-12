package com.ipoint.coursegenerator.server.handlers;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.ipoint.coursegenerator.server.exception.PaypalSetCheckoutCodeException;
import com.ipoint.coursegenerator.server.paypal.PaypalUtils;
import com.ipoint.coursegenerator.shared.CheckoutWithPaypal;
import com.ipoint.coursegenerator.shared.CheckoutWithPaypalResult;

public class CheckoutWithPaypalActionHandler implements ActionHandler<CheckoutWithPaypal, CheckoutWithPaypalResult> {

	private final static String SERVER_NAME = "http://localhost:8888/";

	public CheckoutWithPaypalActionHandler() {
	}

	@Override
	public CheckoutWithPaypalResult execute(CheckoutWithPaypal action, ExecutionContext context) throws ActionException {
		PaypalUtils paypal = new PaypalUtils();
		try {
			String token = paypal.setCheckoutCode(
					SERVER_NAME + "purchase?subscriptionId=" + action.getSubscriptionId(), SERVER_NAME + "cancel.html",
					"10.0", "Any", "USD");
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
