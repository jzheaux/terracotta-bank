package com.joshcummings.codeplay.terracotta.app;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public abstract class ApplicationAwareServlet extends HttpServlet {
	protected ApplicationContext context;
	
	@Override
	public void init() throws ServletException {
		context = (ApplicationContext)getServletContext().getAttribute("applicationContext");
	}
}
