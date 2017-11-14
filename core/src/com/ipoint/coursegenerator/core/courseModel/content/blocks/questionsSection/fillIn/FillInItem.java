package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.fillIn;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionSectionItem;
import com.ipoint.coursegenerator.core.utils.Tools;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class FillInItem extends AbstractQuestionSectionItem<String> {

	public static final String FILL_IN_ID = "fill_in_field";

	public FillInItem(String answer) throws ItemCreationException {
		super((answer == null) ? null : Tools.removeExtraSpaces(answer));
	}

	@Override
	public boolean isValidValue(String value) {
		return (this.value == null) && (value != null) && !value.isEmpty();
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
		return super.getText() + "(Input field)";
	}
}
