package com.ipoint.coursegenerator.server.marketplace;

import java.util.List;

public class Notification {

	private String id;
	private List<MarketplaceEdition> provisions;
	private List expiries;
	private List<MarketplaceEdition> deletes;	
	private String applicationId;
	private String customerId;
	private long timestamp;
	private List<MarketplaceReassignment> reassignments;

	private String kind;

	public Notification() {
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public List<MarketplaceEdition> getProvisions() {
		return provisions;
	}

	public void setProvisions(List<MarketplaceEdition> provisions) {
		this.provisions = provisions;
	}

	public List getExpiries() {
		return expiries;
	}

	public void setExpiries(List expiries) {
		this.expiries = expiries;
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

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public List<MarketplaceReassignment> getReassignments() {
		return reassignments;
	}

	public void setReassignments(List<MarketplaceReassignment> reassignments) {
		this.reassignments = reassignments;
	}

	public List<MarketplaceEdition> getDeletes() {
		return deletes;
	}

	public void setDeletes(List<MarketplaceEdition> deletes) {
		this.deletes = deletes;
	}
}
