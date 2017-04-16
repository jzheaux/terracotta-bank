package com.joshcummings.codeplay.terracotta.defense.http;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDateTime;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class SpringSecurityJWTBasedCsrfTokenRepository implements CsrfTokenRepository {
	static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";

	static final String DEFAULT_CSRF_HEADER_NAME = "X-XSRF-TOKEN";
	
	private final SecureRandom rnd = new SecureRandom();
	
	protected final Algorithm algorithm;
	protected final JWTVerifier verifier;

	private static final String SECRET = "neverhardcodethis";
	private static final String ISSUER = "terracotta-bank";
	
	public SpringSecurityJWTBasedCsrfTokenRepository() {
		try {
			algorithm = Algorithm.HMAC256(SECRET);
			verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	@Override
	public CsrfToken generateToken(HttpServletRequest request) {
		String nonce = new BigInteger(130, rnd).toString(32);
		
		String subject = resolveSubject();
		
		Date expiry = LocalDateTime.now().plusMinutes(30).toDate();
		
		try {
			String encoded = JWT.create()
					.withIssuer(ISSUER)
					.withSubject(subject)
					.withExpiresAt(expiry)
					.withClaim("nonce", nonce)
					.sign(algorithm);
			return new DefaultCsrfToken(DEFAULT_CSRF_HEADER_NAME, DEFAULT_CSRF_PARAMETER_NAME, encoded);
		} catch (JWTCreationException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
		// nothing to do
	}

	@Override
	public CsrfToken loadToken(HttpServletRequest request) {
		// since we are effectively treating the signed parameter as our repo, we'll look it up that way here
		String encoded = request.getParameter(DEFAULT_CSRF_PARAMETER_NAME);
		if ( encoded == null ) {
			encoded = request.getHeader(DEFAULT_CSRF_HEADER_NAME);
		}
		
		if ( encoded == null || failsVerification(encoded) ) {
			return null;
		}

		return new DefaultCsrfToken(DEFAULT_CSRF_HEADER_NAME, DEFAULT_CSRF_PARAMETER_NAME, encoded);
	}

	protected boolean failsVerification(String encoded) {
		DecodedJWT jwt = verifier.verify(encoded);
		if (jwt.getExpiresAt().after(new Date())) {
			String subject = resolveSubject();
			if (jwt.getSubject().equals(subject)) {
				// we could take an additional test and verify the nonce,
				// giving this approach revocability
				// String nonce = jwt.getClaim("nonce").asString();

				return false;
			}
		}
		return true;
	}
	
	protected final String resolveSubject() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth.isAuthenticated() ? auth.getName() : "prelogin";
	}
}
