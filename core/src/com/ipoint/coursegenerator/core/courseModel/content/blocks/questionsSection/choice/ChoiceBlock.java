package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.choice;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ChoiceBlockCreationException;
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

	public ChoiceBlock(List<ChoiceItem> items, String task) throws BlockCreationException {
		super(items, task, ChoiceItem::isCorrect, item -> String.valueOf(item.getIndex()));
		String[] correctAnswers = this.getCorrect();
		if (correctAnswers.length == 0) {
			throw new ChoiceBlockCreationException(this, items);
		} else {
			this.isOneChoice = (correctAnswers.length == 1);
		}
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
	public QuestionType getType() {
		return (isOneChoice) ? QuestionType.CHOICE : QuestionType.MULTIPLE_CHOICE;
	}

}
