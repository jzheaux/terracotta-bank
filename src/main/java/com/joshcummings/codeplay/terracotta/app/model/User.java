package com.joshcummings.codeplay.terracotta.app.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "user")
@NamedQueries({
	@NamedQuery(name=User.BY_USERNAME_AND_PASSWORD,
			query="FROM User WHERE username = :username "
					+ "AND password = :password")
})
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String BY_USERNAME_AND_PASSWORD = "byUsernameAndPassword";
	
	@Id
	private String id;
	
	@Column
	private String username;
	
	@Column
	private String password;
	
	@Column
	private String name;
	
	@Column
	private String email;
	
	@Column(name="is_admin")
	private Boolean admin;
	
	public User() {}
	
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

	public void setId(String id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
