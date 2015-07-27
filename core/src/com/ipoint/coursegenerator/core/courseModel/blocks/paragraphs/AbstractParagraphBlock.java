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
 */
public abstract class AbstractParagraphBlock extends AbstractBlock {

	protected AbstractParagraphBlock(List<? extends AbstractItem> items) {
		super(items);
	}

}
