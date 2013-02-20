package com.ipoint.coursegenerator.server.authorization;

import java.io.IOException;
import java.util.Collections;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.http.HttpServletRequest;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.jdo.auth.oauth2.JdoCredentialStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.ipoint.coursegenerator.server.db.CourseGeneratorDAO;

public class GoogleAuthorizationUtils {
	

	public static final String GOOGLE_USER_EMAIL_SCOPE = "https://apps-apis.google.com/a/feeds/user/#readonly";

	public static final String APP_NAME = "ipoint-ilogos-course-generator-0.1";

	public static final String APPLICATION_ID = "274434146714";

	public static final String CONSUMER_KEY = "274434146714.apps.googleusercontent.com";

	public static final String CONSUMER_SECRET = "H0xga5JLxQuLfvUohwtu7nYl";

	public static final String XOAUTH_PARAM = "xoauth_requestor_id";

	public static final String PRODUCT_LISTING_ID = "23741+10727418611876118780";
	
	static String getRedirectUri(HttpServletRequest req) {
		GenericUrl url = new GenericUrl(req.getRequestURL().toString());
		url.setRawPath("/oauth2callback");
		return url.build();
	}

	static AuthorizationCodeFlow newFlow() throws IOException {
		return new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(), new JacksonFactory(),
				"788842293516-ubkjoslfcetkttrujqhpi1of743mkf7m.apps.googleusercontent.com", "4WoW3NAIJBROYuQBBoo-wOcb",
				Collections.singleton("https://www.googleapis.com/auth/userinfo.email")).setCredentialStore(
				CourseGeneratorDAO.getJdoCredentialStore()).build();
	}
}
