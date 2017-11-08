package com.ipoint.coursegenerator.core.courseModel.structure.exceptions;

import com.ipoint.coursegenerator.core.courseModel.content.TheoryPage;

public class TreeNodeCreationException extends Exception {

	private static final long serialVersionUID = 6389957125272650964L;

	private TheoryPage page;
	private String title;

	public TreeNodeCreationException(TheoryPage page, String title) {
		super(((title == null) || title.isEmpty()) ? "Don't have title" : "");
		this.page = page;
		this.title = title;
	}

	public TheoryPage getPage() {
		return page;
	}

	public String getTitle() {
		return title;
	}

}
