package com.ipoint.coursegenerator.server.authorization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;

public class CourseGeneratorServletAuth extends AbstractAuthorizationCodeServlet {

	private static final long serialVersionUID = 8003570633306044820L;

	private static final String EDITION_PARAMETER = "edition"; 
	
	public static final String PURCHASE_TOKEN_PARAMETER = "appsmarket.purchaseToken";
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//response.setContentType("text/html;charset=UTF-8");
		String edition = request.getParameter(EDITION_PARAMETER );
		String purchaseToken = request.getParameter(PURCHASE_TOKEN_PARAMETER);
		request.getSession().setAttribute("appsmarket.edition", edition);
		request.getSession().setAttribute("appsmarket.purchaseToken", purchaseToken);
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
		return GoogleAuthorizationUtils.newFlow();
	}

	@Override
	protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
		return (String) req.getSession().getAttribute("userId") == null ? "notauser" : (String) req.getSession()
				.getAttribute("userId");
	}
}
