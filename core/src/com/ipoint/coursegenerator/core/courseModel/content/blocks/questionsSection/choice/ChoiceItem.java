package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.choice;

import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.ParagraphSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionSectionItem;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ChoiceItem extends AbstractQuestionSectionItem<ParagraphSectionBlock> {

	private final boolean isCorrect;

	public static final String NAME = "answerGrp";

	public static final String CLASS = "choice_answer";

	public ChoiceItem(ParagraphSectionBlock content, boolean isCorrect) throws ItemCreationException {
		super(content);
		this.isCorrect = isCorrect;
	}

	public boolean isCorrect() {
		return this.isCorrect;
	}

	/**
	 * @return span with pair input-label
	 */
	@Override
	public Element toHtmlModel(Document creatorTags) {
		Element span = creatorTags.createElement("span");
		Element input = creatorTags.createElement("input");
		input.setAttribute("name", NAME);
		input.setAttribute("value", String.valueOf(this.getIndex()));

		Element label = creatorTags.createElement("label");
		label.setAttribute("class", CLASS);
		NodeList labelItems = this.getValue().toSimpleHtmlModel(creatorTags);
		while (labelItems.getLength() != 0) {
			label.appendChild(labelItems.item(0));
		}

		String id = UUID.randomUUID().toString();
		label.setAttribute("for", id);
		input.setAttribute("id", id);

		span.appendChild(input);
		span.appendChild(label);

		return span;
	}

	@Override
	public String getText() {
		return super.getText() + this.getValue().getText();
	}

}
