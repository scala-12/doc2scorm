package com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.fillIn;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.AbstractQuestionBlock;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class FillInBlock extends AbstractQuestionBlock<FillInItem> {

	public FillInBlock(String answer) {
		this(answer, null);
	}

	public FillInBlock(String answer, String task) {
		super(null, task);
		this.correctOrder = new String[] { answer };
	}

}
