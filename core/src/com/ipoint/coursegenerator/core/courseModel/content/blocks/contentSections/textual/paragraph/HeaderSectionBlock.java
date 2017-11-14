package com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.content.AbstractContentRunItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.contentSections.textual.paragraph.content.runs.TextRunItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;

/**
 * This block is an analogue of paragraph header.
 * 
 * @see ParagraphSectionBlock
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class HeaderSectionBlock extends ParagraphSectionBlock {

	private int level;

	private static final int MIN_HEADER_LEVEL = 1;
	// started from 1 because 0 is level of page header

	/**
	 * Number of different between header of model and html. In model header of
	 * page is 0, but in HTML it is 1
	 */
	public static final int LEVEL_OFFSET = 1;

	public HeaderSectionBlock(List<ParagraphSectionItem> items) throws BlockCreationException {
		this(items, 1);
	}

	public HeaderSectionBlock(List<ParagraphSectionItem> items, int level) throws BlockCreationException {
		super(items, null);
		if (!this.setLevel(level)) {
			this.setLevel(MIN_HEADER_LEVEL);
		}
	}

	/**
	 * Returns level of header on page. More that 0 (level of header of page)
	 * 
	 * @return level of header on page
	 */
	public int getLevel() {
		return this.level;
	}

	/**
	 * Set level of header
	 * 
	 * @param level
	 *            Level of header. More than 0
	 * @return If successful then true
	 */
	public boolean setLevel(int level) {
		if (level >= MIN_HEADER_LEVEL) {
			this.level = level;

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns html-element of header
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		Element header = creatorTags.createElement("h".concat(String.valueOf(this.getLevel() + LEVEL_OFFSET)));

		for (AbstractContentRunItem<?> item : this.getItems().get(0).getValue().getItems()) {
			if (item instanceof TextRunItem) {
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
