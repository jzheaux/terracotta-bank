package com.joshcummings.codeplay.terracotta.testng;

import org.apache.catalina.startup.Tomcat;

import com.joshcummings.codeplay.terracotta.TomcatBootstrapper;

public class TomcatSupport {
	private static Tomcat tomcat;

	public void startContainer() throws Exception {
		tomcat = new TomcatBootstrapper().startTomcat(8080, "src/main/webapp");
	}
	
	public void stopContainer() throws Exception {
		tomcat.stop();
	}
}
