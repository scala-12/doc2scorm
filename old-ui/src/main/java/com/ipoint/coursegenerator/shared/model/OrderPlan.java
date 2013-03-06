package com.ipoint.coursegenerator.shared.model;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.ipoint.coursegenerator.shared.OrderPlanType;

@PersistenceCapable(detachable="true")
public class OrderPlan implements Serializable {

	private static final long serialVersionUID = -2139136149949719508L;

	@PrimaryKey
    @Persistent
    private String id;
	
	@Persistent
	private String name;
	
	@Persistent
	private OrderPlanType type;
	
	@Persistent
	private double amount;
	
	@Persistent
	private int expiresIn;
	
	@Persistent
	private int expiresAfter;
	
	@Persistent
	private String description;
	
	@Persistent
	private boolean active;

	
	public OrderPlan() {
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OrderPlanType getType() {
		return type;
	}

	public void setType(OrderPlanType type) {
		this.type = type;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

	public int getExpiresAfter() {
		return expiresAfter;
	}

	public void setExpiresAfter(int expiresAfter) {
		this.expiresAfter = expiresAfter;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
