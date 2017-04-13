package com.joshcummings.codeplay.terracotta.model;

import java.math.BigDecimal;

public class Check {
	private final String id;
	private final String number;
	private final BigDecimal amount;
	private final String accountId;
	
	public Check(String id, String number, BigDecimal amount, String accountId) {
		this.id = id;
		this.number = number;
		this.amount = amount;
		this.accountId = accountId;
	}

	public String getId() {
		return id;
	}

	public String getNumber() {
		return number;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getAccountId() {
		return accountId;
	}
}
