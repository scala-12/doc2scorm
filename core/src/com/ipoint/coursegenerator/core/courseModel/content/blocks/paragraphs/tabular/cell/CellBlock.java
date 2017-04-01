package com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.tabular.cell;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Lists;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.AbstractParagraphBlock;

/**
 * This block is an analogue of table cell. There may includes only one
 * {@link CellItem}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class CellBlock extends AbstractParagraphBlock<CellItem> {

	public CellBlock(CellItem item) {
		super(Lists.newArrayList(item));
	}

	@Override
	public Element toHtml(Document creatorTags) {
		return toHtml(creatorTags, true);
	}

	@Override
	public Element toHtmlWithoutStyles(Document creatorTags) {
		return this.toHtml(creatorTags, false);
	}

	private Element toHtml(Document creatorTags, boolean styled) {
		return (styled) ? this.getFirstItem().toHtml(creatorTags)
				: this.getFirstItem().toHtmlWithoutStyles(creatorTags);
	}

	@Override
	public String getText() {
		return this.getFirstItem().getText();
	}

}
