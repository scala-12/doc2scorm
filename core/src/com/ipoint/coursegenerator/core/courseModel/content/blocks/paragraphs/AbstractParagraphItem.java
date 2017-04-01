package com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractItem;

public abstract class AbstractParagraphItem<T> extends AbstractItem<T> implements ConvertableWithText {

	protected AbstractParagraphItem(T value) {
		super(value);
	}

}
