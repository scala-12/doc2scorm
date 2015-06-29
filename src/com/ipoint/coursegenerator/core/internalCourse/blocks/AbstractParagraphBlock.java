package com.ipoint.coursegenerator.core.internalCourse.blocks;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.internalCourse.items.AbstractItem;

public abstract class AbstractParagraphBlock extends AbstractBlock {

	/**
	 * @see AbstractBlock#AbstractBlock(List)
	 */
	protected AbstractParagraphBlock(List<? extends AbstractItem> items) {
		super(items);
	}

	@Override
	public Element toHtml(Document creatorTags) {
		return null;
	}

}
