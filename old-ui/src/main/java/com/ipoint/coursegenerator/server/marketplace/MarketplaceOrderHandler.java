package com.ipoint.coursegenerator.server.marketplace;

import java.io.IOException;

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
		String edition = req.getParameter(EDITION_PARAMETER);
		String purchaseToken = req.getParameter(PURCHASE_TOKEN_PARAMETER);
		
		if (edition.equals(TRIAL)) {
			
		} else if (edition.equals(ONE_WEEK)) {
			
		} else if (edition.equals(ONE_MONTH)) {
			
		}
	}
}
