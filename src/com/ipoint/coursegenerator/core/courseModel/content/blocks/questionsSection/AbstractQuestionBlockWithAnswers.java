package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;

public abstract class AbstractQuestionBlockWithAnswers<T extends AbstractQuestionItem<?>>
		extends AbstractQuestionBlock<T> {

	protected AbstractQuestionBlockWithAnswers(List<T> items, String task, Function<T, String> correctAnswersSelector,
			Predicate<T> correctAnswersFilterBeforeSelect) throws BlockCreationException {
		super(items, task, correctAnswersSelector, correctAnswersFilterBeforeSelect);
	}

	protected AbstractQuestionBlockWithAnswers(List<T> items, String task, Function<T, String> correctAnswersSelector)
			throws BlockCreationException {
		this(items, task, correctAnswersSelector, null);
	}

	@Override
	public List<T> getItems() {
		return super.getItems();
	}

}
