package com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content;

import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.contentOptions.FormulaOptionItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.contentOptions.ImageOptionItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.contentOptions.TextOptionItem;

/**
 * Item that may be {@link TextOptionItem}, {@link FormulaOptionItem} or
 * {@link ImageOptionItem}
 * 
 * @author Kalashnikov Vladislav
 * 
 * @param <T>
 *            Type of item that included in block
 *
 */
public abstract class AbstractContentItem<T> extends AbstractItem<T> {

	protected AbstractContentItem(T value) {
		super(value);
	}

}
