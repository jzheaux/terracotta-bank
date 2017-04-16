package com.joshcummings.codeplay.terracotta.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.joshcummings.codeplay.terracotta.app.ApplicationAwareServlet;
import com.joshcummings.codeplay.terracotta.service.EmailService;

/**
 * Servlet implementation class SendResponseServlet
 */
@WebServlet("/sendResponse")
public class SendResponseServlet extends ApplicationAwareServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String to = request.getParameter("sendResponseTo");
		String subject = "In Response To Your Inquiry";
		String content = request.getParameter("sendResponseContent");
		context.get(EmailService.class).sendMessage(to, subject, content);
	}
}
