package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.tabular.cell.CellBlock;

/**
 * This item includes {@link CellBlock}).
 * 
 * @author Kalashnikov Vladislav
 *
 */
public abstract class AbstractQuestionItem<T> extends AbstractSectionItem<T> {

	public AbstractQuestionItem(T value) throws ItemCreationException {
		super(value);
	}

	@Override
	public String getText() {
		return this.getIndex() + ". ";
	}

}
