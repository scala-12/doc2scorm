package com.ipoint.coursegenerator.server.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class User {

	public User(String userId, String userEmail) {
		super();
		this.userId = userId;
		this.userEmail = userEmail;
		this.expirationDate = new Date();
		this.transactions = new ArrayList<PaypalTransaction>();
	}

	public User() {
	}

	@PrimaryKey
	@Persistent(primaryKey="true")
	private String userId;	

	@Persistent
	private List<PaypalTransaction> transactions;
	
	@Persistent
	private String userEmail;
	
	@Persistent
	private int currentConvertionCount;
	
	@Persistent
	private int totalConvertionCount;
	
	@Persistent
	private Date expirationDate;
	
	@Persistent
	private int currentPlanCount;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public int getCurrentConvertionCount() {
		return currentConvertionCount;
	}

	public void setCurrentConvertionCount(int currentConvertionCount) {
		this.currentConvertionCount = currentConvertionCount;
	}

	public int getTotalConvertionCount() {
		return totalConvertionCount;
	}

	public void setTotalConvertionCount(int totalConvertionCount) {
		this.totalConvertionCount = totalConvertionCount;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public int getCurrentPlanCount() {
		return currentPlanCount;
	}

	public void setCurrentPlanCount(int currentPlanCount) {
		this.currentPlanCount = currentPlanCount;
	}

	public List<PaypalTransaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<PaypalTransaction> transactions) {
		this.transactions = transactions;
	}
	
	public void addTransaction(PaypalTransaction transaction) {
		this.transactions.add(transaction);
	}
}
