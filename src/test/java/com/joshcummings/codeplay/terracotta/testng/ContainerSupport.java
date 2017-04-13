package com.joshcummings.codeplay.terracotta.testng;

import org.testng.ITestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

public interface ContainerSupport {
	TomcatSupport tomcat = new TomcatSupport();
	DockerSupport docker = new DockerSupport();
	
	@BeforeTest(alwaysRun=true)
	public default void start(ITestContext ctx) throws Exception {
		if ( "docker".equals(ctx.getName()) ) {
			docker.startContainer();
		} else {
			tomcat.startContainer();
		}
	}
	
	@AfterTest(alwaysRun=true)
	public default void stop(ITestContext ctx) throws Exception {
		if ( "docker".equals(ctx.getName()) ) {
			docker.stopContainer();
		} else {
			tomcat.stopContainer();
		}
	}
}
