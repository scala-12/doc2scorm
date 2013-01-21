package com.ipoint.coursegenerator.server.authorization;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jdo.auth.oauth2.JdoCredentialStore;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;

public class CourseGeneratorServletCallBack extends
	AbstractAuthorizationCodeCallbackServlet {

    private static final long serialVersionUID = -5536522035960228306L;

    private User user = new User();

    public static final PersistenceManagerFactory pmfInstance = JDOHelper
	    .getPersistenceManagerFactory("transactions-optional");

    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp,
	    final Credential credential) throws ServletException, IOException {
	HttpRequestInitializer initializer = new HttpRequestInitializer() {
	    public void initialize(HttpRequest request) throws IOException {
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
	    e.printStackTrace();
	}
	if (userInfo.getEmail() != null && !userInfo.getEmail().isEmpty()) {
	    PersistenceManager pm = pmfInstance.getPersistenceManager();
	   // User user = new User("iddd", userInfo.getEmail());
	   user.setUserEmail(userInfo.getEmail());
	    pm.makePersistent(user);
	    // pm.putUserObject("userEmail",user);
	    // pm.flush();
	    pm.close();
	    req.getSession().setAttribute("userEmail", userInfo.getEmail());
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
	user.setUserId(uuid);
	req.getSession().setAttribute("userId", uuid);
	return uuid;
    }
}
