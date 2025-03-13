package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.sortable;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionMultipleAnswerSectionBlock;

public abstract class QuestionWithSortableItems<T extends SortableItem> extends AbstractQuestionMultipleAnswerSectionBlock<T> {

	protected QuestionWithSortableItems(List<T> items, String task) throws BlockCreationException {
		super(items, task, item -> String.valueOf(item.getIndex()));
	}

	@Override
	public String toString() {
		StringBuilder text = new StringBuilder();
		int i = -1;
		final String[] correctAnswers = this.getCorrect();
		for (String item : this.getItems().stream().map(T::getText).toArray(String[]::new)) {
			i += 1;
			text.append(correctAnswers[i]).append(". ").append(item).append('\n');
		}

		return text.toString();
	}

}
