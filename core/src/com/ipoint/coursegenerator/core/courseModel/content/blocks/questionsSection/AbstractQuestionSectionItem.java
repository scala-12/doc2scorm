package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractSectionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.tabular.cell.CellBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;

/**
 * This item includes {@link CellBlock}).
 * 
 * @author Kalashnikov Vladislav
 *
 */
public abstract class AbstractQuestionSectionItem<T> extends AbstractSectionItem<T> {

	public AbstractQuestionSectionItem(T value) throws ItemCreationException {
		super(value);
	}

	@Override
	public String getText() {
		return this.getIndex() + ". ";
	}

}
