package com.joshcummings.codeplay.terracotta.defense.http;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public abstract class AbstractCsrfDetectionFilter implements CsrfDetectionFilter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if ( request instanceof HttpServletRequest ) {
			if ( "POST".equals(((HttpServletRequest)request).getMethod()) && hasCsrf((HttpServletRequest)request) ) {
				throw new ServletException("CSRF detected!");
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}
}
