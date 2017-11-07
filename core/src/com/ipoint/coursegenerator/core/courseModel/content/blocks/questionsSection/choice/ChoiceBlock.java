package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.choice;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionBlockWithAnswers;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.HyperlinkRunsBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.content.TextualRunsBlock;
import com.ipoint.coursegenerator.core.utils.Tools;

/**
 * This block is an analogue of text paragraph. These includes several
 * {@link TextualRunsBlock}, {@link HyperlinkRunsBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ChoiceBlock extends AbstractQuestionBlockWithAnswers<ChoiceItem> {

	public static final String CHOICE_ANSWERS_FIELDSET_ID = "choice_answers_fieldset";

	private final boolean isOneChoice;

	public ChoiceBlock(List<ChoiceItem> items) throws BlockCreationException {
		this(items, null);
	}

	public ChoiceBlock(List<ChoiceItem> items, String task) throws BlockCreationException {
		super(items, task);

		boolean isOneChoice = true;

		boolean hasCorrect = false;
		ArrayList<String> correctAnswers = new ArrayList<>(items.size());

		for (ChoiceItem item : this.getItems()) {
			if (item.isCorrect()) {
				correctAnswers.add(String.valueOf(item.getIndex()));
				if (hasCorrect) {
					isOneChoice = false;
				}
				hasCorrect = true;
			}
		}

		if (!hasCorrect) {
			// TODO: exception
		}

		this.isOneChoice = isOneChoice;
		this.correctAnswers = correctAnswers.stream().toArray(String[]::new);
	}

	/**
	 * add fieldset to answer block that included inputs with type and value and
	 * labels
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element div = super.toHtml(creatorTags);
		Element answersBlock = (Element) Tools.getElementById(div, AbstractQuestionBlock.ANSWER_BLOCK_ID);

		String type = (isOneChoice) ? "radio" : "checkbox";

		Element fieldset = creatorTags.createElement("fieldset");
		fieldset.setAttribute("id", CHOICE_ANSWERS_FIELDSET_ID);

		while (answersBlock.hasChildNodes()) {
			Element span = (Element) answersBlock.getFirstChild();
			Element answer = (Element) span.getElementsByTagName("input").item(0);
			answer.setAttribute("type", type);

			fieldset.appendChild(span.getFirstChild());
			fieldset.appendChild(span.getLastChild());

			answersBlock.removeChild(span);
		}

		answersBlock.appendChild(fieldset);

		return div;
	}

	@Override
	public int getType() {
		return (isOneChoice) ? CHOICE : MULTIPLE_CHOICE;
	}

}
