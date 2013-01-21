package com.ipoint.coursegenerator.server.authorization;

import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
public class User {

    public User(String userId, String userEmail) {
	super();
	this.userId = userId;
	this.userEmail = userEmail;
    }

    public User() {

    }

    private String userId;

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
