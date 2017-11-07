package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;

public abstract class AbstractQuestionBlockWithOneAnswer<T extends AbstractQuestionItem<?>>
		extends AbstractQuestionBlock<T> {

	protected AbstractQuestionBlockWithOneAnswer(T item) throws BlockCreationException {
		super(item);
	}

	protected AbstractQuestionBlockWithOneAnswer(T item, String task) throws BlockCreationException {
		super(item, task);
	}

	@Override
	public T getItem() {
		return super.getItem();
	}

}
