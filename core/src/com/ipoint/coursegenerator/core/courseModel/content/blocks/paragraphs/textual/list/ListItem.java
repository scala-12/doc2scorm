package com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.list;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.AbstractParagraphItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.AbstractTextualBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.ParagraphBlock;

/**
 * Item includes {@link ParagraphBlock} or {@link ListBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ListItem extends AbstractParagraphItem<AbstractTextualBlock<?>> {

	public ListItem(AbstractTextualBlock<?> paragraph) {
		super(paragraph);
	}

	/**
	 * @return html-element li
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		return this.toHtml(creatorTags, true);
	}

	@Override
	public Element toHtmlWithoutStyles(Document creatorTags) {
		return this.toHtml(creatorTags, false);
	}

	private Element toHtml(Document creatorTags, boolean styled) {
		Element listItem = creatorTags.createElement("li");
		Node itemValue = (styled) ? this.getValue().toHtml(creatorTags)
				: this.getValue().toHtmlWithoutStyles(creatorTags);
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

	@Override
	public String getText() {
		return this.getValue().getText();
	}

}
