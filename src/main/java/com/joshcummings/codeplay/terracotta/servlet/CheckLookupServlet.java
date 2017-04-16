package com.joshcummings.codeplay.terracotta.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.joshcummings.codeplay.terracotta.app.ApplicationAwareServlet;
import com.joshcummings.codeplay.terracotta.service.CheckService;

/**
 * Servlet implementation class CheckLookupServlet
 */
@WebServlet("/checkLookup")
public class CheckLookupServlet extends ApplicationAwareServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String checkNumber = request.getParameter("checkLookupNumber");
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			context.get(CheckService.class).findCheckImage(checkNumber, baos);
			response.setContentType("image/jpg");
			response.getOutputStream().write(baos.toByteArray());
			response.flushBuffer();
		} catch ( IllegalArgumentException e ) {
			response.setStatus(400);
			request.setAttribute("message", checkNumber + " is invalid");
			request.getRequestDispatcher("/WEB-INF/json/error.jsp").forward(request, response);
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
