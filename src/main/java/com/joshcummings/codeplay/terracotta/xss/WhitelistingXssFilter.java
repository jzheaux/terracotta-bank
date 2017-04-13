package com.joshcummings.codeplay.terracotta.xss;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.errors.IntrusionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Servlet Filter implementation class WhitelistingXssFilter
 */
//@WebFilter("/*")
public class WhitelistingXssFilter implements Filter {
	private final Logger xssIncidentLogger = LoggerFactory.getLogger("xss-logger");
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if ( request instanceof HttpServletRequest && response instanceof HttpServletResponse ) {
			if ( hasXss((HttpServletRequest)request) ) {
				xssIncidentLogger.error("XSS Detected!");
			}
			chain.doFilter(request, response);
		}
	}

	private static final Pattern onlyAlphaNumeric = Pattern.compile("[A-Za-z0-9\\.\\+@\\$]+");
	
	private boolean hasXss(HttpServletRequest request) {
		Encoder encoder = ESAPI.encoder();
		for ( Map.Entry<String, String[]> entry : request.getParameterMap().entrySet() ) {
			for ( String value : entry.getValue() ) {
				try {
					String canonicalized = encoder.canonicalize(value);
					if ( !onlyAlphaNumeric.matcher(canonicalized).matches() ) {
						return true;
					}
				} catch ( IntrusionException e ) {
					return true;
				}
			}
		}
		return false;
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
	}
}
