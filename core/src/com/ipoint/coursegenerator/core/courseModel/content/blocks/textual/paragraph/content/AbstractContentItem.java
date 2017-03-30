package com.ipoint.coursegenerator.core.courseModel.content.blocks.textual.paragraph.content;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.textual.paragraph.content.items.FormulaContentItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.textual.paragraph.content.items.ImageContentItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.textual.paragraph.content.items.TextContentItem;

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

	@Override
	abstract public Element toHtml(Document creatorTags);

}
