package com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.choice.ChoiceBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.choice.ChoiceItem;

public class ChoiceBlockCreationException extends BlockCreationException {

	public ChoiceBlockCreationException(ChoiceBlock block, List<ChoiceItem> items) {
		super(block, items, (items.stream().filter(item -> item.isCorrect()).toArray().length == 0)
				? "Don't have correct answer" : null);
	}

	private static final long serialVersionUID = 7325216385954726851L;

}
