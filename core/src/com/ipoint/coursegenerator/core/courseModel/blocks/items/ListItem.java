package com.ipoint.coursegenerator.core.courseModel.blocks.items;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractTextualParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.ListBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.ParagraphBlock;

/**
 * Item includes {@link ParagraphBlock} or {@link ListBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ListItem extends AbstractItem {

	private AbstractTextualParagraphBlock value;

	/**
	 * @param paragraph
	 *            Paragraph. There cannot be null
	 */
	public ListItem(AbstractTextualParagraphBlock paragraph) {
		if (!this.setValue(paragraph)) {
			// TODO:exception
		}
	}

	public AbstractTextualParagraphBlock getValue() {
		return this.value;
	}

	/**
	 * @param block
	 *            Paragraph. If there is null then return false
	 * @return if successful then true
	 */
	public boolean setValue(AbstractTextualParagraphBlock block) {
		if (block == null) {
			return false;
		} else {
			this.value = block;
			return true;
		}
	}

	/**
	 * @return html-element li
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element listItem = creatorTags.createElement("li");
		listItem.appendChild(this.getValue().toHtml(creatorTags));

		return listItem;
	}

}
