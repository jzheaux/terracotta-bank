package com.joshcummings.codeplay.terracotta.app;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

public abstract class ApplicationAwareFilter implements Filter {
	protected ApplicationContext context;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		context = (ApplicationContext)filterConfig.getServletContext().getAttribute("applicationContext");
	}
}
