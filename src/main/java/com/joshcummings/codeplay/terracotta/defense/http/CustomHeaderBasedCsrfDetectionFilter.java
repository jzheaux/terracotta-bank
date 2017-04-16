package com.joshcummings.codeplay.terracotta.defense.http;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

//@WebFilter("/*")
public class CustomHeaderBasedCsrfDetectionFilter extends AbstractCsrfDetectionFilter {
	private List<String> pathExceptions =
			Arrays.asList("/login", "/logout", "/employeeLogin", "/register");
	
	@Override
	public boolean hasCsrf(HttpServletRequest request) {
		return !(pathExceptions.contains(request.getRequestURI()) ||
				"XmlHttpRequest".equals(request.getHeader("X-Requested-With"))); 
	}
}
