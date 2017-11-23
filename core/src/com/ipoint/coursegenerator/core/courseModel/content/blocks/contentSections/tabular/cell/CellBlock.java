package com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.tabular.cell;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.AbstractContentSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;

/**
 * This block is an analogue of table cell. There may includes only one
 * {@link CellItem}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class CellBlock extends AbstractContentSectionBlock<CellItem> {

	public CellBlock(CellItem item) throws BlockCreationException {
		super(item);
	}

	@Override
	public Element toHtmlModel(Document creatorTags) {
		return toHtml(creatorTags, true);
	}

	private Element toHtml(Document creatorTags, boolean styled) {
		return this.getItem().toHtmlModel(creatorTags);
	}

	@Override
	public String getText() {
		return this.getItem().getText();
	}

	@Override
	public CellItem getItem() {
		return super.getItem();
	}

}
