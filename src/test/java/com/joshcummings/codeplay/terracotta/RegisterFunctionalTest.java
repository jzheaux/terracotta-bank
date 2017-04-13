package com.joshcummings.codeplay.terracotta;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

public class RegisterFunctionalTest extends AbstractXssTest {

	@AfterClass(alwaysRun=true)
	public void doLogout() {
		logout();
	}

	@Test(groups="web")
	public void testRegisterForXSS() {
		for (String template : templates) {
			goToPage("/");
			
			try {
				String username = String.format(template, "registerUsername");
				String password = String.format(template, "registerPassword");
				String name = String.format(template, "registerName");
				String email = String.format(template, "registerEmail");

				driver.findElement(By.name("registerUsername")).sendKeys(username);
				driver.findElement(By.name("registerPassword")).sendKeys(password);
				driver.findElement(By.name("registerName")).sendKeys(name);
				driver.findElement(By.name("registerEmail")).sendKeys(email);
				driver.findElement(By.name("register")).submit();

				Alert alert = switchToAlertEventually(driver, 2000);
				Assert.fail(getTextThenDismiss(alert) + " using " + template);
			} catch (NoAlertPresentException e) {
				// awesome!
			}
			
			logout();
		}

	}
}
