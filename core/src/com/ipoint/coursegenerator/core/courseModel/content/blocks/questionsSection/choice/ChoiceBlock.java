package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.choice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionBlock;
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
public class ChoiceBlock extends AbstractQuestionBlock<ChoiceItem> {

	public static final String CHOICE_ANSWERS_FIELDSET_ID = "choice_answers_fieldset";

	private final boolean isOneChoice;

	// TODO: fix in iLogos this "feature" (+1)
	private static final int _SHIFT = 1;

	public ChoiceBlock(Set<ChoiceItem> items) {
		this(items, null);
	}

	public ChoiceBlock(Set<ChoiceItem> items, String task) {
		super(new ArrayList<>(items), task);

		boolean isOneChoice = true;

		Iterator<ChoiceItem> iter = items.iterator();
		boolean hasCorrect = false;
		Integer index = _SHIFT - 1;
		ArrayList<Integer> correctAnswers = new ArrayList<>(this.getItems().size());
		while (iter.hasNext()) {
			index += 1;
			if (iter.next().isCorrect()) {
				correctAnswers.add(index);
				if (hasCorrect) {
					isOneChoice = false;
				}
				hasCorrect = true;
			}
		}

		this.isOneChoice = isOneChoice;
		this.correctAnswers = correctAnswers.stream().sorted().map(number -> number.toString()).toArray(String[]::new);
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

		int i = _SHIFT - 1;
		while (answersBlock.hasChildNodes()) {
			Element span = (Element) answersBlock.getFirstChild();
			Element answer = (Element) span.getElementsByTagName("input").item(0);
			answer.setAttribute("type", type);

			i += 1;
			answer.setAttribute("value", String.valueOf(i));

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
