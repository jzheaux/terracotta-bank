package com.joshcummings.codeplay.terracotta.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Account implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final String id;
	private final BigDecimal amount;
	private final Long number;
	private final String ownerId;
	
	public Account(String id, BigDecimal amount, Long number, String ownerId) {
		this.id = id;
		this.amount = amount;
		this.number = number;
		this.ownerId = ownerId;
	}

	public String getId() {
		return id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Long getNumber() {
		return number;
	}

	public String getOwnerId() {
		return ownerId;
	}
}
