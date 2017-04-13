package com.joshcummings.codeplay.terracotta;

import java.io.File;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MakeDepositFunctionalTest extends AbstractXssTest {
	@BeforeClass(alwaysRun=true)
	public void doLogin() {
		login("john.coltraine", "j0hn");
	}
	
	@AfterClass(alwaysRun=true)
	public void doLogout() {
		logout();
	}
	
	@Test(groups="web")
	public void testMakeDepositForXSS() {
		for ( String template : templates ) {
			template = escapeQuotes(template);
			goToPage("/");
			
			try {
				String depositAccountNumberXss = String.format(template, "depositAccountNumber");
				String depositCheckNumberXss = String.format(template, "depositCheckNumber");
				String depositAmountXss = String.format(template, "depositAmount");
				
				driver.findElement(By.name("depositAccountNumber")).sendKeys(depositAccountNumberXss);
				driver.findElement(By.name("depositCheckNumber")).sendKeys(depositCheckNumberXss);
				driver.findElement(By.name("depositAmount")).sendKeys(depositAmountXss);
				driver.findElement(By.name("depositCheckImage")).sendKeys(new File("src/test/resources/check.png").getAbsolutePath());
				
				ignoreErrors(() -> driver.findElement(By.name("deposit")).submit());
				
			   	Alert alert = switchToAlertEventually(driver, 2000);
			   	Assert.fail(getTextThenDismiss(alert));
			} catch ( NoAlertPresentException e ) {
				// awesome!
			}
		}
	}
	
	@Test(groups="filesystem")
	public void testMakeDepositForDirectoryTraversal() {
		goToPage("/");
		String depositAccountNumber = "987654321";
		String depositCheckNumber = "../../explorer.exe";
		String depositAmount = "450.00";
		
		driver.findElement(By.name("depositAccountNumber")).sendKeys(depositAccountNumber);
		driver.findElement(By.name("depositCheckNumber")).sendKeys(depositCheckNumber);
		driver.findElement(By.name("depositAmount")).sendKeys(depositAmount);
		driver.findElement(By.name("depositCheckImage")).sendKeys(new File("src/test/resources/check.png").getAbsolutePath());
		
		ignoreErrors(() -> driver.findElement(By.name("deposit")).submit());
		
		String checkLookupNumber = depositCheckNumber;
		
		driver.findElement(By.name("checkLookupNumber")).sendKeys(checkLookupNumber);
		driver.findElement(By.name("lookup")).submit();
		
		File uploaded = new File("explorer.exe");
		boolean exists = uploaded.exists();
		
		uploaded.delete();
		
		Assert.assertFalse(exists, "File was uploaded. :(");
		
	}
}
