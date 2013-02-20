package com.ipoint.coursegenerator.server.handlers;

import java.util.Calendar;
import java.util.Date;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.ipoint.coursegenerator.server.authorization.CourseGeneratorServletAuth;
import com.ipoint.coursegenerator.server.authorization.GoogleAuthorizationUtils;
import com.ipoint.coursegenerator.server.db.CourseGeneratorDAO;
import com.ipoint.coursegenerator.server.db.model.GoogleAppsDomain;
import com.ipoint.coursegenerator.server.db.model.PaypalTransaction;
import com.ipoint.coursegenerator.server.db.model.User;
import com.ipoint.coursegenerator.server.paypal.PaypalUtils;
import com.ipoint.coursegenerator.shared.BuyNow;
import com.ipoint.coursegenerator.shared.BuyNowResult;
import com.ipoint.coursegenerator.shared.OrderPlanType;
import com.ipoint.coursegenerator.shared.model.OrderPlan;

public class BuyNowActionHandler implements ActionHandler<BuyNow, BuyNowResult> {

	@Autowired
	private HttpSession httpSession;


	public BuyNowActionHandler() {
	}

	@Override
	public BuyNowResult execute(BuyNow action, ExecutionContext context) throws ActionException {
		PaypalUtils paypal = new PaypalUtils();
		BuyNowResult buyNowResult = null;
		String token = (String) httpSession.getAttribute("paypalToken");
		String payerID = (String) httpSession.getAttribute("paypalPayerID");
		OrderPlan plan = (OrderPlan) httpSession.getAttribute("subscription");
		if (token != null && payerID != null && plan != null) {
			String result = paypal.executeCheckoutCode(token, payerID, String.valueOf(plan.getAmount()));
			PersistenceManager pm = CourseGeneratorDAO.getPersistenceManager();
			Transaction trans = pm.currentTransaction();
			trans.begin();
			PaypalTransaction transaction = new PaypalTransaction();
			User user = pm.getObjectById(User.class, httpSession.getAttribute("userId"));
			pm.refresh(user);
			if (!result.equals("Success")) {
				transaction.setAmount(plan.getAmount());
				transaction.setSuccessful(false);
				transaction.setTimestamp(new Date());
				throw new ActionException("Failed to execute PayPal checkout.");
			} else {
				transaction.setAmount(plan.getAmount());
				transaction.setSuccessful(true);
				transaction.setTimestamp(new Date());
				GoogleAppsDomain domain = user.getDomain();
				pm.refresh(domain);
				if (plan.getType().equals(OrderPlanType.TIME)) {
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.DAY_OF_MONTH, plan.getExpiresIn());
					user.setExpirationDate(calendar.getTime());
					if (domain != null) {
						domain.setExpirationDate(calendar.getTime());
					}
				} else if (plan.getType().equals(OrderPlanType.COUNT)) {
					user.setCurrentPlanCount(plan.getExpiresAfter());
					if (domain != null) {
						domain.setCurrentPlanCount(user.getCurrentPlanCount());
					}
				}
				if (domain != null
						&& httpSession.getAttribute(CourseGeneratorServletAuth.PURCHASE_TOKEN_PARAMETER) != null) {
					buyNowResult = new BuyNowResult("https://www.google.com/a/cpanel/" + domain.getName()
							+ "/DomainAppInstall?appId=" + GoogleAuthorizationUtils.APPLICATION_ID
							+ "&productListingId=" + GoogleAuthorizationUtils.PRODUCT_LISTING_ID
							+ "&editionId&paymentModel=PAID");
				} else {
					buyNowResult = new BuyNowResult("/Coursegenerator.html");
				}
			}
			user.addTransaction(transaction);
			System.out.println("teset");
			trans.commit();
		} else {
			throw new ActionException("Required parameters are missing.");
		}
		return buyNowResult;
	}

	@Override
	public void undo(BuyNow action, BuyNowResult result, ExecutionContext context) throws ActionException {
	}

	@Override
	public Class<BuyNow> getActionType() {
		return BuyNow.class;
	}
}
