package com.ipoint.coursegenerator.core.internalCourse.blocks;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.internalCourse.items.AbstractItem;
import com.ipoint.coursegenerator.core.internalCourse.items.ParagraphItem;

/**
 * Block which may includes other blocks. This block is an analogue of paragraph
 * text and extends of {@link AbstractBlock}
 * 
 * @see AbstractBlock
 * @author Kalashnikov Vladislav
 *
 */
public class ParagraphBlock extends AbstractBlock {

	/**
	 * @see AbstractBlock#AbstractBlock(AbstractItem)
	 */
	public ParagraphBlock(AbstractBlock block) {
		super(new ParagraphItem(block));
	}

	/**
	 * @see AbstractBlock#AbstractBlock(List)
	 */
	public ParagraphBlock(List<AbstractBlock> items) {
		super(blockToBlockItems(items));
	}

	/**
	 * Conversion blocks to block items list
	 * 
	 * @param blocks
	 *            Blocks for conversion
	 * @return Block items List
	 */
	private static List<AbstractItem> blockToBlockItems(
			List<AbstractBlock> blocks) {
		ArrayList<AbstractItem> items = new ArrayList<AbstractItem>();
		for (AbstractBlock block : blocks) {
			if (block.getClass().equals(ParagraphBlock.class)) {
				// TODO: Exception
			} else {
				items.add(new ParagraphItem(block));
			}
		}

		return items;
	}

	@Override
	public boolean isRightItem(AbstractItem item) {
		if (item.getClass().equals(ParagraphItem.class)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Node toHtml() {
		// TODO Auto-generated method stub
		return null;
	}

}
