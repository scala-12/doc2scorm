package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.choice;

import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.ParagraphBlock;
import com.ipoint.coursegenerator.core.utils.Tools;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ChoiceItem extends AbstractQuestionItem<ParagraphBlock> {

	private boolean isCorrect;

	public static final String NAME = "answerGrp";

	public static final String CLASS = "choice_answer";

	public ChoiceItem(ParagraphBlock content, boolean isCorrect) {
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
	public Element toHtml(Document creatorTags) {
		Element span = creatorTags.createElement("span");
		Element input = creatorTags.createElement("input");
		input.setAttribute("name", NAME);

		Element label = creatorTags.createElement("label");
		label.setAttribute("class", CLASS);
		NodeList labelItems = this.getValue().toSimpleHtml(creatorTags);
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
		return Tools.removeExtraSpaces(this.getValue().getText());
	}

	@Override
	public String toString() {
		return this.getText() + ((this.isCorrect()) ? " - (correct)" : "");
	}

}
