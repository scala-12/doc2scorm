package com.ipoint.coursegenerator.core.internalCourse.items;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.internalCourse.blocks.HyperlinkBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.ListBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.ParagraphBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TableBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TextBlock;

/**
 * Item for {@link ParagraphBlock}. This item is other block: {@link TextBlock},
 * {@link TableBlock}, {@link ListBlock} or {@link HyperlinkBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ParagraphItem extends AbstractItem {

	private TextBlock value;

	/**
	 * Create item as block item
	 * 
	 * @param textBlock
	 *            Value of item. There cannot be null
	 */
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
	 *            Value of item. Cannot be null
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

	public Element toHtml(Document creatorTags) {
		return this.getValue().toHtml(creatorTags);
	}

}
