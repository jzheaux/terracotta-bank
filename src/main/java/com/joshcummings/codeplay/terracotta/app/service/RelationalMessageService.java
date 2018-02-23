package com.joshcummings.codeplay.terracotta.app.service;

import java.sql.SQLException;
import java.util.Set;

import org.codehaus.plexus.util.StringUtils;

import com.joshcummings.codeplay.terracotta.app.model.Message;

public class RelationalMessageService extends ServiceSupport implements MessageService {
	public Set<Message> findAll(String description) {
		String withLike = "%" + StringUtils.defaultString(description) + "%";
		return runQuery("SELECT * FROM message WHERE message LIKE ?", 
				ps -> {
					ps.setString(1, withLike);
					return ps;
				},
				(rs) -> {
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
