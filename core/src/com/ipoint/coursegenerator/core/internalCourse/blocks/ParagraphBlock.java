package com.ipoint.coursegenerator.core.internalCourse.blocks;

import java.util.List;

import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.internalCourse.items.ParagraphItem;

/**
 * Block which may includes other blocks. This block is an analogue of paragraph
 * text and extends of {@link AbstractBlock}
 * 
 * @see AbstractBlock
 * @author Kalashnikov Vladislav
 *
 */
public class ParagraphBlock extends AbstractParagraphBlock {

	/**
	 * Create ParagraphBlock which includes items
	 */
	public ParagraphBlock(List<ParagraphItem> items) {
		super(items);
	}

	@Override
	public List<ParagraphItem> getItems() {
		return (List<ParagraphItem>) super.getItems();
	}

	@Override
	public Node toHtml() {
		// TODO Auto-generated method stub
		return null;
	}

}
