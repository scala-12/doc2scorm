package com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.choice;

import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.AbstractQuestionItem;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ChoiceItem extends AbstractQuestionItem<AbstractParagraphBlock<?>> {

	private boolean isCorrect;

	public static final String NAME = "answerGrp";

	public static final String CLASS = "choice_answer";

	public ChoiceItem(AbstractParagraphBlock<?> content, boolean isCorrect) {
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
		label.appendChild(this.getValue().toHtmlWithoutStyles(creatorTags));

		String id = UUID.randomUUID().toString();
		label.setAttribute("for", id);
		input.setAttribute("id", id);

		span.appendChild(input);
		span.appendChild(label);

		return span;
	}

}
