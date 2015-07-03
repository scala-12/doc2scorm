package com.ipoint.coursegenerator.core.courseModel.blocks.items;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.HyperlinkBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.TextBlock;

/**
 * This item is other block: {@link TextBlock} or {@link HyperlinkBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ParagraphItem extends AbstractItem {

	private TextBlock value;

	public ParagraphItem(TextBlock textBlock) {
		this.setValue(textBlock);
	}

	public TextBlock getValue() {
		return value;
	}

	/**
	 * Set value of item
	 * 
	 * @param textBlock
	 *            Text as block. If there is null then return false
	 * @return If successful then true
	 */
	public boolean setValue(TextBlock textBlock) {
		if (textBlock == null) {
			return false;
		} else {
			this.value = textBlock;
			return true;
		}
	}

	/**
	 * @see TextBlock#toHtml(Document)
	 * @see HyperlinkBlock#toHtml(Document)
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		return this.getValue().toHtml(creatorTags);
	}

}
