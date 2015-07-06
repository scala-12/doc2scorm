package com.ipoint.coursegenerator.core.courseModel.blocks;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.blocks.items.AbstractItem;

/**
 * Abstract paragraph block. These may be {@link ParagraphBlock} or
 * {@link ListBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public abstract class AbstractTextualParagraphBlock extends
		AbstractParagraphBlock {

	protected AbstractTextualParagraphBlock(List<? extends AbstractItem> items) {
		super(items);
	}

}
