package com.joshcummings.codeplay.terracotta;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TransferMoneyFunctionalTest extends AbstractEmbeddedTomcatSeleniumTest {
	@BeforeClass
	public void doLogin() {
		login("john.coltraine", "j0hn");
	}
	
	@AfterClass
	public void doLogout() {
		logout();
	}
	
	@Test(expectedExceptions=NoAlertPresentException.class)
	public void testTransferMoneyForXSS() {
		driver.get("http://localhost:8080");
		
		String fromAccountNumber = "<script>alert(\'fromAccountNumber is vulnerable on \' + document.domain);</script>";
		String toAccountNumber = "<script>alert(\'toAccountNumber is vulnerable on \' + document.domain);</script>";
		String transferAmount = "<script>alert(\'transferAmount is vulnerable \' + document.domain);</script>";

		driver.findElement(By.name("fromAccountNumber")).sendKeys(fromAccountNumber);
		driver.findElement(By.name("toAccountNumber")).sendKeys(toAccountNumber);
		driver.findElement(By.name("transferAmount")).sendKeys(transferAmount);
		driver.findElement(By.name("transfer")).submit();
		
	   	 Alert alert = driver.switchTo().alert();
	   	 Assert.fail(getTextThenDismiss(alert));
	}
}
