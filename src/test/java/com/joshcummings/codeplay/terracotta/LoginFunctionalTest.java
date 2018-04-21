package com.joshcummings.codeplay.terracotta;

import com.joshcummings.codeplay.terracotta.testng.TestConstants;
import com.joshcummings.codeplay.terracotta.testng.XssCheatSheet;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.testng.Assert.assertEquals;

public class LoginFunctionalTest extends AbstractEmbeddedTomcatSeleniumTest {
	@AfterMethod(alwaysRun=true)
	public void doLogout() {
		logout();
	}
	
	@Test(groups="web")
	public void testLoginForXss() throws InterruptedException {		
		for ( String template : new XssCheatSheet() ) {
			goToPage("/");
			
			try {
				String usernameXss = String.format(template, "username");
				
				driver.findElement(By.name("username")).sendKeys(usernameXss);
				driver.findElement(By.name("login")).submit();
				
			   	 Alert alert = switchToAlertEventually(driver, 2000);
			   	 Assert.fail(getTextThenDismiss(alert) + " using " + template);
			} catch ( NoAlertPresentException e ) {
				// okay!
			}
		}
	}
	
	@Test(groups="http")
	public void testLoginForOpenRedirect() throws InterruptedException {
		goToPage("/?relay=http://" + TestConstants.evilHost);

		driver.findElement(By.name("username")).sendKeys("admin");
		driver.findElement(By.name("password")).sendKeys("admin");
		driver.findElement(By.name("login")).submit();
		
		Thread.sleep(2000);
		
		assertEquals(driver.getCurrentUrl(), "http://honestsite.com/", "You got redirected to: " + driver.getCurrentUrl());
	}

	@Test(groups="data", expectedExceptions=NoSuchElementException.class)
	public void testLoginForSQLi() {
		goToPage("/");
			
		String usernameSQLi = "' OR 1=1 --";
			
		driver.findElement(By.name("username")).sendKeys(usernameSQLi);
		driver.findElement(By.name("login")).submit();

		findElementEventually(driver, By.id("deposit"), 2000);
		Assert.fail("Successful login with SQLi!");
	}

	private CloseableHttpResponse attemptLogin(String username, String password) throws IOException {
		return http.post("/login",
					new BasicNameValuePair("username", username),
					new BasicNameValuePair("password", password));
	}

	@Test(groups="enumeration")
	public void testLoginForEnumeration() {
		try (
			CloseableHttpResponse registered = attemptLogin("admin","asdf" );
			CloseableHttpResponse unregistered = attemptLogin("7spelledwithtwovees", "asdf");
		) {

			String registeredBody = IOUtils.toString(registered.getEntity().getContent(), "UTF-8");
			String registeredStatusLine = registered.getStatusLine().toString();
			Collection<String> registeredHeaders = Arrays.stream(registered.getAllHeaders())
													.map(header -> header.toString())
													.collect(Collectors.toList());

			String unregisteredBody = IOUtils.toString(unregistered.getEntity().getContent(), "UTF-8");
			String unregisteredStatusLine = unregistered.getStatusLine().toString();
			Collection<String> unregisteredHeaders = Arrays.stream(unregistered.getAllHeaders())
													.map(header -> header.toString())
													.collect(Collectors.toList());

			assertEquals(registeredBody, unregisteredBody);
			assertEquals(registeredStatusLine, unregisteredStatusLine);
			assertEquals(registeredHeaders, unregisteredHeaders);
		} catch ( IOException e ) {
			throw new IllegalStateException(e);
		}
	}

	@Test(groups="enumeration", enabled=false)
	public void attackLoginUsingEnumeration() throws Exception {
		MultiValueMap<String, String> usernamesByResponseType = new LinkedMultiValueMap<>();

		ExecutorService executors = Executors.newFixedThreadPool(32);
		List<Future<Map.Entry<String, String>>> futures = new ArrayList<>();

		Set<String> usernamesToTest = new HashSet<>();

		Set<String> firstNames =
			readAllLines("first-names.csv")
				.flatMap((firstName) -> Stream.of(firstName.split(",")))
				.collect(Collectors.toSet());

		firstNames.stream()
			.forEach((firstName) -> {
				readAllLines("last-names.csv")
					.forEach((lastName) -> {
						usernamesToTest.add(firstName.toLowerCase());
						usernamesToTest.add(firstName.toLowerCase().substring(0, 1) + lastName.toLowerCase());
						usernamesToTest.add(firstName.toLowerCase() + "." + lastName.toLowerCase());
					});
				}
			);

		System.out.println("Will test " + usernamesToTest.size() + " usernames");

		for ( String username : usernamesToTest ) {
			futures.add(executors.submit(() -> attemptLogin(username)));
		}

		for ( Future<Map.Entry<String, String>> f : futures ) {
			Map.Entry<String, String> ret = f.get();
			usernamesByResponseType.add(ret.getKey(), ret.getValue());
		}

		assertEquals(1, usernamesByResponseType.size(), "Potential enumeration vulnerability, these appear to be legit usernames " + usernamesByResponseType.get("password"));
	}

	private Map.Entry<String, String> attemptLogin(String username) {
		try (
			CloseableHttpResponse response =
				http.post("/login",
					new BasicNameValuePair("username", username),
					new BasicNameValuePair("password", "oi12bu34ci 123h 4dp2i3h4 234jn"));
		) {
			String str = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
			boolean passwordFailed = str.contains("The password");

			return new AbstractMap.SimpleImmutableEntry<>(passwordFailed ? "password" : "username", username);
		} catch ( IOException e ) {
			throw new IllegalStateException(e);
		}
	}

	private Stream<String> readAllLines(String filename) {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		return br.lines();
	}
}
