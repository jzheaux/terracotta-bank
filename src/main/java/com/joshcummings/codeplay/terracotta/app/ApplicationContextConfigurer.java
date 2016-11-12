package com.joshcummings.codeplay.terracotta.app;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.joshcummings.codeplay.terracotta.service.AccountService;
import com.joshcummings.codeplay.terracotta.service.CheckService;
import com.joshcummings.codeplay.terracotta.service.EmailService;
import com.joshcummings.codeplay.terracotta.service.MessageService;
import com.joshcummings.codeplay.terracotta.service.UserService;

/**
 * Application Lifecycle Listener implementation class ApplicationContextConfigurer
 *
 */
@WebListener
public class ApplicationContextConfigurer implements ServletContextListener {
	private final ApplicationContext context = new ApplicationContext();
	
	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
    	context.clear();
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
    	context.set(new AccountService());
    	context.set(new UserService());
    	context.set(new CheckService());
    	context.set(new EmailService());
    	context.set(new MessageService());
    	arg0.getServletContext().setAttribute("applicationContext", context);
    }
	
}
