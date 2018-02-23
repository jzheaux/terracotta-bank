package com.joshcummings.codeplay.terracotta.app.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

public abstract class ServiceSupport {
	private static final String DATABASE_URL = "jdbc:hsqldb:mem:db";
	
	public <T> Set<T> runQuery(String query, Function<ResultSet, T> inflater) {
		return runQuery(query, ps -> ps, inflater);
	}
	
	public <T> Set<T> runQuery(String query, Preparer preparer, Function<ResultSet, T> inflater) {
		Set<T> results = new LinkedHashSet<T>();
		try ( Connection conn = 
				DriverManager.getConnection(DATABASE_URL);
			  PreparedStatement ps = 
				conn.prepareStatement(query);
			  ResultSet rs =
				preparer.prepare(ps).executeQuery(); )
		{
			while ( rs.next() ) {
				results.add(inflater.apply(rs));
			}
		} catch ( SQLException e ) {
			throw new IllegalArgumentException(e);
		}
		return results;
	}
	
	public void runUpdate(String query) {
		runUpdate(query, ps -> ps);
	}
	
	public void runUpdate(String query, Preparer preparer ) {
		try ( Connection conn = DriverManager.getConnection(DATABASE_URL);
				PreparedStatement ps = preparer.prepare(conn.prepareStatement(query)); ){
			ps.executeUpdate();
		} catch ( SQLException e ) {
			throw new IllegalArgumentException(e);
		}
	}
	
	@FunctionalInterface
	public interface Preparer {
		PreparedStatement prepare(PreparedStatement ps) throws SQLException;
	}
}
