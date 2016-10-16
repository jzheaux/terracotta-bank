package com.joshcummings.codeplay.terracotta.servlet;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.joshcummings.codeplay.terracotta.model.Account;
import com.joshcummings.codeplay.terracotta.service.AccountService;

/**
 * Servlet implementation class TransferMoneyServlet
 */
@WebServlet("/transferMoney")
public class TransferMoneyServlet extends ApplicationAwareServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fromAccountNumber = request.getParameter("fromAccountNumber");
		String toAccountNumber = request.getParameter("toAccountNumber");
		String amount = request.getParameter("transferAmount");
		Account from = context.get(AccountService.class).findByAccountNumber(Integer.parseInt(fromAccountNumber));
		Account to = context.get(AccountService.class).findByAccountNumber(Integer.parseInt(toAccountNumber));
		
		from = context.get(AccountService.class).transferMoney(from, to, new BigDecimal(amount));
		request.setAttribute("account", from);
		request.getRequestDispatcher("/WEB-INF/json/account.jsp").forward(request, response);
	}

}
