package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.fillIn;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionBlockWithOneAnswer;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class FillInBlock extends AbstractQuestionBlockWithOneAnswer<FillInItem> {

	public FillInBlock(FillInItem answerItem, String task) throws BlockCreationException {
		super(answerItem, task, answerItem.getCorrectAnswer());
	}

	@Override
	public QuestionType getType() {
		return QuestionType.FILL_IN;
	}

}
