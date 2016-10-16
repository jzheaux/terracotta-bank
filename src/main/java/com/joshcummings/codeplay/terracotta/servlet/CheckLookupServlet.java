package com.joshcummings.codeplay.terracotta.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		response.setContentType("image/jpg");
		context.get(CheckService.class).findCheckImage(checkNumber, response.getOutputStream());
		response.flushBuffer();
	}

}
