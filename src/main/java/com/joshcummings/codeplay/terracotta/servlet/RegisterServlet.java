package com.joshcummings.codeplay.terracotta.servlet;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.joshcummings.codeplay.terracotta.app.ApplicationAwareServlet;
import com.joshcummings.codeplay.terracotta.model.Account;
import com.joshcummings.codeplay.terracotta.model.User;
import com.joshcummings.codeplay.terracotta.service.AccountService;
import com.joshcummings.codeplay.terracotta.service.UserService;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/register")
public class RegisterServlet extends ApplicationAwareServlet {
	private static final long serialVersionUID = 1L;

	private Long nextUserNumber = 4L;
	private Long nextAccountNumber = 4L;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/register.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("registerUsername");
		String password = request.getParameter("registerPassword");
		String name = request.getParameter("registerName");
		String email = request.getParameter("registerEmail");
		User user = new User(String.valueOf(nextUserNumber++), username, password, name, email);
		Account account = new Account(String.valueOf(nextAccountNumber++), new BigDecimal("25"), nextAccountNumber++, user.getId());
		context.get(UserService.class).addUser(user);
		context.get(AccountService.class).addAccount(account);
		
		request.getSession().setAttribute("authenticatedUser", user);
		request.getSession().setAttribute("authenticatedAccount", account);
		response.sendRedirect(request.getContextPath() + "/index.jsp");
	}

}
