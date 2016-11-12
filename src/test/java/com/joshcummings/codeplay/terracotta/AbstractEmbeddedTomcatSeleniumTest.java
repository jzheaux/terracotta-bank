package com.joshcummings.codeplay.terracotta;

import java.io.File;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import io.github.bonigarcia.wdm.ChromeDriverManager;

public class AbstractEmbeddedTomcatSeleniumTest  {
	protected static WebDriver driver;
	protected static Tomcat tomcat;
	
	@BeforeSuite
	public void startTomcat() throws Exception {
		String webappDirLocation = "src/main/webapp/";
        tomcat = new Tomcat();

        //The port that we should run on can be set into an environment variable
        //Look for that variable and default to 8080 if it isn't there.
        String webPort = System.getenv("PORT");
        if(webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }

        tomcat.setPort(Integer.valueOf(webPort));

        StandardContext ctx = (StandardContext) tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
        System.out.println("configuring app with basedir: " + new File("./" + webappDirLocation).getAbsolutePath());

        // Declare an alternative location for your "WEB-INF/classes" dir
        // Servlet 3.0 annotation will work
        File additionWebInfClasses = new File("target/classes");
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                additionWebInfClasses.getAbsolutePath(), "/"));
        ctx.setResources(resources);

        tomcat.start();
        //tomcat.getServer().await();
	}
	
	@BeforeSuite
	public void startSelenium() {
		ChromeDriverManager.getInstance().setup();
		driver = new ChromeDriver();
	}
	
	@AfterSuite
	public void shutdownTomcat() throws Exception {
		tomcat.stop();
	}
	
	@AfterSuite
	public void shutdownSelenium() {
		if ( driver != null ) {
			driver.quit();
		}
	}
	
	protected void login(String username, String password) {
		driver.get("http://localhost:8080");
		driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.name("login")).submit();
	}
	
	protected void logout() {
		driver.get("http://localhost:8080/logout");
	}
	
	protected String getTextThenDismiss(Alert alert) {
		String text = alert.getText();
		alert.dismiss();
		return text;
	}
}
