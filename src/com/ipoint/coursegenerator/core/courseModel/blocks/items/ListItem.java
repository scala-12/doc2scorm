package com.ipoint.coursegenerator.core.courseModel.blocks.items;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.ParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.ParagraphHyperlinkBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.ParagraphTextBlock;

/**
 * Item includes {@link ParagraphHyperlinkBlock} and {@link ParagraphTextBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ListItem extends AbstractItem {

	private ParagraphBlock value;

	/**
	 * @param paragraph
	 *            Paragraph. There cannot be null
	 */
	public ListItem(ParagraphBlock paragraph) {
		if (!this.setValue(paragraph)) {
			// TODO:exception
		}
	}

	public ParagraphBlock getValue() {
		return this.value;
	}

	/**
	 * @param block
	 *            Paragraph. If there is null then return false
	 * @return if successful then true
	 */
	public boolean setValue(ParagraphBlock block) {
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
