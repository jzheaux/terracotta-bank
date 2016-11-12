package com.joshcummings.codeplay.terracotta;

import java.io.File;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MakeDepositFunctionalTest extends AbstractEmbeddedTomcatSeleniumTest {
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
		
		String depositAccountNumber = "<script>alert(\'depositAccountNumber is vulnerable on \' + document.domain);</script>";
		String depositCheckNumber = "<script>alert(\'depositCheckNumber is vulnerable on \' + document.domain);</script>";
		String depositAmount = "<script>alert(\'depositAmount is vulnerable \' + document.domain);</script>";
		
		driver.findElement(By.name("depositAccountNumber")).sendKeys(depositAccountNumber);
		driver.findElement(By.name("depositCheckNumber")).sendKeys(depositCheckNumber);
		driver.findElement(By.name("depositAmount")).sendKeys(depositAmount);
		driver.findElement(By.name("depositCheckImage")).sendKeys(new File("src/test/resources/check.png").getAbsolutePath());
		
		driver.findElement(By.name("deposit")).submit();
		
	   	 Alert alert = driver.switchTo().alert();
	   	 Assert.fail(getTextThenDismiss(alert));
	}
}
