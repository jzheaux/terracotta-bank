package com.joshcummings.codeplay.terracotta.xss;

import java.io.IOException;
import java.util.Map;

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
 * Servlet Filter implementation class WhitelistingXssFilter
 */
//@WebFilter("/*")
public class BlacklistingXssFilter implements Filter {
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if ( request instanceof HttpServletRequest && response instanceof HttpServletResponse ) {
			if ( hasXss((HttpServletRequest)request) ) {
				((HttpServletResponse)response).sendError(400, "XSS Detected");
			} else {
				chain.doFilter(request, response);
			}
		}
	}
	
	private boolean hasXss(HttpServletRequest request) {
		for ( Map.Entry<String, String[]> entry : request.getParameterMap().entrySet() ) {
			for ( String value : entry.getValue() ) {
				if ( hasXss(value) ) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean hasXss(String value) {
		String[] checkThese = new String[] {
			"<script>"
		};
		
		for ( String checkThis : checkThese ) {
			if ( value.contains(checkThis) ) {
				return true;
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
