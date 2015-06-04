package com.ipoint.coursegenerator.core.internalCourse.blocks;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.internalCourse.items.AbstractItem;
import com.ipoint.coursegenerator.core.internalCourse.items.ParagraphItem;

public class ParagraphBlock extends AbstractBlock {

	public ParagraphBlock(AbstractBlock block) {
		super(new ParagraphItem(block));
	}

	public ParagraphBlock(List<AbstractBlock> blockList) {
		super(blockToBlockItems(blockList));
	}

	private static List<AbstractItem> blockToBlockItems(
			List<AbstractBlock> blockList) {
		ArrayList<AbstractItem> items = new ArrayList<AbstractItem>();
		for (AbstractBlock block : blockList) {
			items.add(new ParagraphItem(block));
		}

		return items;
	}

	@Override
	public Node toHtml() {
		// TODO Auto-generated method stub
		return null;
	}

}
