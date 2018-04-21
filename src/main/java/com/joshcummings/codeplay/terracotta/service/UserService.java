package com.joshcummings.codeplay.terracotta.service;

import com.joshcummings.codeplay.terracotta.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

public class UserService extends ServiceSupport {
	public void addUser(User user) {
		runUpdate("INSERT INTO user (id, username, password, name, email)"
				+ " VALUES ('" + user.getId() + "','" + user.getUsername() + 
				"','" + user.getPassword() + "','" + user.getName() + "','" + user.getEmail() + "')");
	}

	public User findByUsername(String username) {
		Set<User> users = runQuery("SELECT * FROM user WHERE username = '" + username + "'",
			(rs) -> {
				try {
					return new User(rs.getString(1), rs.getString(4), rs.getString(5), rs.getString(2), rs.getString(3));
				} catch ( SQLException e ) {
					throw new IllegalStateException(e);
				}
			});
		return users.isEmpty() ? null : users.iterator().next();
	}

	private transient byte[] random = BCrypt.hashpw(UUID.randomUUID().toString(), BCrypt.gensalt()).getBytes();

	public boolean findByUsernameAndPassword(String username, String password) {
		Set<String> counts = runQuery("SELECT password FROM user WHERE username = '" + username + "'",
				(rs) -> {
					try {
						return rs.getString(1);//new User(rs.getString(1), rs.getString(4), rs.getString(5), rs.getString(2), rs.getString(3));
					} catch ( SQLException e ) {
						throw new IllegalStateException(e);
					}
				});
		return BCrypt.checkpw(password, counts.isEmpty() ? new String(random) : counts.iterator().next()) && !counts.isEmpty(); //users.isEmpty() ? null : users.iterator().next();
	}
}
