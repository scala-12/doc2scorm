package com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.choice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.HyperlinkBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.TextBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.AbstractQuestionBlock;
import com.ipoint.coursegenerator.core.utils.Tools;
import com.ipoint.coursegenerator.core.utils.Tools.Pair;

/**
 * This block is an analogue of text paragraph. These includes several
 * {@link TextBlock}, {@link HyperlinkBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ChoiceBlock extends AbstractQuestionBlock<ChoiceItem> {

	private static class AnswerNodePair extends Pair<Element, Node> {

		public AnswerNodePair(Element input, Node label) {
			super(input, label);
		}

		public Element getInputElement() {
			return this.left;
		}

		public Node getLabelElement() {
			return this.right;
		}

	}

	public static final String CHOICE_ANSWERS_FIELDSET_ID = "choice_answers_fieldset";

	private boolean isOneChoice;
	private int[] answerOrder;

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
		this.answerOrder = null;
	}

	@Override
	public String[] getCorrect() {
		if (super.getCorrect() == null) {
			this.toHtml(Tools.createEmptyDocument());
		}

		return this.correctAnswers;
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

		int answersCount = answersBlock.getChildNodes().getLength();

		ArrayList<Integer> numbers = null;
		boolean withoutCorrectness = null == this.answerOrder;
		ArrayList<Integer> correctAnswers = null;
		if (withoutCorrectness) {
			correctAnswers = new ArrayList<>(answersCount);
			this.answerOrder = new int[answersCount];
			numbers = new ArrayList<>(answersCount);
			for (int i = 0; i < answersCount; i++) {
				numbers.add(i);
			}
		}

		AnswerNodePair[] stirredAnswers = new AnswerNodePair[answersCount];

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
				if (this.getItems().get(i).isCorrect()) {
					// TODO: fix in iLogos this "feature" (+1)
					correctAnswers.add(number + 1);
				}
				this.answerOrder[i] = number;
			} else {
				number = this.answerOrder[i];
			}

			// TODO: fix in iLogos this "feature" (+1)
			answer.setAttribute("value", String.valueOf(number + 1));

			stirredAnswers[number] = new AnswerNodePair(answer, span.getElementsByTagName("label").item(0));
		}

		if (withoutCorrectness) {
			this.correctAnswers = correctAnswers.stream().sorted().map(number -> number.toString())
					.toArray(String[]::new);
		}

		Arrays.stream(stirredAnswers).forEach(pair -> {
			fieldset.appendChild(pair.getInputElement());
			fieldset.appendChild(pair.getLabelElement());
		});

		answersBlock.appendChild(fieldset);

		return div;
	}

	@Override
	public int getType() {
		return (isOneChoice) ? CHOICE : MULTIPLE_CHOICE;
	}

}
