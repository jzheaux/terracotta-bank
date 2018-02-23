package com.joshcummings.codeplay.terracotta.app;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.hibernate.cfg.AvailableSettings;

import com.joshcummings.codeplay.terracotta.app.service.AccountService;
import com.joshcummings.codeplay.terracotta.app.service.CheckService;
import com.joshcummings.codeplay.terracotta.app.service.EmailService;
import com.joshcummings.codeplay.terracotta.app.service.MessageService;
import com.joshcummings.codeplay.terracotta.app.service.PreparedStatementUserService;
import com.joshcummings.codeplay.terracotta.app.service.RelationalMessageService;
import com.joshcummings.codeplay.terracotta.app.service.UserService;
import com.joshcummings.codeplay.terracotta.defense.fs.ImageDetector;
import com.joshcummings.codeplay.terracotta.defense.fs.TikaBasedImageDetector;
import com.joshcummings.codeplay.terracotta.defense.fs.VirusCheckingImageDetector;
import com.joshcummings.codeplay.terracotta.defense.http.CsrfTokenRepository;
import com.joshcummings.codeplay.terracotta.defense.http.JwtBasedCsrfTokenRepository;

/**
 * Sets up the DI context. Here you can switch out implementations of various classes to compare
 * their security strengths and weaknesses.
 *
 */
@WebListener
public class ApplicationContextConfigurer implements ServletContextListener {
	private final ApplicationContext context = new ApplicationContext();
	
	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
    	context.get(EntityManager.class).close();
    	context.get(EntityManagerFactory.class).close();
    	
    	context.clear();
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
    	Map<String, Object> integration = new HashMap<>();
    	integration.put(AvailableSettings.CLASSLOADERS, Arrays.asList(this.getClass().getClassLoader()));
    	EntityManagerFactory emf = Persistence
                .createEntityManagerFactory("terracottaPersistenceUnit", integration);
        EntityManager em = emf.createEntityManager();
        
    	context.set(new AccountService());
    	context.put(UserService.class, new PreparedStatementUserService());//new JpaUserService(em));
    	context.set(new CheckService());
    	context.set(new EmailService());
    	context.set(new RedirectCache());
    	context.put(ImageDetector.class, new VirusCheckingImageDetector(new TikaBasedImageDetector()));
    	context.put(CsrfTokenRepository.class, new JwtBasedCsrfTokenRepository());
    	context.put(MessageService.class, new RelationalMessageService());
    	
    	context.put(EntityManagerFactory.class, emf);
    	context.put(EntityManager.class, em);
    	
    	arg0.getServletContext().setAttribute("applicationContext", context);
    	
    }
}
