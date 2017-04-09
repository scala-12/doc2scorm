package com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractItem;

public abstract class AbstractParagraphItem<T> extends AbstractItem<T> implements ConvertableWithText {

	protected AbstractParagraphItem(T value) {
		super(value);
	}

	@Override
	public NodeList toSimpleHtml(Document creatorTags) {
		Element span = creatorTags.createElement("span");

		if (this.getValue() instanceof AbstractParagraphBlock) {
			NodeList items = ((AbstractParagraphBlock<?>) this.getValue()).toSimpleHtml(creatorTags);
			while (items.getLength() != 0) {
				span.appendChild(items.item(0));
			}
		} else {
			span.appendChild(this.toHtml(creatorTags));
		}

		return span.getChildNodes();
	}

}
