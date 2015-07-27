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
 */
public abstract class AbstractTextualParagraphBlock extends
		AbstractParagraphBlock {

	protected AbstractTextualParagraphBlock(List<? extends AbstractItem> items) {
		super(items);
	}

}
