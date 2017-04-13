package com.joshcummings.codeplay.terracotta.xss;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.owasp.esapi.ESAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Servlet implementation class ContentSecurityPolicyViolationsServlet
 */
@WebServlet("/cspViolation")
public class ContentSecurityPolicyViolationsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Long CSP_MAX_SIZE = 5000L;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final Gson gson = new Gson();
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		InputStream body = new BoundedInputStream(request.getInputStream(), CSP_MAX_SIZE);
		InputStreamReader reader = new InputStreamReader(body);
		String reportJson = IOUtils.toString(reader);
		reportJson = ESAPI.encoder().canonicalize(reportJson);
		
		// any other validation we want to do
		
		ContentSecurityReportEnvelope envelope = gson.fromJson(reportJson, ContentSecurityReportEnvelope.class);
		log.error(gson.toJson(envelope));
	}
	
	private class ContentSecurityReportEnvelope {
		@SerializedName("csp-report")
		private ContentSecurityReport cspReport;
	}
	
	private class ContentSecurityReport {
		@SerializedName("document-uri")
		private String documentUri;
		
		private String referrer;
		
		@SerializedName("blocked-uri")
		private String blockedUri;
		
		@SerializedName("violated-directive")
		private String violatedDirective;
		
		@SerializedName("original-policy")
		private String originalPolicy;
	}
}
