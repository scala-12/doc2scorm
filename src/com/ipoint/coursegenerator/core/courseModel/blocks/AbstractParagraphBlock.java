package com.ipoint.coursegenerator.core.courseModel.blocks;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.blocks.tabular.TableBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.AbstractTextualBlock;

/**
 * Abstract paragraph block. These may be {@link TableBlock} or
 * {@link AbstractTextualBlock}
 * 
 * @author Kalashnikov Vladislav
 * 
 * @param <T>
 *            Type of item that included in block
 */
public abstract class AbstractParagraphBlock<T extends AbstractItem<?>> extends AbstractBlock<T> {

	protected AbstractParagraphBlock(List<T> items) {
		super(items);
	}

}
