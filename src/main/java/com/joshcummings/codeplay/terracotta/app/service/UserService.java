package com.joshcummings.codeplay.terracotta.app.service;

import com.joshcummings.codeplay.terracotta.app.model.User;

public interface UserService {
	void addUser(User user);
	User findByUsernameAndPassword(String username, String password);
}
