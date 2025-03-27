package com.ipoint.coursegenerator.server.authorization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;

public class CourseGeneratorServletAuth extends AbstractAuthorizationCodeServlet {

	private static final long serialVersionUID = 8003570633306044820L;

	public static final String EDITION_PARAMETER = "edition";
	public static final String DOMAIN_PARAMETER = "domain";
	public static final String PURCHASE_EDITION_PARAMETER = "appsmarket.edition";

	public static final String PURCHASE_TOKEN_PARAMETER = "appsmarket.purchaseToken";

	private Locale russianLocale = new Locale("ru");

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// response.setContentType("text/html;charset=UTF-8");
		if (request.getLocale().getLanguage().equals(russianLocale.getLanguage())
				&& request.getParameter("locale") == null) {
			response.sendRedirect(request.getRequestURI() + "?" + "locale=ru"
					+ (request.getQueryString() == null ? "" : "&" + request.getQueryString()));
			return;
		}
		String edition = request.getParameter(EDITION_PARAMETER);
		String purchaseToken = request.getParameter(PURCHASE_TOKEN_PARAMETER);
		String domain = request.getParameter(DOMAIN_PARAMETER);
		if ((request.getRequestURI().equals("/orderchoice") && edition != null && purchaseToken != null && domain != null)
				|| !request.getRequestURI().equals("/orderchoice")) {
			request.getSession().setAttribute("appsmarket.edition", edition);
			request.getSession().setAttribute(PURCHASE_TOKEN_PARAMETER, purchaseToken);
			request.getSession().setAttribute(DOMAIN_PARAMETER, domain);
		}
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
		PrintWriter writer = response.getWriter();
		writer.print(result.toString());
		writer.flush();
	}

	@Override
	protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
		return GoogleAuthorizationUtils.getRedirectUri(req);
	}

	@Override
	protected AuthorizationCodeFlow initializeFlow() throws IOException {
		//TODO: add variables for method newFlow
		return GoogleAuthorizationUtils.newFlow();
	}

	@Override
	protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
		String edition = req.getParameter(EDITION_PARAMETER);
		String purchaseToken = req.getParameter(PURCHASE_TOKEN_PARAMETER);
		String domain = req.getParameter(DOMAIN_PARAMETER);
		if (req.getRequestURI().equals("/orderchoice") && edition != null && purchaseToken != null && domain != null) {
			req.getSession().setAttribute(PURCHASE_EDITION_PARAMETER, edition);
			req.getSession().setAttribute(PURCHASE_TOKEN_PARAMETER, purchaseToken);
			req.getSession().setAttribute(DOMAIN_PARAMETER, domain);
		}
		return (String) req.getSession().getAttribute("userId") == null ? "notauser" : (String) req.getSession()
				.getAttribute("userId");
	}
}
