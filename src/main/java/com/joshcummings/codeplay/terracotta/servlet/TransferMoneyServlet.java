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
		List<String> errors = new ArrayList<>();
		
		Optional<Integer> fromAccountNumber = tryParse(request.getParameter("fromAccountNumber"), Integer::parseInt, errors);
		Optional<Integer> toAccountNumber = tryParse(request.getParameter("toAccountNumber"), Integer::parseInt, errors);
		Optional<BigDecimal> transferAmount = tryParse(request.getParameter("transferAmount"), BigDecimal::new, errors);
		
		if ( errors.isEmpty() ) {
			Account from = context.get(AccountService.class).findByAccountNumber(fromAccountNumber.get());
			Account to = context.get(AccountService.class).findByAccountNumber(toAccountNumber.get());
			
			from = context.get(AccountService.class).transferMoney(from, to, transferAmount.get());
			request.setAttribute("account", from);
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
		} catch ( NumberFormatException e ) {
			errors.add(possibleInteger + " is invalid");
			return Optional.empty();
		}
	}
}
