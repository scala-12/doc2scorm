package com.ipoint.coursegenerator.core.courseModel.blocks;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.items.AbstractTextItem;

/**
 * Text block which may include {@link AbstractTextItem}
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
	 * @return html-element span
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		return this.toHtml(creatorTags, false);
	}

	/**
	 * 
	 * @see TextBlock#toHtml(Document)
	 * 
	 * @return html-element span that may includes tags "u" and "font" with
	 *         color parameter
	 */
	protected Element toHtml(Document creatorTags, boolean isHyperlink) {
		Element span = creatorTags.createElement("span");

		if (this.getItems().size() == 1) {
			span = this.getItems().get(0).toHtml(creatorTags, isHyperlink);
		} else {
			for (AbstractTextItem run : this.getItems()) {
				span.appendChild(run.toHtml(creatorTags, isHyperlink));
			}
		}

		return span;
	}

}
