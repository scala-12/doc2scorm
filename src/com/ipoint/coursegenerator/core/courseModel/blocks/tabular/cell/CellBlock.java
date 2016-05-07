package com.ipoint.coursegenerator.core.courseModel.blocks.tabular.cell;

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
public class CellBlock extends AbstractBlock<CellItem> {

	public CellBlock(CellItem item) {
		super(Lists.newArrayList(item));
	}

	@Override
	public Element toHtml(Document creatorTags) {
		return this.getFirstItem().toHtml(creatorTags);
	}

}
