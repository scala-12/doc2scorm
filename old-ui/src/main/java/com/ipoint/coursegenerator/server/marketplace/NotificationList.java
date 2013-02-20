package com.ipoint.coursegenerator.server.marketplace;

import java.util.List;

public class NotificationList {
	
	private String kind;
	
	private List<Notification> notifications;

	private String nextPageToken;
	
	public NotificationList() {
		
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	public String getNextPageToken() {
		return nextPageToken;
	}

	public void setNextPageToken(String nextPageToken) {
		this.nextPageToken = nextPageToken;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}
}
