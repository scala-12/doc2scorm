package com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.AbstractParagraphItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.HyperlinkBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.TextBlock;

/**
 * This item is other block: {@link TextBlock} or {@link HyperlinkBlock}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class ParagraphItem extends AbstractParagraphItem<TextBlock> {

	public ParagraphItem(TextBlock value) {
		super(value);
	}

	/**
	 * @see TextBlock#toHtml(Document)
	 * @see HyperlinkBlock#toHtml(Document)
	 */
	@Override
	public Element toHtml(Document creatorTags) {
		return this.toHtml(creatorTags, true);
	}

	@Override
	public Element toHtmlWithoutStyles(Document creatorTags) {
		return this.toHtml(creatorTags, false);
	}

	private Element toHtml(Document creatorTags, boolean styled) {
		return (styled) ? this.getValue().toHtml(creatorTags) : this.getValue().toHtmlWithoutStyles(creatorTags);
	}

	@Override
	public String getText() {
		return this.getValue().getText();
	}

}
