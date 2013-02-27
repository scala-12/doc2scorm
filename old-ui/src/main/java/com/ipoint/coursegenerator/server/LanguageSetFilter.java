package com.ipoint.coursegenerator.server;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class LanguageSetFilter implements Filter {

	static class FilteredRequest extends HttpServletRequestWrapper {

		private Locale russianLocale = new Locale("ru");

		private Map<String, String[]> allParameters = null;

		private boolean setRussianLocale = false;

		public FilteredRequest(ServletRequest request) {
			super((HttpServletRequest) request);
			if (request.getLocale().getLanguage().equals(russianLocale.getLanguage())) {
				setRussianLocale = true;
			}
		}

		@Override
		public String getParameter(final String name) {
			String[] strings = (String[]) getParameterMap().get(name);
			if (strings != null) {
				return strings[0];
			}
			return super.getParameter(name);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Map getParameterMap() {
			if (allParameters == null) {
				allParameters = new TreeMap<String, String[]>();
				allParameters.putAll(super.getParameterMap());
				if (setRussianLocale) {
					allParameters.put("locale", new String[] { "ru" });
				}
			}
			return Collections.unmodifiableMap(allParameters);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Enumeration<String> getParameterNames() {
			return Collections.enumeration(getParameterMap().keySet());
		}

		@Override
		public String[] getParameterValues(String name) {
			return (String[]) getParameterMap().get(name);
		}
	}

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		chain.doFilter(new FilteredRequest(request), response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

}
