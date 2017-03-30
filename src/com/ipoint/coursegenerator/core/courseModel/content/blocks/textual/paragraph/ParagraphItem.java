package com.ipoint.coursegenerator.core.courseModel.content.blocks.textual.paragraph;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.textual.paragraph.content.HyperlinkBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.textual.paragraph.content.TextBlock;

/**
 * This item is other block: {@link TextBlock} or {@link HyperlinkBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ParagraphItem extends AbstractItem<TextBlock> {

	public ParagraphItem(TextBlock value) {
		super(value);
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
