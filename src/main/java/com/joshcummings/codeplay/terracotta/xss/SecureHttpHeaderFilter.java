package com.joshcummings.codeplay.terracotta.xss;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class SecureHttpHeaderFilter
 */
//@WebFilter(value="/*", dispatcherTypes={ DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ERROR })
public class SecureHttpHeaderFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		} finally {
			// Headers can't be added after the response is committed, but because this filter
			// is configured to fire on forward and error dispatches as well, they will get set
			// before any response is written back to the client
			if ( request instanceof HttpServletRequest && response instanceof HttpServletResponse ) {
				((HttpServletResponse)response).setHeader("X-XSS-Protection", "1;mode=block");
				((HttpServletResponse)response).setHeader("Content-Security-Policy",
						  "default-src 'self';"
						+ "script-src 'self' code.jquery.com;"
						+ "img-src 'self' blob: data: ;"
						+ "style-src 'self' fonts.googleapis.com;"
						+ "font-src 'self' fonts.googleapis.com fonts.gstatic.com data: ;"
						+ "report-uri /cspViolation");
			}
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

}
