package com.ipoint.coursegenerator.core.courseModel.blocks.textual;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.list.ListBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.ParagraphBlock;

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
public abstract class AbstractTextualBlock<T extends AbstractItem<?>>
		extends AbstractParagraphBlock<T> {

	protected AbstractTextualBlock(List<T> items) {
		super(items);
	}
	
	abstract public String getText();

}
