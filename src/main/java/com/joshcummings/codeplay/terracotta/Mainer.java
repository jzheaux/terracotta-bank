package com.joshcummings.codeplay.terracotta;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

public class Mainer {
	public static void main(String[] args) throws ServletException, LifecycleException {
		Tomcat tomcat = new TomcatBootstrapper().startTomcat(8080, "src/main/webapp/");
		
		tomcat.getServer().await();
	}
}
