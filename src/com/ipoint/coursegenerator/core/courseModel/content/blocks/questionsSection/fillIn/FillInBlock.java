package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.fillIn;

import java.util.Collections;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionBlock;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class FillInBlock extends AbstractQuestionBlock<FillInItem> {

	public FillInBlock(FillInItem answer) throws BlockCreationException {
		this(answer, null);
	}

	public FillInBlock(FillInItem answer, String task) throws BlockCreationException {
		super(Collections.singletonList(answer), task, false);
		this.correctAnswers = new String[] { answer.getCorrectAnswer() };
	}

	@Override
	public int getType() {
		return FILL_IN;
	}

}
