package com.ipoint.coursegenerator.core.internalCourse.items;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.internalCourse.blocks.HyperlinkBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.ListBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.ParagraphBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TextBlock;

/**
 * Item for {@link ListBlock}. This item includes other blocks:
 * {@link HyperlinkBlock} and {@link TextBlock}
 * 
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ListItem extends AbstractItem {

	private ParagraphBlock value;

	/**
	 * Create item as block item
	 * 
	 * @param paragraph
	 *            Item of block. There cannot be null
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
	 * Set value of item
	 * 
	 * @param block
	 *            Item of block. There cannot be null
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

	public Element toHtml(Document creatorTags) {
		return this.getValue().toHtml(creatorTags);
	}

}
