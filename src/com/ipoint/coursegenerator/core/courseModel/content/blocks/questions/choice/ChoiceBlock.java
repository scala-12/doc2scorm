package com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.choice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.HyperlinkBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.TextBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.AbstractQuestionBlock;

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
			try {
				toHtml(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}

		return this.correctOrder;
	}

	/**
	 * add fieldset to answer block that included inputs with type and value and
	 * labels
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		String type = (isOneChoice) ? "radio" : "checkbox";
		Element div = super.toHtml(creatorTags);
		Element answersBlock = div.getOwnerDocument().getElementById(AbstractQuestionBlock.ANSWER_BLOCK_ID);

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
		ArrayList<Node[]> sortedAnswers = new ArrayList<>(this.correctOrder.length);

		for (int i = 0; answersBlock.getFirstChild() != null; i++) {
			Element span = (Element) answersBlock.getFirstChild();

			Element answer = (Element) span.getElementsByTagName("input").item(0);
			answer.setAttribute("type", type);

			int number;
			if (withoutCorrectness) {
				number = numbers.remove(ThreadLocalRandom.current().nextInt(0, numbers.size()));
				this.correctOrder[i] = String.valueOf(number);
			} else {
				number = Integer.parseInt(this.correctOrder[i]);
			}

			answer.setAttribute("value", this.correctOrder[i]);

			sortedAnswers.set(number, new Node[] { answer, span.getElementsByTagName("label").item(0) });
		}

		for (Node[] pair : sortedAnswers) {
			fieldset.appendChild(pair[0]);
			fieldset.appendChild(pair[1]);
		}

		answersBlock.appendChild(fieldset);

		return div;
	}

}
