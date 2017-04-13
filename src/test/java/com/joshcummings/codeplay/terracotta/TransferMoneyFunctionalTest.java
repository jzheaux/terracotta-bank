package com.joshcummings.codeplay.terracotta;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TransferMoneyFunctionalTest extends AbstractXssTest {
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

	@Test(groups="http")
	public void testTransferMoneyForCSRF() throws Exception {
		goToPage("/");
		String originalTotal = driver.findElement(By.id("accountBalance-987654321")).getText();
		
		driver.get("http://" + evilHost + "/evilsite/csrf.html");
		
		Thread.sleep(5000);
		
		goToPage("/");
		String newTotal = driver.findElement(By.id("accountBalance-987654321")).getText();
		
		Assert.assertEquals(Double.parseDouble(originalTotal), Double.parseDouble(newTotal));
	}
	
	@Test(groups="web")
	public void testTransferMoneyForXSS() {
		for (String template : templates) {
			template = escapeQuotes(template);
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
}
