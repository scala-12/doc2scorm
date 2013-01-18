package com.ipoint.coursegenerator.server.authorization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;

import javax.jdo.JDOHelper;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.jdo.auth.oauth2.JdoCredentialStore;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

public class CourseConverterServletAuth extends
	AbstractAuthorizationCodeServlet {

    private static final long serialVersionUID = 8003570633306044820L;

    @Override
    protected void doGet(HttpServletRequest request,
	    HttpServletResponse response) throws IOException {
	request.getContextPath();
	response.setContentType("text/html;charset=UTF-8");
	ClassLoader classLoader = Thread.currentThread()
		.getContextClassLoader();
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
    protected String getRedirectUri(HttpServletRequest req)
	    throws ServletException, IOException {
	GenericUrl url = new GenericUrl(req.getRequestURL().toString());
	url.setRawPath("/oauth2callback");
	return url.build();
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws IOException {
	return new GoogleAuthorizationCodeFlow.Builder(
		new NetHttpTransport(),
		new JacksonFactory(),
		"1049616207696-7a28savq1jvghrhmsbhqtif974q0aqvp.apps.googleusercontent.com",
		"GpgSjVBqBQmVmUAS6aLYj9kC",
		Collections
			.singleton("https://www.googleapis.com/auth/userinfo.email"))
		.setCredentialStore(
			new JdoCredentialStore(
				JDOHelper
					.getPersistenceManagerFactory("transactions-optional")))
		.build();
    }

    @Override
    protected String getUserId(HttpServletRequest req) throws ServletException,
	    IOException {
	return (String) req.getSession().getAttribute("userId") == null ? "notauser"
		: (String) req.getSession().getAttribute("userId");
    }
}
