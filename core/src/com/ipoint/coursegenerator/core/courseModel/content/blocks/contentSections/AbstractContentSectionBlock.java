package com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections;

import java.util.List;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractSectionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;

public abstract class AbstractContentSectionBlock<T extends AbstractSectionItem<?>> extends AbstractSectionBlock<T> {

	protected AbstractContentSectionBlock(List<T> items) throws BlockCreationException {
		super(items);
	}

	protected AbstractContentSectionBlock(T item) throws BlockCreationException {
		super(item);
	}

}
