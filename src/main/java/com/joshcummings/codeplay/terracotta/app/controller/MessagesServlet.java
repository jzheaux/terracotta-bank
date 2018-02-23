package com.joshcummings.codeplay.terracotta.app.controller;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.owasp.encoder.Encode;

import com.joshcummings.codeplay.terracotta.app.ApplicationAwareServlet;
import com.joshcummings.codeplay.terracotta.app.model.Message;
import com.joshcummings.codeplay.terracotta.app.service.MessageService;

/**
 * Servlet implementation class MessagesServlet
 */
@WebServlet("/showMessages")
public class MessagesServlet extends ApplicationAwareServlet {
	private static final long serialVersionUID = 1L;

	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if ( request.getSession().getAttribute("authenticatedUser") == null ) {
			//*/
			String relay = request.getRequestURI();//Encode.forUriComponent(request.getRequestURI());
			//*/
			
			/*/
			RedirectCache cache = context.get(RedirectCache.class);
			String relay = cache.key(request.getRequestURL().toString());
			//*/
			
			response.sendRedirect(request.getContextPath() + 
					"/employee.jsp?relay=" + relay);
		} else {
			String criteria = request.getParameter("q");
			Set<Message> messages = context.get(MessageService.class).findAll(criteria);
			request.setAttribute("messages", messages);
			request.getRequestDispatcher("/WEB-INF/messages.jsp").forward(request, response);
		}
	}
}
