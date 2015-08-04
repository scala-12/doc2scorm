package com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;
import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.AbstractTextualParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.HyperlinkBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.TextBlock;

/**
 * This block is an analogue of text paragraph. These includes {@link TextBlock}
 * , {@link HyperlinkBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ParagraphBlock extends AbstractTextualParagraphBlock {

	public ParagraphBlock(List<ParagraphItem> items) {
		super(items);
	}

	public ParagraphBlock(ParagraphItem item) {
		super(Lists.newArrayList(item));
	}

	@Override
	public List<ParagraphItem> getItems() {
		ArrayList<ParagraphItem> items = new ArrayList<ParagraphItem>();
		for (AbstractItem item : super.getItems()) {
			items.add((ParagraphItem) item);
		}

		return items;
	}

	/**
	 * @return html-element p
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element par = creatorTags.createElement("p");
		for (ParagraphItem item : this.getItems()) {
			NodeList nodes = item.toHtml(creatorTags).getChildNodes();
			for (int i = 0; i < nodes.getLength(); ++i) {
				par.appendChild(nodes.item(i));
			}
		}

		return par;
	}

}
