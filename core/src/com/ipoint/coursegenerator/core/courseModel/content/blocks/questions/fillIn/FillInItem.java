package com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.fillIn;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.AbstractQuestionItem;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class FillInItem extends AbstractQuestionItem<Object> {

	public static final String FILL_IN_ID = "fill_in_field";

	public FillInItem() {
		super(null);
	}

	@Override
	public Element toHtml(Document creatorTags) {
		Element input = creatorTags.createElement("input");
		input.setAttribute("id", FILL_IN_ID);
		input.setAttribute("name", FILL_IN_ID);
		input.setAttribute("type", "text");

		return input;
	}

}
