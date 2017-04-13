package com.joshcummings.codeplay.terracotta.model;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final String id;
	private final String username;
	private final String password;
	private final String name;
	private final String email;
	
	public User(String id, String username, String password, String name, String email) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.name = name;
		this.email = email;
	}

	public String getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}
}
