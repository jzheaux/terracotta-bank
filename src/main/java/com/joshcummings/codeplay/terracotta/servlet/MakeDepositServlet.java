package com.joshcummings.codeplay.terracotta.servlet;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.joshcummings.codeplay.terracotta.model.Account;
import com.joshcummings.codeplay.terracotta.model.Check;
import com.joshcummings.codeplay.terracotta.service.AccountService;
import com.joshcummings.codeplay.terracotta.service.CheckService;

/**
 * Servlet implementation class MakeDepositServlet
 */
@WebServlet("/makeDeposit")
@MultipartConfig
public class MakeDepositServlet extends ApplicationAwareServlet {
	private static final long serialVersionUID = 1L;

	private static final String CHECK_IMAGE_LOCATION = "images/checks";
	static {
		new File(CHECK_IMAGE_LOCATION).mkdirs();
	}
	
	private Integer nextCheckNumber = 1;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<String> errors = new ArrayList<>();
		
		Optional<Integer> accountNumber = tryParse(request.getParameter("depositAccountNumber"), Integer::parseInt, errors);
		Optional<BigDecimal> amount = tryParse(request.getParameter("depositAmount"), BigDecimal::new, errors);

		if ( errors.isEmpty() ) {
			String checkNumber = request.getParameter("depositCheckNumber");
			
			Part image = request.getPart("depositCheckImage");
			Account account = context.get(AccountService.class).findByAccountNumber(accountNumber.get());
			context.get(CheckService.class).updateCheckImage(checkNumber, image.getInputStream());
			
			Check check = new Check(String.valueOf(nextCheckNumber++), Integer.parseInt(checkNumber), amount.get(), account.getId());
			context.get(CheckService.class).addCheck(check);
			
			account = context.get(AccountService.class).makeDeposit(account, check);
			request.setAttribute("account", account);
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
