package com.ipoint.coursegenerator.shared;

public enum OrderPlanType {
	TIME("TIME"), COUNT("COUNT");

	private final String type;

	OrderPlanType(String type) {
		this.type = type;
	}

	private String type() {
		return this.type;
	}
}
