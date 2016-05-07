package com.ipoint.coursegenerator.core.courseModel.blocks.textual.list;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.AbstractTextualBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.ParagraphBlock;

/**
 * Item includes {@link ParagraphBlock} or {@link ListBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ListItem extends AbstractItem<AbstractTextualBlock<?>> {

	public ListItem(AbstractTextualBlock<?> paragraph) {
		super(paragraph);
	}

	/**
	 * @return html-element li
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element listItem = creatorTags.createElement("li");
		Node itemValue = this.getValue().toHtml(creatorTags);
		if (this.getValue() instanceof ParagraphBlock) {
			// because in this situation tag p equal tag li
			while (itemValue.hasChildNodes()) {
				listItem.appendChild(itemValue.getFirstChild());
			}
		} else {
			listItem.appendChild(itemValue);
		}

		return listItem;
	}

}
