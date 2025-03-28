package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.sortable.match;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.AbstractContentSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.sortable.SortableItem;

/**
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class MatchItem extends SortableItem {

	public static final String MATCH_ANSWER_CLASS = "match_answer";
	public static final String MATCH_ANSWER_ALL_CLASSES = MATCH_ANSWER_CLASS + " "
			+ String.join(" ", SortableItem.ANSWER_OTHER_CLASSES);
	public static final String MATCH_ANSWER_ID_PREFIX = MATCH_ANSWER_CLASS + '_';

	MatchItem(List<AbstractContentSectionBlock<?>> answer) throws ItemCreationException {
		super(answer);
	}

	@Override
	protected String getItemClasses() {
		return MATCH_ANSWER_ALL_CLASSES;
	}

	@Override
	protected String getItemIdPrefix() {
		return MATCH_ANSWER_ID_PREFIX;
	}

}
