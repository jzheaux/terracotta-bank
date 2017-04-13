package com.joshcummings.codeplay.terracotta.service;

import java.sql.SQLException;
import java.util.Set;

import com.joshcummings.codeplay.terracotta.model.User;

public class UserService extends ServiceSupport {
	public void addUser(User user) {
		runUpdate("INSERT INTO user (id, username, password, name, email)"
				+ " VALUES ('" + user.getId() + "','" + user.getUsername() + 
				"','" + user.getPassword() + "','" + user.getName() + "','" + user.getEmail() + "')");
	}

	public User findByUsernameAndPassword(String username, String password) {
		Set<User> users = runQuery("SELECT * FROM user WHERE username = '" + username + "' AND password = '" + password + "'",
				(rs) -> {
					try {
						return new User(rs.getString(1), rs.getString(4), rs.getString(3), rs.getString(2), rs.getString(5));
					} catch ( SQLException e ) {
						throw new IllegalStateException(e);
					}
				});
		return users.isEmpty() ? null : users.iterator().next();
	}
}
