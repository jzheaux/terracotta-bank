package com.joshcummings.codeplay.terracotta.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.joshcummings.codeplay.terracotta.app.ApplicationAwareServlet;
import com.joshcummings.codeplay.terracotta.model.Account;
import com.joshcummings.codeplay.terracotta.model.User;
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
		User user = (User)request.getSession().getAttribute("authenticatedUser");

		List<String> errors = new ArrayList<>();
		AccountService accountService = context.get(AccountService.class);
		
		Function<String, Account> accountParser = (possibleInteger) -> {
			if ( possibleInteger != null ) {
				Integer accountNumber = tryParse(possibleInteger, Integer::parseInt, errors).get();
				return accountService.findByAccountNumber(accountNumber);
			} else {
				return accountService.findDefaultAccountForUser(user);
			}
		};
		
		Optional<Account> from = tryParse(request.getParameter("fromAccountNumber"), accountParser, errors);
		Optional<Account> to = tryParse(request.getParameter("toAccountNumber"), accountParser, errors);
		Optional<BigDecimal> transferAmount = tryParse(request.getParameter("transferAmount"), BigDecimal::new, errors);
		
		if ( errors.isEmpty() ) {
			Account acct = context.get(AccountService.class).transferMoney(from.get(), to.get(), transferAmount.get());
			request.setAttribute("account", acct);
			request.getRequestDispatcher("/WEB-INF/json/account.jsp").forward(request, response);
		} else {
			response.setStatus(400);
			request.setAttribute("message", errors.stream().findFirst().get());
			request.getRequestDispatcher("/WEB-INF/json/error.jsp").forward(request, response);
		}
	}
	
	private <E> Optional<E> tryParse(String possibleInteger, Function<String, E> parser, List<String> errors) {
		try {
			return Optional.of(parser.apply(possibleInteger));
		} catch ( Exception e ) {
			errors.add(possibleInteger + " is invalid");
			return Optional.empty();
		}
	}
}
