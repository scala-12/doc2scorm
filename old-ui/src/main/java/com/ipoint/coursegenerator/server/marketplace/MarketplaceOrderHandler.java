package com.ipoint.coursegenerator.server.marketplace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MarketplaceOrderHandler extends HttpServlet {
	
	private static final long serialVersionUID = -2734267290216150767L;

	private static final String EDITION_PARAMETER = "edition"; 
	
	private static final String PURCHASE_TOKEN_PARAMETER = "appsmarket.purchaseToken";
	
	private static final String TRIAL = "trial";
	
	private static final String ONE_WEEK = "week";
	
	private static final String ONE_MONTH = "month";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String token = req.getParameter("token");
		String payerID = req.getParameter("PayerID");
		req.getSession().setAttribute("paypalToken", token);
		req.getSession().setAttribute("paypalPayerID", payerID);
		resp.setContentType("text/html;charset=UTF-8");
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				classLoader.getResourceAsStream("Coursegenerator.html")));
		StringBuffer result = new StringBuffer();
		while (br.ready()) {
			String line = br.readLine();
			if (line != null) {
				result.append(line);
			} else {
				break;
			}
		}
		PrintWriter writer = resp.getWriter();
		writer.print(result.toString());
		writer.flush();		
	}
}
