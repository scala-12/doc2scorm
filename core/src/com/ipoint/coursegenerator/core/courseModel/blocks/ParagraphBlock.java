package com.ipoint.coursegenerator.core.courseModel.blocks;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.items.ParagraphItem;

/**
 * This block is an analogue of text paragraph. These includes {@link TextBlock}
 * , {@link HyperlinkBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ParagraphBlock extends AbstractParagraphBlock {

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
		Element paragraph = creatorTags.createElement("p");

		for (ParagraphItem item : this.getItems()) {
			paragraph.appendChild(item.toHtml(creatorTags));
		}

		return paragraph;
	}

}
