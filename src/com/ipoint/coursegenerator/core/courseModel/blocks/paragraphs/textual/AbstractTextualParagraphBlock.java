package com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.list.ListBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.ParagraphBlock;

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
public abstract class AbstractTextualParagraphBlock<T extends AbstractItem<?>> extends AbstractParagraphBlock<T> {

	protected AbstractTextualParagraphBlock(List<T> items) {
		super(items);
	}

}
