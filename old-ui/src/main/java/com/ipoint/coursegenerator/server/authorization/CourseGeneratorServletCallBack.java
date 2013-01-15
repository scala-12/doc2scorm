package com.ipoint.coursegenerator.server.authorization;

import java.io.IOException;
import java.util.Collections;

import javax.jdo.JDOHelper;
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
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;



public class CourseGeneratorServletCallBack extends AbstractAuthorizationCodeCallbackServlet{
   
    private static final long serialVersionUID = -5536522035960228306L;
    
    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
        throws ServletException, IOException {
      resp.sendRedirect("/");
    }

    @Override
    protected void onError(
        HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse)
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
	return new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(), new GsonFactory(),
	          "", "",
	          Collections.singleton("")).setCredentialStore(
	          new JdoCredentialStore(JDOHelper.getPersistenceManagerFactory("transactions-optional")))
	          .build(); 
    }

    @Override
    protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
       return null;
    }
}
