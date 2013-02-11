package com.ipoint.coursegenerator.server.db;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class User {

	public User(String userId, String userEmail) {
		super();
		this.userId = userId;
		this.userEmail = userEmail;
	}

	public User() {

	}

	@PrimaryKey
	@Persistent(primaryKey="true")
	private String userId;	

	@Persistent
	private String userEmail;

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

}
