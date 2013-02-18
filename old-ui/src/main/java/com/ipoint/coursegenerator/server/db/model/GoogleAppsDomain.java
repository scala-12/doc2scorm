package com.ipoint.coursegenerator.server.db.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;

@PersistenceCapable
public class GoogleAppsDomain {

	@Persistent(mappedBy = "domain")
	private List<User> users;

	@Persistent
	@Unique
	private String name;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	@Persistent
	private int currentConvertionCount;

	@Persistent
	private int totalConvertionCount;

	@Persistent
	private Date expirationDate;

	@Persistent
	private int currentPlanCount;

	@Persistent
	private boolean trialUsed;

	public GoogleAppsDomain(String name){
		this.name = name;
		this.expirationDate = new Date(0);
		this.trialUsed = false;
		users = new ArrayList<User>();
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
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

	public boolean isTrialUsed() {
		return trialUsed;
	}

	public void setTrialUsed(boolean trialUsed) {
		this.trialUsed = trialUsed;
	}
	
	public void increaseCurrentConvertionCount() {
		this.currentConvertionCount++;
	}
	
	public void increaseTotalConvertionCount() {
		this.totalConvertionCount++;
	}
	
	public void addUser(User user) {
		users.add(user);
	}
	
}
