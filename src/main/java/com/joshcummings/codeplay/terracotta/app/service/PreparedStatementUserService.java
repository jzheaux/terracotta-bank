package com.joshcummings.codeplay.terracotta.app.service;

import java.sql.SQLException;
import java.util.Set;

import com.joshcummings.codeplay.terracotta.app.model.User;

public class PreparedStatementUserService extends ServiceSupport implements UserService {
	public void addUser(User user) {
		runUpdate("INSERT INTO user (id, username, password, name, email)"
				+ " VALUES (?,?,?,?,?)",
				ps -> {
					ps.setString(1, user.getId());
					ps.setString(2, user.getUsername());
					ps.setString(3, user.getPassword());
					ps.setString(4, user.getName());
					ps.setString(5, user.getEmail());
					return ps;
				});
	}

	public User findByUsernameAndPassword
					(String username, String password) {
		Set<User> users =
			runQuery(
				"SELECT * FROM user WHERE username = ? AND password = ?",
				ps -> {
					ps.setString(1, username);
					ps.setString(2, password);
					
					
					return ps;
				},
				rs -> {
					try {
						return new User(rs.getString(1), 
								rs.getString(4), rs.getString(3),
								rs.getString(2), rs.getString(5));
					} catch ( SQLException e ) {
						throw new IllegalStateException(e);
					}
				});
		return users.isEmpty() ? null : users.iterator().next();
	}
}
