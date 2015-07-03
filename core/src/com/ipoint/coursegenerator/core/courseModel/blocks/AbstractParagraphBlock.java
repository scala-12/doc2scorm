package com.ipoint.coursegenerator.core.courseModel.blocks;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.blocks.items.AbstractItem;

/**
 * Abstract paragraph block. These may be {@link ParagraphBlock},
 * {@link TableBlock} or {@link ListBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public abstract class AbstractParagraphBlock extends AbstractBlock {

	protected AbstractParagraphBlock(List<? extends AbstractItem> items) {
		super(items);
	}

}
