package com.joshcummings.codeplay.terracotta.app;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.joshcummings.codeplay.terracotta.testng.CrlfCheatSheet;
import com.joshcummings.codeplay.terracotta.testng.TestConstants;
import com.joshcummings.codeplay.terracotta.testng.XssCheatSheet;

public class TransferMoneyFunctionalTest extends AbstractEmbeddedTomcatSeleniumTest {
	@BeforeClass(alwaysRun=true)
	public void doLogin(ITestContext ctx) {
		System.out.println("Logging in b4 trying to transfer money");
		login("john.coltraine", "j0hn");
	}

	@AfterClass(alwaysRun=true)
	public void doLogout() {
		System.out.println("Logging out After trying to transfer money");
		logout();
	}
	
	@Test(groups="web")
	public void testTransferMoneyForXss() {
		for (String template : new XssCheatSheet(true)) {
			goToPage("/");
			
			try {
				String fromAccountNumber = String.format(template, "fromAccountNumber");
				String toAccountNumber = String.format(template, "toAccountNumber");
				String transferAmount = String.format(template, "transferAmount");

				driver.findElement(By.name("fromAccountNumber")).sendKeys(fromAccountNumber);
				driver.findElement(By.name("toAccountNumber")).sendKeys(toAccountNumber);
				driver.findElement(By.name("transferAmount")).sendKeys(transferAmount);
				driver.findElement(By.name("transfer")).submit();
				
				Alert alert = switchToAlertEventually(driver, 2000);
				Assert.fail(getTextThenDismiss(alert));
			} catch (NoAlertPresentException e) {
				// awesome!
			}
		}
	}
	
	/**
	 * Tomcat 8 and up are not vulnerable to this attack
	 * @throws IOException
	 */
	@Test(groups="http")
	public void testTransferMoneyForCrlf() {
		for ( String template : new CrlfCheatSheet() ) {
			goToPage("/");
			
			try {
				String c = String.format(template, "c");
				String fromAccountNumber = String.format(template, "fromAccountNumber");
				String toAccountNumber = String.format(template, "toAccountNumber");
				String transferAmount = String.format(template, "transferAmount");
				
				try ( CloseableHttpResponse response = 
					http.post("/transferMoney?c=" + c,
						new BasicNameValuePair("c", c),
						new BasicNameValuePair("fromAccountNumber", fromAccountNumber),
						new BasicNameValuePair("toAccountNumber", toAccountNumber),
						new BasicNameValuePair("transferAmount", transferAmount)) ) {
					Header[] headers = response.getHeaders("X-Evil-Header");
					Assert.assertTrue(headers.length == 0, Arrays.toString(headers));					
				}
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}
	}
	
	@Test(groups="http")
	public void testTransferMoneyForCsrf() throws Exception {
		goToPage("/");
		
		// read what is in John's account right now
		String originalTotal = driver.findElement(By.id("accountBalance-987654321")).getText();
		
		driver.get("http://" + TestConstants.evilHost + "/evilsite/csrf.html");
		
		// wait for the stealing script to take effect
		Thread.sleep(5000);
		
		goToPage("/");
		
		// refresh the page and see how much John has now that the csrf script has run
		String newTotal = driver.findElement(By.id("accountBalance-987654321")).getText();
		
		// if they are equal, no csrf vulnerability!
		Assert.assertEquals(Double.parseDouble(originalTotal), Double.parseDouble(newTotal));
	}
	
	@Test(groups="data")
	public void testTransferMoneyForInformationLeakage() throws Exception {
		String wrongValue = "@#$%^&";
		String maliciousValue = "' @#$5";
		
		String wrong = http.postFully("/transferMoney",  
				new BasicNameValuePair("fromAccountNumber", wrongValue + " in fromAccountNumber"),
				new BasicNameValuePair("toAccountNumber", wrongValue + " in toAccountNumber"),
				new BasicNameValuePair("transferAmount", wrongValue + " in transferAmount"));
		
		String malicious = http.postFully("/transferMoney",  
				new BasicNameValuePair("fromAccountNumber", maliciousValue + " in fromAccountNumber"),
				new BasicNameValuePair("toAccountNumber", maliciousValue + " in toAccountNumber"),
				new BasicNameValuePair("transferAmount", maliciousValue + " in transferAmount"));
		
		boolean isFishy = StringUtils.getLevenshteinDistance(wrong, malicious) > 100;
		
		Assert.assertFalse(isFishy, 
				"Potential information leakage when using " + maliciousValue);
	}
}
