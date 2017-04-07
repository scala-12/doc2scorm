package com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.fillIn;

import java.util.Collections;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.AbstractQuestionBlock;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class FillInBlock extends AbstractQuestionBlock<FillInItem> {

	public FillInBlock(FillInItem answer) {
		this(answer, null);
	}

	public FillInBlock(FillInItem answer, String task) {
		super(Collections.singletonList(answer), task);
		this.correctOrder = new String[] { answer.getCorrectAnswer() };
	}

	@Override
	public int getType() {
		return FILL_IN;
	}

}
