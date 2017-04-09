package com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.choice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.HyperlinkBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.TextBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.AbstractQuestionBlock;
import com.ipoint.coursegenerator.core.utils.Tools;

/**
 * This block is an analogue of text paragraph. These includes several
 * {@link TextBlock}, {@link HyperlinkBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ChoiceBlock extends AbstractQuestionBlock<ChoiceItem> {

	public static final String CHOICE_ANSWERS_FIELDSET_ID = "choice_answers_fieldset";

	private boolean isOneChoice;

	public ChoiceBlock(List<ChoiceItem> items) {
		this(items, null);

		isOneChoice = true;
		for (int i = 0, correct = 0; isOneChoice && (i < items.size()); i++) {
			if (items.get(i).isCorrect()) {
				correct++;
				if (correct > 1) {
					isOneChoice = false;
				}
			}
		}
	}

	public ChoiceBlock(List<ChoiceItem> items, String task) {
		super(items, task);
	}

	@Override
	public String[] getCorrect() {
		if (super.getCorrect() == null) {
			this.toHtml(Tools.createEmptyDocument());
		}

		return this.correctOrder;
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

		ArrayList<Integer> numbers = null;
		boolean withoutCorrectness = this.correctOrder == null;
		if (withoutCorrectness) {
			this.correctOrder = new String[answersBlock.getChildNodes().getLength()];
			numbers = new ArrayList<>(this.correctOrder.length);
			for (int i = 0; i < this.correctOrder.length; i++) {
				numbers.add(i);
			}
		}
		Node[][] sortedAnswers = new Node[this.correctOrder.length][];

		for (int i = 0; answersBlock.hasChildNodes(); i++) {
			// old answer will be transformative and removed after
			// new answer will be added after
			Element span = (Element) answersBlock.getFirstChild();
			answersBlock.removeChild(span);

			Element answer = (Element) span.getElementsByTagName("input").item(0);
			answer.setAttribute("type", type);

			int number;
			if (withoutCorrectness) {
				number = numbers.remove(ThreadLocalRandom.current().nextInt(0, numbers.size()));
				this.correctOrder[i] = String.valueOf(number);
			} else {
				number = Integer.parseInt(this.correctOrder[i]);
			}

			// TODO: fix in iLogos this "feature" (+1)
			answer.setAttribute("value", String.valueOf(Integer.parseInt(this.correctOrder[i]) + 1));

			sortedAnswers[number] = new Node[] { answer, span.getElementsByTagName("label").item(0) };
		}

		for (Node[] pair : sortedAnswers) {
			fieldset.appendChild(pair[0]);
			fieldset.appendChild(pair[1]);
		}

		answersBlock.appendChild(fieldset);

		return div;
	}

	@Override
	public int getType() {
		return (isOneChoice) ? CHOICE : MULTIPLE_CHOICE;
	}

}
