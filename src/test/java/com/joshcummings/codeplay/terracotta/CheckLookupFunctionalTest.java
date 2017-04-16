package com.joshcummings.codeplay.terracotta;

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
	public void testCheckLookupForXSS() {		
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
}
