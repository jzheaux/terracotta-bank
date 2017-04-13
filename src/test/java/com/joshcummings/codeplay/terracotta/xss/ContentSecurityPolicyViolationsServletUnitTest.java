package com.joshcummings.codeplay.terracotta.xss;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.mockito.Mockito;

public class ContentSecurityPolicyViolationsServletUnitTest {
	private ContentSecurityPolicyViolationsServlet servlet = new ContentSecurityPolicyViolationsServlet();
	private SimpleDateFormat jdkDefaultFormat = new SimpleDateFormat("MMM dd, yyyy h:mm:ss a");
	private SimpleDateFormat isoStandardFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
	
	private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
	
	public static void main(String[] args) throws Exception {
		ContentSecurityPolicyViolationsServletUnitTest test = new ContentSecurityPolicyViolationsServletUnitTest();
		test.testWithJavaLogging();
	}
	
	public void testWithJavaLogging() throws ServletException, IOException {
		String sampleCsp = IOUtils.toString(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("sample-csp-report")));
		String falseLog = jdkDefaultFormat.format(new Date()) + " oracle.jdbc.driver.OracleDriver connect\n"
				+ "SEVERE: Connection Refused";
				
		InputStream injectedLog = new ByteArrayInputStream((sampleCsp + "\n" + falseLog).getBytes());
					
		Mockito.when(request.getInputStream()).thenReturn(new MockServletInputStream(injectedLog));
		
		servlet.doPost(request, null);
	}

	public void testWithLog4j() throws ServletException, IOException {
		String sampleCsp = IOUtils.toString(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("sample-csp-report")));
		String falseLog = isoStandardFormat.format(new Date()) + " "
				+ "ERROR oracle.jdbc.driver.OracleDriver:587 - Connection Refused";
				
		InputStream injectedLog = new ByteArrayInputStream((sampleCsp + "\n" + falseLog).getBytes());
					
		Mockito.when(request.getInputStream()).thenReturn(new MockServletInputStream(injectedLog));
		
		servlet.doPost(request, null);
	}
	
	public void testWithLogback() throws ServletException, IOException {
		String sampleCsp = IOUtils.toString(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("sample-csp-report")));
		String falseLog = isoStandardFormat.format(new Date()) + " [http-nio-8080-exec-6] ERROR oracle.jdbc.driver.OracleDriver - Connection Refused";
				
		InputStream injectedLog = new ByteArrayInputStream((sampleCsp + "\n" + falseLog).getBytes());
					
		Mockito.when(request.getInputStream()).thenReturn(new MockServletInputStream(injectedLog));
		
		servlet.doPost(request, null);
	}
	
	private class MockServletInputStream extends ServletInputStream {
		private InputStream delegate;
		private int next = -1;
		
		public MockServletInputStream(InputStream delegate) throws IOException {
			this.delegate = delegate;
			next = delegate.read();
		}
		
		@Override
		public boolean isFinished() {
			return next == -1;
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setReadListener(ReadListener readListener) {
		}

		@Override
		public int read() throws IOException {
			int toReturn = next;
			next = delegate.read();
			return toReturn;
		}
		
	}
}
