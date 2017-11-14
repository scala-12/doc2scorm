package com.ipoint.coursegenerator.core.courseModel.content.blocks;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractBlock.AbstractItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;

public abstract class AbstractSectionItem<T> extends AbstractItem<T> implements ConvertableWithText {

	protected AbstractSectionItem(T value) throws ItemCreationException {
		super(value);
	}

	@Override
	public NodeList toSimpleHtml(Document creatorTags) {
		Element span = creatorTags.createElement("span");

		if (this.getValue() instanceof AbstractSectionBlock) {
			NodeList items = ((AbstractSectionBlock<?>) this.getValue()).toSimpleHtml(creatorTags);
			while (items.getLength() != 0) {
				span.appendChild(items.item(0));
			}
		} else {
			span.appendChild(this.toHtml(creatorTags));
		}

		return span.getChildNodes();
	}

	@Override
	public String toString() {
		return this.getText();
	}

}
