package com.joshcummings.codeplay.terracotta.testng;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import io.github.bonigarcia.wdm.MarionetteDriverManager;

public class SeleniumSupport {
	public WebDriver start() {
		MarionetteDriverManager.getInstance().setup("0.15.0");

		FirefoxProfile profile = new FirefoxProfile();

		profile.setPreference("network.proxy.type", 1);
        profile.setPreference("network.proxy.http", "localhost");
        profile.setPreference("network.proxy.http_port", 8081);
        profile.setPreference("network.proxy.ssl", "localhost");
        profile.setPreference("network.proxy.ssl_port", 8081);        
		
		FirefoxOptions options = new FirefoxOptions();
		options.setProfile(profile);

		return new FirefoxDriver(options);
	}
	
	public void stop(WebDriver driver) {
		if ( driver != null ) {
			driver.quit();
		}
	}
}
