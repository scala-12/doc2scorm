package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.match;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
public class MatchBlock extends AbstractQuestionBlock<MatchItem> {

	public static final String MATCH_BLOCK_ID = "match_block";
	public static final String MATCH_ANSWERS_BLOCK_ID = "match_answers_block";
	public static final String MATCH_LABEL_BLOCK_ID = "match_labels_block";
	public static final String MATCH_ANSWER_ID_PREFIX = MatchItem.MATCH_ANSWER_CLASS + '_';

	public MatchBlock(List<MatchItem> items) {
		this(items, null);
	}

	public MatchBlock(List<MatchItem> items, String task) {
		super(items, task);
		this.correctAnswers = null;
	}

	@Override
	public String[] getCorrect() {
		if (super.getCorrect() == null) {
			this.toHtml(Tools.createEmptyDocument());
		}

		return this.correctAnswers;
	}

	/**
	 * add table to answer block that included list of labels and list of
	 * answers
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element div = super.toHtml(creatorTags);
		Element answersBlock = (Element) Tools.getElementById(div, AbstractQuestionBlock.ANSWER_BLOCK_ID);

		Element table = creatorTags.createElement("table");
		table.setAttribute("id", MATCH_BLOCK_ID);
		table.appendChild(creatorTags.createElement("tbody"));
		table.getFirstChild().appendChild(creatorTags.createElement("tr"));
		table.getFirstChild().getFirstChild().appendChild(creatorTags.createElement("td"));
		table.getFirstChild().getFirstChild().appendChild(creatorTags.createElement("td"));

		Element labels = creatorTags.createElement("ul");
		labels.setAttribute("id", MATCH_LABEL_BLOCK_ID);
		Element answers = creatorTags.createElement("ul");
		answers.setAttribute("id", MATCH_ANSWERS_BLOCK_ID);
		table.getFirstChild().getFirstChild().getFirstChild().appendChild(labels);
		table.getFirstChild().getFirstChild().getLastChild().appendChild(answers);

		ArrayList<Integer> numbers = null;
		boolean withoutCorrectness = this.correctAnswers == null;
		if (withoutCorrectness) {
			this.correctAnswers = new String[answersBlock.getChildNodes().getLength()];
			numbers = new ArrayList<>(this.correctAnswers.length);
			for (int i = 0; i < this.correctAnswers.length; i++) {
				numbers.add(i);
			}
		}
		Element[] sortedAnswers = new Element[this.correctAnswers.length];

		for (int i = 0; answersBlock.hasChildNodes(); i++) {
			// old answer will be transformative and removed after
			// new answer will be added after
			Element span = (Element) answersBlock.getFirstChild();
			answersBlock.removeChild(span);

			Element labelSpan = (Element) span.getFirstChild();
			Element answerSpan = (Element) span.getLastChild();

			if (!labelSpan.getAttribute("id").equals(MatchItem.MATCH_LABEL_4_ANSWER_CLASS)) {
				Element tmp = labelSpan;
				labelSpan = answerSpan;
				answerSpan = tmp;
			}

			Element label = creatorTags.createElement("li");
			label.setAttribute("class", labelSpan.getAttribute("class"));
			Element answer = creatorTags.createElement("li");
			answer.setAttribute("class", answerSpan.getAttribute("class"));

			int number;
			if (withoutCorrectness) {
				number = numbers.remove(ThreadLocalRandom.current().nextInt(0, numbers.size()));
				this.correctAnswers[i] = String.valueOf(number);
			} else {
				number = Integer.parseInt(this.correctAnswers[i]);
			}

			answer.setAttribute("id", MATCH_ANSWER_ID_PREFIX + this.correctAnswers[i]);

			while (labelSpan.hasChildNodes()) {
				label.appendChild(labelSpan.getFirstChild());
			}

			while (answerSpan.hasChildNodes()) {
				answer.appendChild(answerSpan.getFirstChild());
			}

			sortedAnswers[number] = answer;

			labels.appendChild(label);
		}

		for (Element answer : sortedAnswers) {
			answers.appendChild(answer);
		}

		answersBlock.appendChild(table);

		return div;
	}

	@Override
	public int getType() {
		return MATCHING;
	}
}
