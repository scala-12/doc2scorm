package com.ipoint.coursegenerator.core.internalCourse.blocks;

import java.util.List;

import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.internalCourse.items.AbstractItem;

public class TextBlock extends AbstractBlock {

	public TextBlock(List<AbstractItem> itemList) {
		super(itemList);
	}

	public TextBlock() {
		this(null);
	}

	@Override
	public Node toHtml() {
		// TODO Auto-generated method stub

		return null;
	}

}
