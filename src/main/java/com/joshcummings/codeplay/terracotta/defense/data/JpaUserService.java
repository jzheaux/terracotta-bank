package com.joshcummings.codeplay.terracotta.defense.data;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.joshcummings.codeplay.terracotta.app.model.User;
import com.joshcummings.codeplay.terracotta.app.service.UserService;

public class JpaUserService implements UserService {
	private final EntityManager em;
	
	public JpaUserService(EntityManager em) {
		this.em = em;
	}
	
	public void addUser(User user) {
		em.persist(user);
	}

	public User findByUsernameAndPassword(String username, String password) {
		TypedQuery<User> query = 
				em.createNamedQuery(
						User.BY_USERNAME_AND_PASSWORD, 
						User.class);
		query.setParameter("username", username);
		query.setParameter("password", password);
		List<User> users = query.getResultList();
		return users.isEmpty() ? null : users.get(0);
	}
}
