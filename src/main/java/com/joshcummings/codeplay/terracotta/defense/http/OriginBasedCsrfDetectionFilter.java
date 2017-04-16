package com.joshcummings.codeplay.terracotta.defense.http;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@WebFilter("/*")
public class OriginBasedCsrfDetectionFilter extends AbstractCsrfDetectionFilter {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public boolean hasCsrf(HttpServletRequest request) {
		String origin = establishOrigin(request);
		String target = establishTarget(request);
		
		return origin == null || target == null || !origin.equals(target);
	}

	protected String establishOrigin(HttpServletRequest request) {
		String origin = request.getHeader("Origin");
		
		if ( origin == null ) {
			String referer = request.getHeader("Referer");
			if ( referer != null ) {
				try {
					origin = new URL(referer).getHost();
				} catch ( MalformedURLException e ) {
					logger.error("Referer is malformed; won't use it to determine csrf", new MalformedURLException());
				}
			}
		}
		
		return origin;
	}
	
	protected String establishTarget(HttpServletRequest request) {
		String target = request.getHeader("X-Forwarded-Host");
		
		if ( target == null ) {
			target = request.getHeader("Host");
		}
		
		return target;
	}
}
