package com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.list.ListSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.ParagraphBlock;

/**
 * Abstract paragraph block. These may be {@link ParagraphBlock} or
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
