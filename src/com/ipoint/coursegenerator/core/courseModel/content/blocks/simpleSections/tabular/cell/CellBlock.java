package com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.tabular.cell;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Lists;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionBlock;

/**
 * This block is an analogue of table cell. There may includes only one
 * {@link CellItem}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class CellBlock extends AbstractSectionBlock<CellItem> {

	public CellBlock(CellItem item) throws BlockCreationException {
		super(Lists.newArrayList(item));
	}

	@Override
	public Element toHtml(Document creatorTags) {
		return toHtml(creatorTags, true);
	}

	private Element toHtml(Document creatorTags, boolean styled) {
		return this.getFirstItem().toHtml(creatorTags);
	}

	@Override
	public String getText() {
		return this.getFirstItem().getText();
	}

}
