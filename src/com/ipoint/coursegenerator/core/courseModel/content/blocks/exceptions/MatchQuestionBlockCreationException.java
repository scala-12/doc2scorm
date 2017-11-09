package com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.sortable.match.MatchBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.sortable.match.MatchItem;

public class MatchQuestionBlockCreationException extends BlockCreationException {

	private static final long serialVersionUID = 2451397512289317700L;

	public MatchQuestionBlockCreationException(MatchBlock block, List<MatchItem> items) {
		this(block, items, null);
	}

	protected MatchQuestionBlockCreationException(MatchBlock block, List<MatchItem> items, String errMsg) {
		super(block, items,
				(items.size() != block.getLabels().size()) ? "Count of labels and answers is not equaled" : null);
	}

}
