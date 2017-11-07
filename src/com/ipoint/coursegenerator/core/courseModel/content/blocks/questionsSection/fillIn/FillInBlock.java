package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.fillIn;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionBlockWithOneAnswer;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class FillInBlock extends AbstractQuestionBlockWithOneAnswer<FillInItem> {

	public FillInBlock(FillInItem answer) throws BlockCreationException {
		this(answer, null);
	}

	public FillInBlock(FillInItem answer, String task) throws BlockCreationException {
		super(answer, task);
		this.correctAnswers = new String[] { answer.getCorrectAnswer() };
	}

	@Override
	public QuestionType getType() {
		return QuestionType.FILL_IN;
	}

}
