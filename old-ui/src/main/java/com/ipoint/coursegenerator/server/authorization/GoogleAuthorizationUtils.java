package com.ipoint.coursegenerator.server.authorization;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

public class GoogleAuthorizationUtils {

	public static final String GOOGLE_USER_EMAIL_SCOPE = "https://apps-apis.google.com/a/feeds/user/#readonly";

	public static final String APP_NAME = "ipoint-ilogos-course-generator-0.1";

	public static final String APPLICATION_ID = "941037917146";

	public static final String XOAUTH_PARAM = "xoauth_requestor_id";

	public static final String PRODUCT_LISTING_ID = "14092+1382165968457555329";
	
	private static final String USER_ID = "";
	
	private static final String USER_SECRET = "";

	static String getRedirectUri(HttpServletRequest req) {
		GenericUrl url = new GenericUrl(req.getRequestURL().toString());
		url.setRawPath("/oauth2callback");
		return url.build();
	}

	static AuthorizationCodeFlow newFlow() throws IOException {
		//TODO: refactor authorization method (add credential info), add variables like 'newFlow(something)'
		return new GoogleAuthorizationCodeFlow.Builder(
				new NetHttpTransport(),
				new JacksonFactory(),
				USER_ID,
				USER_SECRET,
				Collections.singleton("https://www.googleapis.com/auth/userinfo.email")
		).setAccessType("offline").setApprovalPrompt("force").build();
	}
}
