package com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.AbstractContentItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.contentOptions.TextOptionItem;

/**
 * This block is an analogue of paragraph header.
 * 
 * @see ParagraphBlock
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class HeaderBlock extends ParagraphBlock {

	private int level;

	public HeaderBlock(List<ParagraphItem> items) {
		this(items, null);
	}

	public HeaderBlock(List<ParagraphItem> items, Integer level) {
		super(items);
		this.setLevel(level);
	}

	public boolean setLevel(Integer level) {
		if (level != null) {
			if (level >= 0) {
				this.level = level;
				return true;
			}
		}

		return false;
	}

	public int getLevel() {
		return this.level;
	}

	@Override
	public Element toHtml(Document creatorTags) {
		Element header = creatorTags.createElement("h".concat(String
				.valueOf(this.getLevel() + 1)));

		for (AbstractContentItem item : this.getItems().get(0).getValue()
				.getItems()) {
			if (item instanceof TextOptionItem) {
				Node text = item.toHtml(creatorTags).getFirstChild();
				while (text.hasChildNodes()) {
					// remove all tags for text style - only text
					text = text.getFirstChild();
				}
				header.appendChild(text);
			}
		}

		return header;
	}

}
