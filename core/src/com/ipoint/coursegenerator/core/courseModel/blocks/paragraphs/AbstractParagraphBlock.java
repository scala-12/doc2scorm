package com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.tabular.TableBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.AbstractTextualParagraphBlock;

/**
 * Abstract paragraph block. These may be {@link TableBlock} or
 * {@link AbstractTextualParagraphBlock}
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
