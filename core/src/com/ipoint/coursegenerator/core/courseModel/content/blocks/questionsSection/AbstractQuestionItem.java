package com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.tabular.cell.CellBlock;

/**
 * This item includes {@link CellBlock}).
 * 
 * @author Kalashnikov Vladislav
 *
 */
public abstract class AbstractQuestionItem<T> extends AbstractItem<T> {

	public AbstractQuestionItem(T value) {
		super(value);
	}
}
