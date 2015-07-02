package com.ipoint.coursegenerator.core.courseModel.blocks;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.blocks.items.AbstractTextItem;

/**
 * Text block which may include images or text. This block is an extends of
 * {@link AbstractBlock}
 * 
 * @see AbstractBlock
 * @author Kalashnikov Vladislav
 *
 */
public class TextBlock extends AbstractBlock {

	/**
	 * Create Text block
	 * 
	 * @param runs
	 *            Runs of text
	 */
	public TextBlock(List<AbstractTextItem> runs) {
		super(runs);
	}

	@Override
	public List<AbstractTextItem> getItems() {
		return (List<AbstractTextItem>) super.getItems();
	}

	@Override
	public Element toHtml(Document creatorTags) {
		return this.toHtml(creatorTags, false);
	}

	protected Element toHtml(Document creatorTags, boolean isHyperlink) {
		Element span = creatorTags.createElement("span");

		for (AbstractTextItem run : this.getItems()) {
			span.appendChild(run.toHtml(creatorTags, isHyperlink));
		}

		return span;
	}

}
