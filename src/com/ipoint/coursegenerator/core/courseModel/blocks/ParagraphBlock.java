package com.ipoint.coursegenerator.core.courseModel.blocks;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.items.ParagraphItem;

/**
 * Block which may includes other blocks. This block is an analogue of paragraph
 * text and extends of {@link AbstractBlock}
 * 
 * @see AbstractBlock
 * @author Kalashnikov Vladislav
 *
 */
public class ParagraphBlock extends AbstractParagraphBlock {

	/**
	 * Create ParagraphBlock which includes items
	 */
	public ParagraphBlock(List<ParagraphItem> items) {
		super(items);
	}

	@Override
	public List<ParagraphItem> getItems() {
		return (List<ParagraphItem>) super.getItems();
	}

	@Override
	public Element toHtml(Document creatorTags) {
		Element par = creatorTags.createElement("p");
		
		for (ParagraphItem item : this.getItems()) {
			par.appendChild(item.toHtml(creatorTags));
		}
		
		return par;
	}

}
