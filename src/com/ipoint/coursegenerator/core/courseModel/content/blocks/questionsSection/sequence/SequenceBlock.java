package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.sequence;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionBlock;
import com.ipoint.coursegenerator.core.utils.Tools;

/**
 * @author Kalashnikov Vladislav
 *
 */
public class SequenceBlock extends AbstractQuestionBlock<SequenceItem> {

	public static final String SEQUENCE_ANSWERS_BLOCK_ID = "sequence_answers_block";
	public static final String SEQUENCE_ANSWER_ID_PREFIX = SequenceItem.SEQUENCE_ANSWER_CLASS + '_';

	public SequenceBlock(List<SequenceItem> items) {
		this(items, null);
	}

	public SequenceBlock(List<SequenceItem> items, String task) {
		super(new ArrayList<>(items), task, true);

		ArrayList<String> correctAnswers = new ArrayList<>(items.size());

		List<SequenceItem> shuffledItems = this.getItems();
		for (SequenceItem item : items) {
			correctAnswers.add(String.valueOf(shuffledItems.indexOf(item)));
		}

		this.correctAnswers = correctAnswers.stream().toArray(String[]::new);
	}

	/**
	 * add list to answer block that included items with id
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element div = super.toHtml(creatorTags);
		Element answersBlock = (Element) Tools.getElementById(div, AbstractQuestionBlock.ANSWER_BLOCK_ID);

		Element list = creatorTags.createElement("ul");
		list.setAttribute("id", SEQUENCE_ANSWERS_BLOCK_ID);

		int i = -1;
		while (answersBlock.hasChildNodes()) {
			i += 1;

			Element item = (Element) answersBlock.getFirstChild();
			item.setAttribute("id", SEQUENCE_ANSWER_ID_PREFIX + String.valueOf(i));

			list.appendChild(item);
		}

		answersBlock.appendChild(list);

		return div;
	}

	@Override
	public int getType() {
		return SEQUENCING;
	}

	@Override
	public String toString() {
		StringBuilder text = new StringBuilder();
		int i = -1;
		for (String item : this.getItems().stream().map(item -> item.getText() + " ").toArray(String[]::new)) {
			i += 1;
			text.append(this.correctAnswers[i]).append(". ").append(item);
		}

		return text.toString();
	}

}
