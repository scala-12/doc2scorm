package com.ipoint.coursegenerator.server.paypal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ipoint.coursegenerator.shared.ApplicationProperties;


public class PayPalIPNHandler extends HttpServlet {

	private static final long serialVersionUID = -5538594404825478772L;
	
	public static final String PAYPAL_SERVER = ApplicationProperties.debugEnabled() ? "https://www.sandbox.paypal.com/" : "https://www.paypal.com/";

	public static final String PAYPAL_VERIFY_IPN_URL = "cgi-bin/webscr?cmd=_notify-validate";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
}
