package com.joshcummings.codeplay.terracotta.defense.http;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionBasedTokenRepository implements CsrfTokenRepository {
	private SecureRandom rnd = new SecureRandom();
	
	@Override
	public String loadToken(HttpServletRequest request) {
		return (String)request
				.getSession().getAttribute("csrfToken");
	}

	@Override
	public String makeToken(HttpServletRequest request) {
		// thanks to https://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string/41156#41156
		return new BigInteger(130, rnd).toString(32);
	}

	@Override
	public void storeToken(String token, HttpServletRequest request, HttpServletResponse response) {
		request.getSession().setAttribute("csrfToken", token);
	}

}
