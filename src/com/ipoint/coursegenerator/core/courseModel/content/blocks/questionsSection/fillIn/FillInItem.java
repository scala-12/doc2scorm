package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.fillIn;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionItem;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class FillInItem extends AbstractQuestionItem<String> {

	public static final String FILL_IN_ID = "fill_in_field";

	private final String answer;

	public FillInItem(String answer) {
		super();
		this.answer = answer;
	}

	public String getCorrectAnswer() {
		return this.answer;
	}

	@Override
	public Element toHtml(Document creatorTags) {
		Element input = creatorTags.createElement("input");
		input.setAttribute("id", FILL_IN_ID);
		input.setAttribute("name", FILL_IN_ID);
		input.setAttribute("type", "text");

		return input;
	}

	@Override
	public String getText() {
		return "(Input field)";
	}

	@Override
	public String toString() {
		return "(" + this.answer + ")";
	}
}
