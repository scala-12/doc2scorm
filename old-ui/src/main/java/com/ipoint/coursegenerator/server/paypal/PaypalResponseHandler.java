package com.ipoint.coursegenerator.server.paypal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PaypalResponseHandler extends HttpServlet {

	private static final long serialVersionUID = 6283758751714304736L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PaypalUtils paypal = new PaypalUtils();
		String token = req.getParameter("token");
		String payerID = req.getParameter("PayerID");
		paypal.getCheckoutCode(token);
		paypal.executeCheckoutCode(token, payerID, "10.0");
	}
}
