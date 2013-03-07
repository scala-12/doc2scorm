package com.ipoint.coursegenerator.server.paypal;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ipoint.coursegenerator.server.authorization.CourseGeneratorServletAuth;
import com.ipoint.coursegenerator.server.authorization.GoogleAuthorizationUtils;
import com.ipoint.coursegenerator.server.db.CourseGeneratorDAO;
import com.ipoint.coursegenerator.server.db.model.GoogleAppsDomain;
import com.ipoint.coursegenerator.server.db.model.User;

public class PayPalAutoReturnServlet extends HttpServlet {

	private static final long serialVersionUID = -9026394925451413998L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PersistenceManager pm = CourseGeneratorDAO.getPersistenceManager();
		User user = pm.getObjectById(User.class, req.getSession().getAttribute("userId"));
		pm.refresh(user);
		GoogleAppsDomain domain = user.getDomain();
		pm.refresh(domain);
		if (domain != null
				&& req.getSession().getAttribute(CourseGeneratorServletAuth.PURCHASE_TOKEN_PARAMETER) != null) {
			resp.sendRedirect("https://www.google.com/a/cpanel/" + domain.getName()
					+ "/DomainAppInstall?appId=" + GoogleAuthorizationUtils.APPLICATION_ID
					+ "&productListingId=" + GoogleAuthorizationUtils.PRODUCT_LISTING_ID
					+ "&editionId=free&paymentModel=FREE");
		} else {
			resp.sendRedirect("/Coursegenerator.html");
		}
		pm.close();
	}
}
