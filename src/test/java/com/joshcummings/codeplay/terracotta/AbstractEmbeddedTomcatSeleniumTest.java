package com.joshcummings.codeplay.terracotta;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.littleshoot.proxy.HostResolver;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import com.joshcummings.codeplay.terracotta.testng.ContainerSupport;
import com.joshcummings.codeplay.terracotta.testng.DockerSupport;
import com.joshcummings.codeplay.terracotta.testng.TomcatSupport;

import io.github.bonigarcia.wdm.MarionetteDriverManager;

public class AbstractEmbeddedTomcatSeleniumTest implements ContainerSupport {
	protected static final String host = "honestsite.com";
	protected static final String evilHost = "evilsite.com";
	
	protected static WebDriver driver;
	
	protected static HttpProxyServer proxy;
	
	TomcatSupport tomcat = new TomcatSupport();
	DockerSupport docker = new DockerSupport();
	
	@BeforeTest(alwaysRun=true)
	public void start(ITestContext ctx) throws Exception {
		if ( "docker".equals(ctx.getName()) ) {
			docker.startContainer();
		} else {
			tomcat.startContainer();
		}
	}
	
	@AfterTest(alwaysRun=true)
	public void stop(ITestContext ctx) throws Exception {
		if ( "docker".equals(ctx.getName()) ) {
			docker.stopContainer();
		} else {
			tomcat.stopContainer();
		}
	}
	
	@BeforeTest(alwaysRun=true)
	public void startSelenium() {
		MarionetteDriverManager.getInstance().setup("0.15.0");

		FirefoxProfile profile = new FirefoxProfile();

		profile.setPreference("network.proxy.type", 1);
        profile.setPreference("network.proxy.http", "localhost");
        profile.setPreference("network.proxy.http_port", 8081);
        profile.setPreference("network.proxy.ssl", "localhost");
        profile.setPreference("network.proxy.ssl_port", 8081);        
		
		FirefoxOptions options = new FirefoxOptions();
		options.setProfile(profile);

		driver = new FirefoxDriver(options);
	}
	
	@BeforeTest(alwaysRun=true)
	public void startProxy(ITestContext ctx) {
		proxy = DefaultHttpProxyServer.bootstrap()
		        .withPort(8081)
		        .withServerResolver(new HostResolver() {
					@Override
					public InetSocketAddress resolve(String host, int port) throws UnknownHostException {
						if ( host.equals(AbstractEmbeddedTomcatSeleniumTest.host) ||
								host.equals(AbstractEmbeddedTomcatSeleniumTest.evilHost)) {
							return new InetSocketAddress("docker".equals(ctx.getName()) ? "192.168.99.100" : "localhost", 8080);
						}
						return new InetSocketAddress(host, port);
					}
		        })
		        .start();
	}
	
	@AfterTest(alwaysRun=true)
	public void shutdownSelenium() {
		if ( driver != null ) {
			driver.quit();
		}
	}
	
	@AfterTest(alwaysRun=true)
	public void shutdownProxy() {
		proxy.stop();
	}
	
	protected void goToPage(String page) {
		driver.get("http://" + host + page);
	}
	
	protected void login(String username, String password) {
		goToPage("/");
		driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.name("login")).submit();
        FluentWait<WebDriver> wait = new WebDriverWait(driver, 2).pollingEvery(100, TimeUnit.MILLISECONDS);
        wait.until((Function<WebDriver, Boolean>)driver -> driver.findElement(By.id("service")) != null);
	}
	
	protected void employeeLogin(String username, String password) {
		goToPage("/employee.jsp");
		driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.name("login")).submit();
        FluentWait<WebDriver> wait = new WebDriverWait(driver, 2).pollingEvery(100, TimeUnit.MILLISECONDS);
        wait.until((Function<WebDriver, Boolean>)driver -> driver.findElement(By.id("service")) != null);
	}
	
	protected void logout() {
		goToPage("/logout");
	}
	
	protected String getTextThenDismiss(Alert alert) {
		String text = alert.getText();
		alert.dismiss();
		return text;
	}
	
	protected void ignoreErrors(Runnable r) {
		try { 
			r.run();
		} catch ( Exception e ) {
			// eat
		}
	}
	
	protected Alert switchToAlertEventually(WebDriver driver, long timeoutInMilliseconds) throws NoAlertPresentException {
		long now = System.currentTimeMillis();
		try {
			return driver.switchTo().alert();
		} catch ( NoAlertPresentException e ) {
			if ( timeoutInMilliseconds <= 0 ) {
				throw e;
			}
		}
		
		try {
			Thread.sleep(100);
			return switchToAlertEventually(driver, timeoutInMilliseconds - ( System.currentTimeMillis() - now ));
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
			throw new NoAlertPresentException(e);
		}
	}
	
	protected WebElement findElementEventually(WebDriver driver, By by, long timeoutInMilliseconds) throws NoSuchElementException {
		long now = System.currentTimeMillis();
		try {
			return driver.findElement(by);
		} catch ( NoSuchElementException e ) {
			if ( timeoutInMilliseconds <= 0 ) {
				throw e;
			}
		}
		
		try {
			Thread.sleep(100);
			return findElementEventually(driver, by, timeoutInMilliseconds - ( System.currentTimeMillis() - now ));
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
			throw new NoSuchElementException(e.getMessage());
		}
	}
}
