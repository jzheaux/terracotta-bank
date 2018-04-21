package com.joshcummings.codeplay.terracotta.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.hsqldb.jdbcDriver;
import org.mindrot.jbcrypt.BCrypt;

@WebListener
public class DatabaseConfigurer implements ServletContextListener {
	private static final Driver driver = new jdbcDriver();
	
	private static final String DATABASE_URL = "jdbc:hsqldb:mem:db";
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		try {
			DriverManager.deregisterDriver(driver);
		} catch ( SQLException e ) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			DriverManager.registerDriver(driver);
		} catch ( SQLException e ) {
			throw new IllegalStateException(e);
		}
		
		try ( Connection c = DriverManager.getConnection(DATABASE_URL);
				BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("schema.sql"))) ) {
			String line;
			while ( ( line = reader.readLine() ) != null ) {
				if ( !line.isEmpty() ) {
					int indexOf = line.indexOf("{bcrypt}");
					if ( indexOf != -1 ) {
						String password = line.substring(indexOf+8, line.indexOf("'", indexOf));
						String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
						line = line.replace("{bcrypt}" + password, hashed);
					}
					try ( PreparedStatement ps = c.prepareStatement(line) ) {
						ps.executeUpdate();
					}  
				}
			}
		} catch ( Exception e ) {
			throw new IllegalStateException(e);
		}
	}

}
