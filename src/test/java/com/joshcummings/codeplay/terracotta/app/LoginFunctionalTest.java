package com.joshcummings.codeplay.terracotta.app;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.joshcummings.codeplay.terracotta.testng.OpenRedirectCheatSheet;
import com.joshcummings.codeplay.terracotta.testng.TestConstants;
import com.joshcummings.codeplay.terracotta.testng.XssCheatSheet;

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
		
		Assert.assertEquals(driver.getCurrentUrl(), "http://honestsite.com/", "You got redirected to: " + driver.getCurrentUrl());
	}

	/**
	 * Tomcat 8 and up are not vulnerable to this attack
	 * @throws IOException
	 */
	@Test(groups="http")
	public void testLoginForCrlfOverOpenRedirect() throws IOException {
		for ( String exploit : new OpenRedirectCheatSheet() ) {
			BasicNameValuePair relay = new BasicNameValuePair("relay", String.format(exploit, "relay"));
			BasicNameValuePair username = new BasicNameValuePair("username", "admin");
			BasicNameValuePair password = new BasicNameValuePair("password", "admin");
			try (CloseableHttpResponse response = http.post("/login", relay, username, password)) {
				Header[] h = response.getHeaders("X-Evil-Header");
				Assert.assertTrue(h.length == 0);
			}
		}
	}
	
	@Test(groups="http")
	public void testLoginForXssOverOpenRedirect() throws InterruptedException {
		goToPage("/?relay=http://" + TestConstants.host + "?evilParameter=xss");

		driver.findElement(By.name("username")).sendKeys("admin");
		driver.findElement(By.name("password")).sendKeys("admin");
		driver.findElement(By.name("login")).submit();
		
		Thread.sleep(2000);
		
		Assert.assertEquals(driver.getCurrentUrl(), "http://honestsite.com/", "You got redirected to: " + driver.getCurrentUrl());
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
	
	@Test(groups="data")
	public void testLoginForInformationLeakage() throws Exception {
		goToPage("/");
			
		String wrongUsername = "@#$%^&234";
		String maliciousUsername = "' @#$5";
		
		String wrong = http.postFully("/login", 
				new BasicNameValuePair("username", wrongUsername));
		String malicious = http.postFully("/login",  
				new BasicNameValuePair("username", maliciousUsername));
		
		boolean isFishy = StringUtils.getLevenshteinDistance(wrong, malicious) > 100;
		
		Assert.assertFalse(isFishy, 
				"Potential information leakage when using " + maliciousUsername);
	}
}
