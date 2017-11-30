package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.choice;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.content.HyperlinkRunsBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.content.TextualRunsBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ChoiceBlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionMultipleAnswerSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionSectionBlock;
import com.ipoint.coursegenerator.core.utils.Tools;

/**
 * This block is an analogue of text paragraph. These includes several
 * {@link TextualRunsBlock}, {@link HyperlinkRunsBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ChoiceBlock extends AbstractQuestionMultipleAnswerSectionBlock<ChoiceItem> {

	public static final String CHOICE_ANSWERS_FIELDSET_ID = "choice_answers_fieldset";

	private final ChoiceQuestionType type;

	public ChoiceBlock(List<ChoiceItem> items, String task) throws BlockCreationException {
		super(items, task, item -> String.valueOf(item.getIndex()), ChoiceItem::isCorrect);
		String[] correctAnswers = this.getCorrect();
		if (correctAnswers.length == 0) {
			throw new ChoiceBlockCreationException(this, items);
		} else if (correctAnswers.length != 1) {
			this.type = ChoiceQuestionType.MULTIPLE;
		} else {
			this.type = ((items.size() == 2) && (items.get(0).getValue().toString().trim().equals("Да")
					&& items.get(1).getValue().toString().trim().equals("Нет"))) ? ChoiceQuestionType.TRUE_FALSE
							: ChoiceQuestionType.SINGLE;
		}
	}

	/**
	 * add fieldset to answer block that included inputs with type and value and
	 * labels
	 */
	@Override
	public Element toHtmlModel(Document creatorTags) {
		Element div = super.toHtmlModel(creatorTags);
		Element answersBlock = (Element) Tools.getElementById(div, AbstractQuestionSectionBlock.ANSWER_BLOCK_ID);

		String type = (this.type == ChoiceQuestionType.MULTIPLE) ? "checkbox" : "radio";

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
	public ChoiceQuestionType getType() {
		return type;
	}

}
