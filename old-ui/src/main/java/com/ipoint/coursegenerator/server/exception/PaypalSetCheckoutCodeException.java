package com.ipoint.coursegenerator.server.exception;

public class PaypalSetCheckoutCodeException extends Exception {

	private static final long serialVersionUID = 6033132031448007232L;

	public PaypalSetCheckoutCodeException(String message) {
		super(message);
	}
}
