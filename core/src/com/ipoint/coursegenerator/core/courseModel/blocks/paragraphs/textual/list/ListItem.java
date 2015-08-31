package com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.list;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.AbstractTextualParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.ParagraphBlock;

/**
 * Item includes {@link ParagraphBlock} or {@link ListBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ListItem extends AbstractItem<AbstractTextualParagraphBlock<?>> {

	public ListItem(AbstractTextualParagraphBlock<?> paragraph) {
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
			itemValue = itemValue.getFirstChild();
		}
		listItem.appendChild(itemValue);

		return listItem;
	}

}
