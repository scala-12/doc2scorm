package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.sortable.sequence;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.AbstractContentSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.sortable.SortableItem;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class SequenceItem extends SortableItem {

	public static final String SEQUENCE_ANSWER_CLASS = "sequence_answer";
	public static final String SEQUENCE_ANSWER_ALL_CLASSES = SEQUENCE_ANSWER_CLASS + " "
			+ String.join(" ", SortableItem.ANSWER_OTHER_CLASSES);
	public static final String SEQUENCE_ANSWER_ID_PREFIX = SequenceItem.SEQUENCE_ANSWER_CLASS + '_';

	public SequenceItem(List<AbstractContentSectionBlock<?>> content) throws ItemCreationException {
		super(content);
	}

	@Override
	protected String getItemClasses() {
		return SEQUENCE_ANSWER_ALL_CLASSES;
	}

	@Override
	protected String getItemIdPrefix() {
		return SEQUENCE_ANSWER_ID_PREFIX;
	}

}
