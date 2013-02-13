package com.ipoint.coursegenerator.server.authorization;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
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
import com.ipoint.coursegenerator.server.db.model.User;

public class CourseGeneratorServletCallBack extends AbstractAuthorizationCodeCallbackServlet {

	private static final long serialVersionUID = -5536522035960228306L;

	public static final PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");

	@Override
	protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, final Credential credential)
			throws ServletException, IOException {
		HttpRequestInitializer initializer = new HttpRequestInitializer() {
			public void initialize(HttpRequest request) throws IOException {
				request.getHeaders().setAuthorization("Bearer " + credential.getAccessToken());
			}
		};
		Oauth2 userInfoService = new Oauth2.Builder(new NetHttpTransport(), new JacksonFactory(), initializer).build();
		Userinfo userInfo = null;
		try {
			userInfo = userInfoService.userinfo().get().execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		PersistenceManager pm = pmfInstance.getPersistenceManager();
		if (userInfo.getEmail() != null && !userInfo.getEmail().isEmpty()
				&& pm.getUserObject(userInfo.getEmail()) == null) {
			Query query = pm.newQuery(User.class);
			query.setFilter("userEmail == userEmailParam");
			query.declareParameters("String userEmailParam");
			@SuppressWarnings("unchecked")
			List<User> userList = (List<User>) query.execute(userInfo.getEmail());
			User user = null;
			if (userList == null || userList.size() == 0) {
				user = new User((String) req.getSession().getAttribute("userId"), userInfo.getEmail());
				pm.makePersistent(user);
				pm.flush();
				pm.close();
			} else {
				user = userList.get(0);
			}
			req.getSession().setAttribute("userId", user.getUserId());
			req.getSession().setAttribute("userEmail", userInfo.getEmail());
		}
		resp.sendRedirect("/Coursegenerator.html");
	}

	@Override
	protected void onError(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse)
			throws ServletException, IOException {
		// handle error
	}

	@Override
	protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
		GenericUrl url = new GenericUrl(req.getRequestURL().toString());
		url.setRawPath("/oauth2callback");
		return url.build();
	}

	@Override
	protected AuthorizationCodeFlow initializeFlow() throws IOException {
		return new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(), new JacksonFactory(),
				"788842293516-ubkjoslfcetkttrujqhpi1of743mkf7m.apps.googleusercontent.com", "4WoW3NAIJBROYuQBBoo-wOcb",
				Collections.singleton("https://www.googleapis.com/auth/userinfo.email")).setCredentialStore(
				new JdoCredentialStore(JDOHelper.getPersistenceManagerFactory("transactions-optional"))).build();
	}

	@Override
	protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
		String uuid = UUID.randomUUID().toString();
		req.getSession().setAttribute("userId", uuid);
		return uuid;
	}
}
