package com.joshcummings.codeplay.terracotta.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.joshcummings.codeplay.terracotta.app.ApplicationContext;

public abstract class ApplicationAwareServlet extends HttpServlet {
	protected ApplicationContext context;
	
	@Override
	public void init() throws ServletException {
		context = (ApplicationContext)getServletContext().getAttribute("applicationContext");
	}
}
