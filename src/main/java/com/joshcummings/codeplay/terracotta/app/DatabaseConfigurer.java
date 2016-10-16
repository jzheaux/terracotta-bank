package com.joshcummings.codeplay.terracotta.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.hsqldb.jdbcDriver;

@WebListener
public class DatabaseConfigurer implements ServletContextListener {
	private static final Driver driver = new jdbcDriver();
	
	private static final String DATABASE_URL = "jdbc:hsqldb:mem:db";
	
	private static final String DROP_USER_TABLE = "DROP TABLE IF EXISTS user";
	private static final String DROP_ACCOUNT_TABLE = "DROP TABLE IF EXISTS account";
	private static final String DROP_CHECK_TABLE = "DROP TABLE IF EXISTS check";
	private static final String CREATE_USER_TABLE = "CREATE TABLE user (id VARCHAR(64) PRIMARY KEY, name VARCHAR(256), email VARCHAR(256), username VARCHAR(64), password VARCHAR(64))";
	private static final String CREATE_ACCOUNT_TABLE = "CREATE TABLE account (id VARCHAR(64) PRIMARY KEY, amount NUMERIC(12,4), number INTEGER, owner_id VARCHAR(64))";
	private static final String CREATE_CHECK_TABLE = "CREATE TABLE check (id VARCHAR(64) PRIMARY KEY, amount NUMERIC(12,4), number INTEGER, account_id VARCHAR(64))";
	
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
					try ( PreparedStatement ps = c.prepareStatement(line) ) {
						ps.executeUpdate();
					}  
				}
			}
		} catch ( IOException | SQLException e ) {
			throw new IllegalStateException(e);
		}
	}

}
