package com.joshcummings.codeplay.terracotta.defense.http;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDateTime;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.joshcummings.codeplay.terracotta.model.User;

public class JwtBasedCsrfTokenRepository implements CsrfTokenRepository {
	static final String DEFAULT_CSRF_PARAMETER_NAME = "csrfToken";

	static final String DEFAULT_CSRF_HEADER_NAME = "X-XSRF-TOKEN";
	
	private final SecureRandom rnd = new SecureRandom();
	
	protected final Algorithm algorithm;
	protected final JWTVerifier verifier;

	private static final String SECRET = "neverhardcodethis";
	private static final String ISSUER = "terracotta-bank";
	
	public JwtBasedCsrfTokenRepository() {
		try {
			algorithm = Algorithm.HMAC256(SECRET);
			verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	@Override
	public String loadToken(HttpServletRequest request) {
		// since we are effectively treating the signed parameter as our repo, we'll look it up that way here
		String encoded = request.getParameter(DEFAULT_CSRF_PARAMETER_NAME);
		
		if ( encoded == null || failsVerification(encoded, request) ) {
			return null;
		}

		return encoded;

	}
	
	protected boolean failsVerification(String encoded, HttpServletRequest request) {
		DecodedJWT jwt = verifier.verify(encoded);
		if (jwt.getExpiresAt().after(new Date())) {
			String subject = resolveSubject(request);
			if (jwt.getSubject().equals(subject)) {
				// we could take an additional test and verify the nonce,
				// giving this approach revocability
				// String nonce = jwt.getClaim("nonce").asString();

				return false;
			}
		}
		return true;
	}

	@Override
	public String makeToken(HttpServletRequest request) {
		String nonce = new BigInteger(130, rnd).toString(32);
		
		String subject = resolveSubject(request);
		
		Date expiry = LocalDateTime.now().plusMinutes(30).toDate();
		
		try {
			String encoded = JWT.create()
					.withIssuer(ISSUER)
					.withSubject(subject)
					.withExpiresAt(expiry)
					.withClaim("nonce", nonce)
					.sign(algorithm);
			return encoded;
		} catch (JWTCreationException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void storeToken(String token, HttpServletRequest request, HttpServletResponse response) {
		// nothing to do
	}
	
	protected String resolveSubject(HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("authenticatedUser");
		return user == null ? "prelogin" : user.getUsername();
	}

}
