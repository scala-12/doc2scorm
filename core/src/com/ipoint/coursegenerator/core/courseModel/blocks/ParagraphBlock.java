package com.ipoint.coursegenerator.core.courseModel.blocks;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.items.ParagraphItem;

/**
 * This block is an analogue of text paragraph. These includes
 * {@link TextBlock} , {@link HyperlinkBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ParagraphBlock extends AbstractTextualParagraphBlock {

	public ParagraphBlock(List<ParagraphItem> items) {
		super(items);
	}

	@Override
	public List<ParagraphItem> getItems() {
		return (List<ParagraphItem>) super.getItems();
	}

	/**
	 * @return html-element p
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		return this.getItems().get(0).toHtml(creatorTags);
	}

}
