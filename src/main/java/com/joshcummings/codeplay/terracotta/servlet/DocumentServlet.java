package com.joshcummings.codeplay.terracotta.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DocumentServlet
 */
public class DocumentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private static final String BASE_DOCUMENT_PATH = "/var/docs";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String documentId = request.getParameter("documentId");
		String accessLevel = (String)request.getSession().getAttribute("acccess");
		File file = new File(BASE_DOCUMENT_PATH, documentId + ".pdf");
		if ( file.exists() ) {
			if ( "admin".equals(accessLevel) ) {
				response.setContentType("application/pdf");
				OutputStream os = response.getOutputStream();
				try ( InputStream is = new FileInputStream(file) ) {
					byte[] b = new byte[1024];
					while ( ( is.read(b, 0, 1024) ) > -1 ) {
						os.write(b);
					}
					os.flush();
				}
			} else {
				response.getWriter().write("Sorry, you don't have access to " + documentId + ".pdf");
				response.setStatus(403);
				logger.warning("Failed attempt to access " + documentId);
			}
		} else {
			response.getWriter().write("Sorry, " + documentId + ".pdf does not exist");
			response.setStatus(404);
			logger.warning("Failed attempt to access " + documentId);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
