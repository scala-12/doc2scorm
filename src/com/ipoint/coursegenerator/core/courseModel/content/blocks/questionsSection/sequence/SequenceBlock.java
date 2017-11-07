package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.sequence;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionBlock;
import com.ipoint.coursegenerator.core.utils.Tools;

/**
 * @author Kalashnikov Vladislav
 *
 */
public class SequenceBlock extends AbstractQuestionBlock<SequenceItem> {

	public static final String SEQUENCE_ANSWERS_BLOCK_ID = "sequence_answers_block";

	public SequenceBlock(List<SequenceItem> items) throws BlockCreationException {
		this(items, null);
	}

	public SequenceBlock(List<SequenceItem> items, String task) throws BlockCreationException {
		super(items, task);

		ArrayList<String> correctAnswers = new ArrayList<>(items.size());

		for (SequenceItem item : items) {
			correctAnswers.add(String.valueOf(item.getIndex()));
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

		while (answersBlock.hasChildNodes()) {
			Element item = (Element) answersBlock.getFirstChild();

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
