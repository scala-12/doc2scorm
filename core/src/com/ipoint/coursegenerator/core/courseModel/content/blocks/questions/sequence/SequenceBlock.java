package com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.AbstractQuestionBlock;
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
	 * add list to answer block that included items with id
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element div = super.toHtml(creatorTags);
		Element answersBlock = (Element) Tools.getElementById(div, AbstractQuestionBlock.ANSWER_BLOCK_ID);

		Element list = creatorTags.createElement("ul");
		list.setAttribute("id", SEQUENCE_ANSWERS_BLOCK_ID);

		ArrayList<Integer> numbers = null;
		boolean withoutCorrectness = this.correctOrder == null;
		if (withoutCorrectness) {
			this.correctOrder = new String[answersBlock.getChildNodes().getLength()];
			numbers = new ArrayList<>(this.correctOrder.length);
			for (int i = 0; i < this.correctOrder.length; i++) {
				numbers.add(i);
			}
		}
		Element[] sortedAnswers = new Element[this.correctOrder.length];

		for (int i = 0; answersBlock.hasChildNodes(); i++) {
			// old answer will be transformative and removed after
			// new answer will be added after
			Element item = (Element) answersBlock.getFirstChild();
			answersBlock.removeChild(item);

			int number;
			if (withoutCorrectness) {
				number = numbers.remove(ThreadLocalRandom.current().nextInt(0, numbers.size()));
				this.correctOrder[i] = String.valueOf(number);
			} else {
				number = Integer.parseInt(this.correctOrder[i]);
			}
			item.setAttribute("id", SEQUENCE_ANSWER_ID_PREFIX + this.correctOrder[i]);
			sortedAnswers[number] = item;
		}

		for (Element answer : sortedAnswers) {
			list.appendChild(answer);
		}

		answersBlock.appendChild(list);

		return div;
	}

	@Override
	public int getType() {
		return SEQUENCING;
	}

}
