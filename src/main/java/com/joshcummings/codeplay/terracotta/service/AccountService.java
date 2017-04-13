package com.joshcummings.codeplay.terracotta.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Set;

import com.joshcummings.codeplay.terracotta.model.Account;
import com.joshcummings.codeplay.terracotta.model.Check;
import com.joshcummings.codeplay.terracotta.model.Message;
import com.joshcummings.codeplay.terracotta.model.User;

public class AccountService extends ServiceSupport {
	public Account findById(String id) {
		Set<Account> accounts = runQuery("SELECT * FROM account WHERE id = " + id, (rs) -> {
			try {
				return new Account(rs.getString(1), new BigDecimal(rs.getString(2)), 
						rs.getLong(3), rs.getString(4));
			} catch ( SQLException e ) {
				throw new IllegalStateException(e);
			}
		});
		return accounts.size() > 0 ? accounts.iterator().next() : null;
	}
	
	public Set<Account> findByUsername(String username) {
		Set<Account> accounts = runQuery("SELECT account.* FROM account, user WHERE user.username = '" + username + "' AND account.owner_id = user.id", (rs) -> {
			try {
				return new Account(rs.getString(1), new BigDecimal(rs.getString(2)), 
						rs.getLong(3), rs.getString(4));
			} catch ( SQLException e ) {
				throw new IllegalStateException(e);
			}
		});
		return accounts;
	}
	
	public Account findByAccountNumber(Integer accountNumber) {
		Set<Account> accounts = runQuery("SELECT * FROM account WHERE number = " + accountNumber, (rs) -> {
			try {
				return new Account(rs.getString(1), new BigDecimal(rs.getString(2)), 
						rs.getLong(3), rs.getString(4));
			} catch ( SQLException e ) {
				throw new IllegalStateException(e);
			}
		});
		return accounts.size() > 0 ? accounts.iterator().next() : null;		
	}
	
	public Set<Account> findAll() {
		return runQuery("SELECT * FROM account", (rs) -> {
			try {
				return new Account(rs.getString(1), new BigDecimal(rs.getString(2)), 
						rs.getLong(3), rs.getString(4));
			} catch ( SQLException e ) {
				throw new IllegalStateException(e);
			}
		});
	}
	
	public Account findDefaultAccountForUser(User user) {
		Set<Account> accounts = runQuery("SELECT * FROM account WHERE owner_id = '" + user.getId() + "'", (rs) -> {
			try {
				return new Account(rs.getString(1), new BigDecimal(rs.getString(2)), 
						rs.getLong(3), rs.getString(4));
			} catch ( SQLException e ) {
				throw new IllegalStateException(e);
			}
		});
		
		return accounts.size() > 0 ? accounts.iterator().next() : null;		
	}
	
	public void addAccount(Account account) {
		runUpdate("INSERT INTO account (id, amount, number, owner_id)"
				+ " VALUES ('" + account.getId() + "','" + account.getAmount() + 
				"','" + account.getNumber() + "','" + account.getOwnerId() + "')");
	}
	
	public Account makeDeposit(Account account, Check check) {
		runUpdate("UPDATE account SET amount = " + account.getAmount().add(check.getAmount()).toString() + " WHERE id = " + account.getId());
		return findById(account.getId());
	}
	
	public Account transferMoney(Account from, Account to, BigDecimal amount) {
		runUpdate("UPDATE account SET amount = " + from.getAmount().subtract(amount).toString() + " WHERE id = " + from.getId());
		runUpdate("UPDATE account SET amount = " + to.getAmount().add(amount).toString() + " WHERE id = " + to.getId());
		return findById(from.getId());
	}
}
