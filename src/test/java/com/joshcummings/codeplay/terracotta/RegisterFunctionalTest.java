package com.joshcummings.codeplay.terracotta;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

public class RegisterFunctionalTest extends AbstractEmbeddedTomcatSeleniumTest {
	
	@AfterClass
	public void doLogout() {
		logout();
	}
	
	@Test(expectedExceptions=NoAlertPresentException.class)
	public void testRegisterForXSS() {
		 driver.get("http://localhost:8080");
		
		 String username = "<script>alert(\"registerUsername is vulnerable\");</script>";
		 String password = "<script>alert(\"registerPassword is vulnerable\");</script>";
		 String name = "<script>alert(\"registerName is vulnerable\");</script>";
		 String email = "<script>alert(\"registerEmail is vulnerable\");</script>";
		 
         driver.findElement(By.name("registerUsername")).sendKeys(username);
         driver.findElement(By.name("registerPassword")).sendKeys(password);
         driver.findElement(By.name("registerName")).sendKeys(name);
         driver.findElement(By.name("registerEmail")).sendKeys(email);
         driver.findElement(By.name("register")).submit();
         driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
         
    	 Alert alert = driver.switchTo().alert();
    	 Assert.fail(getTextThenDismiss(alert));
	}
}
