package com.ipoint.coursegenerator.server.handlers;

import java.util.Calendar;
import java.util.Date;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.ipoint.coursegenerator.server.db.model.PaypalTransaction;
import com.ipoint.coursegenerator.server.db.model.User;
import com.ipoint.coursegenerator.server.paypal.PaypalUtils;
import com.ipoint.coursegenerator.shared.BuyNow;
import com.ipoint.coursegenerator.shared.BuyNowResult;
import com.ipoint.coursegenerator.shared.OrderPlanType;
import com.ipoint.coursegenerator.shared.model.OrderPlan;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class BuyNowActionHandler implements ActionHandler<BuyNow, BuyNowResult> {

	@Autowired
	private HttpSession httpSession;

	public static final PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");

	public BuyNowActionHandler() {
	}

	@Override
	public BuyNowResult execute(BuyNow action, ExecutionContext context) throws ActionException {
		PaypalUtils paypal = new PaypalUtils();
		String token = (String) httpSession.getAttribute("paypalToken");
		String payerID = (String) httpSession.getAttribute("paypalPayerID");
		OrderPlan plan = (OrderPlan) httpSession.getAttribute("subscription");
		if (token != null && payerID != null && plan != null) {
			String result = paypal.executeCheckoutCode(token, payerID, String.valueOf(plan.getAmount()));
			PersistenceManager pm = pmfInstance.getPersistenceManager();
			Transaction trans = pm.currentTransaction();
			trans.begin();
			PaypalTransaction transaction = new PaypalTransaction();
			User user = pm.getObjectById(User.class, httpSession.getAttribute("userId"));
			if (!result.equals("Success")) {
				transaction.setAmount(plan.getAmount());
				transaction.setSuccessful(false);
				transaction.setTimestamp(new Date());
				throw new ActionException("Failed execute PayPal checkout.");
			} else {
				transaction.setAmount(plan.getAmount());
				transaction.setSuccessful(true);
				transaction.setTimestamp(new Date());
				if (plan.getType().equals(OrderPlanType.TIME)) {
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.DAY_OF_MONTH, plan.getExpiresIn());					
					user.setExpirationDate(calendar.getTime());
				} else if (plan.getType().equals(OrderPlanType.COUNT)) {
					user.setCurrentPlanCount(plan.getExpiresAfter());
				}
			}
			user.addTransaction(transaction);
			trans.commit();
		} else {
			throw new ActionException("Required parameters are missing.");
		}
		return null;
	}

	@Override
	public void undo(BuyNow action, BuyNowResult result, ExecutionContext context) throws ActionException {
	}

	@Override
	public Class<BuyNow> getActionType() {
		return BuyNow.class;
	}
}
