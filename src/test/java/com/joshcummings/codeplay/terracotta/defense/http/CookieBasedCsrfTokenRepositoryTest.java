package com.joshcummings.codeplay.terracotta.defense.http;

import org.testng.annotations.Test;

public class CookieBasedCsrfTokenRepositoryTest {

	@Test
	public void makeToken() {
		CookieBasedCsrfTokenRepository repository = new CookieBasedCsrfTokenRepository();
		System.out.println(repository.makeToken(null));
		System.out.println(repository.makeToken(null));
		System.out.println(repository.makeToken(null));
		System.out.println(repository.makeToken(null));
		System.out.println(repository.makeToken(null));
		System.out.println(repository.makeToken(null));
		System.out.println(repository.makeToken(null));
		System.out.println(repository.makeToken(null));
		System.out.println(repository.makeToken(null));
		System.out.println(repository.makeToken(null));
	}
}
