package com.joshcummings.codeplay.terracotta.defense.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CsrfTokenRepository {
	String loadToken(HttpServletRequest request);
	String makeToken(HttpServletRequest request);
	void storeToken(String token, HttpServletRequest request, HttpServletResponse response);
	
	default void replaceToken(HttpServletRequest request, HttpServletResponse response) {
		String token = loadToken(request);
		if ( token != null ) {
			token = makeToken(request);
			storeToken(token, request, response);
			request.setAttribute("csrfToken", token);
		}
	}
}
