package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;

public abstract class AbstractQuestionBlockWithAnswers<T extends AbstractQuestionItem<?>>
		extends AbstractQuestionBlock<T> {

	protected AbstractQuestionBlockWithAnswers(List<T> items, String task, Predicate<T> filter,
			Function<T, String> selector) throws BlockCreationException {
		super(items, task, filter, selector);
	}

	@Override
	public List<T> getItems() {
		return super.getItems();
	}

}
