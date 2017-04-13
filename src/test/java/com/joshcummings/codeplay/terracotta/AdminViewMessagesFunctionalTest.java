package com.joshcummings.codeplay.terracotta;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AdminViewMessagesFunctionalTest extends AbstractEmbeddedTomcatSeleniumTest {
	@BeforeClass(alwaysRun=true)
	protected void doLogin() {
		employeeLogin("admin", "admin");
	}
	
	@AfterClass(alwaysRun=true)
	protected void doLogout() {
		logout();
	}
	
	@Test(groups="web", expectedExceptions={ NoAlertPresentException.class })
	public void testViewMessagesForXSS() {
		goToPage("/employee.jsp");
		driver.findElement(By.name("show")).click();
		Alert alert = switchToAlertEventually(driver, 2000);
	   	Assert.fail(getTextThenDismiss(alert));
	}
}
