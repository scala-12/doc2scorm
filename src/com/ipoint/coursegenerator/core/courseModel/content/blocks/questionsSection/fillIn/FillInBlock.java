package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.fillIn;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionOneAnswerSectionBlock;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class FillInBlock extends AbstractQuestionOneAnswerSectionBlock<FillInItem> {

	public FillInBlock(FillInItem answerItem, String task) throws BlockCreationException {
		super(answerItem, task, answerItem.getValue());
	}

	@Override
	public QuestionType getType() {
		return QuestionType.FILL_IN;
	}

}
