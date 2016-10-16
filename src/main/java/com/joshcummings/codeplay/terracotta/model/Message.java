package com.joshcummings.codeplay.terracotta.model;

public class Message {
	private final String id;
	private final String name;
	private final String email;
	private final String subject;
	private final String message;
	
	public Message(String id, String name, String email, String subject, String message) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.subject = subject;
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getSubject() {
		return subject;
	}

	public String getMessage() {
		return message;
	}
}
