package com.ipoint.coursegenerator.server;

import java.io.IOException;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ipoint.coursegenerator.server.db.model.User;

public class ExpirationFilter implements Filter {

	public static final PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String userId = (String) request.getSession().getAttribute("userId");
		if (!request.getRequestURI().startsWith("/orderchoice") && userId != null) {
			PersistenceManager pm = pmfInstance.getPersistenceManager();
			Transaction trans = pm.currentTransaction();
			trans.begin();
			User user = pm.getObjectById(User.class, userId);
			pm.refresh(user);
			if (user != null
					&& (user.getExpirationDate().getTime() < System.currentTimeMillis() || user
							.getCurrentConvertionCount() > user.getCurrentPlanCount())) {
				response.sendRedirect("/orderchoice");
			} else {
				chain.doFilter(req, resp);
			}
			trans.commit();
		} else {
			chain.doFilter(req, resp);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {

	}

}
