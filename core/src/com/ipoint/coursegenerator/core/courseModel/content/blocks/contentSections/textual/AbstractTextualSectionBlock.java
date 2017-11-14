package com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractSectionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.list.ListSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.ParagraphSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;

/**
 * Abstract paragraph block. These may be {@link ParagraphSectionBlock} or
 * {@link ListSectionBlock}
 * 
 * @author Kalashnikov Vladislav
 * 
 * @param <T>
 *            Type of item that included in block
 *
 */
public abstract class AbstractTextualSectionBlock<T extends AbstractSectionItem<?>> extends AbstractSectionBlock<T> {

	protected AbstractTextualSectionBlock(List<T> items) throws BlockCreationException {
		super(items);
	}

	@Override
	public List<T> getItems() {
		return super.getItems();
	}

}
