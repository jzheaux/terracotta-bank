package com.joshcummings.codeplay.terracotta.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.joshcummings.codeplay.terracotta.app.ApplicationAwareServlet;
import com.joshcummings.codeplay.terracotta.app.RedirectCache;
import com.joshcummings.codeplay.terracotta.model.Account;
import com.joshcummings.codeplay.terracotta.model.User;
import com.joshcummings.codeplay.terracotta.service.AccountService;
import com.joshcummings.codeplay.terracotta.service.UserService;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/employeeLogin")
public class EmployeeLoginServlet extends ApplicationAwareServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		User user = context.get(UserService.class).findByUsernameAndPassword(username, password);
		request.getSession().setAttribute("authenticatedUser", user);
		
		String relay = request.getParameter("relay");
		//relay = context.get(RedirectCache.class).url(relay);
		if ( relay == null || relay.isEmpty() ) {
			response.sendRedirect(request.getContextPath() + "/employee.jsp");
		} else {
			response.sendRedirect(relay);
		}
	}

}
