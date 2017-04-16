package com.joshcummings.codeplay.terracotta.defense.http;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieBasedCsrfTokenRepository implements CsrfTokenRepository {
	private SecureRandom rnd = new SecureRandom();
	
	protected String getCookieValue(String name,
			HttpServletRequest request) {
		for ( Cookie cookie : nonNull(request.getCookies()) ) {
			if ( cookie.getName().equals(name) ) {
				return cookie.getValue();
			}
		}
		return null;
	}
	
	@Override
	public String loadToken(HttpServletRequest request) {
		return getCookieValue("csrfToken", request);
	}

	@Override
	public String makeToken(HttpServletRequest request) {
		// thanks to https://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string/41156#41156
		return new BigInteger(130, rnd).toString(32);
	}

	@Override
	public void storeToken(String token,
			HttpServletRequest request,
			HttpServletResponse response) {
		Cookie c = new Cookie("csrfToken", token);
		c.setHttpOnly(true);
		c.setSecure(request.getProtocol().equals("https"));
		c.setMaxAge(30 * 60); // 30 minutes
		
		response.addCookie(c);
	}
	
	protected Cookie[] nonNull(Cookie[] cookies) {
		if ( cookies == null ) return new Cookie[0];
		return cookies;
	}
}
