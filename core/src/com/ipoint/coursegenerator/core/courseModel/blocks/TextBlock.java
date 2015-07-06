package com.ipoint.coursegenerator.core.courseModel.blocks;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.items.AbstractTextItem;

/**
 * This block is analogue paragraph in life. Text block which may include
 * {@link AbstractTextItem}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TextBlock extends AbstractBlock {

	public TextBlock(List<AbstractTextItem> runs) {
		super(runs);
	}

	@Override
	public List<AbstractTextItem> getItems() {
		return (List<AbstractTextItem>) super.getItems();
	}

	/**
	 * @return html-element p
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		return this.toHtml(creatorTags, false);
	}

	/**
	 * 
	 * @see TextBlock#toHtml(Document)
	 * 
	 * @return html-element p that may includes tags "u" and "font" with color
	 *         parameter
	 */
	protected Element toHtml(Document creatorTags, boolean isHyperlink) {
		Element paragraph = creatorTags.createElement("p");

		for (AbstractTextItem run : this.getItems()) {
			paragraph.appendChild(run.toHtml(creatorTags, isHyperlink));
		}

		return paragraph;
	}

}
