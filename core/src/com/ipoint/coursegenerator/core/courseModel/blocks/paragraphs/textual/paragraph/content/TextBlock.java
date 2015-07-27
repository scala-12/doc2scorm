package com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Lists;
import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractItem;

/**
 * This block is analogue paragraph in life. Text block which may include
 * {@link AbstractContentItem}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TextBlock extends AbstractBlock {

	public TextBlock(List<AbstractContentItem> runs) {
		super(runs);
	}

	public TextBlock(AbstractContentItem run) {
		this(Lists.newArrayList(run));
	}

	@Override
	public List<AbstractContentItem> getItems() {
		ArrayList<AbstractContentItem> items = new ArrayList<AbstractContentItem>();
		for (AbstractItem item : super.getItems()) {
			items.add((AbstractContentItem) item);
		}

		return items;
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

		for (AbstractContentItem run : this.getItems()) {
			paragraph.appendChild(run.toHtml(creatorTags, isHyperlink));
		}

		return paragraph;
	}

}
