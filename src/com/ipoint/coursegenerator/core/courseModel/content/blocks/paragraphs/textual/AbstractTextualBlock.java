package com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.AbstractParagraphItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.list.ListBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.ParagraphBlock;

/**
 * Abstract paragraph block. These may be {@link ParagraphBlock} or
 * {@link ListBlock}
 * 
 * @author Kalashnikov Vladislav
 * 
 * @param <T>
 *            Type of item that included in block
 *
 */
public abstract class AbstractTextualBlock<T extends AbstractParagraphItem<?>> extends AbstractParagraphBlock<T> {

	protected AbstractTextualBlock(List<T> items) {
		super(items);
	}

}
