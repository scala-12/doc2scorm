package com.ipoint.coursegenerator.core.courseModel.structure.exceptions;

public class SimpleModelNodeCreationException extends Exception {

	private static final long serialVersionUID = 6389957125272650964L;

	private String title;

	public SimpleModelNodeCreationException(String title) {
		super(((title == null) || title.isEmpty()) ? "Don't have title" : "");
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

}
