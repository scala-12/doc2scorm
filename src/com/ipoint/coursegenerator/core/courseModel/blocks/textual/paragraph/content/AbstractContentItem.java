package com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.content;

import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.content.items.FormulaContentItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.content.items.ImageContentItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.content.items.TextContentItem;

/**
 * Item that may be {@link TextContentItem}, {@link FormulaContentItem} or
 * {@link ImageContentItem}
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
