package com.ipoint.coursegenerator.core.courseModel.blocks.items;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.ParagraphHyperlinkBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.ParagraphTextBlock;

/**
 * This item is other block: {@link ParagraphTextBlock} or
 * {@link ParagraphHyperlinkBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ParagraphItem extends AbstractItem {

	private ParagraphTextBlock value;

	public ParagraphItem(ParagraphTextBlock textBlock) {
		this.setValue(textBlock);
	}

	public ParagraphTextBlock getValue() {
		return value;
	}

	/**
	 * Set value of item
	 * 
	 * @param textBlock
	 *            Text as block. If there is null then return false
	 * @return If successful then true
	 */
	public boolean setValue(ParagraphTextBlock textBlock) {
		if (textBlock == null) {
			return false;
		} else {
			this.value = textBlock;
			return true;
		}
	}

	/**
	 * @see ParagraphTextBlock#toHtml(Document)
	 * @see ParagraphHyperlinkBlock#toHtml(Document)
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		return this.getValue().toHtml(creatorTags);
	}

}
