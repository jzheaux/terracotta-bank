package com.joshcummings.codeplay.terracotta;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

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

	@Test(groups="data", expectedExceptions=NoSuchElementException.class)
	public void testLoginForSQLi() {
		goToPage("/");
			
		String usernameSQLi = "' OR 1=1 --";
			
		driver.findElement(By.name("username")).sendKeys(usernameSQLi);
		driver.findElement(By.name("login")).submit();

		findElementEventually(driver, By.id("deposit"), 2000);
		Assert.fail("Successful login with SQLi!");
	}
}
