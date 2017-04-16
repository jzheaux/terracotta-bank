package com.joshcummings.codeplay.terracotta.defense.http;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.joshcummings.codeplay.terracotta.app.ApplicationAwareFilter;

@WebFilter("/*")
public class TokenBasedCsrfDetectionFilter extends ApplicationAwareFilter implements CsrfDetectionFilter {
	protected CsrfTokenRepository repository;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
		repository = context.get(CsrfTokenRepository.class);
	}
	
	@Override
	public void destroy() {}
	
	@Override
	public void doFilter(ServletRequest request,
			ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if ( request instanceof HttpServletRequest && response instanceof HttpServletResponse ) {
			HttpServletRequest req = (HttpServletRequest)request;
			HttpServletResponse resp = (HttpServletResponse)response;
			
			if ( "POST".equals(req.getMethod()) ) {
				if ( hasCsrf(req) ) {
					throw new ServletException("CSRF Detected!");
				}
			}
			
			String token = repository.loadToken(req);
			if ( token == null ) {
				token = repository.makeToken(req);
			}
			repository.storeToken(token, req, resp);
			request.setAttribute("csrfToken", token);
		}
		
		chain.doFilter(request, response);
	}
	
	@Override
	public boolean hasCsrf(HttpServletRequest request) {
		// get the token from the client
		String syncToken = loadSyncToken(request);
		
		// get the token that was stored by the server
		String sourceToken = repository.loadToken(request);
		
		// if either is missing or they aren't equal, reject
		return syncToken == null || sourceToken == null || 
				!syncToken.equals(sourceToken);
	}

	protected String loadSyncToken(HttpServletRequest request) {
		return request.getParameter("csrfToken");
	}
}
