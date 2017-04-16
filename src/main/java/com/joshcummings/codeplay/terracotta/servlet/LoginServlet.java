package com.joshcummings.codeplay.terracotta.servlet;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.joshcummings.codeplay.terracotta.app.ApplicationAwareServlet;
import com.joshcummings.codeplay.terracotta.defense.http.CsrfTokenRepository;
import com.joshcummings.codeplay.terracotta.model.Account;
import com.joshcummings.codeplay.terracotta.model.User;
import com.joshcummings.codeplay.terracotta.service.AccountService;
import com.joshcummings.codeplay.terracotta.service.UserService;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/login")
public class LoginServlet extends ApplicationAwareServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		User user = context.get(UserService.class).findByUsernameAndPassword(username, password);
		if ( user == null ) {
			request.setAttribute("loginErrorMessage", "Either the username you provided (" + username + ") or the password is incorrect.");
			request.getRequestDispatcher(request.getContextPath() + "index.jsp").forward(request, response);
		} else {
			Set<Account> accounts = context.get(AccountService.class).findByUsername(user.getUsername());
			
			// replace any pre-login csrf tokens
			context.get(CsrfTokenRepository.class).replaceToken(request, response);
			
			request.getSession().setAttribute("authenticatedUser", user);
			request.getSession().setAttribute("authenticatedAccounts", accounts);
			
			String relay = request.getParameter("relay");
			//relay = context.get(RedirectCache.class).url(relay);
			if ( relay == null || relay.isEmpty() ) {
				response.sendRedirect(request.getContextPath());
			} else {
				response.sendRedirect(relay);
			}
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
