package com.joshcummings.codeplay.terracotta.defense.http;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.google.common.base.Charsets;

/**
 * A filter that is helpful for older versions of Tomcat and JBoss which 
 * don't correctly encode header values and are thus vulnerable to CRLF injection.
 * 
 * @author Josh
 *
 */
//@WebFilter("/*")
public class SafeEncodedHeadersFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if ( response instanceof HttpServletResponse ) {
			chain.doFilter(request, new SafeEncodedHeadersHttpServletResponseWrapper((HttpServletResponse)response));
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
	}

	private static class SafeEncodedHeadersHttpServletResponseWrapper extends HttpServletResponseWrapper {

		public SafeEncodedHeadersHttpServletResponseWrapper(HttpServletResponse response) {
			super(response);
		}
		
		protected final String prepareHeaderValue(String value) {
			// check against whitelist, if possible
			
			// encode the output
			byte[] b = value.getBytes(Charsets.ISO_8859_1);
			
			for ( int i = 0; i < b.length; i++ ) {
				if ( b[i] == 10 || b[i] == 13 ) {
					/* Also possible to reject the request entirely, refuse to add the header, etc.
					throw new IllegalArgumentException("CRLF detected!");
					 */
					b[i] = (byte)' ';
				}
			}
			
			return new String(b);
		}
		
		@Override
		public void addHeader(String name, String value) {
			super.addHeader(name, prepareHeaderValue(value));
		}
		
		@Override
		public void setHeader(String name, String value) {
			super.setHeader(name, prepareHeaderValue(value));
		}
	}
}
