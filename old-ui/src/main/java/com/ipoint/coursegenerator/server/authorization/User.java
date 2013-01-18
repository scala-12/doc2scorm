package com.ipoint.coursegenerator.server.authorization;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
public class User implements Serializable {

    private static final long serialVersionUID = -5401208538282914354L;

    public User(String userId, String userEmail) {
	super();
	this.userId = userId;
	this.userEmail = userEmail;
    }

    @Persistent
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
