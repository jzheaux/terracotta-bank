package com.joshcummings.codeplay.terracotta.service;

import java.sql.SQLException;
import java.util.Set;

import com.joshcummings.codeplay.terracotta.model.Message;

public class MessageService extends ServiceSupport {
	public Set<Message> findAll() {
		return runQuery("SELECT * FROM message", (rs) -> {
			try {
				return new Message(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
			} catch ( SQLException e ) {
				throw new IllegalStateException(e);
			}
		});
	}
	
	public void addMessage(Message message) {
		runUpdate("INSERT INTO message (id, name, email, subject, message) VALUES ('" +
			message.getId() + "','" + message.getName() + "','" + message.getEmail() + "','" +
			message.getSubject() + "','" + message.getMessage() + "')");
	}
}
