package com.joshcummings.codeplay.terracotta;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CheckLookupFunctionalTest extends AbstractEmbeddedTomcatSeleniumTest {
	@BeforeClass
	public void doLogin() {
		login("john.coltraine", "j0hn");
	}
	
	@AfterClass
	public void doLogout() {
		logout();
	}
	
	@Test(expectedExceptions=NoAlertPresentException.class)
	public void testMakeDepositForXSS() {
		driver.get("http://localhost:8080");
		
		String checkLookupNumber = "<script>alert(\'checkLookupNumber is vulnerable on \' + document.domain);</script>";
		
		driver.findElement(By.name("checkLookupNumber")).sendKeys(checkLookupNumber);
		
		driver.findElement(By.name("lookup")).submit();
		
	   	 Alert alert = driver.switchTo().alert();
	   	 Assert.fail(getTextThenDismiss(alert));
	}
}
