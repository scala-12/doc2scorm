package com.ipoint.coursegenerator.server.paypal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ipoint.coursegenerator.server.db.CourseGeneratorDAO;
import com.ipoint.coursegenerator.server.db.model.GoogleAppsDomain;
import com.ipoint.coursegenerator.server.db.model.PaypalTransaction;
import com.ipoint.coursegenerator.server.db.model.User;
import com.ipoint.coursegenerator.shared.ApplicationProperties;
import com.ipoint.coursegenerator.shared.OrderPlanType;
import com.ipoint.coursegenerator.shared.model.OrderPlan;

public class PayPalIPNHandler extends HttpServlet {

	private static final long serialVersionUID = -5538594404825478772L;

	public static final String PAYPAL_SERVER = ApplicationProperties.debugEnabled() ? "https://www.sandbox.paypal.com/"
			: "https://www.paypal.com/";

	public static final String PAYPAL_VERIFY_IPN_URL = "cgi-bin/webscr";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println(req.getContentType());
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		Enumeration en = request.getParameterNames();
		StringBuffer strBuffer = new StringBuffer("cmd=_notify-validate");
		String paramName;
		String paramValue;
		while (en.hasMoreElements()) {
			paramName = (String) en.nextElement();
			paramValue = request.getParameter(paramName);
			strBuffer.append("&").append(paramName).append("=").append(URLEncoder.encode(paramValue));
		}

		URL u = new URL(PAYPAL_SERVER + PAYPAL_VERIFY_IPN_URL);
		HttpsURLConnection uc = (HttpsURLConnection) u.openConnection();
		uc.setDoOutput(true);
		uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		uc.setRequestProperty("Host", "sandbox.paypal.com");
		PrintWriter pw = new PrintWriter(uc.getOutputStream());
		pw.println(strBuffer.toString());
		pw.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		String res = in.readLine();
		in.close();

		// assign posted variables to local variables
		String itemName = request.getParameter("item_name");
		String itemNumber = request.getParameter("item_number");
		String paymentStatus = request.getParameter("payment_status");
		String paymentAmount = request.getParameter("mc_gross");
		String paymentCurrency = request.getParameter("mc_currency");
		String txnId = request.getParameter("txn_id");
		String txnType = request.getParameter("txn_type");
		String receiverEmail = request.getParameter("receiver_email");
		String payerEmail = request.getParameter("payer_email");
		String userId = request.getParameter("custom");
		// check notification validation
		if (res.equals("VERIFIED") && userId != null && userId.length() == 36) {
			PersistenceManager pm = CourseGeneratorDAO.getPersistenceManager();
			Transaction trans = pm.currentTransaction();
			trans.begin();
			User user = pm.getObjectById(User.class, userId);
			if ("Completed".equals(paymentStatus) && "subscr_payment".equals(txnType)) {
				OrderPlan plan = pm.getObjectById(OrderPlan.class, itemNumber);
				plan = pm.detachCopy(plan);
				double amount = Double.parseDouble(paymentAmount);
				if (!user.transactionExists(txnId)
						&& getServletContext().getInitParameter("receiver_email").equals(receiverEmail)
						&& "USD".equals(paymentCurrency) && Math.round(amount) == Math.round(plan.getAmount())) {
					PaypalTransaction transaction = new PaypalTransaction();
					transaction.setAmount(amount);
					transaction.setSuccessful(true);
					transaction.setTimestamp(new Date());
					transaction.setTxnId(txnId);
					GoogleAppsDomain domain = user.getDomain();
					pm.refresh(domain);
					if (plan.getType().equals(OrderPlanType.TIME)) {
						Calendar calendar = Calendar.getInstance();
						calendar.add(Calendar.DAY_OF_MONTH, plan.getExpiresIn());
						user.setExpirationDate(calendar.getTime());
						user.setSubscribed(true);
						if (domain != null) {
							domain.setExpirationDate(calendar.getTime());
							domain.setSubscribed(true);
						}
					} else if (plan.getType().equals(OrderPlanType.COUNT)) {
						user.setCurrentPlanCount(plan.getExpiresAfter());
						if (domain != null) {
							domain.setCurrentPlanCount(user.getCurrentPlanCount());
						}
					}
					user.addTransaction(transaction);
				}
			} else if ("subscr_cancel".equals(txnType) || "subscr_eot".equals(txnType)
					|| "subscr_failed".equals(txnType)) {
				GoogleAppsDomain domain = user.getDomain();
				pm.refresh(domain);
				user.setExpirationDate(new Date());
				user.setSubscribed(false);
				if (domain != null) {
					domain.setExpirationDate(new Date());
					domain.setSubscribed(false);
				}
			}
			trans.commit();
			pm.close();
		} else if (res.equals("INVALID")) {
		} else {
		}
	}
}
