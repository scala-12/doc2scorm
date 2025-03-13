package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;

public abstract class AbstractQuestionOneAnswerSectionBlock<T extends AbstractQuestionSectionItem<?>>
		extends AbstractQuestionSectionBlock<T> {

	protected AbstractQuestionOneAnswerSectionBlock(T item, String task, String answer) throws BlockCreationException {
		super(item, task, answer);
	}

	@Override
	public T getItem() {
		return super.getItem();
	}

}
