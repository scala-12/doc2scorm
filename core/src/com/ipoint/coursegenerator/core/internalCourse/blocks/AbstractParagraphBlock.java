package com.ipoint.coursegenerator.core.internalCourse.blocks;

import java.util.List;

import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.internalCourse.items.AbstractItem;

public abstract class AbstractParagraphBlock extends AbstractBlock {

	/**
	 * @see AbstractBlock#AbstractBlock(List)
	 */
	protected AbstractParagraphBlock(List<? extends AbstractItem> items) {
		super(items);
	}

	@Override
	public Node toHtml() {
		// TODO Auto-generated method stub
		return null;
	}

}
