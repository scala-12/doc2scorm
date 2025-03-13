package com.ipoint.coursegenerator.core.courseModel.structure.exceptions;

import com.ipoint.coursegenerator.core.courseModel.content.TheoryPage;

public class ModelTreeNodeCreationException extends SimpleModelNodeCreationException {

	private static final long serialVersionUID = 8449338953826795976L;

	private TheoryPage page;

	public ModelTreeNodeCreationException(TheoryPage page, String title) {
		super(title);
		this.page = page;
	}

	public TheoryPage getPage() {
		return page;
	}

}
