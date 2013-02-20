package com.ipoint.coursegenerator.server.marketplace;

public class MarketplaceReassignment {

	public enum ReassignmentType {
		ASSIGN, REVOKE
	}

	private String editionId;
	private String userId;
	private ReassignmentType type;

	private String kind;
	
	public MarketplaceReassignment() {
		
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getEditionId() {
		return editionId;
	}

	public void setEditionId(String editionId) {
		this.editionId = editionId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public ReassignmentType getType() {
		return type;
	}

	public void setType(ReassignmentType type) {
		this.type = type;
	}
}
