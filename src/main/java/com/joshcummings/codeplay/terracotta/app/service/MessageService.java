package com.joshcummings.codeplay.terracotta.app.service;

import java.util.Set;

import com.joshcummings.codeplay.terracotta.app.model.Message;

public interface MessageService {
	Set<Message> findAll(String criteria);
	
	void addMessage(Message message);
}
