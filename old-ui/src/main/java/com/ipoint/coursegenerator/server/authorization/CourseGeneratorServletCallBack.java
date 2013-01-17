package com.ipoint.coursegenerator.server.authorization;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import javax.jdo.JDOHelper;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.net.util.URLUtil;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jdo.auth.oauth2.JdoCredentialStore;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.gwt.http.client.URL;

import com.google.api.client.http.HttpRequest;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;

public class CourseGeneratorServletCallBack extends
	AbstractAuthorizationCodeCallbackServlet {

    private static final String mainPartUrl = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=";

    private static final long serialVersionUID = -5536522035960228306L;

    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp,
	    final Credential credential) throws ServletException, IOException {
	HttpRequestInitializer initializer = new HttpRequestInitializer() {
	    public void initialize(HttpRequest request) throws IOException {
		HttpHeaders httpHeaders = new HttpHeaders();
		request.getHeaders().setAuthorization(
			"Bearer " + credential.getAccessToken());
	    }
	};
	Oauth2 userInfoService = new Oauth2.Builder(new NetHttpTransport(),
		new JacksonFactory(), initializer).build();
	Userinfo userInfo = null;
	try {
	    userInfo = userInfoService.userinfo().get().execute();
	} catch (IOException e) {
	    System.err.println("An error occurred: " + e);
	}
	if (userInfo != null && userInfo.getId() != null) {
	} else {
	}
	resp.sendRedirect("/Coursegenerator.html");
    }

    @Override
    protected void onError(HttpServletRequest req, HttpServletResponse resp,
	    AuthorizationCodeResponseUrl errorResponse)
	    throws ServletException, IOException {
	// handle error
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
	String uuid = UUID.randomUUID().toString();
	req.getSession().setAttribute("userId", uuid);
	return uuid;
    }
}
