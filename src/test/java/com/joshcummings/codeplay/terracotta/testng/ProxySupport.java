package com.joshcummings.codeplay.terracotta.testng;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.littleshoot.proxy.HostResolver;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.testng.ITestContext;

public class ProxySupport {
	protected static HttpProxyServer proxy;
	
	public void start(ITestContext ctx) {
		start(ctx.getName());
	}
	
	public void start(String type) {
		proxy = DefaultHttpProxyServer.bootstrap()
		        .withPort(8081)
		        .withServerResolver(new HostResolver() {
					@Override
					public InetSocketAddress resolve(String host, int port) throws UnknownHostException {
						if ( host.equals(TestConstants.host) ||
								host.equals(TestConstants.evilHost)) {
							return new InetSocketAddress("docker".equals(type) ? "192.168.99.100" : "localhost", 8080);
						}
						return new InetSocketAddress(host, port);
					}
		        })
		        .start();
	}
	
	public void stop() {
		proxy.stop();
	}
	
	public static void main(String[] args) {
		new ProxySupport().start("tomcat");
	}
}
