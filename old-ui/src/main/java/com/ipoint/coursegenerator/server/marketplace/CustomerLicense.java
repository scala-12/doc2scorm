package com.ipoint.coursegenerator.server.marketplace;

import java.util.List;

public class CustomerLicense {

	public enum State {		
		ACTIVE, UNLICENSED, EXPIRED;
	}

	public enum SubscriptionState {
		ACTIVE, TRIAL, CANCELED, LOCKED, NOTEXIST;
	}

	private String id;
	
	private String applicationId;
	
	private String customerId;
	
	private State state;

	private List<MarketplaceEdition> editions;
	
	private SubscriptionState subscriptionState;

	private long nextRenewalTimestamp;
	
	public CustomerLicense() {
		
	}

	private String kind;
	
	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public List<MarketplaceEdition> getEditions() {
		return editions;
	}

	public void setEditions(List<MarketplaceEdition> editions) {
		this.editions = editions;
	}

	public long getNextRenewalTimestamp() {
		return nextRenewalTimestamp;
	}

	public void setNextRenewalTimestamp(long nextRenewalTimestamp) {
		this.nextRenewalTimestamp = nextRenewalTimestamp;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public SubscriptionState getSubscriptionState() {
		return subscriptionState;
	}

	public void setSubscriptionState(SubscriptionState subscriptionState) {
		this.subscriptionState = subscriptionState;
	}
}
