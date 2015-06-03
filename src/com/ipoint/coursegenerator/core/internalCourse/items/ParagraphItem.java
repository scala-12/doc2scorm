package com.ipoint.coursegenerator.core.internalCourse.items;

import java.util.ArrayList;
import java.util.List;

import com.ipoint.coursegenerator.core.internalCourse.blocks.AbstractBlock;

public class ParagraphItem extends AbstractItem {

	private AbstractBlock item;
	
	public ParagraphItem() {
		this.item = null;
	}

	public ParagraphItem(AbstractBlock item) {
		this();
		setValue(item);
	}

	@Override
	public AbstractBlock getValue() {
		return this.item;
	}

	@Override
	public void setValue(Object value) {
		this.item = (AbstractBlock) value;
	}
}
