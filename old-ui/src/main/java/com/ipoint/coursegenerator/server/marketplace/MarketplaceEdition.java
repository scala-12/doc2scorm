package com.ipoint.coursegenerator.server.marketplace;

public class MarketplaceEdition {
	
	public static final String FREE_EDITION = "free";
	
	private String editionId;
	
	private int seatCount;
	
	private int assignedSeats;
	
	private String kind;
	
	public MarketplaceEdition() {
		
	}

	public String getEditionId() {
		return editionId;
	}

	public void setEditionId(String editionId) {
		this.editionId = editionId;
	}

	public int getSeatCount() {
		return seatCount;
	}

	public void setSeatCount(int seatCount) {
		this.seatCount = seatCount;
	}

	public int getAssignedSeats() {
		return assignedSeats;
	}

	public void setAssignedSeats(int assignedSeats) {
		this.assignedSeats = assignedSeats;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}
}
