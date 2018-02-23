package com.joshcummings.codeplay.terracotta.app;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.owasp.esapi.ESAPI;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AdminViewMessagesFunctionalTest extends AbstractEmbeddedTomcatSeleniumTest {
	@BeforeClass(alwaysRun=true)
	protected void doLogin() throws IOException {
		employeeLogin("admin", "admin");
		http.login("admin", "admin");
	}
	
	@AfterClass(alwaysRun=true)
	protected void doLogout() {
		logout();
		http.logout();
	}
	
	@Test(groups="web", expectedExceptions={ NoAlertPresentException.class })
	public void testViewMessagesForXss() {
		goToPage("/employee.jsp");
		driver.findElement(By.name("show")).click();
		Alert alert = switchToAlertEventually(driver, 2000);
	   	Assert.fail(getTextThenDismiss(alert));
	}
	
	@Test(groups="data")
	public void testViewMessagesForInformationLeakageAgainstRelational() throws Exception {
		String wrongValue = "@#$%^&";
		String maliciousValue = "' @#$5";
		
		wrongValue = ESAPI.encoder().encodeForURL(wrongValue + " in query");
		maliciousValue = ESAPI.encoder().encodeForURL(maliciousValue + " in query");
		
		String wrong = http.getFully("/showMessages?q=" + wrongValue);
		
		String malicious = http.getFully("/showMessages?q=" + maliciousValue);
		
		boolean isFishy = StringUtils.getLevenshteinDistance(wrong, malicious) > 100;
		
		Assert.assertFalse(isFishy, 
				"Potential information leakage when using " + maliciousValue);
	}
	
	@Test(groups="data")
	public void testViewMessagesForInformationLeakageAgainstElasticsearch() throws Exception {
		String wrongValue = "@#$%^&";
		String maliciousValue = "\" !@#$ }";
		
		wrongValue = ESAPI.encoder().encodeForURL(wrongValue);
		maliciousValue = ESAPI.encoder().encodeForURL(maliciousValue);
		
		String wrong = http.getFully("/showMessages?q=" + wrongValue);
		
		String malicious = http.getFully("/showMessages?q=" + maliciousValue);
		
		boolean isFishy = StringUtils.getLevenshteinDistance(wrong, malicious) > 100;
		
		Assert.assertFalse(isFishy, 
				"Potential information leakage when using " + maliciousValue);
	}
}
