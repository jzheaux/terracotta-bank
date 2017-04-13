package com.joshcummings.codeplay.terracotta.app;

import java.io.IOException;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import com.joshcummings.codeplay.terracotta.model.Account;
import com.joshcummings.codeplay.terracotta.model.User;
import com.joshcummings.codeplay.terracotta.service.AccountService;

@WebFilter("/*")
public class UserFilter implements Filter {
	private ApplicationContext context;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		context = (ApplicationContext)filterConfig.getServletContext().getAttribute("applicationContext");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			User user = (User)((HttpServletRequest)request).getSession().getAttribute("authenticatedUser");
			if ( user != null ) {
				AccountService accountService = context.get(AccountService.class);
				Set<Account> accounts = accountService.findByUsername(user.getUsername());
				request.setAttribute("authenticatedAccounts", accounts);
			}
		} finally {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
