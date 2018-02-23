package com.joshcummings.codeplay.terracotta.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.poi.util.IOUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.joshcummings.codeplay.terracotta.testng.XssCheatSheet;

public class CheckLookupFunctionalTest extends AbstractEmbeddedTomcatSeleniumTest {
	@BeforeClass(alwaysRun=true)
	public void doLogin() {
		login("john.coltraine", "j0hn");
	}
	
	@AfterClass(alwaysRun=true)
	public void doLogout() {
		logout();
	}
	
	@Test(groups="web")
	public void testCheckLookupForXss() {		
		for ( String template : new XssCheatSheet(true) ) {
			goToPage("/");
			
			try {
				String checkLookupNumber = String.format(template, "checkLookupNumber");
				
				driver.findElement(By.name("checkLookupNumber")).sendKeys(checkLookupNumber);
				
				driver.findElement(By.name("lookup")).submit();
				
			   	 Alert alert = switchToAlertEventually(driver, 2000);
			   	 Assert.fail(getTextThenDismiss(alert));
			} catch ( NoAlertPresentException e ) {
				// okay!
			}
		}
	}
	
	protected byte[] attemptTraversedLookup(String maliciousCheckNumber) throws IOException {
		String checkLookupNumber = maliciousCheckNumber;

		try ( CloseableHttpResponse response =
				http.post("/checkLookup", new BasicNameValuePair("checkLookupNumber", checkLookupNumber)) ) {
			ByteArrayOutputStream destination = new ByteArrayOutputStream();
			IOUtils.copy(response.getEntity().getContent(), destination);
	
			return destination.toByteArray();
		}
	}
	
	@Test(groups="filesystem")
	public void testCheckLookupDirectoryTraversal() throws IOException {
		goToPage("/");
		
		String path = "etc/passwd";
		for ( int i = 0; i < 10; i++ ){
			byte[] b = attemptTraversedLookup(path);
			String response = new String(b);
			Assert.assertTrue(response.contains("The provided check number is invalid"), response);
			path = "../" + path;
		}
	}
}
