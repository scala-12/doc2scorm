package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.sortable.sequence;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.sortable.QuestionWithSortableItems;
import com.ipoint.coursegenerator.core.utils.Tools;

/**
 * @author Kalashnikov Vladislav
 *
 */
public class SequenceBlock extends QuestionWithSortableItems<SequenceItem> {

	public static final String SEQUENCE_ANSWERS_BLOCK_ID = "sequence_answers_block";

	public SequenceBlock(List<SequenceItem> items, String task) throws BlockCreationException {
		super(items, task);
	}

	/**
	 * add list to answer block that included items with id
	 */
	@Override
	public Element toHtmlModel(Document creatorTags) {
		Element div = super.toHtmlModel(creatorTags);
		Element answersBlock = (Element) Tools.getElementById(div, AbstractQuestionSectionBlock.ANSWER_BLOCK_ID);

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
	public ComplexQuestionType getType() {
		return ComplexQuestionType.SEQUENCING;
	}

}
