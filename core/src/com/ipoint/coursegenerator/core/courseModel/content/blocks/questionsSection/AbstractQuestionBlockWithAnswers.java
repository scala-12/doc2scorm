package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;

public abstract class AbstractQuestionBlockWithAnswers<T extends AbstractQuestionItem<?>>
		extends AbstractQuestionBlock<T> {

	protected AbstractQuestionBlockWithAnswers(List<T> items) throws BlockCreationException {
		super(items);
	}

	protected AbstractQuestionBlockWithAnswers(List<T> items, String task) throws BlockCreationException {
		super(items, task);
	}

	@Override
	public List<T> getItems() {
		return super.getItems();
	}

}
