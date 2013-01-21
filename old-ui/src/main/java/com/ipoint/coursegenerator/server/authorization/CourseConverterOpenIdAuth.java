package com.ipoint.coursegenerator.server.authorization;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openid4java.OpenIDException;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.ParameterList;

public class CourseConverterOpenIdAuth extends HttpServlet {
/*
    private static final long serialVersionUID = -2849404301451768071L;
    
    protected ConsumerHelper consumerHelper;
    protected String realm;
    protected String returnToPath;
    protected String homePath;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        returnToPath = getInitParameter("return_to_path", "/openid");
        homePath = getInitParameter("home_path", "/");
        realm = getInitParameter("realm", null);
        ConsumerFactory factory = new ConsumerFactory(new InMemoryConsumerAssociationStore());
        consumerHelper = factory.getConsumerHelper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String domain = req.getParameter("hd");
        if (domain != null) {
            // User attempting to login with provided domain, build and OpenID request and redirect
            try {
                AuthRequest authRequest = startAuthentication(domain, req);
                String url = authRequest.getDestinationUrl(true);
                resp.sendRedirect(url);
            } catch (OpenIDException e) {
                throw new ServletException("Error initializing OpenID request", e);
            }
        } else {
            // This is a response from the provider, go ahead and validate
            doPost(req, resp);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            UserInfo user = completeAuthentication(req);
            req.getSession().setAttribute("user", user);
            resp.sendRedirect(homePath);
        } catch (OpenIDException e) {
            throw new ServletException("Error processing OpenID response", e);
        }
    }

    AuthRequest startAuthentication(String op, HttpServletRequest request)
            throws OpenIDException {
        IdpIdentifier openId = new IdpIdentifier(op);

        String realm = realm(request);
        String returnToUrl = returnTo(request);

        AuthRequestHelper helper = consumerHelper.getAuthRequestHelper(openId, returnToUrl);
        addAttributes(helper);

        HttpSession session = request.getSession();
        AuthRequest authReq = helper.generateRequest();
        authReq.setRealm(realm);

        UiMessageRequest uiExtension = new UiMessageRequest();
        uiExtension.setIconRequest(true);
        authReq.addExtension(uiExtension);

        session.setAttribute("discovered", helper.getDiscoveryInformation());
        return authReq;
    }

    UserInfo completeAuthentication(HttpServletRequest request)
            throws OpenIDException {
        HttpSession session = request.getSession();
        ParameterList openidResp = Step2.getParameterList(request);
        String receivingUrl = currentUrl(request);
        DiscoveryInformation discovered =
                (DiscoveryInformation) session.getAttribute("discovered");


        AuthResponseHelper authResponse =
                consumerHelper.verify(receivingUrl, openidResp, discovered);
        if (authResponse.getAuthResultType() == AuthResponseHelper.ResultType.AUTH_SUCCESS) {
            return onSuccess(authResponse, request);
        }
        return onFail(authResponse, request);
    }

    void addAttributes(AuthRequestHelper helper) {
        helper.requestAxAttribute(Step2.AxSchema.EMAIL, true)
            .requestAxAttribute(Step2.AxSchema.FIRST_NAME, true)
            .requestAxAttribute(Step2.AxSchema.LAST_NAME, true);
    }

    String currentUrl(HttpServletRequest request) {
        return Step2.getUrlWithQueryString(request);
    }

    String realm(HttpServletRequest request) {
        if (StringUtils.isNotBlank(realm)) {
            return realm;
        } else {
            return baseUrl(request);
        }
    }

    String returnTo(HttpServletRequest request) {
        return new StringBuffer(baseUrl(request))
                .append(request.getContextPath())
                .append(returnToPath).toString();
    }

    String baseUrl(HttpServletRequest request) {
        StringBuffer url = new StringBuffer(request.getScheme())
                .append("://").append(request.getServerName());

        if ((request.getScheme().equalsIgnoreCase("http")
                && request.getServerPort() != 80)
                || (request.getScheme().equalsIgnoreCase("https")
                && request.getServerPort() != 443)) {
            url.append(":").append(request.getServerPort());
        }

        return url.toString();
    }

    UserInfo onSuccess(AuthResponseHelper helper, HttpServletRequest request) {
        return new UserInfo(helper.getClaimedId().toString(),
                helper.getAxFetchAttributeValue(Step2.AxSchema.EMAIL),
                helper.getAxFetchAttributeValue(Step2.AxSchema.FIRST_NAME),
                helper.getAxFetchAttributeValue(Step2.AxSchema.LAST_NAME));
    }

    UserInfo onFail(AuthResponseHelper helper, HttpServletRequest request) {
        return null;
    }

    protected String getInitParameter(String key, String defaultValue) {
        String value = getInitParameter(key);
        return StringUtils.isBlank(value) ? defaultValue : value;
    }*/
}
