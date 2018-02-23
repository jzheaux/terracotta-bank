package com.joshcummings.codeplay.terracotta.app.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.joshcummings.codeplay.terracotta.app.ApplicationAwareServlet;
import com.joshcummings.codeplay.terracotta.app.service.CheckService;

/**
 * Servlet implementation class CheckLookupServlet
 */
@WebServlet("/checkLookup")
public class CheckLookupServlet extends ApplicationAwareServlet {
	private static final long serialVersionUID = 1L;

	private static final Pattern CHECK_NUMBER_PATTERN = Pattern.compile("[A-Fa-f0-9]+");
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String checkNumber = request.getParameter("checkLookupNumber");
		
		try {
			/*checkNumber = ESAPI.encoder().canonicalize(checkNumber);
			checkNumber = new URI("images/checks/" + checkNumber).normalize().toString();
			
			if ( CHECK_NUMBER_PATTERN.matcher(checkNumber).matches() ) {*/
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				context.get(CheckService.class).findCheckImage(checkNumber, baos);
				response.setContentType("image/jpg");
				response.getOutputStream().write(baos.toByteArray());
				response.flushBuffer();
			/*} else {
				response.setStatus(400);
				request.setAttribute("message", "The provided check number is invalid");
				request.getRequestDispatcher("/WEB-INF/json/error.jsp").forward(request, response);
			}*/
		} catch ( IllegalArgumentException e ) {
			response.setStatus(400);
			request.setAttribute("message", "The provided check number is invalid");
			request.getRequestDispatcher("/WEB-INF/json/error.jsp").forward(request, response);
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
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
