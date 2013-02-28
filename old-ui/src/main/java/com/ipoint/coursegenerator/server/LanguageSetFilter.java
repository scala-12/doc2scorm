package com.ipoint.coursegenerator.server;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class LanguageSetFilter implements Filter {
	private Locale russianLocale = new Locale("ru");

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		if ((((HttpServletRequest) request).getRequestURI() == null
				|| ((HttpServletRequest) request).getRequestURI().equals("/"))
				&& request.getLocale().getLanguage().equals(russianLocale.getLanguage())) {
			request.getRequestDispatcher("/index-ru.html").forward(request, response);
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

}
