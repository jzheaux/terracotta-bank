package com.joshcummings.codeplay.terracotta.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joshcummings.codeplay.terracotta.app.ApplicationAwareServlet;
import com.joshcummings.codeplay.terracotta.defense.fs.ImageDetector;
import com.joshcummings.codeplay.terracotta.model.Account;
import com.joshcummings.codeplay.terracotta.model.Check;
import com.joshcummings.codeplay.terracotta.service.AccountService;
import com.joshcummings.codeplay.terracotta.service.CheckService;

/**
 * Servlet implementation class MakeDepositServlet
 */
@WebServlet("/makeDeposit")
@MultipartConfig(fileSizeThreshold=1024*1024, maxFileSize=1024*1024)
public class MakeDepositServlet extends ApplicationAwareServlet {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final long serialVersionUID = 1L;

	private static final String CHECK_IMAGE_LOCATION = "images/checks";
	static {
		new File(CHECK_IMAGE_LOCATION).mkdirs();
	}
	
	private Integer nextCheckNumber = 1;

	private static final Pattern CHECK_NUMBER_PATTERN = Pattern.compile("[A-Fa-f0-9]+");
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<String> errors = new ArrayList<>();
		
		Optional<Integer> accountNumber = tryParse(request.getParameter("depositAccountNumber"), Integer::parseInt, errors);
		Optional<BigDecimal> amount = tryParse(request.getParameter("depositAmount"), BigDecimal::new, errors);
		Optional<String> checkNumber = tryParse(request.getParameter("depositCheckNumber"), (number) -> {
			/*number = ESAPI.encoder().canonicalize(number);
			if ( CHECK_NUMBER_PATTERN.matcher(number).matches() ) {
				return number;
			} else {
				throw new IllegalArgumentException("Check numbers should be hexadecimal");
			}*/
			return number;
		}, errors);

		Part image = request.getPart("depositCheckImage");
		
		String name = image.getSubmittedFileName();
		InputStream file = image.getInputStream();
		
		try {
			ImageDetector detector = context.get(ImageDetector.class);
			logger.debug("Considering whether or not {} is an image using {}", name, detector.getClass());
			if ( !detector.isAnImage(name, file) ) {
				logger.debug("It is NOT an image!");
				errors.add("Provided file is not an image.");
			}
		} catch ( IOException e ) {
			errors.add("Failed image upload: " + e.getMessage());
		}
		
		if ( errors.isEmpty() ) {			
			Account account = context.get(AccountService.class).findByAccountNumber(accountNumber.get());
			context.get(CheckService.class).updateCheckImage(checkNumber.get(), image.getInputStream());
			
			Check check = new Check(String.valueOf(nextCheckNumber++), checkNumber.get(), amount.get(), account.getId());
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
