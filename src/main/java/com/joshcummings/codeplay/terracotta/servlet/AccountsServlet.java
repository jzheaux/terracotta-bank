package com.joshcummings.codeplay.terracotta.servlet;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.joshcummings.codeplay.terracotta.app.ApplicationAwareServlet;
import com.joshcummings.codeplay.terracotta.model.Account;
import com.joshcummings.codeplay.terracotta.service.AccountService;

/**
 * Servlet implementation class AccountsServlet
 */
@WebServlet("/showAccounts")
public class AccountsServlet extends ApplicationAwareServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if ( request.getSession().getAttribute("authenticatedUser") == null ) {
			response.sendRedirect(request.getContextPath() + "/employee.jsp?relay=" + request.getRequestURL().toString());
		} else {
			Set<Account> accounts = context.get(AccountService.class).findAll();
			request.setAttribute("accounts", accounts);
			request.getRequestDispatcher("/WEB-INF/accounts.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
