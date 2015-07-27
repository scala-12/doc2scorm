package com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.tabular.tableCell;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Lists;
import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractBlock;

/**
 * This block is an analogue of table cell. There may includes only one
 * {@link CellItem}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class CellBlock extends AbstractBlock {

	public CellBlock(CellItem item) {
		super(Lists.newArrayList(item));
	}

	public CellItem getItem() {
		return (CellItem) super.getFirstItem();
	}

	@Override
	public Element toHtml(Document creatorTags) {
		return this.getItem().toHtml(creatorTags);
	}

}
